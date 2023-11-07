package com.r3.developers.samples.TokenisedCarRegistry.workflows;
import com.r3.developers.samples.TokenisedCarRegistry.contracts.CarContract;
import com.r3.developers.samples.TokenisedCarRegistry.states.CarState;
import net.corda.v5.application.crypto.DigestService;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.FlowEngine;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//CURRENT PROBLEM IS ASIGNING AN ID TO THE TRANSACTION TO BE RECALLED LATER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static net.corda.v5.crypto.DigestAlgorithmName.SHA2_256;

public class CarIssueFlow implements ClientStartableFlow {
    private final static Logger log =  LoggerFactory.getLogger(CarIssueFlow.class);
    @CordaInject
    private JsonMarshallingService jsonMarshallingService;
    @CordaInject
    private MemberLookup memberLookup;
    @CordaInject
    private NotaryLookup notaryLookup;
    @CordaInject
    private UtxoLedgerService ledgerService;
    @CordaInject
    private DigestService digestService;

    @CordaInject
    public FlowEngine flowEngine;

    @NotNull
    @Override
    @Suspendable
    public String call(@NotNull ClientRequestBody requestBody) {
        try{
            CarIssueFlowArg flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, CarIssueFlowArg.class);
            MemberInfo myInfo = memberLookup.myInfo();
            NotaryInfo notary = notaryLookup.getNotaryServices().iterator().next();
            MemberInfo owner = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getOwner())),
                    "MemberLookup can't find otherMember specified in flow arguments."
            );
            MemberInfo thirdParty = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getThirdParty())),
                    "MemberLookup can't find otherMember specified in flow arguments."
            );

            CarState carState = new CarState(
                    digestService.hash(myInfo.getName().getCommonName().getBytes(), SHA2_256),
                    digestService.hash(owner.getName().getCommonName().getBytes(), SHA2_256),
                    digestService.hash(thirdParty.getName().getCommonName().getBytes(), SHA2_256),
                    Arrays.asList(myInfo.getLedgerKeys().get(0), owner.getLedgerKeys().get(0)),
                    flowArgs.getMileage(),
                    flowArgs.isConsumed(),
                    flowArgs.getMakeAndModel(),
                    flowArgs.getReasonForRequest(),
                    owner.getName().getCommonName(),
                    myInfo.getName().getCommonName(),
                    thirdParty.getName().getCommonName(),
                    flowArgs.getLscTire(),
                    flowArgs.getLscOil(),
                    flowArgs.getLscCoolant(),
                    flowArgs.getLscFilter(),
                    flowArgs.getLscBattery(),
                    flowArgs.getAmountOfTimesServiced(),
                    flowArgs.getReasonsForRepair().size(),
                    flowArgs.getReasonsForRepair(),
                    flowArgs.getMods(),
                    flowArgs.getCurrentOwnerMileage(),
                    flowArgs.getUserName(),
                    flowArgs.getIdentifier()
            );
            UtxoTransactionBuilder transactionBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(notary.getName())
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofMinutes(5).toMillis()))
                    .addOutputState(carState)
                    .addCommand(new CarContract.Commands.Issue())
                    .addSignatories(carState.getParticipants());
            UtxoSignedTransaction signedTransaction = transactionBuilder.toSignedTransaction();
            return flowEngine.subFlow(new FinalizeCarTokenSubFlow(signedTransaction, owner.getName()));

            //return finalizedTransaction.getId().toString();
        }catch (Exception e){
            log.warn("Failed to process flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}

/* Example JSON to put into REST-API POST requestBody
{
 "clientRequestId": "issue-1",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.CarIssueFlow",
    "requestBody": {
"consumed": "False",
"mileage" : "0",
"makeAndModel" : "Toyota Supra 2024",
"lscTire": "20230604",
"lscOil" : "20230604",
"lscCoolant" : "20230604",
"lscFilter" : "20230604",
"lscBattery" : "20230604",
"amountOfTimesServiced" : "1",
"currentOwnerMileage" : "0",
"userName" : "Faris",
"thirdParty" : "CN=ADInsurance, OU=Test Dept, O=ADInsurance, L=AbuDhabi, C=AE",
"mods" : ["Exhaust Change", "Spoilers"],
"reasonsForRepair" : [],
"owner": "CN=Faris, OU=Test Dept, O=R3, L=AbuDhabi, C=AE",
"identifier" : "1",
"reasonForRequest" : "Issue a new vehicle"

        }
}
*/
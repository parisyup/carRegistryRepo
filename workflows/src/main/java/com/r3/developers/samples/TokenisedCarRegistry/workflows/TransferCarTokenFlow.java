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
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.token.selection.TokenSelection;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static net.corda.v5.crypto.DigestAlgorithmName.SHA2_256;

public class TransferCarTokenFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(TransferCarTokenFlow.class);

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public MemberLookup memberLookup;

    @CordaInject
    public NotaryLookup notaryLookup;

    // Token Selection API can be injected with CordaInject
    @CordaInject
    public TokenSelection tokenSelection;

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public FlowEngine flowEngine;

    @CordaInject
    public DigestService digestService;

    String authNode = "CN=AUTORITY, OU=Test Dept, O=UAEPD, L=AbuDhabi, C=AE";

    @Suspendable
    @Override
    public String call( ClientRequestBody requestBody) {
        try{
            TransferCarFlowInputArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, TransferCarFlowInputArgs.class);
            MemberInfo myInfo = memberLookup.myInfo();
            NotaryInfo notary = notaryLookup.getNotaryServices().iterator().next();
            MemberInfo owner = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getOwner())),
                    "MemberLookup can't find otherMember specified in flow arguments."
            );
            MemberInfo auth = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(authNode)),
                    "MemberLookup can't find otherMember specified in flow arguments."
            );
            MemberInfo thirdParty = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getThirdParty())),
                    "MemberLookup can't find otherMember specified in flow arguments."
            );
            List<StateAndRef<CarState>> carStatesAndRef = ledgerService.findUnconsumedStatesByType(CarState.class);
            List<StateAndRef<CarState>> carStateAndRefsWithId = carStatesAndRef.stream()
                    .filter(sar -> sar.getState().getContractState().getLinerIdentifier().equals(flowArgs.getIdentifier())).collect(toList());
            CarState carState = new CarState(
                    digestService.hash(myInfo.getName().getCommonName().getBytes(), SHA2_256),
                    digestService.hash(owner.getName().getCommonName().getBytes(), SHA2_256),
                    digestService.hash(thirdParty.getName().getCommonName().getBytes(), SHA2_256),
                    Arrays.asList(myInfo.getLedgerKeys().get(0), owner.getLedgerKeys().get(0), auth.getLedgerKeys().get(0)),
                    flowArgs.getMileage(),
                    flowArgs.isConsumed(),
                    carStateAndRefsWithId.get(0).getState().getContractState().getMakeAndModel(),
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
                    .addInputState(carStateAndRefsWithId.get(0).getRef())
                    .addOutputState(carState)
                    .addCommand(new CarContract.Commands.Update())
                    .addSignatories(carState.getParticipants());
            UtxoSignedTransaction signedTransaction = transactionBuilder.toSignedTransaction();
            flowEngine.subFlow(new FinalizeCarTokenSubFlow(signedTransaction, owner.getName()));
            return flowEngine.subFlow(new FinalizeCarTokenSubFlow(signedTransaction, owner.getName()));

            //return finalizedTransaction.getId().toString();
        }catch (Exception e){
            log.warn("Failed to process flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}
/*
{
    "clientRequestId": "transfer-1",
    "flowClassName": "com.r3.developers.samples.TokenisedCarRegistry.workflows.TransferCarTokenFlow",
    "requestBody": {
"consumed": "False",
"mileage" : "11000",
"lscTire": "20231104",
"lscOil" : "20231104",
"lscCoolant" : "20231104",
"lscFilter" : "20231104",
"lscBattery" : "20231104",
"amountOfTimesServiced" : "2",
"currentOwnerMileage" : "11000",
"userName" : "Faris",
"thirdParty" : "CN=ADInsurance, OU=Test Dept, O=ADInsurance, L=AbuDhabi, C=AE",
"mods" : ["Exhaust Change", "Spoilers", "Speakers Changed"],
"reasonsForRepair" : ["Check Engine light"],
"owner": "CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
"identifier" : "1",
"reasonForRequest" : "Service Check"
        }
}
 */
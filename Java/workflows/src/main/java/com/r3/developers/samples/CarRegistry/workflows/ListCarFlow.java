package com.r3.developers.samples.CarRegistry.workflows;


import com.r3.developers.samples.TokenisedCarRegistry.states.CarState;
import net.corda.v5.application.crypto.DigestService;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;

import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class ListCarFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(ListCarFlow.class);

    // Injects the JsonMarshallingService to read and populate JSON parameters.
    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    // Injects the UtxoLedgerService to enable the flow to make use of the Ledger API.
    @CordaInject
    public UtxoLedgerService utxoLedgerService;

    @CordaInject
    private DigestService digestService;

    @CordaInject
    private MemberLookup memberLookup;

    @Suspendable
    @Override
    public String call(ClientRequestBody requestBody) {

        log.info("LCF 1");

        // Queries the VNode's vault for unconsumed states and converts the result to a serializable DTO.
        List<StateAndRef<CarState>> states = utxoLedgerService.findUnconsumedStatesByType(CarState.class);
        log.info("LCF 2");
        List<ListCarFlowResults> results = states.stream().map(stateAndRef ->
                new ListCarFlowResults(
                        stateAndRef.getState().getContractState().getIdentifier(),
                        stateAndRef.getState().getContractState().getIssuerInfo(),
                        stateAndRef.getState().getContractState().getOwnerInfo(),
                        stateAndRef.getState().getContractState().getThirdPartyInfo(),
                        stateAndRef.getState().getContractState().getMileage(),
                        stateAndRef.getState().getContractState().isConsumed(),
                        stateAndRef.getState().getContractState().getReasonForRequest(),
                        stateAndRef.getState().getContractState().getLscTire(),
                        stateAndRef.getState().getContractState().getLscOil(),
                        stateAndRef.getState().getContractState().getLscCoolant(),
                        stateAndRef.getState().getContractState().getLscFilter(),
                        stateAndRef.getState().getContractState().getLscBattery(),
                        stateAndRef.getState().getContractState().getAmountOfTimesServiced(),
                        stateAndRef.getState().getContractState().getAmountOfRepairs(),
                        stateAndRef.getState().getContractState().getRepairReasons(),
                        stateAndRef.getState().getContractState().getMods(),
                        stateAndRef.getState().getContractState().getCurrentOwnerMileage(),
                        stateAndRef.getState().getContractState().getUserName(),
                        stateAndRef.getState().getContractState().getMakeAndModel()
                )
        ).collect(Collectors.toList());
        log.info("LCF 3");
        // Uses the JsonMarshallingService's format() function to serialize the DTO to Json.
        return jsonMarshallingService.format(results);
    }
}
/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "list-1",
    "flowClassName": "com.r3.developers.samples.CarRegistry.workflows.ListCarFlow",
    "requestBody": {}
}
*/
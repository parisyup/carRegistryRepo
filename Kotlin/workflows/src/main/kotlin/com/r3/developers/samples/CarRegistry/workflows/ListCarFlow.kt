package com.r3.developers.samples.CarRegistry.workflows

import com.r3.developers.samples.CarRegistry.states.CarState
import net.corda.v5.application.flows.ClientRequestBody
import net.corda.v5.application.flows.ClientStartableFlow
import net.corda.v5.application.flows.CordaInject
import net.corda.v5.application.marshalling.JsonMarshallingService
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.ledger.utxo.UtxoLedgerService
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

// This flow is used to list all the gold tokens available in the vault.
class ListCarFlow: ClientStartableFlow {

    private companion object {
        val log = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @CordaInject
    lateinit var jsonMarshallingService: JsonMarshallingService

    // Injects the UtxoLedgerService to enable the flow to make use of the Ledger API.
    @CordaInject
    lateinit var utxoLedgerService: UtxoLedgerService

    @Suspendable
    override fun call(requestBody: ClientRequestBody): String {
        val states = utxoLedgerService.findUnconsumedStatesByType(
            CarState::class.java
        )

        // Queries the VNode's vault for unconsumed states and converts the result to a serializable DTO.
        val results = states.stream().map{
            CarStateList(
                it.state.contractState.ownerInfo    ,
                it.state.contractState.userName,
                it.state.contractState.makeAndModel,
                it.state.contractState.reasonForRequest,
                it.state.contractState.mileage,
                it.state.contractState.consumed,
                it.state.contractState.repairReasons,
                it.state.contractState.mods,
                it.state.contractState.currentOwnerMileage,
                it.state.contractState.lscTire,
                it.state.contractState.lscOil,
                it.state.contractState.lscCoolant,
                it.state.contractState.lscFilter,
                it.state.contractState.lscBattery,
                it.state.contractState.amountOfTimesServiced,
                it.state.contractState.identifier
            )
        }.collect(Collectors.toList())
        // Uses the JsonMarshallingService's format() function to serialize the DTO to Json.
        return jsonMarshallingService.format(results)
    }
}

data class CarStateList(
    val ownerInfo: String,
    val userName: String,
    val makeAndModel: String,
    val reasonForRequest: String,
    val mileage : Int,
    val consumed : Boolean,
    val repairReasons : List<String>,
    val mods : List<String>,
    val currentOwnerMileage : Int,
    val lscTire: Int,
    val lscOil: Int,
    val lscCoolant: Int,
    val lscFilter: Int,
    val lscBattery: Int,
    val amountOfTimesServiced: Int,
    val identifier: Int
)
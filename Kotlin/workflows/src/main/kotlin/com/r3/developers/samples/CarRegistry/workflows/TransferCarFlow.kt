package com.r3.developers.samples.CarRegistry.workflows

import com.r3.developers.samples.CarRegistry.states.CarState
import com.r3.developers.samples.CarRegistry.contracts.CarContract
import net.corda.v5.application.crypto.DigestService
import net.corda.v5.application.flows.ClientRequestBody
import net.corda.v5.application.flows.ClientStartableFlow
import net.corda.v5.application.flows.CordaInject
import net.corda.v5.application.flows.FlowEngine
import net.corda.v5.application.marshalling.JsonMarshallingService
import net.corda.v5.application.membership.MemberLookup
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.base.exceptions.CordaRuntimeException
import net.corda.v5.base.types.MemberX500Name
import net.corda.v5.crypto.DigestAlgorithmName
import net.corda.v5.ledger.common.NotaryLookup
import net.corda.v5.ledger.utxo.StateAndRef
import net.corda.v5.ledger.utxo.UtxoLedgerService
import net.corda.v5.ledger.utxo.token.selection.TokenClaim
import net.corda.v5.ledger.utxo.token.selection.TokenSelection
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder
import net.corda.v5.membership.MemberInfo
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

// This flow will be triggered by Bob to transfer some of his tokens to Charlie. The remaining
// amount of tokens will be given back as change to Bob.
class TransferCarFlow : ClientStartableFlow{

    private companion object {
        val log = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @CordaInject
    lateinit var jsonMarshallingService: JsonMarshallingService

    @CordaInject
    lateinit var memberLookup: MemberLookup

    @CordaInject
    lateinit var notaryLookup: NotaryLookup

    @CordaInject
    lateinit var tokenSelection: TokenSelection

    @CordaInject
    lateinit var ledgerService: UtxoLedgerService

    @CordaInject
    lateinit var flowEngine: FlowEngine

    @CordaInject
    lateinit var digestService: DigestService

    @Suspendable
    override fun call(requestBody: ClientRequestBody): String {
        var tokenClaim: TokenClaim? = null
        var totalAmount: BigDecimal? = null
        var change: BigDecimal? = null
        try{
            val (owner,
                thirdParty,
                mileage,
                consumed,
                repairReasons,
                mods,
                currentOwnerMileage,
                userName,
                reasonForRequest,
                lscTire,
                lscOil,
                lscCoolant,
                lscFilter,
                lscBattery,
                amountOfTimesServiced,
                identifier
            )
                    = requestBody.getRequestBodyAs(
                jsonMarshallingService,
                TransferCarFlowInputArgs::class.java
            )
            // Get MemberInfos for the Vnode running the flow and the issuerMember.
            val myInfo = memberLookup.myInfo()
            val ownerMember = Objects.requireNonNull<MemberInfo?>(
                memberLookup.lookup(owner),
                "MemberLookup can't find owner specified in flow arguments."
            )
            val thirdPartyMember = Objects.requireNonNull<MemberInfo?>(
                memberLookup.lookup(thirdParty),
                "MemberLookup can't find owner specified in flow arguments."
            )

            // Obtain the Notary
            val notary = notaryLookup.notaryServices.iterator().next()


            val carStatesAndRef: List<StateAndRef<CarState>> = ledgerService.findUnconsumedStatesByType(
                CarState::class.java
            )
            val carStateAndRefsWithId: List<StateAndRef<CarState>> = carStatesAndRef.stream()
                .filter { sar: StateAndRef<CarState> ->
                    sar.getState().getContractState().identifier.equals(identifier)
                }.collect(Collectors.toList<StateAndRef<CarState>>())
            val carState = CarState(
                digestService.hash(ownerMember.name.commonName!!.toByteArray(), DigestAlgorithmName.SHA2_256),
                digestService.hash(myInfo.name.commonName!!.toByteArray(), DigestAlgorithmName.SHA2_256),
                digestService.hash(thirdPartyMember.name.commonName!!.toByteArray(), DigestAlgorithmName.SHA2_256),
                mileage,
                consumed,
                repairReasons,
                mods,
                currentOwnerMileage,
                userName,
                carStateAndRefsWithId[0].state.contractState.makeAndModel,
                reasonForRequest,
                ownerMember.name.commonName!!,
                myInfo.name.commonName!!,
                thirdPartyMember.name.commonName!!,
                lscTire,
                lscOil,
                lscCoolant,
                lscFilter,
                lscBattery,
                amountOfTimesServiced,
                repairReasons.size,
                identifier,
                Arrays.asList(myInfo.ledgerKeys[0], ownerMember.ledgerKeys[0])
            )
            val transactionBuilder: UtxoTransactionBuilder = ledgerService.createTransactionBuilder()
                .setNotary(notary.name)
                .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofMinutes(5).toMillis()))
                .addInputState(carStateAndRefsWithId[0].getRef())
                .addOutputState(carState)
                .addCommand(CarContract.Transfer())
                .addSignatories(carState.getParticipants())
            val signedTransaction = transactionBuilder.toSignedTransaction()
            return flowEngine.subFlow<String>(FinalizeCarSubFlow(signedTransaction, ownerMember.name))

            //return finalizedTransaction.getId().toString();
        } catch (e: Exception) {
            log.warn("Failed to process flow for request body " + requestBody + " because: " + e.message);
            throw CordaRuntimeException(e.message);
        }
    }
}

data class TransferCarFlowInputArgs(
    val owner : MemberX500Name,
    val thirdParty : MemberX500Name,
    val mileage : Int,
    val consumed : Boolean,
    val reasonsForRepair : List<String>,
    val mods : List<String>,
    val currentOwnerMileage : Int,
    val userName: String,
    val reasonForRequest: String,
    val lscTire: Int,
    val lscOil: Int,
    val lscCoolant: Int,
    val lscFilter: Int,
    val lscBattery: Int,
    val amountOfTimesServiced: Int,
    val identifier: Int
)
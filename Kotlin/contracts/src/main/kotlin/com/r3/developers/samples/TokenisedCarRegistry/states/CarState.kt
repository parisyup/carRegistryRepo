package com.r3.developers.samples.encumbrance.states

import com.r3.developers.samples.TokenisedCarRegistry.contracts.CarContract
import net.corda.v5.crypto.SecureHash
import net.corda.v5.ledger.utxo.BelongsToContract
import net.corda.v5.ledger.utxo.ContractState
import java.security.PublicKey

@BelongsToContract(CarContract::class)
data class CarState (
    val owner : SecureHash,
    val issuer : SecureHash,
    val thirdParty : SecureHash,
    val mileage : Int,
    val consumed : Boolean,
    val repairReasons : List<String>,
    val mods : List<String>,
    val currentOwnerMileage : Int,
    val userName: String,
    val makeAndModel: String,
    val reasonForRequest: String,
    val ownerInfo: String,
    val issuerInfo: String,
    val thirdPartyInfo: String,
    val lscTire: Int,
    val lscOil: Int,
    val lscCoolant: Int,
    val lscFilter: Int,
    val lscBattery: Int,
    val amountOfTimesServiced: Int,
    val amountOfRepairs: Int,
    val identifier: Int,
    private val participants: List<PublicKey>) : ContractState {

    override fun getParticipants(): List<PublicKey> {
        return participants
    }
}
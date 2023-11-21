package com.r3.developers.samples.CarRegistry.contracts

import com.r3.developers.samples.CarRegistry.states.CarState
import net.corda.v5.ledger.utxo.Command
import net.corda.v5.ledger.utxo.Contract
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction


class CarContract: Contract {

    class Issue: Command
    class Transfer: Command

    override fun verify(transaction: UtxoLedgerTransaction) {
        var outputCarState: CarState
        //Issuing a car contract check
        require(transaction.commands.size == 1) { "Car contract requires 1 command" }

        if (transaction.commands[0] is Issue) {
            require(
                transaction.getInputStates(CarState::class.java).isEmpty()
            ) { "Car contract issue requires 0 inputs" }
            require(transaction.getOutputStates(CarState::class.java).size == 1) { "Car contract issue requires 1 output" }
        }
        if (transaction.commands[0] is Transfer) {
            require(
                !transaction.getInputStates(CarState::class.java).isEmpty()
            ) { "Car contract update requires 1 inputs" }
            require(transaction.getOutputStates(CarState::class.java).size == 1) { "Car contract update requires 1 output" }
        }
        //CHECKING SIGNATURES FOR BOTH UPDATE AND ISSUE SINCE THEY BOTH HAVE OUTPUTS
        if (transaction.commands[0] is Transfer || transaction.commands[0] is Issue) {
            outputCarState = transaction.getOutputStates(CarState::class.java).get(0) as CarState
            require(
                transaction.signatories.contains(
                    outputCarState.getParticipants().get(0)
                )
            ) {  //index 0 is always the issuer
                "Issuer has to sign"
            }
            require(
                transaction.signatories.contains(
                    outputCarState.getParticipants().get(1)
                )
            ) {  //index 0 is always the owner
                "Owner has to sign"
            }
        }

        if (transaction.commands[0] is Transfer) {
            val inputCarState: CarState = transaction.getInputStates(CarState::class.java).get(0) as CarState
            outputCarState = transaction.getOutputStates(CarState::class.java).get(0) as CarState
            val check1: Boolean = inputCarState.amountOfRepairs > outputCarState.amountOfRepairs
            val check2: Boolean = inputCarState.mileage > outputCarState.mileage
            val check3: Boolean = inputCarState.consumed
            val check4: Boolean = inputCarState.repairReasons.size > outputCarState.repairReasons.size
            val check5: Boolean = inputCarState.mods.size > outputCarState.mods.size
            val check6 =
                inputCarState.currentOwnerMileage > outputCarState.currentOwnerMileage && inputCarState.userName === outputCarState.userName
            val check7: Boolean = inputCarState.lscBattery > outputCarState.lscBattery
            val check8: Boolean = inputCarState.lscCoolant > outputCarState.lscCoolant
            val check9: Boolean = inputCarState.lscFilter > outputCarState.lscFilter
            val check10: Boolean = inputCarState.lscOil > outputCarState.lscOil
            val check11: Boolean = inputCarState.lscTire > outputCarState.lscTire
            val check12: Boolean = !outputCarState.mods.containsAll(inputCarState.mods)
            val check13: Boolean = !outputCarState.repairReasons.containsAll(inputCarState.repairReasons)
            val check14: Boolean = outputCarState.currentOwnerMileage > outputCarState.mileage
            val check15 = outputCarState.repairReasons.size !== outputCarState.amountOfRepairs
            val check16 =
                outputCarState.currentOwnerMileage + inputCarState.currentOwnerMileage > outputCarState.mileage && inputCarState.userName === outputCarState.userName
            val check17: Boolean = !outputCarState.makeAndModel.equals(inputCarState.makeAndModel)
            require(!(check1 || check2 || check3 || check4 || check5 || check6 || check7 || check8 || check9 || check10 || check11 || check12 || check13 || check14 || check15 || check16 || check17)) {
                ("Input has mismatched with the output. checks : " + check1 + ", " +
                        check2 + ", " + check3 + ", " + check4 + ", " + check5 + ", " + check6 + ", " + check7 + ", " + check8
                        + ", " + check9 + ", " + check10 + ", " + check11 + ", " + check12 + ", " + check13 + ", " + check14 + ", " + check15 + ", " + check16 + ", " + check17)
            }
        }

    }

}

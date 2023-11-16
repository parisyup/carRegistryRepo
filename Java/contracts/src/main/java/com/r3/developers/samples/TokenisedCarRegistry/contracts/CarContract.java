package com.r3.developers.samples.TokenisedCarRegistry.contracts;

import com.r3.developers.samples.TokenisedCarRegistry.states.CarState;
import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.Contract;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarContract implements Contract {

    @Override
    public void verify(@NotNull UtxoLedgerTransaction transaction) {
        CarState outputCarState = null;
        //Issuing a car contract check
        if(transaction.getCommands().size() != 1){
            throw new IllegalArgumentException("Car contract requires 1 command");
        }
        if(transaction.getCommands().get(0) instanceof CarContract.Commands.Issue){
            if(!transaction.getInputStates(CarState.class).isEmpty()){
                throw new IllegalArgumentException("Car contract issue requires 0 inputs");
            }
            if(transaction.getOutputStates(CarState.class).size() != 1){
                throw new IllegalArgumentException("Car contract issue requires 1 output");
            }
        }

        if(transaction.getCommands().get(0) instanceof CarContract.Commands.Update){
            if(transaction.getInputStates(CarState.class).isEmpty()){
                throw new IllegalArgumentException("Car contract update requires 1 inputs");
            }
            if(transaction.getOutputStates(CarState.class).size() != 1){
                throw new IllegalArgumentException("Car contract update requires 1 output");
            }
        }

        //CHECKING SIGNATURES FOR BOTH UPDATE AND ISSUE SINCE THEY BOTH HAVE OUTPUTS
        if(transaction.getCommands().get(0) instanceof CarContract.Commands.Update || transaction.getCommands().get(0) instanceof CarContract.Commands.Issue){
            outputCarState = (CarState) transaction.getOutputStates(CarState.class).get(0);
            if(!(transaction.getSignatories().contains(outputCarState.getParticipants().get(0)))){ //index 0 is always the issuer
                throw new IllegalArgumentException("Issuer has to sign");
            }
            if(!(transaction.getSignatories().contains(outputCarState.getParticipants().get(1)))){ //index 0 is always the owner
                throw new IllegalArgumentException("Owner has to sign");
            }
        }

        if(transaction.getCommands().get(0) instanceof CarContract.Commands.Update){
            CarState inputCarState = (CarState) transaction.getInputStates(CarState.class).get(0);
            outputCarState = (CarState) transaction.getOutputStates(CarState.class).get(0);

            Boolean check1 = inputCarState.getAmountOfRepairs() > outputCarState.getAmountOfRepairs() ;
            Boolean check2 = inputCarState.getMileage() > outputCarState.getMileage() ;
            Boolean check3 = inputCarState.isConsumed() ;
            Boolean check4 = inputCarState.getRepairReasons().size() > outputCarState.getRepairReasons().size() ;
            Boolean check5 = inputCarState.getMods().size() > outputCarState.getMods().size();
            Boolean check6 = (inputCarState.getCurrentOwnerMileage() > outputCarState.getCurrentOwnerMileage() && inputCarState.getUserName() == outputCarState.getUserName());
            Boolean check7 = inputCarState.getLscBattery() > outputCarState.getLscBattery();
            Boolean check8 = inputCarState.getLscCoolant() > outputCarState.getLscCoolant();
            Boolean check9 = inputCarState.getLscFilter() > outputCarState.getLscFilter();
            Boolean check10 = inputCarState.getLscOil() > outputCarState.getLscOil();
            Boolean check11 = inputCarState.getLscTire() > outputCarState.getLscTire();
            Boolean check12 = !outputCarState.getMods().containsAll(inputCarState.getMods());
            Boolean check13 = !outputCarState.getRepairReasons().containsAll(inputCarState.getRepairReasons());
            Boolean check14 = outputCarState.getCurrentOwnerMileage() > outputCarState.getMileage();
            Boolean check15 = outputCarState.getRepairReasons().size() != outputCarState.getAmountOfRepairs();
            Boolean check16 = outputCarState.getCurrentOwnerMileage() + inputCarState.getCurrentOwnerMileage() > outputCarState.getMileage() && inputCarState.getUserName() == outputCarState.getUserName();
            Boolean check17 = !outputCarState.getMakeAndModel().equals(inputCarState.getMakeAndModel());

            if
            (check1 || check2 || check3 || check4 || check5 || check6 || check7 || check8 || check9 || check10 || check11 || check12 || check13 || check14 || check15 || check16 || check17)
            {
                throw new IllegalArgumentException("Input has mismatched with the output. checks : " + check1 +", "+
                        check2 +", "+ check3 +", "+ check4 +", "+ check5 +", "+ check6 +", "+ check7 +", "+ check8
                        +", "+ check9 +", "+ check10 +", "+ check11 +", "+ check12 +", "+ check13 +", "+ check14 +", "+ check15 +", "+ check16 + ", " + check17);
            }
        }
    }
    public interface Commands extends Command{
        class Issue implements Commands {}
        class Update implements Commands {}
    }
}

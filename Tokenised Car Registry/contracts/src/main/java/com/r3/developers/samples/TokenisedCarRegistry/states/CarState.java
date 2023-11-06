package com.r3.developers.samples.TokenisedCarRegistry.states;

import com.r3.developers.samples.TokenisedCarRegistry.contracts.CarContract;
import net.corda.v5.crypto.SecureHash;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

@BelongsToContract(CarContract.class)
public class CarState implements ContractState {

    private  final SecureHash issuer;
    private  final SecureHash owner;
    private  final SecureHash thirdParty;
    private  final List<PublicKey> participants;///////////////////////////////////////////////////////////////////////////
    private final int mileage;
    private final boolean consumed;
    private final List<String> repairReasons;
    private final List<String> mods;
    private final int currentOwnerMileage;
    private final String userName;
    private final String makeAndModel;
    private final String reasonForRequest;
    private final String ownerInfo;
    private final String issuerInfo;
    private final String thirdPartyInfo;

    private final int lscTire; //yearmonthday is the format for the dates
    private final int lscOil;
    private final int lscCoolant;
    private final int lscFilter;
    private final int lscBattery;
    private final int amountOfTimesServiced;
    private final int amountOfRepairs;
    private final int identifier;


    public CarState(SecureHash issuer, SecureHash owner, SecureHash thirdParty, List<PublicKey> participants, int mileage, boolean consumed, String makeAndModel, String reasonForRequest, String ownerInfo, String issuerInfo, String thirdPartyInfo, int lscTire, int lscOil, int lscCoolant, int lscFilter, int lscBattery, int amountOfTimesServiced, int amountOfRepairs, List<String> repairReasons, List<String> mods, int currentOwnerMileage, String userName, int identifier) {
        this.makeAndModel = makeAndModel;
        this.identifier = identifier;
        this.issuer = issuer;
        this.owner = owner;
        this.thirdParty = thirdParty;
        this.participants = participants;
        this.mileage = mileage;
        this.consumed = consumed;
        this.reasonForRequest = reasonForRequest;
        this.ownerInfo = ownerInfo;
        this.issuerInfo = issuerInfo;
        this.thirdPartyInfo = thirdPartyInfo;
        this.lscTire = lscTire;
        this.lscOil = lscOil;
        this.lscCoolant = lscCoolant;
        this.lscFilter = lscFilter;
        this.lscBattery = lscBattery;
        this.amountOfTimesServiced = amountOfTimesServiced;
        this.amountOfRepairs = amountOfRepairs;
        this.repairReasons = repairReasons;
        this.mods = mods;
        this.currentOwnerMileage = currentOwnerMileage;
        this.userName = userName;
    }

    @NotNull
    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }

    public SecureHash getThirdParty() {
        return thirdParty;
    }

    public SecureHash getOwner() {
        return owner;
    }

    public SecureHash getIssuer() {
        return issuer;
    }

    public int getMileage() {
        return mileage;
    }

    public int getLscTire() {
        return lscTire;
    }

    public int getLscOil() {
        return lscOil;
    }

    public int getLscCoolant() {
        return lscCoolant;
    }

    public int getLscFilter() {
        return lscFilter;
    }

    public int getLscBattery() {
        return lscBattery;
    }

    public int getAmountOfTimesServiced() {
        return amountOfTimesServiced;
    }

    public int getAmountOfRepairs() {
        return amountOfRepairs;
    }

    public List<String> getRepairReasons() {
        return repairReasons;
    }

    public List<String> getMods() {
        return mods;
    }

    public int getCurrentOwnerMileage() {
        return currentOwnerMileage;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public Object getLinerIdentifier() {
        return identifier;
    }

    public int getIdentifier(){
        return identifier;
    }

    public String getReasonForRequest() {
        return reasonForRequest;
    }

    public String getOwnerInfo() {
        return ownerInfo;
    }

    public String getIssuerInfo() {
        return issuerInfo;
    }

    public String getThirdPartyInfo() {
        return thirdPartyInfo;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }
}

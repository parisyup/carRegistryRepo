package com.r3.developers.samples.TokenisedCarRegistry.workflows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

// A class to hold the deserialized arguments required to start the flow.

public class ListCarFlowResults {

    private  String issuer;
    private  String owner;
    private  String thirdParty;
    //////////////////////////////////////////////////////////////////////////
    private int mileage;
    private boolean consumed;
    private List<String> repairReasons;
    private List<String> mods;
    private int currentOwnerMileage;
    private String userName;
    private String reasonForRequest;
    private String makeAndModel;

    private int lscTire; //yearmonthday is the format for the dates
    private int lscOil;
    private int lscCoolant;
    private int lscFilter;
    private int lscBattery;
    private int amountOfTimesServiced;
    private int amountOfRepairs;
    private int identifier;

    private final static Logger log = LoggerFactory.getLogger(ListCarFlowResults.class);


    public ListCarFlowResults() {
    }

    public ListCarFlowResults(int identifier, String issuer, String owner, String thirdParty, int mileage, boolean consumed, String reasonForRequest, int lscTire, int lscOil, int lscCoolant, int lscFilter, int lscBattery, int amountOfTimesServiced, int amountOfRepairs, List<String> repairReasons, List<String> mods, int currentOwnerMileage, String userName, String makeAndModel) {
        this.makeAndModel = makeAndModel;
        this.identifier = identifier;
        this.issuer = issuer;
        this.owner = owner;
        this.thirdParty = thirdParty;
        this.mileage = mileage;
        this.consumed = consumed;
        this.reasonForRequest = reasonForRequest;
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


    public int getIdentifier(){
        return identifier;
    }
    public String getThirdParty() {
        return thirdParty;
    }

    public String getOwner() {
        return owner;
    }

    public String getIssuer() {
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


    public String getReasonForRequest() {
        return reasonForRequest;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }
}

package com.r3.developers.samples.CarRegistry.workflows;

import java.util.List;

public class CarIssueFlowArg {

    // Serialisation service requires a default constructor
///////////////////////////////////////////////////////////////////////////
    public CarIssueFlowArg(){}
    public CarIssueFlowArg(String owner, int mileage, boolean consumed, String makeAndModel, int tire, int lscOil, int lscCoolant, int lscFilter, int lscBattery, int amountOfTimesServiced, int amountOfRepairs, int currentOwnerMileage, String userName, int identifier, String reasonForRequest, String thirdParty, List<String> mods, List<String> reasonsForRepair) {
        this.mileage = mileage;
        this.consumed = consumed;
        this.makeAndModel = makeAndModel;
        this.lscTire = tire;
        this.lscOil = lscOil;
        this.lscCoolant = lscCoolant;
        this.lscFilter = lscFilter;
        this.lscBattery = lscBattery;
        this.amountOfTimesServiced = amountOfTimesServiced;
        this.currentOwnerMileage = currentOwnerMileage;
        this.userName = userName;
        this.owner = owner;
        this.identifier = identifier;
        this.reasonForRequest = reasonForRequest;
        this.thirdParty = thirdParty;
        this.mods = mods;
        this.reasonsForRepair = reasonsForRepair;
    }
    private int mileage;
    private boolean consumed;
    private int currentOwnerMileage;
    private String userName;
    private String makeAndModel;

    private int lscTire; //yearmonthday is the format for the dates
    private int lscOil;
    private int lscCoolant;
    private int lscFilter;
    private int lscBattery;
    private int amountOfTimesServiced;
    private int identifier;
    private String owner;
    private String reasonForRequest;
    private String thirdParty;
    private List<String> mods;
    private List<String> reasonsForRepair;



    public int getMileage() {
        return mileage;
    }

    public boolean isConsumed() {
        return consumed;
    }


    public int getCurrentOwnerMileage() {
        return currentOwnerMileage;
    }

    public String getUserName() {
        return userName;
    }

    public int getLscTire() {
        return lscTire;
    }

    public int getLscCoolant() {
        return lscCoolant;
    }

    public int getLscOil() {
        return lscOil;
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


    public String getOwner() {
        return owner;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public List<String> getMods() {
        return mods;
    }

    public List<String> getReasonsForRepair() {
        return reasonsForRepair;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getReasonForRequest() {
        return reasonForRequest;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }
}

package com.health.gounihealth.datainfo;

/**
 * Created by LAL on 7/17/2016.
 */
public class MoreInfoIcuInfo {

    String hospitalName;
    String hospitalContact;
    String hospitalDistance;
    String icuType;
    String lastUpdate;
    String bedsNumber;
    String bedsOccupied;
    String totalCost;
    double latitude;
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIcuType() {
        return icuType;
    }

    public void setIcuType(String icuType) {
        this.icuType = icuType;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getBedsOccupied() {
        return bedsOccupied;
    }

    public void setBedsOccupied(String bedsOccupied) {
        this.bedsOccupied = bedsOccupied;
    }

    public String getBedsNumber() {
        return bedsNumber;
    }

    public void setBedsNumber(String bedsNumber) {
        this.bedsNumber = bedsNumber;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getHospitalDistance() {
        return hospitalDistance;
    }

    public void setHospitalDistance(String hospitalDistance) {
        this.hospitalDistance = hospitalDistance;
    }

    public String getHospitalContact() {
        return hospitalContact;
    }

    public void setHospitalContact(String hospitalContact) {
        this.hospitalContact = hospitalContact;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }


}

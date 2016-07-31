package com.health.gounihealth.datainfo;

/**
 * Created by LAL on 7/11/2016.
 */
public class HospitalListInfo {

    private String id;
    private String icuType;
    private String hospitalId;
    private String hospitalName;
    private String bedNumber;
    private Boolean bedAvailability;
    private String bedOccupied;
    private String address;
    private String distance;
    private String contactReception;
    private String contactEmergency;
    private double latitude;
    private double longitude;

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

    public String getContactEmergency() {
        return contactEmergency;
    }

    public void setContactEmergency(String contactEmergency) {
        this.contactEmergency = contactEmergency;
    }

    public String getContactReception() {
        return contactReception;
    }

    public void setContactReception(String contactReception) {
        this.contactReception = contactReception;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBedOccupied() {
        return bedOccupied;
    }

    public void setBedOccupied(String bedOccupied) {
        this.bedOccupied = bedOccupied;
    }

    public Boolean getBedAvailability() {
        return bedAvailability;
    }

    public void setBedAvailability(Boolean bedAvailability) {
        this.bedAvailability = bedAvailability;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getIcuType() {
        return icuType;
    }

    public void setIcuType(String icuType) {
        this.icuType = icuType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

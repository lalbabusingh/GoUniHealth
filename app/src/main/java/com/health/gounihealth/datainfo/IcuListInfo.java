package com.health.gounihealth.datainfo;

/**
 * Created by LAL on 7/10/2016.
 */
public class IcuListInfo {

    private String id;
    private String icuType;
    private String hospitalId;
    private String hospitalName;
    private String bedNumber;
    private Boolean bedAvailability;
    private String bedOccupied;
    private String address;
    private String distance;


    public Boolean getBedAvailability() {
        return bedAvailability;
    }

    public void setBedAvailability(Boolean bedAvailability) {
        this.bedAvailability = bedAvailability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcuType() {
        return icuType;
    }

    public void setIcuType(String icuType) {
        this.icuType = icuType;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getBedOccupied() {
        return bedOccupied;
    }

    public void setBedOccupied(String bedOccupied) {
        this.bedOccupied = bedOccupied;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}

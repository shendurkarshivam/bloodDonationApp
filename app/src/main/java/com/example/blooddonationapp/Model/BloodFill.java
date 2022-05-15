package com.example.blooddonationapp.Model;

public class BloodFill {
    String facilityType, aPlus, aNeg, bPlus, bNeg, abPlus, abNeg, oPlus, oNeg;
    public BloodFill(){

    }

    public BloodFill(String facilityType, String aPlus, String aNeg, String bPlus, String bNeg, String abPlus, String abNeg, String oPlus, String oNeg) {
        this.facilityType = facilityType;
        this.aPlus = aPlus;
        this.aNeg = aNeg;
        this.bPlus = bPlus;
        this.bNeg = bNeg;
        this.abPlus = abPlus;
        this.abNeg = abNeg;
        this.oPlus = oPlus;
        this.oNeg = oNeg;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getaPlus() {
        return aPlus;
    }

    public void setaPlus(String aPlus) {
        this.aPlus = aPlus;
    }

    public String getaNeg() {
        return aNeg;
    }

    public void setaNeg(String aNeg) {
        this.aNeg = aNeg;
    }

    public String getbPlus() {
        return bPlus;
    }

    public void setbPlus(String bPlus) {
        this.bPlus = bPlus;
    }

    public String getbNeg() {
        return bNeg;
    }

    public void setbNeg(String bNeg) {
        this.bNeg = bNeg;
    }

    public String getAbPlus() {
        return abPlus;
    }

    public void setAbPlus(String abPlus) {
        this.abPlus = abPlus;
    }

    public String getAbNeg() {
        return abNeg;
    }

    public void setAbNeg(String abNeg) {
        this.abNeg = abNeg;
    }

    public String getoPlus() {
        return oPlus;
    }

    public void setoPlus(String oPlus) {
        this.oPlus = oPlus;
    }

    public String getoNeg() {
        return oNeg;
    }

    public void setoNeg(String oNeg) {
        this.oNeg = oNeg;
    }
}

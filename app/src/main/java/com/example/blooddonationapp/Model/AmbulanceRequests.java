package com.example.blooddonationapp.Model;

public class AmbulanceRequests {
    String reqId, personId, latitude, longitude, details, userType;
    long timestamp;

    public AmbulanceRequests(){

    }
    public AmbulanceRequests(String reqId, String personId, String latitude, String longitude, String details, String userType, long timestamp) {
        this.reqId = reqId;
        this.personId = personId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.details = details;
        this.userType = userType;
        this.timestamp = timestamp;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

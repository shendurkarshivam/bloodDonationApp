package com.example.blooddonationapp.Model;

public class BloodRequests {
    String bankId, requesterId, requestId, typeOfDonation, bloodGroup, quantity;
    long timeStamp;
    String status;
    String requesterType;

    public BloodRequests(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BloodRequests(String bankId, String requesterId, String requestId, String typeOfDonation, String bloodGroup, String quantity, long timeStamp, String status, String requesterType) {
        this.bankId = bankId;
        this.requesterId = requesterId;
        this.requestId = requestId;
        this.typeOfDonation = typeOfDonation;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.timeStamp = timeStamp;
        this.status = status;
        this.requesterType = requesterType;
    }

    public String getRequesterType() {
        return requesterType;
    }

    public void setRequesterType(String requesterType) {
        this.requesterType = requesterType;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTypeOfDonation() {
        return typeOfDonation;
    }

    public void setTypeOfDonation(String typeOfDonation) {
        this.typeOfDonation = typeOfDonation;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

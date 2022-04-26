package com.example.blooddonationapp.Model;

public class TripDetails {
    String tripId, consumerId, driverId, consumerStartLatitude, consumerStartLongitude, driverStartLatitude, driverStartLongitude, consumerType, endLatitude, endLongitude, tripStatus;
    long acceptanceTimeStamp, requestTimeStamp;
    String tripDetails;
    public TripDetails(){

    }

    public TripDetails(String tripId, String consumerId, String driverId,
                       String consumerStartLatitude, String consumerStartLongitude,
                       String driverStartLatitude, String driverStartLongitude,
                       String consumerType, String endLatitude, String endLongitude,
                       String tripStatus, long acceptanceTimeStamp, long requestTimeStamp,
                       String tripDetails) {
        this.tripId = tripId;
        this.consumerId = consumerId;
        this.driverId = driverId;
        this.consumerStartLatitude = consumerStartLatitude;
        this.consumerStartLongitude = consumerStartLongitude;
        this.driverStartLatitude = driverStartLatitude;
        this.driverStartLongitude = driverStartLongitude;
        this.consumerType = consumerType;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.tripStatus = tripStatus;
        this.acceptanceTimeStamp = acceptanceTimeStamp;
        this.requestTimeStamp = requestTimeStamp;
        this.tripDetails = tripDetails;
    }

    public String getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(String tripDetails) {
        this.tripDetails = tripDetails;
    }

    public long getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public void setRequestTimeStamp(long requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getConsumerStartLatitude() {
        return consumerStartLatitude;
    }

    public void setConsumerStartLatitude(String consumerStartLatitude) {
        this.consumerStartLatitude = consumerStartLatitude;
    }

    public String getConsumerStartLongitude() {
        return consumerStartLongitude;
    }

    public void setConsumerStartLongitude(String consumerStartLongitude) {
        this.consumerStartLongitude = consumerStartLongitude;
    }

    public String getDriverStartLatitude() {
        return driverStartLatitude;
    }

    public void setDriverStartLatitude(String driverStartLatitude) {
        this.driverStartLatitude = driverStartLatitude;
    }

    public String getDriverStartLongitude() {
        return driverStartLongitude;
    }

    public void setDriverStartLongitude(String driverStartLongitude) {
        this.driverStartLongitude = driverStartLongitude;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public String getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public long getAcceptanceTimeStamp() {
        return acceptanceTimeStamp;
    }

    public void setAcceptanceTimeStamp(long acceptanceTimeStamp) {
        this.acceptanceTimeStamp = acceptanceTimeStamp;
    }
}

package com.example.blooddonationapp.Model;

import android.content.Context;

import java.util.ArrayList;

public class AmbulanceProvider {

    String id, name, number, password, email, bg, type, latitude, longitude, ambulanceNumber, ambulanceOrganization, ambulanceFacilities;

    public AmbulanceProvider(){

    }
    public AmbulanceProvider(String id, String name, String number, String password, String email, String bg, String type, String latitude, String longitude, String ambulanceNumber, String ambulanceOrganization, String ambulanceFacilities) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.password = password;
        this.email = email;
        this.bg = bg;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ambulanceNumber = ambulanceNumber;
        this.ambulanceOrganization = ambulanceOrganization;
        this.ambulanceFacilities = ambulanceFacilities;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAmbulanceNumber() {
        return ambulanceNumber;
    }

    public void setAmbulanceNumber(String ambulanceNumber) {
        this.ambulanceNumber = ambulanceNumber;
    }

    public String getAmbulanceOrganization() {
        return ambulanceOrganization;
    }

    public void setAmbulanceOrganization(String ambulanceOrganization) {
        this.ambulanceOrganization = ambulanceOrganization;
    }

    public String getAmbulanceFacilities() {
        return ambulanceFacilities;
    }

    public void setAmbulanceFacilities(String ambulanceFacilities) {
        this.ambulanceFacilities = ambulanceFacilities;
    }
}

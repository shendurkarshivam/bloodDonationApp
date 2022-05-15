package com.example.blooddonationapp.Model;

public class BloodGroup {
    String bloodGroup;
    String quantity;
    String type;

    public BloodGroup(){

    }

    public BloodGroup(String bloodGroup, String quantity, String type) {
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}

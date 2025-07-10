package com.example.cabinetprivat.models;

import java.util.List;

public class PatientModel {
    private String fullName;
    private String phone;
    private String dob;
    private String bloodType;
    private String allergies;

    public PatientModel() {} // constructor necesar pentru Firebase

    public PatientModel(String uid, String name, String fullName, String phone, String dob, String bloodType, String allergies, List<String> allergiesList) {
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.bloodType = bloodType;
        this.allergies = allergies;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getAllergies() {
        return allergies;
    }


}



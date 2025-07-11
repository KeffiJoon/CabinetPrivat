package com.example.cabinetprivat;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String address;
    private String dateOfBirth;
    private String bloodGroup;
    private String allergies;
    private String emergencyContactName; // NOU CÂMP
    private String emergencyContactPhone; // NOU CÂMP
    private boolean detailsCompleted; // Flag pentru a verifica dacă detaliile esențiale sunt completate (din CompleteProfileActivity)

    public User() {
        // Constructor gol necesar pentru Firestore
    }

    public User(String uid, String fullName, String email, String phoneNumber, String profileImageUrl, String address,
                String dateOfBirth, String bloodGroup, String allergies,
                String emergencyContactName, String emergencyContactPhone, // NOU
                boolean detailsCompleted) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.emergencyContactName = emergencyContactName; // NOU
        this.emergencyContactPhone = emergencyContactPhone; // NOU
        this.detailsCompleted = detailsCompleted;
    }

    // Getters și Setters
    @Exclude
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getEmergencyContactName() { return emergencyContactName; } // NOU GETTER
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; } // NOU SETTER

    public String getEmergencyContactPhone() { return emergencyContactPhone; } // NOU GETTER
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; } // NOU SETTER

    public boolean isDetailsCompleted() { return detailsCompleted; }
    public void setDetailsCompleted(boolean detailsCompleted) { this.detailsCompleted = detailsCompleted; }
}


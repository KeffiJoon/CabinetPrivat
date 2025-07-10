package com.example.cabinetprivat.models;

import com.google.gson.annotations.Expose; // Acestea nu mai sunt strict necesare fără Gson, dar nu strică
import com.google.gson.annotations.SerializedName; // Le poți lăsa sau șterge

public class Doctor {

    // Le păstrăm ca String, Double, String, String
    private String name;
    private String specialty;
    private Double rating;
    private String experience;
    private String imageUrl;

    public Doctor() {} // Constructor fără argumente

    public Doctor(String name, String specialty, String experience, Double rating, String imageUrl) {
        this.name = name;
        this.specialty = specialty;
        this.experience = experience;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public Double getRating() { return rating; }
    public String getExperience() { return experience; }
    public String getImageUrl() { return imageUrl; }

    // Setters (opționale)
    public void setName(String name) { this.name = name; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setRating(Double rating) { this.rating = rating; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

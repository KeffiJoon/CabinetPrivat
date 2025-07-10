package com.example.cabinetprivat;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Doc1 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("specialty")
    @Expose
    private String specialty;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("experience")
    @Expose
    private String experience;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
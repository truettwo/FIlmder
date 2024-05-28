package com.example.vv1.model;

public class Movie {
    private String title;
    private String description;
    private double rating;
    private String imageUrl;

    public Movie(String title, String description, double rating, String imageUrl) {
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
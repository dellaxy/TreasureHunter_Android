package com.example.city_tours.entities;

public class Achievement {
    private String title;
    private String tourName;
    private String image;
    private int rating;

    public Achievement(String title, String tourName, String image, int rating) {
        this.title = title;
        this.tourName = tourName;
        this.image = image;
        this.rating = rating;
    }

    public Achievement(String title, String tourName, String image) {
        this.title = title;
        this.tourName = tourName;
        this.image = image;
        this.rating = 0;
    }

    public String getTitle() {
        return title;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public int getRating() {
        return rating;
    }
}

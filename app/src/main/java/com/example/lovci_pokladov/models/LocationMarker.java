package com.example.lovci_pokladov.models;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarker {
    private final LatLng position;
    private final int id, color, difficulty;
    private final String title, icon, description;

    public LocationMarker(int id, double lat, double lng, String title, int color, String icon, int difficulty, String description) {
        this.difficulty = difficulty;
        this.description = description;
        this.position = new LatLng(lat, lng);
        this.id = id;
        this.title = title;
        this.color = color;
        this.icon = icon;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

}

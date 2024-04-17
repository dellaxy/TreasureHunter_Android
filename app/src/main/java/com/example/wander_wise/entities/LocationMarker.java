package com.example.wander_wise.entities;

import com.google.android.gms.maps.model.LatLng;

public class LocationMarker {
    private final LatLng position;
    private final int id, color;
    private final String title, description;
    private Game game;

    public LocationMarker(int id, LatLng position, String title, int color, String description) {
        this.description = description;
        this.position = position;
        this.id = id;
        this.title = title;
        this.color = color;
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

    public String getDescription() {
        return description;
    }
}

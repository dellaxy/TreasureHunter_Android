package com.example.lovci_pokladov.entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LocationMarker {
    private final LatLng position;
    private final int id, color;
    private final String title, icon, description;
    private List<Level> levels;

    public LocationMarker(int id, LatLng position, String title, int color, String icon, String description) {
        this.description = description;
        this.position = position;
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

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }
}

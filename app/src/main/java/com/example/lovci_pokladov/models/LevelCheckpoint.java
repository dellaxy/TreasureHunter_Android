package com.example.lovci_pokladov.models;

import com.google.android.gms.maps.model.LatLng;

public class LevelCheckpoint {
    private int id, areaSize;
    private final String text;
    private final boolean finalCheckpoint;
    private final LatLng position;
    public LevelCheckpoint(int id, String text, boolean finalCheckpoint, LatLng position) {
        this.id = id;
        this.text = text;
        this.finalCheckpoint = finalCheckpoint;
        this.position = position;
        this.areaSize = 3;
    }

    public LevelCheckpoint(int id, String text, boolean finalCheckpoint, LatLng position, int areaSize) {
        this.id = id;
        this.text = text;
        this.finalCheckpoint = finalCheckpoint;
        this.position = position;
        this.areaSize = areaSize;
    }

    public String getText() {
        return text;
    }

    public boolean isFinalCheckpoint() {
        return finalCheckpoint;
    }

    public LatLng getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public int getAreaSize() {
        return areaSize;
    }
}

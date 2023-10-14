package com.example.lovci_pokladov.models;

import com.google.android.gms.maps.model.LatLng;

public class LevelCheckpoint {
    private final String text;
    private final boolean finalCheckpoint;
    private final LatLng position;

    public LevelCheckpoint(String text, boolean finalCheckpoint, LatLng position) {
        this.text = text;
        this.finalCheckpoint = finalCheckpoint;
        this.position = position;
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
}

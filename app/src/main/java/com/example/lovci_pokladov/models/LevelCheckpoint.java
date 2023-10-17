package com.example.lovci_pokladov.models;

import com.google.android.gms.maps.model.LatLng;

public class LevelCheckpoint {
    private final int id;
    private final String text;
    private final boolean finalCheckpoint;
    private final LatLng position;

    public LevelCheckpoint(int id, String text, boolean finalCheckpoint, LatLng position) {
        this.id = id;
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

    public int getId() {
        return id;
    }
}

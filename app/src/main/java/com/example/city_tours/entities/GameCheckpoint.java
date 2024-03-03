package com.example.city_tours.entities;

import com.google.android.gms.maps.model.LatLng;

public class GameCheckpoint {
    private int id, areaSize, sequence;
    private final String text;
    private final LatLng position;

    public GameCheckpoint(int id, String text, LatLng position, int areaSize, int sequence) {
        this.id = id;
        this.text = text;
        this.position = position;
        this.areaSize = areaSize;
        this.sequence = sequence;
    }

    public GameCheckpoint(int id, String text, LatLng position, int areaSize) {
        this.id = id;
        this.text = text;
        this.position = position;
        this.areaSize = areaSize;
    }


    public String getText() {
        return text;
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

    public int getSequence() {
        return sequence;
    }
}

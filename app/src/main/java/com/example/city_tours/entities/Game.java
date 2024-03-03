package com.example.city_tours.entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Game {
    private final int id;
    private final LatLng position;
    private final String description;
    private List<GameCheckpoint> checkpoints;
    private FinalCheckpoint finalCheckpoint;

    public Game(int id, LatLng position, String description) {
        this.id = id;
        this.position = position;
        this.description = description;
    }

    public int getId() {
        return id;
    }
    public LatLng getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public List<GameCheckpoint> getCheckpoints() {
        return checkpoints;
    }

    public FinalCheckpoint getFinalCheckpoint() {
        return finalCheckpoint;
    }

    public void setCheckpoints(List<GameCheckpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public void setFinalCheckpoint(FinalCheckpoint finalCheckpoint) {
        this.finalCheckpoint = finalCheckpoint;
    }
}

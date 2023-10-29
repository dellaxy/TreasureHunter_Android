package com.example.lovci_pokladov.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Level {
    private final int id, difficulty, sequenceNumber;
    private final LatLng position;
    private final String description;
    private List<LevelCheckpoint> checkpoints;

    public Level(int id, int difficulty, int sequenceNumber, LatLng position, String description) {
        this.id = id;
        this.difficulty = difficulty;
        this.sequenceNumber = sequenceNumber;
        this.position = position;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public List<LevelCheckpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<LevelCheckpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}

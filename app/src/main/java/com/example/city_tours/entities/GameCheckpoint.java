package com.example.city_tours.entities;

import com.example.city_tours.entities.puzzles.Puzzle;
import com.example.city_tours.objects.Utils;
import com.google.android.gms.maps.model.LatLng;

public class GameCheckpoint {
    private int id, areaSize, sequence;
    private final String text;
    private String navigationInstructions;
    private final LatLng position;
    private Puzzle puzzle;

    public GameCheckpoint(int id, String text, String navigationInstructions, LatLng position, int areaSize, int sequence) {
        this.id = id;
        this.text = text;
        this.position = position;
        this.areaSize = areaSize;
        this.sequence = sequence;
        this.navigationInstructions = navigationInstructions;
    }

    public GameCheckpoint(int id, String text, LatLng position, int areaSize) {
        this.id = id;
        this.text = text;
        this.position = position;
        this.areaSize = areaSize;
    }

    public boolean hasPuzzle() {
        return Utils.isNotNull(puzzle);
    }

    public String getNavigationInstructions() {
        return navigationInstructions;
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

    public boolean hasSequence() {
        return sequence > 0;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public String getPuzzleType() {
        return puzzle.getPuzzleType();
    }
}

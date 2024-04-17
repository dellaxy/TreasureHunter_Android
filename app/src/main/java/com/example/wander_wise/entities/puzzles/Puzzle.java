package com.example.wander_wise.entities.puzzles;

public abstract class Puzzle {
    private final String text;

    public Puzzle(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public abstract String getPuzzleType();

}

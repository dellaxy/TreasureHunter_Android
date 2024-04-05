package com.example.city_tours.entities.puzzles;

import static com.example.city_tours.entities.ConstantsCatalog.FETCH;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Fetch extends Puzzle {
    private final LatLng position;
    private final int area;
    private List<Item> items;

    public Fetch(LatLng position, int area, String text) {
        super(text);
        this.position = position;
        this.area = area;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public LatLng getPosition() {
        return position;
    }

    public int getArea() {
        return area;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public String getPuzzleType() {
        return FETCH;
    }
}

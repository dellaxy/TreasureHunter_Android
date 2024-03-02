package com.example.lovci_pokladov.entities;

import com.google.android.gms.maps.model.LatLng;

public class GameCheckpoint {
    private int id, areaSize;
    private final String text;
    private final LatLng position;
    private Item item;

    public GameCheckpoint(int id, String text, LatLng position, int areaSize, Item item) {
        this.id = id;
        this.text = text;
        this.position = position;
        this.areaSize = areaSize;
        this.item = item;
    }

    public void setItem(Item item) {
        this.item = item;
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

    public Item getItem() {
        return item;
    }
}

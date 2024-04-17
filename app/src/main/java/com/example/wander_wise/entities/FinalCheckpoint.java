package com.example.wander_wise.entities;

import com.google.android.gms.maps.model.LatLng;

public class FinalCheckpoint extends GameCheckpoint {
    private int coins;

    public FinalCheckpoint(int id, String text, LatLng position, int areaSize, int coins) {
        super(id, text, position, areaSize);
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }

}

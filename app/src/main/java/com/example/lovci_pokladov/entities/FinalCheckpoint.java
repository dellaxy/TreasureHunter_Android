package com.example.lovci_pokladov.entities;

import com.google.android.gms.maps.model.LatLng;

public class FinalCheckpoint extends GameCheckpoint {
    private int lockType;
    private int coins;

    public FinalCheckpoint(int id, String text, LatLng position, int areaSize, int keyFragments, int coins, Item reward) {
        super(id, text, position, areaSize, reward);
        this.lockType = keyFragments;
        this.coins = coins;
    }

    public Item getReward() {
        return super.getItem();
    }

    public int getKeyFragmentsAmount() {
        return lockType;
    }

    public int getCoins() {
        return coins;
    }

}

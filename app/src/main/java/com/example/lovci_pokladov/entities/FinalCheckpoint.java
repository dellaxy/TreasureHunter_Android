package com.example.lovci_pokladov.entities;

import com.google.android.gms.maps.model.LatLng;

public class FinalCheckpoint extends LevelCheckpoint {

    private String lockType;
    private String lockCode;
    private int coins;

    public FinalCheckpoint(int id, String text, LatLng position, int areaSize, String lockType, String lockCode, int coins, Item reward) {
        super(id, text, position, areaSize, reward);
        this.lockType = lockType;
        this.lockCode = lockCode;
        this.coins = coins;
    }

    public Item getReward() {
        return super.getItem();
    }

    public String getLockType() {
        return lockType;
    }

    public String getLockCode() {
        return lockCode;
    }

    public int getCoins() {
        return coins;
    }

}

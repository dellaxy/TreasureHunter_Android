package com.example.lovci_pokladov.entities;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class DangerZone {

    protected LatLng position;
    protected int radius;
    protected String message;

    public DangerZone(LatLng position, int radius, String message) {
        this.position = position;
        this.radius = radius;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public LatLng getPosition() {
        return position;
    }

    public boolean isCloseToDangerzone(LatLng position) {
        float[] distance = new float[1];
        Location.distanceBetween(this.position.latitude, this.position.longitude, position.latitude, position.longitude, distance);
        return distance[0] <= radius + 6;
    }

    public boolean isInsideDangerzone(LatLng position) {
        float[] distance = new float[1];
        Location.distanceBetween(this.position.latitude, this.position.longitude, position.latitude, position.longitude, distance);
        return distance[0] <= radius;
    }
}

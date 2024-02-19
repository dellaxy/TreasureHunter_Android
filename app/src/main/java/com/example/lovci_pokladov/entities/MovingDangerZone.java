package com.example.lovci_pokladov.entities;

import com.google.android.gms.maps.model.LatLng;

public class MovingDangerZone extends DangerZone {

    private LatLng endPosition;
    private float t = 0.0f;
    private float direction = 0.01f;

    public MovingDangerZone(LatLng startPosition, LatLng endPosition, int radius, String message) {
        super(startPosition, radius, message);
        this.endPosition = endPosition;
    }


    public void move() {
        t += direction;
        if (t > 1.0f || t < 0.0f) {
            direction *= -1;
            t += direction;
        }

        double latitude = (1 - t) * position.latitude + t * endPosition.latitude;
        double longitude = (1 - t) * position.longitude + t * endPosition.longitude;
        this.position = new LatLng(latitude, longitude);
    }
}

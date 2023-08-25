package com.example.lovci_pokladov.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

public class Country {
    LatLng center;
    PolygonOptions countryArea;

    public Country(LatLng center, PolygonOptions countryArea) {
        this.center = center;
        this.countryArea = countryArea;
    }

    public LatLng getCenter() {
        return center;
    }
}

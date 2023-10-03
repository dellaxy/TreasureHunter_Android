package com.example.lovci_pokladov.models;

import com.google.android.gms.maps.model.PolygonOptions;

public class Region {
    int id;
    String name;
    PolygonOptions regionArea;

    public Region(int id, String name, PolygonOptions regionArea) {
        this.id = id;
        this.name = name;
        this.regionArea = regionArea;
    }

    public Region(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolygonOptions getRegionArea() {
        return regionArea;
    }

    public void setRegionArea(PolygonOptions regionArea) {
        this.regionArea = regionArea;
    }
}

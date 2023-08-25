package com.example.lovci_pokladov.entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

package com.example.lovci_pokladov.entities;

import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class Item {
    private int id;
    private String imagePath;
    private String description;

    public Item(int id, String imagePath, String description) {
        this.id = id;
        this.imagePath = imagePath;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getImage() {
        try {
            InputStream ims = getClass().getResourceAsStream("/res/drawable/" + imagePath);
            return Drawable.createFromStream(ims, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

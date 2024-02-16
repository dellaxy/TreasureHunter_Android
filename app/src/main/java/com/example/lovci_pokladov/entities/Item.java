package com.example.lovci_pokladov.entities;

import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class Item {
    private int id;
    private String iconName, description, name;

    public Item(int id, String iconName, String description, String name) {
        this.id = id;
        this.iconName = iconName;
        this.description = description;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Drawable getImage() {
        try {
            InputStream ims = getClass().getResourceAsStream("/res/drawable/" + iconName);
            return Drawable.createFromStream(ims, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isKeyFragment() {
        return iconName.equals("fragment.png");
    }
}

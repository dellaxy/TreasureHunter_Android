package com.example.city_tours.entities.puzzles;

import static com.example.city_tours.objects.Utils.isNotNull;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.city_tours.entities.ResourceManager;
import com.google.android.gms.maps.model.LatLng;

public class Item {
    private final LatLng position;
    private int areaSize;
    private final String text, itemName, imageName;
    private final boolean correctItem;

    public Item(LatLng position, String text, String itemName, String imageName, boolean correctItem) {
        this.position = position;
        this.text = text;
        this.itemName = itemName;
        this.imageName = imageName;
        this.correctItem = correctItem;

        if (isNotNull(itemName)) {
            this.areaSize = 3;
        } else {
            this.areaSize = 6;
        }
    }

    public LatLng getPosition() {
        return position;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public String getText() {
        return text;
    }

    public String getItemName() {
        return itemName;
    }

    public Drawable getImage() {
        Context context = ResourceManager.getContext();
        int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return context.getResources().getDrawable(resourceId, null);
    }

    public boolean isCorrectItem() {
        return correctItem;
    }
}

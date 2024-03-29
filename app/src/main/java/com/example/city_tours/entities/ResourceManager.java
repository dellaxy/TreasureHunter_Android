package com.example.city_tours.entities;

import android.content.Context;

public class ResourceManager {
    private static Context applicationContext;

    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static String getString(int resourceId) {

        return applicationContext.getString(resourceId);
    }
}
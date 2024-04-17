package com.example.wander_wise.entities;

import android.content.Context;

public class ResourceManager {
    private static Context applicationContext;

    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static void updateContext(Context newContext) {
        applicationContext = newContext.getApplicationContext();
    }

    public static Context getContext() {
        return applicationContext;
    }

    public static String getString(int resourceId) {

        return applicationContext.getString(resourceId);
    }
}
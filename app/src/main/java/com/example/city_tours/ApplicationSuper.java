package com.example.city_tours;

import android.app.Application;

import com.example.city_tours.entities.ResourceManager;

public class ApplicationSuper extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ResourceManager.init(this);
    }

}

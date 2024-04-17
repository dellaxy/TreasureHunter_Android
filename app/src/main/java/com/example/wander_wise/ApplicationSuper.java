package com.example.wander_wise;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.wander_wise.entities.ResourceManager;

import java.util.Locale;

public class ApplicationSuper extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ResourceManager.init(this);
    }

    public void setLocale(Locale newLocale) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        ResourceManager.updateContext(this);

    }
}

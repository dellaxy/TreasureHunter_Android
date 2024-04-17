package com.example.wander_wise.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wander_wise.ApplicationSuper;
import com.example.wander_wise.services.PreferencesManager;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguage();
    }

    protected void updateLanguage() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance(this);
        String language = preferencesManager.getLanguageKey();

        Locale locale;
        if (!language.isEmpty()) {
            locale = new Locale(language);
        } else {
            locale = Locale.getDefault();
        }

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        if (getApplication() instanceof ApplicationSuper) {
            ((ApplicationSuper) getApplication()).setLocale(locale);
        }
    }
}
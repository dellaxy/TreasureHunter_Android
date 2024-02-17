package com.example.lovci_pokladov.services;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PLAYER_PREFERENCES = "PROFILE_PREFERENCES";
    private static PreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private Observable<Integer> playerCoinsObservable = new Observable<>();


    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PLAYER_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }


    // REGION AND MAP PREFERENCES
    public void setSelectedRegion(int regionId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedRegionId", regionId);
        editor.apply();
    }

    public int getSelectedRegion(int defaultRegionId) {
        return sharedPreferences.getInt("selectedRegionId", defaultRegionId);
    }

    // TUTORIAL PREFERENCES
    public void setTutorialSeen() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isTutorialSeen", true);
        editor.apply();
    }

    public boolean isTutorialSeen() {
        return sharedPreferences.getBoolean("isTutorialSeen", false);
    }

    // PLAYER PREFERENCES
    public void setPlayerName(String playerName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("playerName", playerName);
        editor.apply();
    }

    public String getPlayerName() {
        return sharedPreferences.getString("playerName", "Player");
    }

    public void setTTSVoice(String voice) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ttsVoice", voice);
        editor.apply();
    }

    public String getTTSVoice() {
        return sharedPreferences.getString("ttsVoice", "en-US-Standard-B");
    }

    public Observable<Integer> getPlayerCoinsObservable() {
        return playerCoinsObservable;
    }

    public void setPlayerCoins(int amount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("playerCoins", amount);
        editor.apply();
        playerCoinsObservable.setValue(amount);
    }

    public int getPlayerCoins() {
        return sharedPreferences.getInt("playerCoins", 0);
    }


}

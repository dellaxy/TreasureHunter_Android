package com.example.lovci_pokladov.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.lovci_pokladov.R;

public class LocationPopup extends LinearLayout {

    String missionTitle, missionDescription, missionLocation, missionRegion;
    int missionDifficulty;

    public LocationPopup(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.location_popup, this, true);
    }

    public void setData(String missionTitle, String missionDescription, String missionLocation, String missionRegion, int missionDifficulty) {;
        this.missionTitle = missionTitle;
        this.missionDescription = missionDescription;
        this.missionLocation = missionLocation;
        this.missionRegion = missionRegion;
        this.missionDifficulty = missionDifficulty;
    }
}

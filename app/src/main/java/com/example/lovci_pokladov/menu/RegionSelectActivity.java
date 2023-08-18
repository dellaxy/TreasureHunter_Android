package com.example.lovci_pokladov.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.objects.GeoJSONLoader;

import java.util.List;
import java.util.Map;

public class RegionSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_select);

        GridLayout gridLayout = findViewById(R.id.regionsGridLayout);

        GeoJSONLoader geoJSONLoader = new GeoJSONLoader(this);
        List<Map<Integer, String>> regions = geoJSONLoader.getRegions();
        for (Map<Integer, String> region : regions) {
            View regionCell = LayoutInflater.from(this).inflate(R.layout.region_cell, gridLayout, false);
            ImageView regionImage = regionCell.findViewById(R.id.regionImage);
            TextView regionName = regionCell.findViewById(R.id.regionName);

            String regionNameString = region.values().iterator().next();
            regionName.setText(regionNameString);

            gridLayout.addView(regionCell);
        }
    }
}

package com.example.lovci_pokladov.menu;

import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.entities.Region;
import com.example.lovci_pokladov.objects.GeoJSONLoader;

import java.util.List;

public class RegionSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_select);

        GridView regionsGrid = findViewById(R.id.regionsGrid);

        GeoJSONLoader geoJSONLoader = new GeoJSONLoader(this);
        List<Region> regionsList = geoJSONLoader.getRegions();

        RegionAdapter regionAdapter = new RegionAdapter(this, regionsList);
        regionsGrid.setAdapter(regionAdapter);

    }
}

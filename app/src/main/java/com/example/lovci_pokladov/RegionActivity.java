package com.example.lovci_pokladov;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lovci_pokladov.objects.ConstantsCatalog.ColorPalette;
import com.example.lovci_pokladov.entities.Region;
import com.example.lovci_pokladov.objects.GeoJSONLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private GeoJSONLoader geoJSONLoader;
    private TextView regionNameTextView;
    private Map<Integer, Polygon> polygonMap;
    private int selectedRegionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoJSONLoader = new GeoJSONLoader(this);
        polygonMap = new HashMap<>();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_clean);
        mMap.setMapStyle(style);
        displayAllRegionsOnMap();

    }

    private void displayAllRegionsOnMap() {
        List<Region> regions = geoJSONLoader.getRegions();
        if (regions != null) {
            for (Region region : regions) {
                Bundle regionInfo = new Bundle();
                regionInfo.putInt("regionId", region.getId());
                regionInfo.putString("regionName", region.getName());

                PolygonOptions regionBoundary = geoJSONLoader.getRegionPolygon(region.getId());
                regionBoundary.fillColor(ColorPalette.PRIMARY.getColor(128));
                regionBoundary.strokeColor(ColorPalette.PRIMARY.getColor(255));
                regionBoundary.strokeWidth(8);

                Polygon regionPolygon = mMap.addPolygon(regionBoundary);
                regionPolygon.setClickable(true);
                regionPolygon.setTag(regionInfo);

                polygonMap.put(region.getId(), regionPolygon);

                mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(@NonNull Polygon clickedPolygon) {
                        setSelectedRegion(clickedPolygon);
                    }
                });
            }
        }
    }

    private void setSelectedRegion(Polygon selectedPolygon){
        Bundle regionInfo = (Bundle) selectedPolygon.getTag();
        if(selectedRegionId == -1) {
            changePolygonColor(selectedPolygon, ColorPalette.PRIMARY.getColor(220));
        } else if (selectedRegionId == regionInfo.getInt("regionId")) {
            changePolygonColor(selectedPolygon, ColorPalette.PRIMARY.getColor(128));
            selectedRegionId = -1;
            return;
        } else {
            Polygon previousSelectedPolygon = polygonMap.get(selectedRegionId);
            changePolygonColor(previousSelectedPolygon, ColorPalette.PRIMARY.getColor(128));
            changePolygonColor(selectedPolygon, ColorPalette.PRIMARY.getColor(220));
        }
        selectedRegionId = regionInfo.getInt("regionId");
    }

    private void changePolygonColor(Polygon polygon, int color) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), polygon.getFillColor(), color);
        colorAnimator.setDuration(300);

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();
                polygon.setFillColor(animatedColor);
                polygon.setStrokeColor(Color.argb(255, Color.red(animatedColor), Color.green(animatedColor), Color.blue(animatedColor)));
            }
        });

        colorAnimator.start();
    }

}


package com.example.lovci_pokladov.activities;

import static com.example.lovci_pokladov.models.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.lovci_pokladov.objects.Utils.isNotNull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.models.Level;
import com.example.lovci_pokladov.models.LocationMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;
import com.example.lovci_pokladov.services.TextToSpeechService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextToSpeechService textToSpeechService;
    private LocationMarker marker;
    private boolean isInsideArea = false;
    private LatLng markerLocation, areaCenter, levelStartLocation;
    private int AREA_RADIUS, MARKER_TOLERANCE =5;
    private Level currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //checkGpsStatus();
        getMarkerData();

        textToSpeechService = new TextToSpeechService();
        //textToSpeechService.synthesizeText("The objective is to locate a treasure near Nitra Castle. The task is relatively easy, and there should be no guards protecting the treasure. Good luck!");
    }

    private void getMarkerData(){
        int id = getIntent().getIntExtra("markerId", 0);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        marker = (id > 0) ? databaseHelper.getMarkerById(id) : null;
        // IF MARKER IS NOT IN PROGRESS -> USE FIRST LEVEL
        // OTHERWISE USE MARKER/PROGRESS (LEVEL SEQUENCE) -> dbHelper.getLevelBySequence()
        currentLevel = databaseHelper.getLevelBySequence(id, 1);
        //currentLevel.setCheckpoints(databaseHelper.getCheckpointsForLevel(currentLevel.getId()));
    }

    private void checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(gpsIntent, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGpsEnabled) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                getMarkerData();
            } else {
                finish();
            }
        }
    }

    private void generateArea() {
        AREA_RADIUS = new Random().nextInt(50 - 25) + 25;
        int maxDistanceFromMarker = AREA_RADIUS - 10;
        int distanceFromMarker = new Random().nextInt(maxDistanceFromMarker);
        double randomAngle = new Random().nextDouble() * 2 * Math.PI;

        double newLat = markerLocation.latitude + distanceFromMarker / 111000.0 * Math.cos(randomAngle);
        double newLng = markerLocation.longitude + distanceFromMarker / (111000.0 * Math.cos(Math.toRadians(markerLocation.latitude))) * Math.sin(randomAngle);

        areaCenter = new LatLng(newLat, newLng);
        mMap.addCircle(new CircleOptions()
                .center(areaCenter)
                .radius(AREA_RADIUS)
                .strokeWidth(5)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(100, 0, 0, 255)));

        mMap.addMarker(new MarkerOptions()
                .position(markerLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 16f));
    }



    @Override
    public void onLocationChanged(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        float distance = calculateDistance(currentLatitude, currentLongitude, areaCenter.latitude, areaCenter.longitude);
        if(distance < MARKER_TOLERANCE && isInsideArea){
            Toast.makeText(this, "You found the treasure!", Toast.LENGTH_SHORT).show();
            isInsideArea = false;
            finish();
        }
        if (distance <= AREA_RADIUS && !isInsideArea) {
            Toast.makeText(this, "You entered the target area!", Toast.LENGTH_SHORT).show();
            isInsideArea = true;
        } else if (distance > AREA_RADIUS && isInsideArea) {
            Toast.makeText(this, "You left the target area!", Toast.LENGTH_SHORT).show();
            isInsideArea = false;
        }
    }

    private float calculateDistance(double playerLat, double playerLon, double finishLat, double finishLon) {
        float[] result = new float[1];
        Location.distanceBetween(playerLat, playerLon, finishLat, finishLon, result);
        return result[0];
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getLastKnownLocation();

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            generateArea();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (isNotNull(location)) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isNotNull(textToSpeechService)) {
            textToSpeechService.cancel();
        }
    }

}


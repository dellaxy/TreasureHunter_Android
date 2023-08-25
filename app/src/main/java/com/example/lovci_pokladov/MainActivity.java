package com.example.lovci_pokladov;

import static com.example.lovci_pokladov.objects.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lovci_pokladov.entities.LocationMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationMarker marker;
    private boolean isInsideArea = false;
    private LatLng markerLocation, areaCenter;
    private int areaRadius, markerTolerance=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getMarkerData();
    }

    private void getMarkerData(){
        int id = getIntent().getIntExtra("markerId", 0);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        marker = (id > 0) ? databaseHelper.getMarkerById(id) : null;
        markerLocation = marker.getPosition();
    }

    private void generateArea() {
        areaRadius = new Random().nextInt(50 - 25) + 25;
        int maxDistanceFromMarker = areaRadius - 10;
        int distanceFromMarker = new Random().nextInt(maxDistanceFromMarker);
        double randomAngle = new Random().nextDouble() * 2 * Math.PI;

        double newLat = markerLocation.latitude + distanceFromMarker / 111000.0 * Math.cos(randomAngle);
        double newLng = markerLocation.longitude + distanceFromMarker / (111000.0 * Math.cos(Math.toRadians(markerLocation.latitude))) * Math.sin(randomAngle);

        areaCenter = new LatLng(newLat, newLng);
        mMap.addCircle(new CircleOptions()
                .center(areaCenter)
                .radius(areaRadius)
                .strokeWidth(5)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(100, 0, 0, 255)));

        mMap.addMarker(new MarkerOptions()
                .position(markerLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 16f));
    }



    @Override
    public void onLocationChanged(android.location.Location location) {
        double currentLatitude = location.getLatitude(),
                currentLongitude = location.getLongitude();

        float distanceFromArea = calculateDistance(currentLatitude, currentLongitude, areaCenter.latitude, areaCenter.longitude);
        float distanceFromMarker = calculateDistance(currentLatitude, currentLongitude, markerLocation.latitude, markerLocation.longitude);
        if (distanceFromMarker <= markerTolerance && isInsideArea){
            Toast.makeText(this, "You found the treasure!", Toast.LENGTH_SHORT).show();
            isInsideArea = false;
            finish();
        }
        if (distanceFromArea <= areaRadius && !isInsideArea) {
            Toast.makeText(this, "You entered the target area!", Toast.LENGTH_SHORT).show();
            isInsideArea = true;
        } else if (distanceFromArea > areaRadius && isInsideArea) {
            Toast.makeText(this, "You left the target area!", Toast.LENGTH_SHORT).show();
            isInsideArea = false;
        }
    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, result);
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
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                getLastKnownLocation();
            }
        }
    }
}

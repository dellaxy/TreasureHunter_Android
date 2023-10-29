package com.example.lovci_pokladov.activities;

import static com.example.lovci_pokladov.activities.GameActivity.LevelState.LEVEL_NOT_STARTED;
import static com.example.lovci_pokladov.models.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.lovci_pokladov.objects.Utils.isNotNull;
import static com.example.lovci_pokladov.objects.Utils.isNull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.models.ConstantsCatalog.ColorPalette;
import com.example.lovci_pokladov.models.Level;
import com.example.lovci_pokladov.objects.DatabaseHelper;
import com.example.lovci_pokladov.services.Observable;
import com.example.lovci_pokladov.services.TextToSpeechService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextToSpeechService textToSpeechService;
    private LatLng levelStartLocation, playerLocation;
    private int AREA_RADIUS;
    private Level currentLevel;
    private Observable<LevelState> currentLevelState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gameActivityMap);
        mapFragment.getMapAsync(this);

        initMarkerData();
    }

    private void onInit() {
        currentLevelState = new Observable<>();
        currentLevelState.onChangeListener(levelState -> {
                switch ((LevelState) levelState) {
                    case LEVEL_NOT_STARTED: {
                        mMap.addCircle(new CircleOptions()
                                .center(levelStartLocation)
                                .radius(AREA_RADIUS)
                                .strokeWidth(5)
                                .strokeColor(ColorPalette.SECONDARY.getColor())
                                .fillColor(ColorPalette.SECONDARY.getColor(200)));
                        break;
                    }
                    case LEVEL_STARTED: {
                        Log.d("LEVEL_STATE", "LEVEL_STARTED");
                        break;
                    }
                    case LEVEL_COMPLETED: {
                        Log.d("LEVEL_STATE", "LEVEL_COMPLETED");
                        break;
                    }
                }
            }
        );

        currentLevelState.setValue(LEVEL_NOT_STARTED);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        textToSpeechService = new TextToSpeechService();
        //textToSpeechService.synthesizeText("The objective is to locate a treasure near Nitra Castle. The task is relatively easy, and there should be no guards protecting the treasure. Good luck!");
    }

    private void initMarkerData() {
        int id = getIntent().getIntExtra("markerId", 0);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        //LocationMarker marker = (id > 0) ? databaseHelper.getMarkerById(id) : null;
        int markerProgressStage = databaseHelper.getMarkerProgress(id);
        currentLevel = databaseHelper.getLevelBySequence(id, markerProgressStage);
        if (isNull(currentLevel)) {
            //create an error handler class that when something from the DB is not found it will check if the DB update is needed
            Toast.makeText(this, "No level found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        levelStartLocation = currentLevel.getPosition();
        AREA_RADIUS = 6;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*private void generateArea() {
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
    }*/

    @Override
    public void onLocationChanged(Location location) {
        playerLocation = new LatLng(location.getLatitude(), location.getLongitude());
        switch (currentLevelState.getValue()) {
            case LEVEL_NOT_STARTED: {
                boolean isInsideArea = calculateDistance(playerLocation, levelStartLocation) < AREA_RADIUS;
                if (calculateDistance(playerLocation, levelStartLocation) < AREA_RADIUS && !isInsideArea) {
                    //open modal
                } else if (calculateDistance(playerLocation, levelStartLocation) > AREA_RADIUS && isInsideArea) {
                    //close modal
                }
                break;
            }
            case LEVEL_STARTED: {

                break;
            }

            case LEVEL_COMPLETED: {

                break;
            }
        }
    }

    private float calculateDistance(LatLng playerLocation, LatLng finishLocation) {
        if (playerLocation != null && finishLocation != null) {
            float[] result = new float[1];
            Location.distanceBetween(playerLocation.latitude, playerLocation.longitude, finishLocation.latitude, finishLocation.longitude, result);
            return result[0];
        } else {
            return Float.MAX_VALUE;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onInit();

            mMap.setMyLocationEnabled(true);
            getLastKnownLocation();
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isNotNull(textToSpeechService)) {
            textToSpeechService.cancel();
        }
    }

    public enum LevelState {
        LEVEL_NOT_STARTED,
        LEVEL_STARTED,
        LEVEL_COMPLETED
    }
}


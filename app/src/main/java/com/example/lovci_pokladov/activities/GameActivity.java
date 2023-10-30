package com.example.lovci_pokladov.activities;

import static com.example.lovci_pokladov.activities.GameActivity.LevelState.LEVEL_STARTED;
import static com.example.lovci_pokladov.models.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.lovci_pokladov.objects.Utils.isNotNull;
import static com.example.lovci_pokladov.objects.Utils.isNull;

import android.Manifest;
import android.content.Context;
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
import com.example.lovci_pokladov.models.LevelCheckpoint;
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
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextToSpeechService textToSpeechService;
    private LatLng levelStartLocation;
    private int AREA_RADIUS;
    private Level currentLevel;
    private Observable<LevelState> currentLevelState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gameActivityMap);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

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
                        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
                            currentLevel.setCheckpoints(databaseHelper.getCheckpointsForLevel(currentLevel.getId()));
                            generatePlayArea();
                            startGame();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case LEVEL_COMPLETED: {
                        Log.d("LEVEL_STATE", "LEVEL_COMPLETED");
                        break;
                    }
                }
            }
        );

        currentLevelState.setValue(LEVEL_STARTED);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        textToSpeechService = new TextToSpeechService();
        //textToSpeechService.synthesizeText("The objective is to locate a treasure near Nitra Castle. The task is relatively easy, and there should be no guards protecting the treasure. Good luck!");
    }

    private void initMarkerData() {
        int id = getIntent().getIntExtra("markerId", 0);
        //LocationMarker marker = (id > 0) ? databaseHelper.getMarkerById(id) : null;
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            int markerProgressStage = databaseHelper.getMarkerProgress(id);
            currentLevel = databaseHelper.getLevelBySequence(id, markerProgressStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isNull(currentLevel)) {
            //create an error handler class that when something from the DB is not found it will check if the DB update is needed
            Toast.makeText(this, "No level found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        levelStartLocation = currentLevel.getPosition();
        AREA_RADIUS = 6;
    }

    private void startGame() {

    }

    private void generatePlayArea() {
        List<LatLng> playAreaVertices = calculatePlayAreaVerticesWithRadii();

        if (playAreaVertices.size() >= 3) {
            // Create a polygon on the map to represent the play area
            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(playAreaVertices)
                    .strokeWidth(5)
                    .strokeColor(ColorPalette.PRIMARY.getColor()) // Customize the color as needed
                    .fillColor(ColorPalette.PRIMARY.getColor(100)); // Customize the fill color as needed

            mMap.addPolygon(polygonOptions);
        }
    }

    private List<LatLng> calculatePlayAreaVerticesWithRadii() {
        List<LatLng> checkpointPositions = new ArrayList<>();

        // Collect the positions of checkpoints
        for (LevelCheckpoint checkpoint : currentLevel.getCheckpoints()) {
            checkpointPositions.add(checkpoint.getPosition());
        }

        // Calculate the convex hull vertices using the Jarvis March algorithm
        List<LatLng> hull = jarvisMarch(checkpointPositions);

        // Expand the vertices based on checkpoint radii
        List<LatLng> expandedVertices = new ArrayList<>();

        for (LatLng vertex : hull) {
            // Expand the vertex based on checkpoint radii
            double expandDistance = getMaxCheckpointRadius();
            LatLng expandedVertex = new LatLng(
                    vertex.latitude + expandDistance,
                    vertex.longitude + expandDistance
            );
            expandedVertices.add(expandedVertex);
        }

        return expandedVertices;
    }

    private double getMaxCheckpointRadius() {
        double maxRadius = 0;

        for (LevelCheckpoint checkpoint : currentLevel.getCheckpoints()) {
            double radius = checkpoint.getAreaSize();
            if (radius > maxRadius) {
                maxRadius = radius;
            }
        }

        return maxRadius;
    }

    // Jarvis March algorithm for finding the convex hull of a set of points
    private List<LatLng> jarvisMarch(List<LatLng> points) {
        List<LatLng> hull = new ArrayList<>();

        if (points.size() < 3) {
            // Convex hull is not possible with less than 3 points
            return hull;
        }

        // Find the point with the lowest latitude (and leftmost if tied)
        LatLng startPoint = points.get(0);
        for (LatLng point : points) {
            if (point.latitude < startPoint.latitude || (point.latitude == startPoint.latitude && point.longitude < startPoint.longitude)) {
                startPoint = point;
            }
        }

        LatLng currentPoint = startPoint;
        do {
            hull.add(currentPoint);
            LatLng nextPoint = points.get(0);

            for (LatLng point : points) {
                if (nextPoint.equals(currentPoint) || isLeftOf(currentPoint, nextPoint, point)) {
                    nextPoint = point;
                }
            }

            currentPoint = nextPoint;
        } while (!currentPoint.equals(startPoint));

        return hull;
    }

    // Helper function to check if a point is to the left of a line formed by two other points
    private boolean isLeftOf(LatLng a, LatLng b, LatLng c) {
        return (b.longitude - a.longitude) * (c.latitude - a.latitude) -
                (b.latitude - a.latitude) * (c.longitude - a.longitude) > 0;
    }



    @Override
    public void onLocationChanged(Location location) {
        LatLng playerLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
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


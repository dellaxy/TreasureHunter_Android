package com.example.city_tours.activities;

import static com.example.city_tours.activities.GameActivity.GameState.GAME_COMPLETED;
import static com.example.city_tours.activities.GameActivity.GameState.GAME_NOT_STARTED;
import static com.example.city_tours.activities.GameActivity.GameState.GAME_STARTED;
import static com.example.city_tours.entities.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.city_tours.objects.Utils.bitmapDescriptorFromVector;
import static com.example.city_tours.objects.Utils.isNotNull;
import static com.example.city_tours.objects.Utils.isNull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.city_tours.R;
import com.example.city_tours.components.CheckpointTextCard;
import com.example.city_tours.components.QuestModal;
import com.example.city_tours.components.RegularModal;
import com.example.city_tours.entities.ConstantsCatalog.ColorPalette;
import com.example.city_tours.entities.FinalCheckpoint;
import com.example.city_tours.entities.Game;
import com.example.city_tours.entities.GameCheckpoint;
import com.example.city_tours.entities.Quest;
import com.example.city_tours.objects.DatabaseHelper;
import com.example.city_tours.services.Observable;
import com.example.city_tours.services.PreferencesManager;
import com.example.city_tours.services.TextToSpeechService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mainMap, miniMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RegularModal gameStartModal;
    private TextToSpeechService textToSpeechService;
    private LatLng startLocation;
    private int AREA_RADIUS, markerId, correctAnswerCount = 0;
    private boolean isInsideArea = false, navigateToLocation;
    private Game currentGame;
    private List<GameCheckpoint> undiscoveredCheckpoints;
    private Observable<GameState> currentGameState;
    private SupportMapFragment mainMapFragment, miniMapFragment;
    private FinalCheckpoint finalCheckpoint;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializeMainMap();

        initMarkerData();
    }

    private void onInit() {
        currentGameState = new Observable<>();
        preferencesManager = PreferencesManager.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        RelativeLayout activeGameLayout = findViewById(R.id.activeGameLayout);
        RelativeLayout completedGameLayout = findViewById(R.id.completedGameLayout);

        currentGameState.onChangeListener(gameState -> {
                    switch ((GameState) gameState) {
                        case GAME_NOT_STARTED: {
                            mainMap.addCircle(new CircleOptions()
                                    .center(startLocation)
                                    .radius(AREA_RADIUS)
                                    .strokeWidth(5)
                                    .strokeColor(ColorPalette.SECONDARY.getColor())
                                    .fillColor(ColorPalette.SECONDARY.getColor(200)));
                            mainMap.addMarker(new MarkerOptions()
                                    .position(startLocation)
                                    .icon(bitmapDescriptorFromVector(this, ColorPalette.SECONDARY.getColor(), R.drawable.marker_default)));
                            break;
                        }
                        case GAME_STARTED: {
                            try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
                                currentGame.setCheckpoints(databaseHelper.getGameCheckpoints(currentGame.getId()));
                                currentGame.setFinalCheckpoint(databaseHelper.getFinalGameCheckpoint(currentGame.getId()));

                                finalCheckpoint = currentGame.getFinalCheckpoint();
                                undiscoveredCheckpoints = currentGame.getCheckpoints();
                                Collections.sort(undiscoveredCheckpoints, Comparator.comparingInt(GameCheckpoint::getSequence));

                                Quest quest = new Quest("Vieš v ktorom roku sa narodil Anton Bernolák? Porozhliadni sa okolo kostola, tam nájdeš všetko čo potrebuješ vedieť.", "1796", "Choď ešte raz k najbližšiemu kostolu a na jeho ľavej stene stojí busta tohto významného slovenského jazykovedca.", "Správne!");
                                undiscoveredCheckpoints.get(0).setQuest(quest);

                                undiscoveredCheckpoints.add(currentGame.getFinalCheckpoint());

                                mainMapFragment.getView().setVisibility(View.GONE);
                                activeGameLayout.setVisibility(View.VISIBLE);
                                initializeMiniMap();
                                startGame();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case GAME_COMPLETED: {
                            try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
                                databaseHelper.updateFinished(markerId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            activeGameLayout.setVisibility(View.GONE);
                            completedGameLayout.setVisibility(View.VISIBLE);
                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            locationManager.removeUpdates(this);
                            clearGameLayout();

                            preferencesManager.setPlayerCoins(preferencesManager.getPlayerCoins() + finalCheckpoint.getCoins() + correctAnswerCount * 100);

                            finish();
                            break;
                        }
                    }
                }
        );
        currentGameState.setValue(GAME_NOT_STARTED);

        gameStartModal = new RegularModal(this) {
            @Override
            public void acceptButtonClicked() {
                currentGameState.setValue(GAME_STARTED);
                closePopup();
            }
        };
        gameStartModal.setModalText("By tapping the button below, you will start your new tour. Good luck!");
    }

    private void initializeMiniMap() {
        mainMap.clear();
        mainMap = null;
        miniMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.miniMapFragment);
        Objects.requireNonNull(miniMapFragment).getMapAsync(googleMap -> {
            miniMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                miniMap.setMyLocationEnabled(true);
                getLastKnownLocation(miniMap);
                addCheckpointsToMiniMap();
            }
        });
    }

    private void addCheckpointsToMiniMap() {
        if (undiscoveredCheckpoints != null) {
            for (GameCheckpoint checkpoint : undiscoveredCheckpoints) {
                miniMap.addCircle(new CircleOptions()
                        .center(checkpoint.getPosition())
                        .radius(checkpoint.getAreaSize())
                        .strokeWidth(5)
                        .strokeColor(ColorPalette.PRIMARY.getColor())
                        .fillColor(ColorPalette.PRIMARY.getColor(100)));
            }
        }
    }


    private void initializeMainMap() {
        mainMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gameActivityMap);
        Objects.requireNonNull(mainMapFragment).getMapAsync(googleMap -> {
            mainMap = googleMap;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                onInit();

                mainMap.setMyLocationEnabled(true);
                getLastKnownLocation(mainMap);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0.5f, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
    }

    private void initMarkerData() {
        markerId = getIntent().getIntExtra("markerId", 0);
        navigateToLocation = getIntent().getBooleanExtra("navigateToStart", false);
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            currentGame = databaseHelper.getGame(markerId);
            if (isNull(currentGame)) {
                Toast.makeText(this, "No game found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String language = currentGame.getLanguage();
            String voice = currentGame.getVoice();
            if (isNotNull(language) && isNotNull(voice)) {
                textToSpeechService = new TextToSpeechService(this, language, voice);
            } else {
                textToSpeechService = new TextToSpeechService(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startLocation = currentGame.getPosition();
        AREA_RADIUS = 6;
    }

    private void navigateToLocation(LatLng destination, LatLng currentLocation) {
        Uri googleMapsUri = Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation.latitude + "," + currentLocation.longitude + "&daddr=" + destination.latitude + "," + destination.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void startGame() {
        textToSpeechService.synthesizeText(currentGame.getDescription());
        textToSpeechService.postTaskToMainThread(() -> {
            addCheckpointToUi(currentGame.getDescription());
        });
    }

    private void clearGameLayout() {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointInfo);
        checkpointLayout.removeAllViews();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng playerLocation = new LatLng(location.getLatitude(), location.getLongitude());
        switch (currentGameState.getValue()) {
            case GAME_NOT_STARTED: {
                if (isPlayerInsideArea(playerLocation, startLocation, AREA_RADIUS) && !isInsideArea) {
                    isInsideArea = true;
                    gameStartModal.openPopup();
                } else if (!isPlayerInsideArea(playerLocation, startLocation, AREA_RADIUS) && isInsideArea) {
                    isInsideArea = false;
                    gameStartModal.closePopup();
                }
                break;
            }
            case GAME_STARTED: {
                miniMap.animateCamera(CameraUpdateFactory.newLatLngZoom(playerLocation, 20f));
                if (isNotNull(undiscoveredCheckpoints) && !undiscoveredCheckpoints.isEmpty()) {
                    Iterator<GameCheckpoint> iterator = undiscoveredCheckpoints.iterator();
                    while (iterator.hasNext()) {
                        GameCheckpoint checkpoint = iterator.next();
                        if (isPlayerInsideArea(playerLocation, checkpoint.getPosition(), checkpoint.getAreaSize())) {
                            if (checkpoint.hasQuest()) {
                                QuestModal questModal = new QuestModal(this, checkpoint.getQuest()) {
                                    @Override
                                    public void correctAnswerEntered() {
                                        correctAnswerCount++;
                                        textToSpeechService.synthesizeText(checkpoint.getQuest().getText());
                                        textToSpeechService.postTaskToMainThread(() -> {
                                            addCheckpointToUi(checkpoint.getQuest().getText());
                                        });
                                    }
                                };
                                questModal.openPopup();
                            }
                            if (checkpoint.getClass() == FinalCheckpoint.class) {
                                finalCheckpointFound();
                            } else {
                                textToSpeechService.synthesizeText(checkpoint.getText());
                                textToSpeechService.postTaskToMainThread(() -> {
                                    addCheckpointToUi(checkpoint.getText());
                                });
                                iterator.remove();
                                miniMap.clear();
                                addCheckpointsToMiniMap();
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    private boolean isPlayerInsideArea(LatLng playerLocation, LatLng checkedLocation, int locationRadius) {
        float[] distance = new float[1];
        Location.distanceBetween(playerLocation.latitude, playerLocation.longitude, checkedLocation.latitude, checkedLocation.longitude, distance);
        return distance[0] < locationRadius;
    }

    private void addCheckpointToUi(String checkpointText) {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointInfo);

        if (checkpointLayout != null) {
            checkpointLayout.removeAllViews();
            CheckpointTextCard checkpointCard = new CheckpointTextCard(this, checkpointText);
            checkpointLayout.addView(checkpointCard);
        }
    }

    private void finalCheckpointFound() {
        currentGameState.setValue(GAME_COMPLETED);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    private void getLastKnownLocation(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (isNotNull(location)) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 20f));
                    if (navigateToLocation && !isPlayerInsideArea(currentLocation, startLocation, AREA_RADIUS)) {
                        navigateToLocation(startLocation, currentLocation);
                    }
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

    public enum GameState {
        GAME_NOT_STARTED,
        GAME_STARTED,
        GAME_COMPLETED
    }
}


package com.example.lovci_pokladov.activities;

import static com.example.lovci_pokladov.activities.GameActivity.LevelState.LEVEL_COMPLETED;
import static com.example.lovci_pokladov.activities.GameActivity.LevelState.LEVEL_NOT_STARTED;
import static com.example.lovci_pokladov.activities.GameActivity.LevelState.LEVEL_STARTED;
import static com.example.lovci_pokladov.entities.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.lovci_pokladov.objects.Utils.isNotNull;
import static com.example.lovci_pokladov.objects.Utils.isNull;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.components.CheckpointTextCard;
import com.example.lovci_pokladov.components.RegularModal;
import com.example.lovci_pokladov.entities.ConstantsCatalog.ColorPalette;
import com.example.lovci_pokladov.entities.DangerZone;
import com.example.lovci_pokladov.entities.FinalCheckpoint;
import com.example.lovci_pokladov.entities.Level;
import com.example.lovci_pokladov.entities.LevelCheckpoint;
import com.example.lovci_pokladov.entities.TimeCounter;
import com.example.lovci_pokladov.objects.DatabaseHelper;
import com.example.lovci_pokladov.objects.Utils;
import com.example.lovci_pokladov.services.Observable;
import com.example.lovci_pokladov.services.PreferencesManager;
import com.example.lovci_pokladov.services.TextToSpeechService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RegularModal gameStartModal;
    private TextToSpeechService textToSpeechService;
    private LatLng levelStartLocation;
    private int AREA_RADIUS, markerId, levelCount;
    private boolean isInsideArea = false, navigateToLocation;
    private boolean isInDangerZone = false, isNearDangerZone = false, hasDangerZones = false;
    private long dangerZoneEntryTime;
    private Level currentLevel;
    private List<LevelCheckpoint> undiscoveredCheckpoints;
    private Observable<LevelState> currentLevelState;
    private TimeCounter timeCounter;
    private FinalCheckpoint finalCheckpoint;
    private int keyFragmentsFound = 0;
    private PreferencesManager profilePreferences;
    private DangerZone dangerZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gameActivityMap);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        initMarkerData();
    }

    private void onInit() {
        String[]
                levelSeqStrings = {"first", "second", "third", "fourth"},
                introTexts = {
                        "You've reached the starting point of the %s level. Time to embark on your journey.",
                        "Welcome to the %s level, the next leg of your treasure hunt!",
                        "Prepare to explore the wonders of the %s level. Let the adventure begin.",
                        "You're now at the beginning of the %s level. Your quest awaits!",
                        "The journey continues at the %s level. Get ready for more adventures.",
                        "Here you stand at the start of the %s level. Excitement is in the air.",
                        "The next chapter in your story unfolds in the %s level. Let's explore!",
                };

        currentLevelState = new Observable<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        textToSpeechService = new TextToSpeechService(this);
        profilePreferences = PreferencesManager.getInstance(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gameActivityMap);
        RelativeLayout activeLevelLayout = findViewById(R.id.activeLevelLayout);
        RelativeLayout completedLevelLayout = findViewById(R.id.completedLevelLayout);

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
                            currentLevel.setFinalCheckpoint(databaseHelper.getFinalCheckpointForLevel(currentLevel.getId()));

                            finalCheckpoint = currentLevel.getFinalCheckpoint();
                            undiscoveredCheckpoints = currentLevel.getCheckpoints();
                            undiscoveredCheckpoints.add(currentLevel.getFinalCheckpoint());

                            dangerZone = new DangerZone(new LatLng(47.992775, 18.253922), AREA_RADIUS, "Bandit camp");
                            hasDangerZones = Utils.isNotNull(dangerZone);

                            mapFragment.getView().setVisibility(View.GONE);
                            activeLevelLayout.setVisibility(View.VISIBLE);
                            mMap.clear();

                            /*mMap.addCircle(new CircleOptions()
                                    .center(dangerZone.getPosition())
                                    .radius(AREA_RADIUS)
                                    .strokeWidth(5)
                                    .strokeColor(Color.RED)
                                    .fillColor(Color.RED));*/

                            startGame();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case LEVEL_COMPLETED: {
                        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
                            if (currentLevel.getSequenceNumber() == levelCount) {
                                databaseHelper.updateFinished(markerId);
                            } else {
                                databaseHelper.updateMarkerProgress(markerId, currentLevel.getSequenceNumber());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        timeCounter.stopTimer();
                        activeLevelLayout.setVisibility(View.GONE);
                        completedLevelLayout.setVisibility(View.VISIBLE);
                        Button backToMapButton = completedLevelLayout.findViewById(R.id.backToMapButton);
                        backToMapButton.setOnClickListener(v -> finish());
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        locationManager.removeUpdates(this);
                        clearGameLayout();
                        AtomicBoolean treasureOpened = new AtomicBoolean(false);
                        LottieAnimationView treasureChest = findViewById(R.id.treasureChest);
                        treasureChest.setOnClickListener(v -> {
                            if (!treasureOpened.get()) {
                                openTreasure(treasureChest);
                                treasureOpened.set(true);
                                backToMapButton.setVisibility(View.VISIBLE);
                            }
                        });
                        break;
                    }
                }
            }
        );
        currentLevelState.setValue(LEVEL_NOT_STARTED);

        gameStartModal = new RegularModal(this) {
            @Override
            public void acceptButtonClicked() {
                currentLevelState.setValue(LEVEL_STARTED);
            }
        };

        gameStartModal.setModalText(String.format(introTexts[(int) (Math.random() * introTexts.length)], levelSeqStrings[currentLevel.getSequenceNumber() - 1]));
    }

    private void initMarkerData() {
        markerId = getIntent().getIntExtra("markerId", 0);
        navigateToLocation = getIntent().getBooleanExtra("navigateToStart", false);
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            levelCount = databaseHelper.getLevelCountForMarker(markerId);
            int upcomingLevelStage = databaseHelper.getMarkerProgress(markerId);
            currentLevel = databaseHelper.getLevelBySequence(markerId, upcomingLevelStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isNull(currentLevel)) {
            Toast.makeText(this, "No level found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        levelStartLocation = currentLevel.getPosition();
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
        textToSpeechService.synthesizeText(currentLevel.getDescription());
        TextView timeCounterView = findViewById(R.id.timeCounter);
        TextView keyFragmentTextview = findViewById(R.id.keyfragment_count);
        if (finalCheckpoint.getKeyFragmentsAmount() > 0) {
            keyFragmentTextview.setText(keyFragmentsFound + "/" + finalCheckpoint.getKeyFragmentsAmount());
        } else {
            keyFragmentTextview.setVisibility(View.GONE);
        }
        timeCounter = new TimeCounter(timeCounterView);
        timeCounter.startTimer();
    }

    private void clearGameLayout() {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointList);
        checkpointLayout.removeAllViews();
    }

    private void openTreasure(LottieAnimationView treasureChest) {
        ImageView item = findViewById(R.id.itemIcon);
        item.setImageDrawable(finalCheckpoint.getItem().getImage());
        item.setVisibility(View.INVISIBLE);
        profilePreferences.setPlayerCoins(profilePreferences.getPlayerCoins() + finalCheckpoint.getCoins());

        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(item, "translationY", 0, -400);
        translationAnimator.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationAnimator);

        treasureChest.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(() -> {
                    animatorSet.start();
                    new Handler().postDelayed(() -> item.setVisibility(View.VISIBLE), 150);
                }, 850);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        treasureChest.playAnimation();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng playerLocation = new LatLng(location.getLatitude(), location.getLongitude());
        switch (currentLevelState.getValue()) {
            case LEVEL_NOT_STARTED: {
                if (isPlayerInsideArea(playerLocation, levelStartLocation, AREA_RADIUS) && !isInsideArea) {
                    isInsideArea = true;
                    gameStartModal.openPopup();
                } else if (!isPlayerInsideArea(playerLocation, levelStartLocation, AREA_RADIUS) && isInsideArea) {
                    isInsideArea = false;
                    gameStartModal.closePopup();
                }
                break;
            }
            case LEVEL_STARTED: {
                //TODO: Change to QaudTree or Grid
                if (hasDangerZones) {
                    handleDangerZoneProximity(playerLocation);
                    if (isInDangerZone) {
                        break;
                    }
                }

                if (isNotNull(undiscoveredCheckpoints)) {
                    Iterator<LevelCheckpoint> iterator = undiscoveredCheckpoints.iterator();
                    while (iterator.hasNext()) {
                        LevelCheckpoint checkpoint = iterator.next();
                        if (isPlayerInsideArea(playerLocation, checkpoint.getPosition(), checkpoint.getAreaSize())) {
                            if (isNotNull(checkpoint.getItem())) {
                                if (checkpoint.getItem().isKeyFragment()) {
                                    keyFragmentsFound++;
                                    keyFragmentFound();
                                } else {
                                    //TODO: add item to inventory
                                }
                            }
                            if (checkpoint.getClass() == FinalCheckpoint.class) {
                                finalCheckpointFound();
                            } else {
                                iterator.remove();
                                textToSpeechService.synthesizeText(checkpoint.getText());
                                textToSpeechService.postTaskToMainThread(() -> {
                                    addCheckpointToUi(checkpoint.getText());
                                });
                            }
                            break;
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

    private void handleDangerZoneProximity(LatLng playerLocation) {
        if (dangerZone.isCloseToDangerzone(playerLocation)) {
            if (!isNearDangerZone) {
                isNearDangerZone = true;
                textToSpeechService.synthesizeText("You are near a " + dangerZone.getMessage() + ". Be careful.");
            }

            if (dangerZone.isInsideDangerzone(playerLocation)) {
                if (!isInDangerZone) {
                    isInDangerZone = true;
                    dangerZoneEntryTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - dangerZoneEntryTime >= 10000) {
                        finish();
                    }
                }
            } else {
                isInDangerZone = false;
            }
        } else {
            isNearDangerZone = false;
        }
    }

    private void addCheckpointToUi(String checkpointText) {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointList);

        if (checkpointLayout != null) {
            CheckpointTextCard checkpointCard = new CheckpointTextCard(this, checkpointText);
            checkpointLayout.addView(checkpointCard);
        }
    }

    private void keyFragmentFound() {
        if (keyFragmentsFound == finalCheckpoint.getKeyFragmentsAmount()) {
            textToSpeechService.synthesizeText("You found the last key fragment. You can now open the treasure chest.");
        } else {
            textToSpeechService.synthesizeText("You found a key fragment. You need to find " + (finalCheckpoint.getKeyFragmentsAmount() - keyFragmentsFound) + " more to open the treasure chest.");
        }
        textToSpeechService.postTaskToMainThread(() -> {
            TextView keyFragmentTextview = findViewById(R.id.keyfragment_count);
            keyFragmentTextview.setText(keyFragmentsFound + "/" + finalCheckpoint.getKeyFragmentsAmount());
        });
    }

    private void finalCheckpointFound() {
        if (finalCheckpoint.getKeyFragmentsAmount() == 0 || keyFragmentsFound == finalCheckpoint.getKeyFragmentsAmount()) {
            textToSpeechService.synthesizeText("You found the treasure chest. Tap on it to open it.");
            currentLevelState.setValue(LEVEL_COMPLETED);
        } else {
            textToSpeechService.synthesizeText("You found the treasure chest, but it's locked. You need to find all the key fragments to open it.");
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
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
                    if (navigateToLocation && !isPlayerInsideArea(currentLocation, levelStartLocation, AREA_RADIUS)) {
                        navigateToLocation(levelStartLocation, currentLocation);
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
    public enum LevelState {
        LEVEL_NOT_STARTED,
        LEVEL_STARTED,
        LEVEL_COMPLETED
    }
}


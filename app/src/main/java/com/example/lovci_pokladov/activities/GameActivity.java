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
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import com.example.lovci_pokladov.entities.FinalCheckpoint;
import com.example.lovci_pokladov.entities.Level;
import com.example.lovci_pokladov.entities.LevelCheckpoint;
import com.example.lovci_pokladov.entities.TimeCounter;
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
    private boolean isInsideArea = false;
    private Level currentLevel;
    private List<LevelCheckpoint> undiscoveredCheckpoints;
    private Observable<LevelState> currentLevelState;
    private TimeCounter timeCounter;
    private FinalCheckpoint finalCheckpoint;

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
        textToSpeechService = new TextToSpeechService();
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

                            mapFragment.getView().setVisibility(View.GONE);
                            activeLevelLayout.setVisibility(View.VISIBLE);
                            mMap.clear();

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
                        completedLevelLayout.findViewById(R.id.backToMapButton).setOnClickListener(v -> finish());
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        locationManager.removeUpdates(this);
                        clearGameLayout();
                        AtomicBoolean treasureOpened = new AtomicBoolean(false);
                        LottieAnimationView treasureChest = findViewById(R.id.treasureChest);
                        treasureChest.setOnClickListener(v -> {
                            if (!treasureOpened.get()) {
                                openTreasure(treasureChest);
                                treasureOpened.set(true);
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
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            levelCount = databaseHelper.getLevelCountForMarker(markerId);
            int upcomingLevelStage = databaseHelper.getMarkerProgress(markerId);
            currentLevel = databaseHelper.getLevelBySequence(markerId, upcomingLevelStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isNull(currentLevel)) {
            //TODO: create an error handler class that when something from the DB is not found it will check if the DB update is needed
            Toast.makeText(this, "No level found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        levelStartLocation = currentLevel.getPosition();
        AREA_RADIUS = 6;
    }

    private void startGame() {
        textToSpeechService.synthesizeText(currentLevel.getDescription());
        TextView timeCounterView = findViewById(R.id.timeCounter);
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
                if (isNotNull(undiscoveredCheckpoints)) {
                    Iterator<LevelCheckpoint> iterator = undiscoveredCheckpoints.iterator();
                    while (iterator.hasNext()) {
                        LevelCheckpoint checkpoint = iterator.next();
                        if (isPlayerInsideArea(playerLocation, checkpoint.getPosition(), checkpoint.getAreaSize())) {
                            iterator.remove();
                            textToSpeechService.synthesizeText(checkpoint.getText());
                            textToSpeechService.postTaskToMainThread(() -> addCheckpointToUi(checkpoint.getText()));

                            if (checkpoint.getClass() == FinalCheckpoint.class) {
                                currentLevelState.setValue(LEVEL_COMPLETED);
                                break;
                            }
                            break;
                        }
                    }
                }

                break;
            }
        }
    }

    private void addCheckpointToUi(String checkpointText) {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointList);

        if (checkpointLayout != null) {
            CheckpointTextCard checkpointCard = new CheckpointTextCard(this, checkpointText);
            checkpointLayout.addView(checkpointCard);
        }
    }


    private boolean isPlayerInsideArea(LatLng playerLocation, LatLng checkedLocation, int locationRadius) {
        float[] distance = new float[1];
        Location.distanceBetween(playerLocation.latitude, playerLocation.longitude, checkedLocation.latitude, checkedLocation.longitude, distance);
        return distance[0] < locationRadius;
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


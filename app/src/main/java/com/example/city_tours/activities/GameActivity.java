package com.example.city_tours.activities;

import static com.example.city_tours.activities.GameActivity.GameState.GAME_COMPLETED;
import static com.example.city_tours.activities.GameActivity.GameState.GAME_NOT_STARTED;
import static com.example.city_tours.activities.GameActivity.GameState.GAME_STARTED;
import static com.example.city_tours.entities.ConstantsCatalog.FETCH;
import static com.example.city_tours.entities.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.city_tours.entities.ConstantsCatalog.QUESTION;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.city_tours.R;
import com.example.city_tours.components.CheckpointTextCard;
import com.example.city_tours.components.RegularModal;
import com.example.city_tours.entities.Achievement;
import com.example.city_tours.entities.ConstantsCatalog.ColorPalette;
import com.example.city_tours.entities.FinalCheckpoint;
import com.example.city_tours.entities.Game;
import com.example.city_tours.entities.GameCheckpoint;
import com.example.city_tours.entities.ResourceManager;
import com.example.city_tours.entities.puzzles.Fetch;
import com.example.city_tours.entities.puzzles.Item;
import com.example.city_tours.entities.puzzles.Quest;
import com.example.city_tours.objects.DatabaseHelper;
import com.example.city_tours.services.FetchManager;
import com.example.city_tours.services.Observable;
import com.example.city_tours.services.PreferencesManager;
import com.example.city_tours.services.QuestManager;
import com.example.city_tours.services.tts_services.TextToSpeechService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GameActivity extends BaseActivity implements LocationListener {
    private GoogleMap mainMap, miniMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RegularModal gameStartModal;
    private TextToSpeechService textToSpeechService;
    private LatLng startLocation;
    private QuestManager questManager;
    private FetchManager fetchManager;
    private int AREA_RADIUS, markerId, correctAnswerCount = 0, puzzleCount = 0;
    private int savedSequence;
    private boolean isInsideArea = false, navigateToLocation = false, isPuzzleActive = false;
    private Game currentGame;
    private float lastCheckTime = 0, previousDistance = -1;
    private static final long CHECK_INTERVAL = 10 * 1000;
    private List<GameCheckpoint> allCheckpoints, undiscoveredCheckpoints;
    private List<Item> items;
    private GameCheckpoint activePuzzleCheckpoint;
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
        savedSequence = preferencesManager.getGameState(currentGame.getId());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0.5f, this);
        }

        currentGameState.onChangeListener(gameState -> {
            switch ((GameState) gameState) {
                case GAME_NOT_STARTED: {
                    if (savedSequence != -1) {
                        currentGameState.setValue(GameState.GAME_STARTED);
                        return;
                    }
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
                            setCurrentGameCheckpoints();
                            startGame();
                            finalCheckpoint = currentGame.getFinalCheckpoint();
                            allCheckpoints = currentGame.getCheckpoints();
                            allCheckpoints.add(currentGame.getFinalCheckpoint());

                            updateCheckpointsList();

                            mainMapFragment.getView().setVisibility(View.GONE);
                            activeGameLayout.setVisibility(View.VISIBLE);
                            initializeMiniMap();
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
                            locationManager.removeUpdates(this);

                            findViewById(R.id.backToMapButton).setOnClickListener(v -> {
                                finish();
                            });

                            ImageView achievementImage = findViewById(R.id.achievementImage);
                            RatingBar ratingBar = findViewById(R.id.achievementRating);
                            try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
                                int achievementId = databaseHelper.getAchievementIdByMarkerId(markerId);
                                if (achievementId != -1) {
                                    Achievement achievement = databaseHelper.getAchievementById(achievementId);
                                    achievementImage.setImageResource(getResources().getIdentifier(achievement.getImage(), "drawable", this.getPackageName()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ratingBar.setRating(Math.round((float) correctAnswerCount / puzzleCount) * 3);

                            clearGameLayout();
                            setAwards();
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
        gameStartModal.setModalText(ResourceManager.getString(R.string.gameStart));
    }

    private void setCurrentGameCheckpoints() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            List<GameCheckpoint> checkpoints = databaseHelper.getGameCheckpoints(currentGame.getId());
            if (savedSequence != -1) {
                Iterator<GameCheckpoint> iterator = checkpoints.iterator();
                while (iterator.hasNext()) {
                    GameCheckpoint checkpoint = iterator.next();
                    if (checkpoint.getSequence() == savedSequence) {
                        break;
                    }
                    iterator.remove();
                }
            }
            currentGame.setCheckpoints(checkpoints);
            currentGame.setFinalCheckpoint(databaseHelper.getFinalGameCheckpoint(currentGame.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFetchLayout(Fetch fetch) {
        LinearLayout fetchLayout = findViewById(R.id.fetchLayout), bottomInfoLayout = findViewById(R.id.bottomInfoLayout);

        miniMap.addCircle(new CircleOptions()
                .center(fetch.getPosition())
                .radius(fetch.getArea())
                .strokeWidth(5)
                .strokeColor(ColorPalette.SECONDARY.getColor())
                .fillColor(ColorPalette.SECONDARY.getColor(100)));

        items = fetch.getItems() != null ? fetch.getItems() : new ArrayList<>();

        fetchManager = new FetchManager(this, bottomInfoLayout, fetchLayout) {
            @Override
            public void correctItemSelected() {
                puzzleCount++;
                isPuzzleActive = false;
                textToSpeechService.synthesizeText(fetch.getText());
                textToSpeechService.postTaskToMainThread(() -> {
                    addTextToUI(fetch.getText());
                });
                miniMap.clear();
                addCheckpointsToMiniMap();
            }
        };

        for (Item item : items) {
            fetchManager.itemCollected(item);
        }
    }

    private void initQuestLayout(Quest quest) {
        LinearLayout questLayout = findViewById(R.id.questLayout), bottomInfoLayout = findViewById(R.id.bottomInfoLayout);

        questManager = new QuestManager(this, bottomInfoLayout, questLayout) {
            @Override
            public void correctAnswerEntered() {
                puzzleCount++;
                correctAnswerCount = questManager.wasHintUsed() ? correctAnswerCount : correctAnswerCount + 1;
                isPuzzleActive = false;
                textToSpeechService.synthesizeText(quest.getText());
                textToSpeechService.postTaskToMainThread(() -> {
                    addTextToUI(quest.getText());
                });
                miniMap.clear();
                addCheckpointsToMiniMap();
            }

            @Override
            public void abandonQuest() {
                puzzleCount++;
                isPuzzleActive = false;
                textToSpeechService.synthesizeText(quest.getText());
                textToSpeechService.postTaskToMainThread(() -> {
                    addTextToUI(quest.getText());
                });
                miniMap.clear();
                addCheckpointsToMiniMap();
            }
        };
        questManager.initializeQuestManager(quest);
    }

    private void initializeMiniMap() {
        mainMap.clear();
        miniMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.miniMapFragment);
        Objects.requireNonNull(miniMapFragment).getMapAsync(googleMap -> {
            miniMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                miniMap.setMyLocationEnabled(true);
                getLastKnownLocation(miniMap);
                miniMap.setMinZoomPreference(16.0f);
                addCheckpointsToMiniMap();
            }
        });
    }

    private void updateCheckpointsList() {
        undiscoveredCheckpoints = new ArrayList<>();
        Iterator<GameCheckpoint> iterator = allCheckpoints.iterator();
        while (iterator.hasNext()) {
            GameCheckpoint checkpoint = iterator.next();
            undiscoveredCheckpoints.add(checkpoint);
            iterator.remove();
            if (checkpoint.hasSequence()) {
                break;
            }
        }
    }

    private void addCheckpointsToMiniMap() {
        if (undiscoveredCheckpoints != null && undiscoveredCheckpoints.size() >= 1) {
            for (int i = 0; i < undiscoveredCheckpoints.size(); i++) {
                GameCheckpoint currentCheckpoint = undiscoveredCheckpoints.get(i);

                if (currentCheckpoint.hasSequence() || currentCheckpoint instanceof FinalCheckpoint) {
                    miniMap.addCircle(new CircleOptions()
                            .center(currentCheckpoint.getPosition())
                            .radius(currentCheckpoint.getAreaSize())
                            .strokeWidth(5)
                            .strokeColor(ColorPalette.PRIMARY.getColor())
                            .fillColor(ColorPalette.PRIMARY.getColor(100)));
                } else {
                    GameCheckpoint nextCheckpoint = i < undiscoveredCheckpoints.size() - 1 ? undiscoveredCheckpoints.get(i + 1) : currentCheckpoint;
                    addArrowMarker(currentCheckpoint.getPosition(), nextCheckpoint.getPosition());
                }
            }
        }
    }

    private void addArrowMarker(LatLng position, LatLng nextCheckpoint) {
        float rotation = getBearing(position, nextCheckpoint);
        miniMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(bitmapDescriptorFromVector(this, ColorPalette.SECONDARY.getColor(), R.drawable.arrow))
                .rotation(rotation));
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lon = Math.toRadians(end.longitude - begin.longitude);
        double startLat = Math.toRadians(begin.latitude);
        double endLat = Math.toRadians(end.latitude);

        double x = Math.sin(lon) * Math.cos(endLat);
        double y = Math.cos(startLat) * Math.sin(endLat) - Math.sin(startLat) * Math.cos(endLat) * Math.cos(lon);

        return (float) ((Math.toDegrees(Math.atan2(x, y)) + 360) % 360);
    }

    private void initializeMainMap() {
        mainMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gameActivityMap);
        Objects.requireNonNull(mainMapFragment).getMapAsync(googleMap -> {
            mainMap = googleMap;
            onInit();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mainMap.setMyLocationEnabled(true);
                getLastKnownLocation(mainMap);

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
        if (savedSequence != -1) {
            textToSpeechService.synthesizeTexts(new String[]{currentGame.getCheckpoints().get(0).getText(), currentGame.getCheckpoints().get(0).getNavigationInstructions()}, 100);
            addCheckpointTextToUI(currentGame.getCheckpoints().get(0));
            currentGame.getCheckpoints().remove(0);
            return;
        }
        textToSpeechService.synthesizeText(currentGame.getDescription());
        textToSpeechService.postTaskToMainThread(() -> {
            addTextToUI(currentGame.getDescription());
        });
    }

    private void clearGameLayout() {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointInfo);
        checkpointLayout.removeAllViews();
    }

    private void setAwards() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            int achievementId = databaseHelper.getAchievementIdByMarkerId(markerId);
            if (achievementId != -1) {
                databaseHelper.updatePlayerAchievement(achievementId, Math.round((float) correctAnswerCount / puzzleCount) * 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean gameWasPlayed = getIntent().getBooleanExtra("gameWasPlayed", false);
        if (!gameWasPlayed) {
            preferencesManager.setPlayerCoins(preferencesManager.getPlayerCoins() + finalCheckpoint.getCoins() + correctAnswerCount * 50);
        }
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
                if (isNull(miniMap)) {
                    return;
                }
                miniMap.animateCamera(CameraUpdateFactory.newLatLngZoom(playerLocation, 18.5f));
                if (isPuzzleActive) {
                    behaveOnActivePuzzle(playerLocation);
                } else {
                    if (isNotNull(undiscoveredCheckpoints) && !undiscoveredCheckpoints.isEmpty()) {
                        float currentTime = System.currentTimeMillis();
                        if (currentTime - lastCheckTime >= CHECK_INTERVAL) {
                            isPlayerApproachingCheckpoint(playerLocation, undiscoveredCheckpoints.get(undiscoveredCheckpoints.size() - 1).getPosition());
                            lastCheckTime = currentTime;
                        }

                        Iterator<GameCheckpoint> iterator = undiscoveredCheckpoints.iterator();
                        while (iterator.hasNext()) {
                            GameCheckpoint checkpoint = iterator.next();
                            if (isPlayerInsideArea(playerLocation, checkpoint.getPosition(), checkpoint.getAreaSize())) {
                                if (checkpoint.hasPuzzle()) {
                                    miniMap.clear();
                                    iterator.remove();
                                    activatePuzzle(checkpoint);
                                }
                                if (checkpoint.hasSequence()) {
                                    updateCheckpointsList();
                                    preferencesManager.saveGameState(currentGame.getId(), checkpoint.getSequence());
                                }
                                if (checkpoint.getClass() == FinalCheckpoint.class) {
                                    finalCheckpointFound();
                                } else {
                                    if (isNotNull(checkpoint.getNavigationInstructions())) {
                                        textToSpeechService.synthesizeTexts(new String[]{checkpoint.getText(), checkpoint.getNavigationInstructions()}, 1000);
                                    } else if (checkpoint.hasPuzzle() && checkpoint.getPuzzleType().equals(FETCH)) {
                                        textToSpeechService.synthesizeTexts(new String[]{checkpoint.getText(), ResourceManager.getString(R.string.fetchText)}, 1000);
                                    } else {
                                        textToSpeechService.synthesizeText(checkpoint.getText());
                                    }
                                    textToSpeechService.postTaskToMainThread(() -> {
                                        addCheckpointTextToUI(checkpoint);
                                    });
                                    if (isPuzzleActive) {
                                        miniMap.addCircle(new CircleOptions()
                                                .center(checkpoint.getPosition())
                                                .radius(checkpoint.getAreaSize())
                                                .strokeWidth(5)
                                                .strokeColor(ColorPalette.PRIMARY.getColor())
                                                .fillColor(ColorPalette.PRIMARY.getColor(100)));
                                    } else {
                                        miniMap.clear();
                                        iterator.remove();
                                        addCheckpointsToMiniMap();
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    private void activatePuzzle(GameCheckpoint checkpoint) {
        isPuzzleActive = true;
        isInsideArea = true;
        activePuzzleCheckpoint = checkpoint;
        switch (checkpoint.getPuzzleType()) {
            case QUESTION:
                initQuestLayout((Quest) checkpoint.getPuzzle());
                questManager.toggleQuestModal(true);
                break;
            case FETCH:
                initFetchLayout((Fetch) checkpoint.getPuzzle());
                break;
            default:
                break;
        }
    }

    private void behaveOnActivePuzzle(LatLng playerLocation) {
        boolean isPlayerInsideQuestCheckpoint = isPlayerInsideArea(playerLocation, activePuzzleCheckpoint.getPosition(), activePuzzleCheckpoint.getAreaSize());

        if (isPlayerInsideQuestCheckpoint && !isInsideArea) {
            handleEnterArea(activePuzzleCheckpoint.getPuzzle().getPuzzleType());
            isInsideArea = true;
        } else if (!isPlayerInsideQuestCheckpoint && isInsideArea) {
            handleExitArea(activePuzzleCheckpoint.getPuzzle().getPuzzleType());
            isInsideArea = false;
        }

        if (activePuzzleCheckpoint.getPuzzleType().equals(FETCH) && isNotNull(items)) {
            Iterator<Item> iterator = items.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (isPlayerInsideArea(playerLocation, item.getPosition(), item.getAreaSize())) {
                    textToSpeechService.synthesizeText(item.getText());
                    fetchManager.itemCollected(item);
                    iterator.remove();
                }
            }
        }
    }

    private void handleEnterArea(String puzzleType) {
        switch (puzzleType) {
            case QUESTION:
                questManager.toggleQuestModal(true);
                break;
            case FETCH:
                fetchManager.toggleItemSelect(true);
                break;
            default:
                break;
        }
    }

    private void handleExitArea(String puzzleType) {
        switch (puzzleType) {
            case QUESTION:
                questManager.toggleQuestModal(false);
                break;
            case FETCH:
                fetchManager.toggleItemSelect(false);
                break;
            default:
                break;
        }
    }

    private void isPlayerApproachingCheckpoint(LatLng playerLocation, LatLng checkpointLocation) {
        float[] distance = new float[1];
        Location.distanceBetween(playerLocation.latitude, playerLocation.longitude, checkpointLocation.latitude, checkpointLocation.longitude, distance);
        if (previousDistance >= 0) {
            if (distance[0] > previousDistance + 100) {
                textToSpeechService.synthesizeText("You are moving away from the checkpoint");

            } else if (distance[0] < previousDistance - 10) {
                previousDistance = distance[0];
            }
        } else {
            previousDistance = distance[0];
        }
    }

    private boolean isPlayerInsideArea(LatLng playerLocation, LatLng checkedLocation, int locationRadius) {
        float[] distance = new float[1];
        Location.distanceBetween(playerLocation.latitude, playerLocation.longitude, checkedLocation.latitude, checkedLocation.longitude, distance);
        return distance[0] < locationRadius;
    }

    private void addTextToUI(String text) {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointInfo);

        clearTextLayouts();
        checkpointLayout.addView(new CheckpointTextCard(this, text));
    }

    private void addCheckpointTextToUI(GameCheckpoint checkpoint) {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointInfo);
        LinearLayout navigationLayout = findViewById(R.id.navigationInfo);
        clearTextLayouts();
        if (isNotNull(checkpoint.getText())) {
            checkpointLayout.addView(new CheckpointTextCard(this, checkpoint.getText()));
        }
        if (isNotNull(checkpoint.getNavigationInstructions())) {
            navigationLayout.addView(new CheckpointTextCard(this, checkpoint.getNavigationInstructions()));
        }
        if (checkpoint.hasPuzzle() && checkpoint.getPuzzleType().equals(FETCH)) {
            navigationLayout.addView(new CheckpointTextCard(this, ResourceManager.getString(R.string.fetchText)));
        }
    }

    private void clearTextLayouts() {
        LinearLayout checkpointLayout = findViewById(R.id.checkpointInfo);
        LinearLayout navigationLayout = findViewById(R.id.navigationInfo);
        if (checkpointLayout != null) {
            checkpointLayout.removeAllViews();
        }
        if (navigationLayout != null) {
            navigationLayout.removeAllViews();
        }
    }

    private void finalCheckpointFound() {
        currentGameState.setValue(GAME_COMPLETED);
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


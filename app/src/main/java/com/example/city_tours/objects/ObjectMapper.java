package com.example.city_tours.objects;

import android.database.Cursor;

import com.example.city_tours.entities.Achievement;
import com.example.city_tours.entities.FinalCheckpoint;
import com.example.city_tours.entities.Game;
import com.example.city_tours.entities.GameCheckpoint;
import com.example.city_tours.entities.LocationMarker;
import com.example.city_tours.entities.puzzles.Fetch;
import com.example.city_tours.entities.puzzles.Item;
import com.example.city_tours.entities.puzzles.Quest;
import com.google.android.gms.maps.model.LatLng;

class ObjectMapper {

    private static final String
            COLUMN_ID = "id",
            COLUMN_LAT = "lat",
            COLUMN_LONG = "long",
            COLUMN_COLOR = "color",
            COLUMN_NAME = "name",
            COLUMN_DESCRIPTION = "description",
            COLUMN_AREA_SIZE = "area_size",
            COLUMN_TEXT = "text",
            COLUMN_COINS = "coins",
            COLUMN_SEQUENCE = "sequence",
            COLUMN_QUESTION = "question",
            COLUMN_LANGUAGE = "language",
            COLUMN_VOICE = "voice",
            COLUMN_IMAGE = "image",
            COLUMN_HINT = "hint",
            COLUMN_ITEM_NAME = "item_name",
            COLUMN_ITEM_IMAGE = "item_image",
            COLUMN_CORRECT = "correct",
            COLUMN_NAV_INSTRUCTIONS = "navigation_instructions";

    public static LocationMarker mapCursorToMarker(Cursor cursor) {
        int locationID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        float locationLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float locationLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int locationColor = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR));
        String locationTitle = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        String locationDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

        return new LocationMarker(locationID, new LatLng(locationLat, locationLong), locationTitle, locationColor, locationDescription);
    }

    public static Game mapCursorToGame(Cursor cursor) {
        int gameId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String gameDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        float gameLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float gameLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        String gameLanguage = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE));
        String gameVoice = cursor.getString(cursor.getColumnIndex(COLUMN_VOICE));

        Game game = new Game(gameId, new LatLng(gameLat, gameLong), gameDescription);
        if (gameLanguage != null && gameVoice != null) {
            game.setLanguage(gameLanguage);
            game.setVoice(gameVoice);
        }
        return game;
    }

    public static GameCheckpoint mapCursorToCheckpoint(Cursor cursor) {
        int checkpointId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int checkpointAreaSizeIndex = cursor.getColumnIndex(COLUMN_AREA_SIZE);
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? 3 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int checkpointSequence = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCE));
        String checkpointNavInstructions = cursor.getString(cursor.getColumnIndex(COLUMN_NAV_INSTRUCTIONS));
        return new GameCheckpoint(checkpointId, checkpointText, checkpointNavInstructions, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, checkpointSequence);
    }

    public static FinalCheckpoint mapCursorToFinalCheckpoint(Cursor cursor) {
        int checkpointId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int checkpointAreaSizeIndex = cursor.getColumnIndex(COLUMN_AREA_SIZE);
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? 3 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int checkpointCoins = cursor.getInt(cursor.getColumnIndex(COLUMN_COINS));

        return new FinalCheckpoint(checkpointId, checkpointText, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, checkpointCoins);
    }

    public static Quest mapCursorToQuest(Cursor questCursor) {
        String question = questCursor.getString(questCursor.getColumnIndex(COLUMN_QUESTION));
        String hint = questCursor.getString(questCursor.getColumnIndex(COLUMN_HINT));
        String text = questCursor.getString(questCursor.getColumnIndex(COLUMN_TEXT));

        return new Quest(text, question, hint);
    }

    public static Fetch mapCursorToFetch(Cursor cursor) {
        String text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        int areaSize = cursor.getInt(cursor.getColumnIndex(COLUMN_AREA_SIZE));
        float lat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float lng = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));

        return new Fetch(new LatLng(lat, lng), areaSize, text);
    }

    public static Item mapCursorToItem(Cursor cursor) {
        float lat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float lng = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        String text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
        String imageName = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_IMAGE));
        boolean correctItem = cursor.getInt(cursor.getColumnIndex(COLUMN_CORRECT)) == 1;
        return new Item(new LatLng(lat, lng), text, itemName, imageName, correctItem);
    }

    public static Achievement mapCursorToAchievement(Cursor achievementCursor) {
        String title = achievementCursor.getString(achievementCursor.getColumnIndex(COLUMN_NAME));
        String image = achievementCursor.getString(achievementCursor.getColumnIndex(COLUMN_IMAGE));

        return new Achievement(title, null, image, 0);
    }
}


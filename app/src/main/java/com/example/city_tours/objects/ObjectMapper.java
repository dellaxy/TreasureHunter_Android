package com.example.city_tours.objects;

import android.database.Cursor;

import com.example.city_tours.entities.FinalCheckpoint;
import com.example.city_tours.entities.Game;
import com.example.city_tours.entities.GameCheckpoint;
import com.example.city_tours.entities.LocationMarker;
import com.google.android.gms.maps.model.LatLng;

import kotlin.Suppress;

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
            COLUMN_SEQUENCE = "sequence";

    @Suppress(names = "Range")
    public static LocationMarker mapCursorToMarker(Cursor cursor) {
        int locationID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        float locationLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float locationLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int locationColor = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR));
        String locationTitle = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        String locationDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

        return new LocationMarker(locationID, new LatLng(locationLat, locationLong), locationTitle, locationColor, locationDescription);
    }

    @Suppress(names = "Range")
    public static Game mapCursorToGame(Cursor cursor) {
        int gameId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String gameDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        float gameLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float gameLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));

        return new Game(gameId, new LatLng(gameLat, gameLong), gameDescription);
    }

    @Suppress(names = "Range")
    public static GameCheckpoint mapCursorToCheckpoint(Cursor cursor) {
        int checkpointId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int checkpointAreaSizeIndex = cursor.getColumnIndex(COLUMN_AREA_SIZE);
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? 3 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int checkpointSequence = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCE));
        return new GameCheckpoint(checkpointId, checkpointText, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, checkpointSequence);
    }

    @Suppress(names = "Range")
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
}


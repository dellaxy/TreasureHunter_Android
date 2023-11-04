package com.example.lovci_pokladov.objects;

import android.database.Cursor;

import com.example.lovci_pokladov.entities.Level;
import com.example.lovci_pokladov.entities.LevelCheckpoint;
import com.example.lovci_pokladov.entities.LocationMarker;
import com.google.android.gms.maps.model.LatLng;

class ObjectMapper {

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LONG = "long";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_ICON = "icon";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DIFFICULTY = "difficulty";
    private static final String COLUMN_SEQUENCE_NUMBER = "sequence";
    private static final String COLUMN_AREA_SIZE = "area_size";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_FINAL = "final";

    public static LocationMarker mapCursorToMarker(Cursor cursor) {
        int locationID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        float locationLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float locationLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int locationColor = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR));
        String locationIcon = cursor.getString(cursor.getColumnIndex(COLUMN_ICON));
        String locationTitle = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        String locationDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

        return new LocationMarker(locationID, new LatLng(locationLat, locationLong), locationTitle, locationColor, locationIcon, locationDescription);
    }

    public static Level mapCursorToLevel(Cursor cursor) {
        int levelId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int levelDifficulty = cursor.getInt(cursor.getColumnIndex(COLUMN_DIFFICULTY));
        int levelSequenceNumber = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCE_NUMBER));
        String levelDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        float levelLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float levelLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));

        return new Level(levelId, levelDifficulty, levelSequenceNumber, new LatLng(levelLat, levelLong), levelDescription);
    }

    public static LevelCheckpoint mapCursorToCheckpoint(Cursor cursor) {
        int checkpointId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int checkpointAreaSizeIndex = cursor.getColumnIndex(COLUMN_AREA_SIZE);
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? -1 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        boolean checkpointFinal = cursor.getInt(cursor.getColumnIndex(COLUMN_FINAL)) == 1;
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));

        return checkpointAreaSize == -1 ?
                new LevelCheckpoint(checkpointId, checkpointText, checkpointFinal, new LatLng(checkpointLat, checkpointLong)) :
                new LevelCheckpoint(checkpointId, checkpointText, checkpointFinal, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize);
    }
}


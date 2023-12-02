package com.example.lovci_pokladov.objects;

import android.database.Cursor;

import com.example.lovci_pokladov.entities.FinalCheckpoint;
import com.example.lovci_pokladov.entities.Item;
import com.example.lovci_pokladov.entities.Level;
import com.example.lovci_pokladov.entities.LevelCheckpoint;
import com.example.lovci_pokladov.entities.LocationMarker;
import com.google.android.gms.maps.model.LatLng;

class ObjectMapper {

    private static final String
            COLUMN_ID = "id",
            COLUMN_LAT = "lat",
            COLUMN_LONG = "long",
            COLUMN_COLOR = "color",
            COLUMN_ICON = "icon",
            COLUMN_NAME = "name",
            COLUMN_DESCRIPTION = "description",
            COLUMN_DIFFICULTY = "difficulty",
            COLUMN_SEQUENCE_NUMBER = "sequence",
            COLUMN_AREA_SIZE = "area_size",
            COLUMN_TEXT = "text",
            COLUMN_ICON_NAME = "icon_name",
            COLUMN_LOCK_TYPE = "lock_type",
            COLUMN_LOCK_CODE = "lock_code",
            COLUMN_COINS = "coins";


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
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? 3 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));

        return new LevelCheckpoint(checkpointId, checkpointText, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, null);
    }

    public static FinalCheckpoint mapCursorToFinalCheckpoint(Cursor cursor) {
        int checkpointId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int checkpointAreaSizeIndex = cursor.getColumnIndex(COLUMN_AREA_SIZE);
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? 3 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        String checkpointLockType = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_TYPE));
        String checkpointLockCode = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_CODE));
        int checkpointCoins = cursor.getInt(cursor.getColumnIndex(COLUMN_COINS));

        return new FinalCheckpoint(checkpointId, checkpointText, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, checkpointLockType, checkpointLockCode, checkpointCoins, null);
    }

    public static Item mapCursorToItem(Cursor cursor) {
        int itemId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String itemDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        String itemIconName = cursor.getString(cursor.getColumnIndex(COLUMN_ICON_NAME));

        return new Item(itemId, itemIconName, itemDescription);
    }
}


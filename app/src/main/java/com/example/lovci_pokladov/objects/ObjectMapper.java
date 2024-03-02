package com.example.lovci_pokladov.objects;

import android.database.Cursor;

import com.example.lovci_pokladov.entities.FinalCheckpoint;
import com.example.lovci_pokladov.entities.Game;
import com.example.lovci_pokladov.entities.GameCheckpoint;
import com.example.lovci_pokladov.entities.Item;
import com.example.lovci_pokladov.entities.LocationMarker;
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
            COLUMN_ICON_NAME = "icon_name",
            COLUMN_KEYFRAGMENTS = "key_fragments",
            COLUMN_COINS = "coins";

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

        return new GameCheckpoint(checkpointId, checkpointText, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, null);
    }

    @Suppress(names = "Range")
    public static FinalCheckpoint mapCursorToFinalCheckpoint(Cursor cursor) {
        int checkpointId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int checkpointAreaSizeIndex = cursor.getColumnIndex(COLUMN_AREA_SIZE);
        int checkpointAreaSize = cursor.isNull(checkpointAreaSizeIndex) ? 3 : cursor.getInt(checkpointAreaSizeIndex);
        String checkpointText = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
        float checkpointLat = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
        float checkpointLong = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));
        int checkpointKeyfragments = cursor.getInt(cursor.getColumnIndex(COLUMN_KEYFRAGMENTS));
        int checkpointCoins = cursor.getInt(cursor.getColumnIndex(COLUMN_COINS));

        return new FinalCheckpoint(checkpointId, checkpointText, new LatLng(checkpointLat, checkpointLong), checkpointAreaSize, checkpointKeyfragments, checkpointCoins, null);
    }

    @Suppress(names = "Range")
    public static Item mapCursorToItem(Cursor cursor) {
        int itemId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String itemDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        String itemIconName = cursor.getString(cursor.getColumnIndex(COLUMN_ICON_NAME));
        String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

        return new Item(itemId, itemIconName, itemDescription, itemName);
    }
}


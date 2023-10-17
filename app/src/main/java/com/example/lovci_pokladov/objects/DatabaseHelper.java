package com.example.lovci_pokladov.objects;

import static com.example.lovci_pokladov.models.ConstantsCatalog.DATABASE_COLLECTIONS;
import static com.example.lovci_pokladov.models.ConstantsCatalog.DATABASE_NAME;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lovci_pokladov.models.Level;
import com.example.lovci_pokladov.models.LevelCheckpoint;
import com.example.lovci_pokladov.models.LocationMarker;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (!databaseExists()) {
            copyDatabaseFromAssets();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    }

    private boolean databaseExists() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        boolean exists = false;
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(0);
            if (DATABASE_COLLECTIONS.contains(tableName)) {
                exists = true;
                break;
            }
        }
        cursor.close();
        db.close();
        return exists;
    }

    private void copyDatabaseFromAssets() {
        InputStream inputStream;
        OutputStream outputStream;
        SQLiteDatabase db = getWritableDatabase();

        try {
            inputStream = context.getAssets().open(DATABASE_NAME);
            File tempFile = File.createTempFile("temp", null);
            outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            db.execSQL("ATTACH DATABASE '" + tempFile.getAbsolutePath() + "' AS tempDb");
            Cursor cursor = db.rawQuery("SELECT name FROM tempDb.sqlite_master WHERE type='table'", null);
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(0);
                if (DATABASE_COLLECTIONS.contains(tableName)){
                    db.execSQL("CREATE TABLE " + tableName + " AS SELECT * FROM tempDb." + tableName);
                }
            }
            cursor.close();
            db.execSQL("DETACH DATABASE tempDb");
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.close();
    }


    public LocationMarker getMarkerById(int id) {
        SQLiteDatabase database = getReadableDatabase();
        LocationMarker marker = null;

        try {
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.MARKERS.getCollectionName(), null, "id = ?", new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                marker = getLocationMarkerFromCursor(cursor);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return marker;
    }

    public List<LocationMarker> getAllMarkers() {
        SQLiteDatabase database = getReadableDatabase();
        List<LocationMarker> markers = new ArrayList<>();

        try {
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.MARKERS.getCollectionName(), null, null, null);

            while (cursor.moveToNext()) {
                LocationMarker marker = getLocationMarkerFromCursor(cursor);
                markers.add(marker);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return markers;
    }

    public List<Level> getLevelsForMarker(int id){
        SQLiteDatabase database = getReadableDatabase();
        List<Level> levels = new ArrayList<>();
        try {
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.LEVELS.getCollectionName(), null, "marker_id = ?", new String[]{String.valueOf(id)});
            while(cursor.moveToNext()){
                int levelId = cursor.getInt(cursor.getColumnIndex("id"));
                int levelDifficulty = cursor.getInt(cursor.getColumnIndex("difficulty"));
                int levelSequenceNumber = cursor.getInt(cursor.getColumnIndex("sequence_number"));
                float levelLat = cursor.getFloat(cursor.getColumnIndex("lat"));
                float levelLong = cursor.getFloat(cursor.getColumnIndex("long"));
                String levelDescription = cursor.getString(cursor.getColumnIndex("description"));
                levels.add(new Level(levelId, levelDifficulty, levelSequenceNumber, new LatLng(levelLat, levelLong), levelDescription));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        Log.d("markerLevelsService", levels.toString());
        return levels;
    }

    public List<LevelCheckpoint> getCheckpointsForLevel(int id){
        SQLiteDatabase database = getReadableDatabase();
        List<LevelCheckpoint> checkpoints = new ArrayList<>();
        try {
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.LEVEL_CHECKPOINTS.getCollectionName(), null, "level_id = ?", new String[]{String.valueOf(id)});
            while(cursor.moveToNext()){
                int checkpointId = cursor.getInt(cursor.getColumnIndex("id"));
                String checkpointText = cursor.getString(cursor.getColumnIndex("text"));
                boolean checkpointFinal = cursor.getInt(cursor.getColumnIndex("final")) == 0;
                float checkpointLat = cursor.getFloat(cursor.getColumnIndex("lat"));
                float checkpointLong = cursor.getFloat(cursor.getColumnIndex("long"));
                checkpoints.add(new LevelCheckpoint(checkpointId, checkpointText, checkpointFinal, new LatLng(checkpointLat, checkpointLong)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return checkpoints;
    }


    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs) {
        return database.query(table, columns, selection, selectionArgs, null, null, null);
    }

    private LocationMarker getLocationMarkerFromCursor(Cursor cursor) {
        int locationID = cursor.getInt(cursor.getColumnIndex("id"));
        float locationLat = cursor.getFloat(cursor.getColumnIndex("lat"));
        float locationLong = cursor.getFloat(cursor.getColumnIndex("long"));
        int locationColor = cursor.getInt(cursor.getColumnIndex("color"));
        String locationIcon = cursor.getString(cursor.getColumnIndex("icon"));
        String locationTitle = cursor.getString(cursor.getColumnIndex("name"));
        String locationDescription = cursor.getString(cursor.getColumnIndex("description"));

        return new LocationMarker(locationID, new LatLng(locationLat,locationLong), locationTitle, locationColor, locationIcon, locationDescription);
    }
}

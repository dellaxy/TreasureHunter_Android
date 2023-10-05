package com.example.lovci_pokladov.objects;

import static com.example.lovci_pokladov.models.ConstantsCatalog.DATABASE_NAME;
import static com.example.lovci_pokladov.models.ConstantsCatalog.LEVELS_TABLE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lovci_pokladov.models.LocationMarker;

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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onOpen(SQLiteDatabase db) {
        if (!isTableExists(db, LEVELS_TABLE)) {
            copyTableFromAssets(db, LEVELS_TABLE);
        }
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean tableExists = false;
        if (Utils.isNotNull(cursor)) {
            if (cursor.getCount() > 0) {
                tableExists = true;
            }
            cursor.close();
        }
        return tableExists;
    }

    private void copyTableFromAssets(SQLiteDatabase db, String tableName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

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
            db.execSQL("CREATE TABLE " + LEVELS_TABLE + " AS SELECT * FROM tempDb." + LEVELS_TABLE);
            db.execSQL("DETACH DATABASE tempDb");
            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LocationMarker getMarkerById(int id) {
        SQLiteDatabase database = getReadableDatabase();
        LocationMarker marker = null;

        try {
            Cursor cursor = database.rawQuery("SELECT * FROM "+ LEVELS_TABLE +" WHERE id = ?", new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                int locationID = cursor.getInt(cursor.getColumnIndex("id"));
                float locationLat = cursor.getFloat(cursor.getColumnIndex("lat"));
                float locationLong = cursor.getFloat(cursor.getColumnIndex("long"));
                int locationColor = cursor.getInt(cursor.getColumnIndex("color"));
                String locationIcon = cursor.getString(cursor.getColumnIndex("icon"));
                int locationDifficulty = cursor.getInt(cursor.getColumnIndex("difficulty"));
                String locationTitle = cursor.getString(cursor.getColumnIndex("name"));
                String locationDescription = cursor.getString(cursor.getColumnIndex("description"));
                marker = new LocationMarker(locationID, locationLat, locationLong, locationTitle, locationColor, locationIcon, locationDifficulty, locationDescription);
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
        List<LocationMarker> markers = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        try {
            String[] columns = {"id", "name", "lat", "long", "color", "icon", "difficulty", "description"};

            Cursor cursor = database.query(LEVELS_TABLE, columns, null, null, null, null, null);
            while (cursor.moveToNext()) {
                int locationID = cursor.getInt(cursor.getColumnIndex("id"));
                float locationLat = cursor.getFloat(cursor.getColumnIndex("lat"));
                float locationLong = cursor.getFloat(cursor.getColumnIndex("long"));
                int locationColor = cursor.getInt(cursor.getColumnIndex("color"));
                String locationIcon = cursor.getString(cursor.getColumnIndex("icon"));
                int locationDifficulty = cursor.getInt(cursor.getColumnIndex("difficulty"));
                String locationTitle = cursor.getString(cursor.getColumnIndex("name"));
                String locationDescription = cursor.getString(cursor.getColumnIndex("description"));
                LocationMarker marker = new LocationMarker(locationID, locationLat, locationLong, locationTitle, locationColor, locationIcon, locationDifficulty, locationDescription);

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
}

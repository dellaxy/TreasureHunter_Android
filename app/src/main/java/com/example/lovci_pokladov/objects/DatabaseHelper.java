package com.example.lovci_pokladov.objects;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "treasures_db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        copyDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void copyDatabase() {
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
            String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();

            File file = new File(outFileName);
            if (!file.exists()) {
                OutputStream outputStream = new FileOutputStream(outFileName);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public CustomMarker getMarkerById(int id) {
        SQLiteDatabase database = getReadableDatabase();
        CustomMarker marker = null;

        try {
            Cursor cursor = database.rawQuery("SELECT * FROM locations WHERE id = ?", new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                int locationID = cursor.getInt(cursor.getColumnIndex("id"));
                float locationLat = cursor.getFloat(cursor.getColumnIndex("lat"));
                float locationLong = cursor.getFloat(cursor.getColumnIndex("long"));
                int locationColor = cursor.getInt(cursor.getColumnIndex("color"));
                String locationIcon = cursor.getString(cursor.getColumnIndex("icon"));
                int locationDifficulty = cursor.getInt(cursor.getColumnIndex("difficulty"));
                String locationTitle = cursor.getString(cursor.getColumnIndex("name"));
                String locationDescription = cursor.getString(cursor.getColumnIndex("description"));
                marker = new CustomMarker(locationID, locationLat, locationLong, locationTitle, locationColor, locationIcon, locationDifficulty, locationDescription);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return marker;
    }


    public List<CustomMarker> getAllMarkers() {
        List<CustomMarker> markers = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        try {
            String tableName = "locations";
            String[] columns = {"id", "name", "lat", "long", "color", "icon", "difficulty", "description"};

            Cursor cursor = database.query(tableName, columns, null, null, null, null, null);
            while (cursor.moveToNext()) {
                int locationID = cursor.getInt(cursor.getColumnIndex("id"));
                float locationLat = cursor.getFloat(cursor.getColumnIndex("lat"));
                float locationLong = cursor.getFloat(cursor.getColumnIndex("long"));
                int locationColor = cursor.getInt(cursor.getColumnIndex("color"));
                String locationIcon = cursor.getString(cursor.getColumnIndex("icon"));
                int locationDifficulty = cursor.getInt(cursor.getColumnIndex("difficulty"));
                String locationTitle = cursor.getString(cursor.getColumnIndex("name"));
                String locationDescription = cursor.getString(cursor.getColumnIndex("description"));
                CustomMarker marker = new CustomMarker(locationID, locationLat, locationLong, locationTitle, locationColor, locationIcon, locationDifficulty, locationDescription);

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


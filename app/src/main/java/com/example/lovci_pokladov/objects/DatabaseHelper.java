package com.example.lovci_pokladov.objects;

import static com.example.lovci_pokladov.entities.ConstantsCatalog.DATABASE_COLLECTIONS;
import static com.example.lovci_pokladov.entities.ConstantsCatalog.DATABASE_COLLECTIONS.FINISHED;
import static com.example.lovci_pokladov.entities.ConstantsCatalog.DATABASE_NAME;
import static com.example.lovci_pokladov.objects.ObjectMapper.mapCursorToLevel;
import static com.example.lovci_pokladov.objects.ObjectMapper.mapCursorToMarker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.lovci_pokladov.entities.Level;
import com.example.lovci_pokladov.entities.LevelCheckpoint;
import com.example.lovci_pokladov.entities.LocationMarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

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

    public LocationMarker getMarkerById(int markerId) {
        SQLiteDatabase database = getReadableDatabase();
        LocationMarker marker = null;
        try {
            String[] selectionArgs = {String.valueOf(markerId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.MARKERS.getCollectionName(), null, "id = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                marker = mapCursorToMarker(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return marker;
    }

    public int getMarkerDifficulty(int markerId){
        SQLiteDatabase database = getReadableDatabase();
        int difficulty = 0;
        try{
            String query = "SELECT AVG(difficulty) FROM " + DATABASE_COLLECTIONS.LEVELS.getCollectionName() + " WHERE marker_id = ?";
            String[] selectionArgs = {String.valueOf(markerId)};
            Cursor cursor = database.rawQuery(query, selectionArgs);
            if(cursor.moveToFirst()){
                difficulty = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return difficulty;
    }

    public List<LocationMarker> getAllMarkers() {
        SQLiteDatabase database = getReadableDatabase();
        List<LocationMarker> markers = new ArrayList<>();
        try {
            String selection = "id NOT IN (SELECT marker_id FROM " + FINISHED.getCollectionName() + ")";
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.MARKERS.getCollectionName(), null, selection, null);
            while (cursor.moveToNext()) {
                LocationMarker marker = mapCursorToMarker(cursor);
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

    public Level getLevelBySequence(int markerId, int sequenceNumber){
        SQLiteDatabase database = getReadableDatabase();
        Level level = null;
        try {
            String[] selectionArgs = {String.valueOf(markerId), String.valueOf(sequenceNumber)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.LEVELS.getCollectionName(), null, "marker_id = ? AND sequence = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                level = mapCursorToLevel(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return level;
    }
    public List<Level> getLevelsForMarker(int markerId){
        SQLiteDatabase database = getReadableDatabase();
        List<Level> levels = new ArrayList<>();
        try {
            String [] selectionArgs = {String.valueOf(markerId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.LEVELS.getCollectionName(), null, "marker_id = ?", selectionArgs, "sequence ASC");
            while(cursor.moveToNext()){
                Level level = mapCursorToLevel(cursor);
                levels.add(level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return levels;
    }

    public List<LevelCheckpoint> getCheckpointsForLevel(int levelId){
        SQLiteDatabase database = getReadableDatabase();
        List<LevelCheckpoint> checkpoints = new ArrayList<>();
        try {
            String[] selectionArgs = {String.valueOf(levelId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.LEVEL_CHECKPOINTS.getCollectionName(), null, "level_id = ?", selectionArgs);
            while(cursor.moveToNext()){
                LevelCheckpoint checkpoint = ObjectMapper.mapCursorToCheckpoint(cursor);
                checkpoints.add(checkpoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return checkpoints;
    }

    public int getMarkerProgress(int markerId){
        SQLiteDatabase database = getReadableDatabase();
        int progress = 1;
        try{
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.PROGRESS.getCollectionName(), new String[]{"level_stage"}, "marker_id = ?", new String[]{String.valueOf(markerId)});
            if(cursor.moveToFirst()){
                progress = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return progress;
    }

    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs) {
        return database.query(table, columns, selection, selectionArgs, null, null, null);
    }

    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return database.query(table, columns, selection, selectionArgs, null, null, orderBy);
    }

    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs, String orderBy, String joinClause) {
        String query = "SELECT " + TextUtils.join(",", columns) +
                " FROM " + table +
                (joinClause != null ? " " + joinClause : "") +
                (selection != null ? " WHERE " + selection : "") +
                (orderBy != null ? " ORDER BY " + orderBy : "");

        return database.rawQuery(query, selectionArgs);
    }

}

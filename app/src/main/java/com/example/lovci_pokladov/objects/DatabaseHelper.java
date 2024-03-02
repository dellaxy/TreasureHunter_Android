package com.example.lovci_pokladov.objects;

import static com.example.lovci_pokladov.entities.ConstantsCatalog.DATABASE_COLLECTIONS;
import static com.example.lovci_pokladov.entities.ConstantsCatalog.DATABASE_NAME;
import static com.example.lovci_pokladov.objects.ObjectMapper.mapCursorToGame;
import static com.example.lovci_pokladov.objects.ObjectMapper.mapCursorToMarker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.lovci_pokladov.entities.FinalCheckpoint;
import com.example.lovci_pokladov.entities.Game;
import com.example.lovci_pokladov.entities.GameCheckpoint;
import com.example.lovci_pokladov.entities.Item;
import com.example.lovci_pokladov.entities.LocationMarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 3;
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

    public Game getGame(int markerId) {
        SQLiteDatabase database = getReadableDatabase();
        Game game = null;
        try {
            String[] selectionArgs = {String.valueOf(markerId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.GAMES.getCollectionName(), null, "marker_id = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                game = mapCursorToGame(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return game;
    }

    public List<GameCheckpoint> getGameCheckpoints(int gameId) {
        SQLiteDatabase database = getReadableDatabase();
        List<GameCheckpoint> checkpoints = new ArrayList<>();
        try {
            String[] selectionArgs = {String.valueOf(gameId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.GAME_CHECKPOINTS.getCollectionName(), null, "level_id = ?", selectionArgs);
            while (cursor.moveToNext()) {
                GameCheckpoint checkpoint = ObjectMapper.mapCursorToCheckpoint(cursor);
                if (!cursor.isNull(cursor.getColumnIndex("item_id")) && cursor.getColumnIndex("item_id") != -1) {
                    Item item = getItem(cursor.getInt(cursor.getColumnIndex("item_id")));
                    checkpoint.setItem(item);
                }
                checkpoints.add(checkpoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return checkpoints;
    }


    public FinalCheckpoint getFinalGameCheckpoint(int gameId) {
        SQLiteDatabase database = getReadableDatabase();
        FinalCheckpoint finalCheckpoint = null;
        try {
            String[] selectionArgs = {String.valueOf(gameId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.FINAL_CHECKPOINTS.getCollectionName(), null, "level_id = ?", selectionArgs, null);
            if (cursor.moveToFirst()) {
                finalCheckpoint = ObjectMapper.mapCursorToFinalCheckpoint(cursor);
                if (cursor.getColumnIndex("item_id") != -1 && !cursor.isNull(cursor.getColumnIndex("item_id"))) {
                    Item item = getItem(cursor.getInt(cursor.getColumnIndex("item_id")));
                    finalCheckpoint.setItem(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return finalCheckpoint;
    }

    public Item getItem(int itemId) {
        SQLiteDatabase database = getReadableDatabase();
        Item item = null;
        try {
            String[] selectionArgs = {String.valueOf(itemId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.ITEMS.getCollectionName(), null, "id = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                item = ObjectMapper.mapCursorToItem(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return item;
    }

    public void updateFinished(int markerId) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("marker_id", markerId);
            database.insert(DATABASE_COLLECTIONS.FINISHED.getCollectionName(), null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs) {
        return database.query(table, columns, selection, selectionArgs, null, null, null);
    }

    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return database.query(table, columns, selection, selectionArgs, null, null, orderBy);
    }

    private Cursor queryDatabase(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs, String orderBy, String joinClause) {
        String query = "SELECT " + (columns != null ? TextUtils.join(",", columns) : "*") +
                " FROM " + table +
                (joinClause != null ? " " + joinClause : "") +
                (selection != null ? " WHERE " + selection : "") +
                (orderBy != null ? " ORDER BY " + orderBy : "");

        return database.rawQuery(query, selectionArgs);
    }

}

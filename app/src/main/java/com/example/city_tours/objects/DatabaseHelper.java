package com.example.city_tours.objects;

import static com.example.city_tours.entities.ConstantsCatalog.DATABASE_COLLECTIONS;
import static com.example.city_tours.entities.ConstantsCatalog.DATABASE_NAME;
import static com.example.city_tours.entities.ConstantsCatalog.FETCH;
import static com.example.city_tours.entities.ConstantsCatalog.QUESTION;
import static com.example.city_tours.objects.ObjectMapper.mapCursorToGame;
import static com.example.city_tours.objects.ObjectMapper.mapCursorToMarker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.city_tours.entities.Achievement;
import com.example.city_tours.entities.FinalCheckpoint;
import com.example.city_tours.entities.Game;
import com.example.city_tours.entities.GameCheckpoint;
import com.example.city_tours.entities.LocationMarker;
import com.example.city_tours.entities.puzzles.Fetch;
import com.example.city_tours.entities.puzzles.Item;
import com.example.city_tours.entities.puzzles.Puzzle;
import com.example.city_tours.entities.puzzles.Quest;

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
            String selection = "";
            //String selection = "id NOT IN (SELECT marker_id FROM " + FINISHED.getCollectionName() + ")";
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
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.GAME_CHECKPOINTS.getCollectionName(), null, "game_id = ?", selectionArgs);
            while (cursor.moveToNext()) {
                GameCheckpoint checkpoint = ObjectMapper.mapCursorToCheckpoint(cursor);
                Puzzle puzzle = getPuzzle(cursor);
                if (puzzle != null) {
                    checkpoint.setPuzzle(puzzle);
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
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.FINAL_CHECKPOINTS.getCollectionName(), null, "game_id = ?", selectionArgs, null);
            while (cursor.moveToNext()) {
                finalCheckpoint = ObjectMapper.mapCursorToFinalCheckpoint(cursor);
                Puzzle puzzle = getPuzzle(cursor);
                if (puzzle != null) {
                    finalCheckpoint.setPuzzle(puzzle);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return finalCheckpoint;
    }

    private Puzzle getPuzzle(Cursor cursor) {
        int puzzleIdIndex = cursor.getColumnIndex("puzzle_id");
        int puzzleTypeIndex = cursor.getColumnIndex("puzzle_type");

        if (!cursor.isNull(puzzleIdIndex) && !cursor.isNull(puzzleTypeIndex)) {
            int puzzleId = cursor.getInt(puzzleIdIndex);
            String puzzleType = cursor.getString(puzzleTypeIndex);

            switch (puzzleType) {
                case QUESTION:
                    return getQuest(puzzleId);
                case FETCH:
                    return getFetch(puzzleId);
                default:
                    return null;
            }
        }

        return null;
    }

    private Fetch getFetch(int fetchId) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.FETCH.getCollectionName(), null, "id = ?", new String[]{String.valueOf(fetchId)});
        try {
            if (cursor.moveToFirst()) {
                Fetch fetch = ObjectMapper.mapCursorToFetch(cursor);

                Cursor itemsCursor = queryDatabase(database, DATABASE_COLLECTIONS.FETCH_ITEMS.getCollectionName(), null, "fetch_id = ?", new String[]{String.valueOf(fetchId)});
                try {
                    ArrayList<Item> items = new ArrayList<>();
                    while (itemsCursor.moveToNext()) {
                        Item item = ObjectMapper.mapCursorToItem(itemsCursor);
                        items.add(item);
                    }
                    fetch.setItems(items);
                } finally {
                    itemsCursor.close();
                }
                return fetch;
            }

        } finally {
            cursor.close();
        }
        return null;
    }

    private Quest getQuest(int questId) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.QUESTS.getCollectionName(), null, "id = ?", new String[]{String.valueOf(questId)});

        try {
            if (cursor.moveToFirst()) {
                Quest quest = ObjectMapper.mapCursorToQuest(cursor);

                Cursor answersCursor = queryDatabase(database, DATABASE_COLLECTIONS.QUEST_ANSWERS.getCollectionName(), null, "quest_id = ?", new String[]{String.valueOf(questId)});
                try {
                    ArrayList<String> answers = new ArrayList<>();
                    while (answersCursor.moveToNext()) {
                        String answer = answersCursor.getString(answersCursor.getColumnIndex("answer"));
                        answers.add(answer);
                        boolean isCorrect = answersCursor.getInt(answersCursor.getColumnIndex("correct")) == 1;
                        if (isCorrect) {
                            quest.setCorrectAnswer(answer);
                        }
                    }
                    quest.setAnswers(answers);
                } finally {
                    answersCursor.close();
                }
                return quest;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public Achievement getAchievementById(int achievementId) {
        SQLiteDatabase database = getReadableDatabase();
        Achievement achievement = null;
        try {
            String[] selectionArgs = {String.valueOf(achievementId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.ACHIEVEMENTS.getCollectionName(), null, "id = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                achievement = ObjectMapper.mapCursorToAchievement(cursor);
                LocationMarker marker = getMarkerById(cursor.getInt(cursor.getColumnIndex("marker_id")));
                if (marker != null) {
                    achievement.setTourName(marker.getTitle());
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return achievement;
    }

    public List<Achievement> getLockedAchievements() {
        SQLiteDatabase database = getReadableDatabase();
        List<Achievement> achievements = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DATABASE_COLLECTIONS.ACHIEVEMENTS.getCollectionName() +
                    " WHERE id NOT IN (SELECT achievement_id FROM " +
                    DATABASE_COLLECTIONS.PLAYER_ACHIEVEMENTS.getCollectionName() + ")";
            Cursor cursor = database.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Achievement achievement = ObjectMapper.mapCursorToAchievement(cursor);
                LocationMarker marker = getMarkerById(cursor.getInt(cursor.getColumnIndex("marker_id")));
                if (marker != null) {
                    achievement.setTourName(marker.getTitle());
                }
                achievements.add(achievement);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return achievements;
    }

    public List<Achievement> getPlayerAchievements() {
        SQLiteDatabase database = getReadableDatabase();
        List<Achievement> achievements = new ArrayList<>();
        try {
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.PLAYER_ACHIEVEMENTS.getCollectionName(), null, null, null, null);
            while (cursor.moveToNext()) {
                int achievementId = cursor.getInt(cursor.getColumnIndex("achievement_id"));
                Achievement achievement = getAchievementById(achievementId);
                achievement.setRating(cursor.getInt(cursor.getColumnIndex("stage")));
                if (achievement != null) {
                    achievements.add(achievement);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return achievements;
    }

    public int getAchievementIdByMarkerId(int markerId) {
        SQLiteDatabase database = getReadableDatabase();
        int achievementId = -1;
        try {
            String[] selectionArgs = {String.valueOf(markerId)};
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.ACHIEVEMENTS.getCollectionName(), new String[]{"id"}, "marker_id = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                achievementId = cursor.getInt(cursor.getColumnIndex("id"));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return achievementId;
    }

    public void updatePlayerAchievement(int achievementId, int stage) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            Cursor cursor = queryDatabase(database, DATABASE_COLLECTIONS.PLAYER_ACHIEVEMENTS.getCollectionName(), null, "achievement_id = ?", new String[]{String.valueOf(achievementId)});
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("stage", stage);
                database.update(DATABASE_COLLECTIONS.PLAYER_ACHIEVEMENTS.getCollectionName(), values, "achievement_id = ?", new String[]{String.valueOf(achievementId)});
            } else {
                ContentValues values = new ContentValues();
                values.put("achievement_id", achievementId);
                values.put("stage", stage);
                database.insert(DATABASE_COLLECTIONS.PLAYER_ACHIEVEMENTS.getCollectionName(), null, values);
            }
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
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

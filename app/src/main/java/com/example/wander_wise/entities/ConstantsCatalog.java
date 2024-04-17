package com.example.wander_wise.entities;

import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.example.wander_wise.R;
import com.example.wander_wise.fragments.AchievementsFragment;
import com.example.wander_wise.fragments.MapsFragment;
import com.example.wander_wise.fragments.RegionFragment;
import com.example.wander_wise.fragments.SettingsFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class ConstantsCatalog {
    // Database constants
    public static final String DATABASE_NAME = "local_tours.db";

    // Activity request codes
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public static final LatLng SLOVAKIA_LOCATION = new LatLng(48.669026, 19.699024);

    public static final Map<String, String> appLanguages = Map.of(
            "English", "en",
            "Slovenčina", "sk"
    );


    // PUZZLE TYPES
    public static final String
            QUESTION = "Q",
            FETCH = "F";

    public enum DATABASE_COLLECTIONS {
        MARKERS("markers"),
        GAMES("games"),
        GAME_CHECKPOINTS("game_checkpoints"),
        FINAL_CHECKPOINTS("final_checkpoints"),
        FINISHED("finished"),
        QUESTS("quests"),
        QUEST_ANSWERS("quest_answers"),
        FETCH("fetch"),
        FETCH_ITEMS("fetch_items"),
        ACHIEVEMENTS("achievements"),
        PLAYER_ACHIEVEMENTS("player_achievements");
        private final String collectionName;

        DATABASE_COLLECTIONS(String collectionName) {
            this.collectionName = collectionName;
        }

        public String getCollectionName() {
            return this.collectionName;
        }

        public static boolean contains(String tableName) {
            for (DATABASE_COLLECTIONS enumValue : DATABASE_COLLECTIONS.values()) {
                if (enumValue.getCollectionName().equals(tableName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum ColorPalette {
        PRIMARY(69, 107, 141),
        SECONDARY(69, 107, 141),
        ARROW(0, 158, 255),
        SELECTED(67, 114, 158);
        private final int color;

        ColorPalette(int red, int green, int blue) {
            this.color = Color.rgb(red, green, blue);
        }

        public int getColor() {
            return this.color;
        }

        public int getColor(int alpha) {
            return Color.argb(alpha, Color.red(this.color), Color.green(this.color), Color.blue(this.color));
        }
    }


    public enum MENU_PAGES {
        HOME(R.string.home, new MapsFragment()),
        REGIONS(R.string.region, new RegionFragment()),
        ACHIEVEMENTS(R.string.achievements, new AchievementsFragment()),
        SETTINGS(R.string.settings, new SettingsFragment());

        private final int pageNameResourceId;
        private final Fragment fragmentClass;

        MENU_PAGES(int pageNameResourceId, Fragment fragmentClass) {
            this.pageNameResourceId = pageNameResourceId;
            this.fragmentClass = fragmentClass;
        }

        public int getPageNameResourceId() {
            return pageNameResourceId;
        }

        public Fragment getFragmentClass() {
            return fragmentClass;
        }
    }
}

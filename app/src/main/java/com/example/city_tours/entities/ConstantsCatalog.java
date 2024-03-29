package com.example.city_tours.entities;

import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.example.city_tours.R;
import com.example.city_tours.fragments.AchievementsFragment;
import com.example.city_tours.fragments.MapsFragment;
import com.example.city_tours.fragments.RegionFragment;
import com.google.android.gms.maps.model.LatLng;

public class ConstantsCatalog {
    // Database constants
    public static final String DATABASE_NAME = "local_tours.db";

    // Activity request codes
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public static final LatLng SLOVAKIA_LOCATION = new LatLng(48.669026, 19.699024);

    public enum DATABASE_COLLECTIONS {
        MARKERS("markers"),
        GAMES("games"),
        GAME_CHECKPOINTS("game_checkpoints"),
        FINAL_CHECKPOINTS("final_checkpoints"),
        FINISHED("finished"),
        QUESTS("quests"),
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
        SETTINGS(R.string.settings, new RegionFragment());

        private final int pageNameResourceId;
        private final Fragment fragmentClass;

        MENU_PAGES(int pageNameResourceId, Fragment fragmentClass) {
            this.pageNameResourceId = pageNameResourceId;
            this.fragmentClass = fragmentClass;
        }

        public String getPageName() {
            return ResourceManager.getString(pageNameResourceId);
        }

        public Fragment getFragmentClass() {
            return fragmentClass;
        }
    }
}

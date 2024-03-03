package com.example.city_tours.entities;

import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.example.city_tours.fragments.MapsFragment;
import com.example.city_tours.fragments.RegionFragment;
import com.example.city_tours.fragments.TestFragment;
import com.google.android.gms.maps.model.LatLng;

public class ConstantsCatalog {
    // Database constants
    public static final String DATABASE_NAME = "local_tours.db";

    // Activity request codes
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public static final LatLng SLOVAKIA_LOCATION = new LatLng(48.669026, 19.699024);

    public enum STARTING_TEXT {
        INTRODUCTORY("Hi, I'm K24-0E, but you can call me whatever you want. \n" +
                "I'm a digital nomad and treasure hunter. \n" +
                "We can help each other out. I'll show you where the treasures are hidden, and you will help me to find them. \n" +
                "It's not going to be an easy journey I can't promise you that \n" +
                "But when you find the treasure, you will be rewarded with a lot of money. \n" +
                "Because digital money is the only thing that matters in this world. \n" +
                "So, what do you say? Are you in?"),
        START("This map consists of all the treasure locations that I gathered troughout the years. \n" +
                "Some of them may be easy to find, some of them may be hard and even dangerous. \n" +
                "But I'm sure you can handle it. \n" +
                "You can select the treasure you want to find by clicking on the marker. \n" +
                "This will show you every information you need to know about the treasure."),
        END("I hope we will form a great team. \n" +
                "I'm looking forward to our next adventure. \n" +
                "See you soon traveler.C");

        private final String text;

        STARTING_TEXT(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }

    public enum DATABASE_COLLECTIONS {
        MARKERS("markers"),
        GAMES("games"),
        GAME_CHECKPOINTS("game_checkpoints"),
        FINAL_CHECKPOINTS("final_checkpoints"),
        FINISHED("finished");
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

        ColorPalette(int red, int green, int blue){
            this.color = Color.rgb(red, green, blue);
        }

        public int getColor(){
            return this.color;
        }

        public int getColor(int alpha){
            return Color.argb(alpha, Color.red(this.color), Color.green(this.color), Color.blue(this.color));
        }
    }

    public enum MENU_PAGES {
        TEST("Test", new TestFragment()),
        HOME("Home", new MapsFragment()),
        SETTINGS("Settings", new RegionFragment()),
        REGIONS("Region Select", new RegionFragment());

        private final String pageName;
        private final Fragment fragmentClass;

        MENU_PAGES(String pageName, Fragment fragmentClass) {
            this.pageName = pageName;
            this.fragmentClass = fragmentClass;
        }

        public String getPageName() {
            return pageName;
        }

        public Fragment getFragmentClass() {
            return fragmentClass;
        }
    }

}

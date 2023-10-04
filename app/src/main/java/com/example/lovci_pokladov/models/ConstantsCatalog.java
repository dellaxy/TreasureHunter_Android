package com.example.lovci_pokladov.models;

import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.example.lovci_pokladov.fragments.MapsFragment;
import com.example.lovci_pokladov.fragments.RegionFragment;
import com.example.lovci_pokladov.fragments.TestFragment;
import com.google.android.gms.maps.model.LatLng;

public class ConstantsCatalog {
    // Database constants
    public static final String DATABASE_NAME = "treasure_hunters.db";
    public static final String LEVELS_TABLE = "levels";
    public static final String LEVEL_STAGES_TABLE = "level_stages";
    public static final String PROGRESS_TABLE = "progress";

    // Activity request codes
    public static final int GAME_ACTIVITY_REQUEST_CODE = 100;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final LatLng SLOVAKIA_LOCATION = new LatLng(48.669026, 19.699024);


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
        private final Fragment fragmentClass;  // Change the type to Fragment class

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

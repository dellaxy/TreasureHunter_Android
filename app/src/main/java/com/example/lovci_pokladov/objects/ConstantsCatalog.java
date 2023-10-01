package com.example.lovci_pokladov.objects;

import android.content.res.Resources;
import android.graphics.Color;

import com.example.lovci_pokladov.MapsActivity;
import com.example.lovci_pokladov.TESTActivity;
import com.example.lovci_pokladov.menu.RegionActivity;
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

    public static final int DEFAULT_MENU_WIDTH = (int) (50 * Resources.getSystem().getDisplayMetrics().density);
    public static final int DEFAULT_MENU_HEIGHT = (int) (50 * Resources.getSystem().getDisplayMetrics().density);
    public static final int OPEN_MENU_WIDTH = (int) (200 * Resources.getSystem().getDisplayMetrics().density);
public static final int OPEN_MENU_HEIGHT = (int) (500 * Resources.getSystem().getDisplayMetrics().density);

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
        TEST("Test", TESTActivity.class),
        HOME("Home", MapsActivity.class),
        SETTINGS("Settings", MapsActivity.class),
        REGIONS("Region Select", RegionActivity.class);
        private final String pageName;
        private final Class<?> activityClass;

        MENU_PAGES(String pageName, Class<?> activityClass) {
            this.pageName = pageName;
            this.activityClass = activityClass;
        }

        public String getPageName() {
            return pageName;
        }

        public Class<?> getActivityClass() {
            return activityClass;
        }
        }

}

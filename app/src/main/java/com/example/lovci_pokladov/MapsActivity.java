package com.example.lovci_pokladov;

import static com.example.lovci_pokladov.objects.ConstantsCatalog.GAME_ACTIVITY_REQUEST_CODE;
import static com.example.lovci_pokladov.objects.ConstantsCatalog.SLOVAKIA_LOCATION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lovci_pokladov.databinding.ActivityMapsBinding;
import com.example.lovci_pokladov.entities.LocationMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;
import com.example.lovci_pokladov.objects.GeoJSONLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseHelper databaseHelper;
    private boolean isPopupOpen = false;
    private PopupWindow popupWindow;
    private int regionId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.location_info_pop, null);
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMapPreferences();
        loadDataFromDatabase();
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                float currentZoom = cameraPosition.zoom;
                float tilt = calculateTilt(currentZoom);

                CameraPosition newCameraPosition = new CameraPosition.Builder()
                        .target(cameraPosition.target)
                        .zoom(cameraPosition.zoom)
                        .tilt(tilt)
                        .bearing(cameraPosition.bearing)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
            }
        });
    }

    private void getMapPreferences() {
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);
        mMap.setMinZoomPreference(5.0f);
        SharedPreferences preferences = getSharedPreferences("MapPreferences", MODE_PRIVATE);
        regionId = preferences.getInt("selectedRegion", -1);
        moveCameraToRegion();
    }

    private void moveCameraToRegion(){
        if (regionId != -1) {
            GeoJSONLoader jsonLoader = new GeoJSONLoader(this);
            PolygonOptions regionPolygon = jsonLoader.getRegionPolygon(regionId);
            LatLng center = getCenterOfPolygon(regionPolygon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8.0f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SLOVAKIA_LOCATION, 8.0f));
        }
    }

    private LatLng getCenterOfPolygon(PolygonOptions polygon) {
        List<LatLng> points = polygon.getPoints();
        double latitude = 0.0;
        double longitude = 0.0;
        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }
        latitude /= points.size();
        longitude /= points.size();
        return new LatLng(latitude, longitude);
    }

    private float calculateTilt(float zoomLevel) {
        float minZoom = 10.0f;
        float maxZoom = 20.0f;
        float minTilt = 0.0f;
        float maxTilt = 45.0f;
        float tilt = ((zoomLevel - minZoom) / (maxZoom - minZoom)) * maxTilt + minTilt;
        tilt = Math.max(minTilt, Math.min(maxTilt, tilt));
        return tilt;
    }

    private void loadDataFromDatabase() {
        List<LocationMarker> markers, allMarkers = databaseHelper.getAllMarkers();
            if (regionId != -1) {
                GeoJSONLoader jsonLoader = new GeoJSONLoader(this);
                PolygonOptions regionPolygon = jsonLoader.getRegionPolygon(regionId);
                markers = getMarkersInsideRegion(regionPolygon, allMarkers);
            } else {
                markers = allMarkers;
            }
        if (markers != null && !markers.isEmpty()) {
            for (LocationMarker marker : markers){
                addMarker(marker);
            }
        } else {
            Toast.makeText(this, "No markers found", Toast.LENGTH_SHORT).show();
        }
    }

    private List<LocationMarker> getMarkersInsideRegion(PolygonOptions regionPolygon, List<LocationMarker> allMarkers) {
        List<LocationMarker> markers = new ArrayList<>();
        for (LocationMarker marker : allMarkers) {
            if(PolyUtil.containsLocation(marker.getPosition(), regionPolygon.getPoints(), true)){
                markers.add(marker);
            }
        }
        return markers;
    }

    private void addMarker(LocationMarker customMarker) {
        LatLng markerLocation = customMarker.getPosition();
        int markerColor = Color.rgb(Color.red(customMarker.getColor()), Color.green(customMarker.getColor()), Color.blue(customMarker.getColor()));

        CircleOptions circleOptions = new CircleOptions()
                .center(markerLocation)
                .radius(50)
                .strokeWidth(15)
                .strokeColor(Color.argb(150, Color.red(markerColor), Color.green(markerColor), Color.blue(markerColor)))
                .fillColor(Color.argb(80, Color.red(markerColor), Color.green(markerColor), Color.blue(markerColor)));

        mMap.addCircle(circleOptions);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(markerLocation)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), customMarker.getIcon(), markerColor)));
        marker.setTag(customMarker);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LocationMarker clickedMarker = (LocationMarker) marker.getTag();
                showLocationInfo(clickedMarker);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
                return true;
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, String iconName, int color) {
        int resourceId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
        Drawable vectorDrawable;
        if (resourceId != 0)
            vectorDrawable = ContextCompat.getDrawable(context, resourceId);
        else
            vectorDrawable = ContextCompat.getDrawable(context, R.drawable.marker_default);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        vectorDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showLocationInfo(LocationMarker marker) {
        if (isPopupOpen) {
            return;
        }
        TextView locationName = popupWindow.getContentView().findViewById(R.id.treasureTitle);
        TextView locationDescription = popupWindow.getContentView().findViewById(R.id.treasureDescription);
        Button acceptButton = popupWindow.getContentView().findViewById(R.id.acceptGameButton);
        ImageButton closeButton = popupWindow.getContentView().findViewById(R.id.closeButton);
        acceptButton.setTag(marker.getId());
        locationName.setText(marker.getTitle());
        locationDescription.setText(marker.getDescription());

        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
        popupWindow.getContentView().startAnimation(slideInAnimation);
        popupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.TOP, 0, 0);

        isPopupOpen = true;

        closeButton.setOnClickListener(v -> closeLocationInfo());

        acceptButton.setOnClickListener(v -> {
            int markerId = (int) v.getTag();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("markerId", markerId);
            startActivityForResult(intent, GAME_ACTIVITY_REQUEST_CODE);
            closeLocationInfo();
        });
    }

    public void closeLocationInfo() {
        if (isPopupOpen) {
            Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_top);
            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.dismiss();
                            isPopupOpen = false;
                        }
                    }, 10);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            popupWindow.getContentView().startAnimation(slideOutAnimation);
        }
    }

}
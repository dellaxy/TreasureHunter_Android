package com.example.lovci_pokladov.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.lovci_pokladov.models.ConstantsCatalog.SLOVAKIA_LOCATION;
import static com.example.lovci_pokladov.objects.Utils.isNotNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.components.LocationPopup;
import com.example.lovci_pokladov.models.LocationMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;
import com.example.lovci_pokladov.objects.GeoJSONLoader;
import com.example.lovci_pokladov.objects.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseHelper databaseHelper;
    private boolean isPopupOpen = false;
    private PopupWindow popupWindow;
    private int regionId = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.homeMap);
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(requireContext());

        LocationPopup locationPopup = new LocationPopup(requireContext());
        popupWindow = new PopupWindow(locationPopup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMapPreferences();
        loadDataFromDatabase();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int clickedMarker = (int) marker.getTag();
                openMissionPopup(clickedMarker);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
                return true;
            }
        });

        /*googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
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
        });*/
    }

    private void getMapPreferences() {
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style);
        mMap.setMapStyle(style);
        mMap.setMinZoomPreference(5.0f);
        SharedPreferences preferences = requireActivity().getSharedPreferences("MapPreferences", MODE_PRIVATE);
        regionId = preferences.getInt("selectedRegion", -1);
        moveCameraToRegion();
    }

    private void moveCameraToRegion(){
        if (regionId != -1) {
            GeoJSONLoader jsonLoader = new GeoJSONLoader(requireContext());
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
                GeoJSONLoader jsonLoader = new GeoJSONLoader(requireContext());
                PolygonOptions regionPolygon = jsonLoader.getRegionPolygon(regionId);
                markers = getMarkersInsideRegion(regionPolygon, allMarkers);
            } else {
                markers = allMarkers;
            }
        if (Utils.isNotEmpty(markers)) {
            for (LocationMarker marker : markers){
                addMarker(marker);
            }
        } else {
            Toast.makeText(requireContext(), "No markers found", Toast.LENGTH_SHORT).show();
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
                .icon(bitmapDescriptorFromVector(requireContext(), customMarker.getIcon(), markerColor)));
        marker.setTag(customMarker.getId());
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

    private void openMissionPopup(int markerId) {
        if (isPopupOpen) return;

        Button acceptButton = popupWindow.getContentView().findViewById(R.id.acceptGameButton);
        ImageButton closeButton = popupWindow.getContentView().findViewById(R.id.closeButton);

        Animation slideInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_side);
        popupWindow.getContentView().startAnimation(slideInAnimation);
        popupWindow.showAtLocation(requireActivity().getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);

        isPopupOpen = true;

        closeButton.setOnClickListener(v -> closeMissionPopup());

/*        TextView locationName = popupWindow.getContentView().findViewById(R.id.treasureTitle);
        TextView locationDescription = popupWindow.getContentView().findViewById(R.id.treasureDescription);
        Button acceptButton = popupWindow.getContentView().findViewById(R.id.acceptGameButton);
        ImageButton closeButton = popupWindow.getContentView().findViewById(R.id.closeButton);
        try (DatabaseHelper databaseHelper = new DatabaseHelper(requireContext())) {
            LocationMarker marker = databaseHelper.getMarkerById(markerId);
            locationName.setText(marker.getTitle());
            locationDescription.setText(marker.getDescription());
            acceptButton.setTag(marker.getId());
            int difficulty = databaseHelper.getMarkerDifficulty(markerId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Animation slideInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_top);
        popupWindow.getContentView().startAnimation(slideInAnimation);
        popupWindow.showAtLocation(requireActivity().getWindow().getDecorView().getRootView(), Gravity.TOP, 0, 0);

        isPopupOpen = true;

        closeButton.setOnClickListener(v -> closeMissionPopup());
        acceptButton.setOnClickListener(v -> {
            int acceptedMarkerId = (int) v.getTag();
            Intent intent = new Intent(requireContext(), GameActivity.class);
            intent.putExtra("markerId", acceptedMarkerId);
            startActivity(intent);

            closeMissionPopup();
        });*/
    }

    public void closeMissionPopup() {
        if (isPopupOpen) {
            Animation slideOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_to_side);
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

    @Override
    public void onPause() {
        super.onPause();
        closeMissionPopup();
        if (isNotNull(mMap)) {
            mMap.clear();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isNotNull(mMap)) {
            loadDataFromDatabase();
        }
    }
}
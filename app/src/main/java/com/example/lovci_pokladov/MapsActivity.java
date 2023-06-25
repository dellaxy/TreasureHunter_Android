package com.example.lovci_pokladov;

import static com.example.lovci_pokladov.objects.ConstantsCatalog.GAME_ACTIVITY_REQUEST_CODE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lovci_pokladov.databinding.ActivityMapsBinding;
import com.example.lovci_pokladov.objects.CustomMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;
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

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseHelper databaseHelper;
    private boolean isPopupOpen = false;
    private PopupWindow popupWindow;

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

    private void loadDataFromDatabase() {
        List<CustomMarker> markers = databaseHelper.getAllMarkers();
        if (markers != null && !markers.isEmpty()) {
            for (CustomMarker marker : markers) {
                addMarker(marker);
            }
        } else {
            Toast.makeText(this, "No markers found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        // nastaví sa štýl mapy (bez obchodov, reštaurácií ...) res/raw/map_style.json
        mMap.setMapStyle(style);
        //nastaví sa pozícia kamery na Slovensko
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.287788, 17.923567), 10.0f));
        googleMap.setMinZoomPreference(5.0f);

        // nastavenie zoomu a tiltu kamery podľa jej pozície
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

        loadDataFromDatabase();
    }

    // vypočíta tilt kamery podľa zoomu kamery
    private float calculateTilt(float zoomLevel) {
        float minZoom = 10.0f;
        float maxZoom = 20.0f;
        float minTilt = 0.0f;
        float maxTilt = 45.0f;
        float tilt = ((zoomLevel - minZoom) / (maxZoom - minZoom)) * maxTilt + minTilt;
        tilt = Math.max(minTilt, Math.min(maxTilt, tilt));
        return tilt;
    }
    public void addMarker(CustomMarker customMarker) {
        LatLng markerLocation = customMarker.getPosition();
        int markerColor = Color.rgb(Color.red(customMarker.getColor()), Color.green(customMarker.getColor()), Color.blue(customMarker.getColor()));
        // vytvorí sa lokácia hernej plochy
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

        // po kliknutí na marker sa zobrazí informácia o hernej ploche a mapa sa priblíži na marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                CustomMarker clickedMarker = (CustomMarker) marker.getTag();
                showLocationInfo(clickedMarker);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
                return true;
            }
        });
    }

    // custom ikonka markera
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

    private void showLocationInfo(CustomMarker marker) {
        if (isPopupOpen) {
            return;
        }
        TextView locationName = popupWindow.getContentView().findViewById(R.id.treasureTitle);
        TextView locationDescription = popupWindow.getContentView().findViewById(R.id.treasureDescription);
        Button acceptButton = popupWindow.getContentView().findViewById(R.id.acceptGameButton);
        acceptButton.setTag(marker.getId());
        locationName.setText(marker.getTitle());
        locationDescription.setText(marker.getDescription());

        popupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.TOP, 0, 0);
        isPopupOpen = true;

        // po kliknutí na tlačidlo sa získa id hry a hra sa začne
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int markerId = (int) v.getTag();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("markerId", markerId);
                startActivityForResult(intent, GAME_ACTIVITY_REQUEST_CODE);
                closeLocationInfo(v);
            }
        });
    }

    public void closeLocationInfo(View view) {
        if (isPopupOpen) {
            popupWindow.dismiss();
            isPopupOpen = false;
        }
    }




}
package com.example.lovci_pokladov.objects;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.lovci_pokladov.models.Region;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeoJSONLoader {

    private Context context;

    public GeoJSONLoader(Context context) {
        this.context = context;
    }

    public PolygonOptions getRegionPolygon(int regionId) { return parseRegionById(readRegionGeoJSON(), regionId);}
    public List<Region> getRegions() {
        return parseRegions(readRegionGeoJSON());
    }

    private JSONArray readRegionGeoJSON() {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("slovakia_regions_boundaries.geojson");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String geoJsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject geoJson = new JSONObject(geoJsonString);
            return geoJson.getJSONArray("features");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<Region> parseRegions(JSONArray regions){
        List<Region> regionsList = new ArrayList<>();
        try {
            for (int i = 0; i < regions.length(); i++) {
                JSONObject region = regions.getJSONObject(i).getJSONObject("properties");
                int regionId = region.optInt("IDN4", -1);
                String regionName = region.optString("NM4", "");
                regionsList.add(new Region(regionId, regionName, parseRegionById(regions, regionId)));
            }
            return regionsList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PolygonOptions parseRegionById(JSONArray regions, int regionId) {
        try {
            for (int i = 0; i < regions.length(); i++) {
                JSONObject feature = regions.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                int id = properties.optInt("IDN4", -1);

                if (id == regionId) {
                    JSONObject geometry = feature.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");

                    PolygonOptions polygonOptions = new PolygonOptions();
                    for (int j = 0; j < coordinates.length(); j++) {
                        JSONArray ring = coordinates.getJSONArray(j);
                        for (int k = 0; k < ring.length(); k++) {
                            JSONArray point = ring.getJSONArray(k);
                            double longitude = point.getDouble(0);
                            double latitude = point.getDouble(1);
                            polygonOptions.add(new LatLng(latitude, longitude));
                        }
                    }
                    return polygonOptions;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

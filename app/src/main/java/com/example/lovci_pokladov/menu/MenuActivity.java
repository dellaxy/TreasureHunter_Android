package com.example.lovci_pokladov.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.lovci_pokladov.MapsActivity;
import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.RegionActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void toRegionSelect(View view) {
        Intent intent = new Intent(this, RegionActivity.class);
        startActivity(intent);
    }

    public void toMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("regionId", "");
        startActivity(intent);
    }
}
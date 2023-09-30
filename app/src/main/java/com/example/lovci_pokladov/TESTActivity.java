package com.example.lovci_pokladov;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class TESTActivity extends AppCompatActivity {
    ImageButton menuButton;
    AnimatedVectorDrawable openMenuDrawable;
    AnimatedVectorDrawable closeMenuDrawable;
    boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

    }
}



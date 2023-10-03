package com.example.lovci_pokladov;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.lovci_pokladov.components.menu.MenuClickListener;

public class MainActivity extends AppCompatActivity implements MenuClickListener {
    private FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainerView = findViewById(R.id.fragmentContainer);
    }
    @Override
    public void onMenuItemClick(Fragment newFragment) {
        changeFragment(newFragment);
    }

    private void changeFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, newFragment)
                .commit();
    }

}
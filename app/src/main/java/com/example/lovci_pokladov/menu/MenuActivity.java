package com.example.lovci_pokladov.menu;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import com.example.lovci_pokladov.R;

public class MenuActivity extends AppCompatActivity implements MenuClickListener {
    private FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
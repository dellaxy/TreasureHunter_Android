package com.example.lovci_pokladov;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovci_pokladov.fragments.GameFragment;
import com.example.lovci_pokladov.service_interfaces.MenuClickListener;

public class MainActivity extends AppCompatActivity implements MenuClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onMenuItemClick(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentTag = newFragment.getClass().getSimpleName();
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (existingFragment == null) {
            transaction.add(R.id.fragment_container, newFragment, fragmentTag);
            transaction.show(newFragment);
        } else {
            transaction.show(existingFragment);
        }
        transaction.setReorderingAllowed(true)
        .addToBackStack(null)
        .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof GameFragment) {
            showExitConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showExitConfirmationDialog() {
    }


}
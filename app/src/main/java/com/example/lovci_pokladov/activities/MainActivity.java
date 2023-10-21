package com.example.lovci_pokladov.activities;

import static com.example.lovci_pokladov.models.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.fragments.MapsFragment;
import com.example.lovci_pokladov.objects.Utils;
import com.example.lovci_pokladov.services.MenuClickListener;

public class MainActivity extends AppCompatActivity implements MenuClickListener {
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new MapsFragment(), "MapsFragment")
                .addToBackStack(null)
                .commit();

    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMenuItemClick(Fragment selectedFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentTag = selectedFragment.getClass().getSimpleName();
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragmentTag);
        currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (!Utils.isNotNull(currentFragment) || !currentFragment.getTag().equals(fragmentTag)) {
            if (Utils.isNotNull(existingFragment)) {
                transaction.replace(R.id.fragment_container, existingFragment, fragmentTag);
            } else {
                transaction.replace(R.id.fragment_container, selectedFragment, fragmentTag);
            }
            transaction.addToBackStack(null)
                .commit();
        }
    }

    private void showExitConfirmationDialog() {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
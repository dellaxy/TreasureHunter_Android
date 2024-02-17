package com.example.lovci_pokladov.activities;

import static com.example.lovci_pokladov.entities.ConstantsCatalog.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.components.TutorialOverlay;
import com.example.lovci_pokladov.fragments.MapsFragment;
import com.example.lovci_pokladov.objects.Utils;
import com.example.lovci_pokladov.services.MenuClickListener;
import com.example.lovci_pokladov.services.PreferencesManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MenuClickListener {
    private FragmentManager fragmentManager;
    private PreferencesManager profilePreferences;

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

    private boolean isTutorialSeen() {
        profilePreferences = PreferencesManager.getInstance(this);
        return profilePreferences.isTutorialSeen();
    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean permissionsGranted(){
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onMenuItemClick(Fragment selectedFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentTag = selectedFragment.getClass().getSimpleName();
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragmentTag);
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (!Utils.isNotNull(currentFragment) || !Objects.equals(currentFragment.getTag(), fragmentTag)) {
            if (Utils.isNotNull(existingFragment)) {
                transaction.replace(R.id.fragment_container, existingFragment, fragmentTag);
            } else {
                transaction.replace(R.id.fragment_container, selectedFragment, fragmentTag);
            }
            transaction.addToBackStack(null)
                .commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissionsGranted()) {
                if (!isTutorialSeen()) {
                    TutorialOverlay tutorialOverlay = new TutorialOverlay(this);
                    addContentView(tutorialOverlay, new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

}
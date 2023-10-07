package com.example.lovci_pokladov;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovci_pokladov.fragments.GameFragment;
import com.example.lovci_pokladov.fragments.MapsFragment;
import com.example.lovci_pokladov.objects.Utils;
import com.example.lovci_pokladov.service_interfaces.MenuClickListener;

public class MainActivity extends AppCompatActivity implements MenuClickListener {
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new MapsFragment(), "MapsFragment")
                .addToBackStack(null)
                .commit();
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



    @Override
    public void onBackPressed() {
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof GameFragment) {
            showExitConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showExitConfirmationDialog() {
    }


}
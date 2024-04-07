package com.example.city_tours.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.city_tours.R;
import com.example.city_tours.entities.ConstantsCatalog;
import com.example.city_tours.entities.ResourceManager;
import com.example.city_tours.services.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class SettingsFragment extends Fragment {
    private Spinner languageSpinner;
    private PreferencesManager preferencesManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_settings, container, false);

        languageSpinner = view.findViewById(R.id.languageSpinner);
        preferencesManager = PreferencesManager.getInstance(requireActivity());

        String deviceLanguageKey = ResourceManager.getString(R.string.device);


        TreeMap<String, String> sortedAppLanguages = new TreeMap<>(ConstantsCatalog.appLanguages);
        List<String> languagesList = new ArrayList<>(sortedAppLanguages.keySet());
        languagesList.add(0, deviceLanguageKey);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, languagesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);


        Button button = view.findViewById(R.id.selectLanguageButton);
        button.setOnClickListener(v -> {
            int selectedLanguagePosition = languageSpinner.getSelectedItemPosition();
            String selectedLanguageKey = languageSpinner.getItemAtPosition(selectedLanguagePosition).toString();
            String selectedLanguageValue = sortedAppLanguages.get(selectedLanguageKey);

            if (selectedLanguageKey.equals(deviceLanguageKey)) {
                setAppLanguage("");
            } else {
                setAppLanguage(selectedLanguageValue);
            }
        });

        Button finishedGamesMapButton = view.findViewById(R.id.finishedGamesMapButton);
        finishedGamesMapButton.setOnClickListener(this::openMapFinishedGames);

        return view;
    }


    public void openMapFinishedGames(View view) {
        MapsFragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showFinishedGames", true);
        mapsFragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapsFragment, "MapsFragment")
                .addToBackStack(null)
                .commit();
    }

    private void setAppLanguage(String languageCode) {
        preferencesManager.setLanguageKey(languageCode);
        requireActivity().recreate();
    }
}

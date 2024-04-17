package com.example.city_tours.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.city_tours.R;
import com.example.city_tours.components.AchievementBadge;
import com.example.city_tours.entities.Achievement;
import com.example.city_tours.objects.DatabaseHelper;

import java.util.List;

public class AchievementsFragment extends Fragment {

    private List<Achievement> lockedAchievements, unlockedAchievements;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_achievements, container, false);

        LinearLayout achievementsLayout = view.findViewById(R.id.achievementsLayout);

        try (DatabaseHelper databaseHelper = new DatabaseHelper(requireContext())) {
            lockedAchievements = databaseHelper.getLockedAchievements();
            unlockedAchievements = databaseHelper.getPlayerAchievements();

            for (Achievement achievement : unlockedAchievements) {
                AchievementBadge badge = new AchievementBadge(requireContext(), achievement.getTitle(), achievement.getTourName(), achievement.getImage(), achievement.getRating());
                achievementsLayout.addView(badge);
            }
            for (Achievement lockedAchievement : lockedAchievements) {
                AchievementBadge badge = new AchievementBadge(requireContext(), lockedAchievement.getTitle(), lockedAchievement.getTourName(), lockedAchievement.getImage());
                achievementsLayout.addView(badge);
            }

        } catch (Exception e) {
            Toast.makeText(requireContext(), "" + e, Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}



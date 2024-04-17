package com.example.wander_wise.components;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.wander_wise.R;
import com.example.wander_wise.entities.ResourceManager;

public class AchievementBadge extends ConstraintLayout {

    private TextView badgeTitle, badgeDescription;
    private RatingBar badgeRating;
    private ImageView badgeImage;
    private String title, tourName, image;
    private int rating;

    public AchievementBadge(@NonNull Context context, String title, String tourName, String image, int rating) {
        super(context);
        this.title = title;
        this.tourName = tourName;
        this.image = image;
        this.rating = rating;
        init();
        badgeDescription.setText(ResourceManager.getString(R.string.reward_text) + " " + tourName);

    }

    public AchievementBadge(@NonNull Context context, String title, String tourName, String image) {
        super(context);
        this.title = title;
        this.tourName = tourName;
        this.image = image;
        this.rating = 0;
        init();
        badgeDescription.setText(ResourceManager.getString(R.string.reward_text) + " " + tourName);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        badgeImage.setColorFilter(cf);

    }

    private void init() {
        inflate(getContext(), R.layout.layout_achievement_badge, this);
        initViews();
        badgeTitle.setText(title);
        badgeRating.setRating(rating);
        badgeImage.setImageResource(getResources().getIdentifier(image, "drawable", getContext().getPackageName()));
    }


    private void initViews() {
        badgeTitle = findViewById(R.id.badgeTitle);
        badgeDescription = findViewById(R.id.badgeDescription);
        badgeRating = findViewById(R.id.badgeRating);
        badgeImage = findViewById(R.id.badgeImage);
    }
}

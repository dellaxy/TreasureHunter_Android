package com.example.city_tours.components;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.city_tours.R;

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
    }

    public AchievementBadge(@NonNull Context context, String title, String tourName, String image) {
        super(context);
        this.title = title;
        this.tourName = tourName;
        this.image = image;
        this.rating = 0;
        init();

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        badgeImage.setColorFilter(cf);

    }

    private void init() {
        inflate(getContext(), R.layout.layout_achievement_badge, this);
        initViews();
        badgeTitle.setText(title);
        badgeDescription.setText("reward for completing the " + tourName + " tour!");
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

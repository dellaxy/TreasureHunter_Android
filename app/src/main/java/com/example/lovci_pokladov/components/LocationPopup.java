package com.example.lovci_pokladov.components;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.activities.GameActivity;
import com.example.lovci_pokladov.models.LocationMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;

public class LocationPopup extends LinearLayout {
    Context context;
    PopupWindow popupWindow;
    boolean isPopupOpen = false;


    public LocationPopup(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(getContext()).inflate(R.layout.layout_mission_popup, this, true);

        popupWindow = new PopupWindow(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);

        LinearLayout actionButtonsGroup = findViewById(R.id.actionButtonsGroup);
        setButtonClickListener(actionButtonsGroup);
    }

    private void setButtonClickListener(LinearLayout actionButtonsGroup) {
        ImageButton closeButton = findViewById(R.id.closeButton);
        RoundedButton acceptButton = findViewById(R.id.acceptGameButton);
        closeButton.setOnClickListener(v -> closePopup());
        acceptButton.setOnClickListener(v -> acceptMission(v));

        TextView missionDescription = findViewById(R.id.missionDescription);
        LinearLayout statsLayout = findViewById(R.id.missionStats);
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.infoActionButton) {
                    missionDescription.setVisibility(View.VISIBLE);
                    statsLayout.setVisibility(View.GONE);
                } else if (v.getId() == R.id.statsActionButton) {
                    missionDescription.setVisibility(View.GONE);
                    statsLayout.setVisibility(View.VISIBLE);
                } else if (v.getId() == R.id.navigationActionButton) {

                }
            }
        };

        for (int i = 0; i < actionButtonsGroup.getChildCount(); i++) {
            View button = actionButtonsGroup.getChildAt(i);
            button.setOnClickListener(buttonClickListener);
        }
    }

    protected void acceptMission(View v) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("markerId", 2);
        context.startActivity(intent);
    }

    public void openPopup(int missionId) {
        if (!isPopupOpen) {
            getMissionData(missionId);
            Animation slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_side);
            popupWindow.getContentView().startAnimation(slideInAnimation);
            popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);

            isPopupOpen = true;
        }
    }

    public void closePopup() {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_side);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(() -> {
                    popupWindow.dismiss();
                    isPopupOpen = false;
                }, 10);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        popupWindow.getContentView().startAnimation(slideOutAnimation);
    }

    private void getMissionData(int missionId) {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(context)) {
            LocationMarker marker = databaseHelper.getMarkerById(missionId);
            int missionDifficulty = databaseHelper.getMarkerDifficulty(missionId);
            setData(marker.getTitle(), marker.getDescription(), "Nové Zámky", "Nitriansky kraj", missionDifficulty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(String missionTitle, String missionDescription, String missionLocation, String missionRegion, int missionDifficulty) {
        TextView title = findViewById(R.id.missionTitle),
                description = findViewById(R.id.missionDescription),
                location = findViewById(R.id.missionLocation),
                region = findViewById(R.id.missionRegion);

        title.setText(missionTitle);
        description.setText(missionDescription);
        location.setText(missionLocation);
        region.setText(missionRegion);
    }
}

package com.example.lovci_pokladov.components;

import static com.example.lovci_pokladov.objects.Utils.isNotEmpty;
import static com.example.lovci_pokladov.objects.Utils.isNotNull;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.activities.GameActivity;
import com.example.lovci_pokladov.entities.BaseModal;
import com.example.lovci_pokladov.entities.LocationMarker;
import com.example.lovci_pokladov.objects.DatabaseHelper;

import java.util.List;

public class MissionModal extends BaseModal {
    private int missionId = -1;


    public MissionModal(Context context) {
        super(context, R.layout.layout_mission_modal);
        LinearLayout actionButtonsGroup = modalView.findViewById(R.id.actionButtonsGroup);
        setButtonClickListener(actionButtonsGroup);
    }

   private void setButtonClickListener(LinearLayout actionButtonsGroup) {
        ImageButton closeButton = modalView.findViewById(R.id.closeButton);
        RoundedButton acceptButton = modalView.findViewById(R.id.acceptGameButton);
        closeButton.setOnClickListener(v -> closePopup());
        acceptButton.setOnClickListener(v -> acceptMission());

        TextView missionDescription = modalView.findViewById(R.id.missionDescription);
        LinearLayout statsLayout = modalView.findViewById(R.id.missionStats);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.infoActionButton) {
                    missionDescription.setVisibility(VISIBLE);
                    statsLayout.setVisibility(GONE);
                } else if (v.getId() == R.id.statsActionButton) {
                    missionDescription.setVisibility(GONE);
                    statsLayout.setVisibility(VISIBLE);
                } else if (v.getId() == R.id.navigationActionButton) {

                }
            }
        };

        for (int i = 0; i < actionButtonsGroup.getChildCount(); i++) {
            View button = actionButtonsGroup.getChildAt(i);
            button.setOnClickListener(buttonClickListener);
        }
    }

    protected void acceptMission() {
        if (missionId != -1) {
            Intent intent = new Intent(context, GameActivity.class);
            intent.putExtra("markerId", missionId);
            context.startActivity(intent);
        }
    }

    private void getMissionData(int missionId) {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(context)) {
            Geocoder geocoder = new Geocoder(context);

            LocationMarker marker = databaseHelper.getMarkerById(missionId);
            int missionDifficulty = databaseHelper.getMarkerDifficulty(missionId);
            List<Address> addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            String location = "", region = "", country = "";

            if (isNotEmpty(addressList)){
                location = isNotNull(addressList.get(0).getLocality()) ? addressList.get(0).getLocality() : addressList.get(0).getSubAdminArea();
                region = addressList.get(0).getAdminArea();
                country = addressList.get(0).getCountryName();
            }

            setData(marker.getTitle(), marker.getDescription(), location, region, country ,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(String missionTitle, String missionDescription, String missionLocation, String missionRegion, String missionCountry ,int missionDifficulty) {
        TextView title = modalView.findViewById(R.id.missionTitle),
                description = modalView.findViewById(R.id.missionDescription),
                location = modalView.findViewById(R.id.missionLocation),
                region = modalView.findViewById(R.id.missionRegion),
                country = modalView.findViewById(R.id.missionCountry);

        title.setText(missionTitle);
        description.setText(missionDescription);
        location.setText(missionLocation);
        region.setText(missionRegion);
        country.setText(missionCountry);
    }

    public void openPopup(int missionId){
        this.missionId = missionId;
        openPopup();
    }


    @Override
    public void beforeModalOpen() {
        getMissionData(missionId);
    }

    @Override
    public void beforeModalClose() {

    }
}

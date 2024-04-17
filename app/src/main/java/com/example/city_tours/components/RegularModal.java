package com.example.city_tours.components;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.city_tours.R;

public abstract class RegularModal extends BaseModal {

    public RegularModal(Context context) {
        super(context, R.layout.layout_regular_modal);
        setButtonClickListener();
    }

    private void setButtonClickListener() {
        RoundedButton closeButton = modalView.findViewById(R.id.regularModalCloseButton);
        closeButton.setOnClickListener(v -> closePopup());

        RoundedButton acceptButton = modalView.findViewById(R.id.regularModalAcceptButton);
        acceptButton.setOnClickListener(v -> {
            acceptButtonClicked();
            //closePopup();
        });
    }

    public void setModalText(String text) {
        TextView modalText = modalView.findViewById(R.id.regularModalText);
        modalText.setText(text);
    }

    public void setModalTextColour(int colour) {
        TextView modalText = modalView.findViewById(R.id.regularModalText);
        modalText.setTextColor(colour);
    }

    public void setModalLocation(int marginTop) {
        CardView modalCard = modalView.findViewById(R.id.regularModalCard);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) modalCard.getLayoutParams();
        float density = context.getResources().getDisplayMetrics().density;
        marginTop = Math.round(marginTop * density);
        params.setMargins(0, marginTop, 0, 0);
        modalCard.setLayoutParams(params);
    }


    public abstract void acceptButtonClicked();

    @Override
    public void beforeModalOpen() {
    }

    @Override
    public void beforeModalClose() {
    }
}

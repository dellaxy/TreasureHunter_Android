package com.example.lovci_pokladov.components;

import android.content.Context;
import android.widget.TextView;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.entities.BaseModal;

public abstract class RegularModal extends BaseModal {

    public RegularModal(Context context) {
        super(context, R.layout.layout_regular_modal);
        setButtonClickListener();
    }

    private void setButtonClickListener() {
        RoundedButton acceptButton = modalView.findViewById(R.id.regularModalAcceptButton);
        acceptButton.setOnClickListener(v -> {
            acceptButtonClicked();
            closePopup();
        });
    }

    public void setModalText(String text) {
        TextView modalText = modalView.findViewById(R.id.regularModalText);
        modalText.setText(text);
    }

    public abstract void acceptButtonClicked();

    @Override
    public void beforeModalOpen() {}

    @Override
    public void beforeModalClose() {}
}

package com.example.lovci_pokladov.components;

import android.content.Context;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.models.BaseModal;

public class RegularModal extends BaseModal {

    public RegularModal(Context context) {
        super(context, R.layout.layout_regular_modal);
    }
    @Override
    public void beforeModalOpen() {}

    @Override
    public void beforeModalClose() {}
}

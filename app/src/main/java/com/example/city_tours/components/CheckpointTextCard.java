package com.example.city_tours.components;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.city_tours.R;

public class CheckpointTextCard extends CardView {
    private TextView checkpointText;

    public CheckpointTextCard(@NonNull Context context, String text) {
        super(context);
        inflate(getContext(), R.layout.layout_checkpoint_card, this);
        initViews();

        checkpointText.setText(text);
    }

    private void initViews() {
        checkpointText = findViewById(R.id.checkpointDescription);
    }
}

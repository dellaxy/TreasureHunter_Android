package com.example.lovci_pokladov.components;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.lovci_pokladov.R;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CheckpointTextCard extends CardView {
    private TextView checkpointText, checkpointTime;

    public CheckpointTextCard(@NonNull Context context, String text) {
        super(context);
        inflate(getContext(), R.layout.layout_checkpoint_card, this);
        initViews();

        LocalTime currentTime = LocalTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = currentTime.format(formatter);
        checkpointText.setText(text);
        checkpointTime.setText(formattedTime);
    }

    private void initViews() {
        checkpointText = findViewById(R.id.checkpointDescription);
        checkpointTime = findViewById(R.id.checkpointTime);
    }
}

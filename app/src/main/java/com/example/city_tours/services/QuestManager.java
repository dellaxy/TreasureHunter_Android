package com.example.city_tours.services;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.city_tours.R;
import com.example.city_tours.components.RegularModal;
import com.example.city_tours.entities.puzzles.Quest;

import java.text.Normalizer;
import java.util.regex.Pattern;

public abstract class QuestManager {
    private Context context;
    private PreferencesManager preferencesManager;
    private RegularModal hintModal;
    private boolean isWrongAnswerShown = false;
    private final LinearLayout bottomInfoLayout, questLayout;
    private TextView questText, hintText;
    private Button acceptButton, hintButton;
    private ImageButton closeButton;
    private Quest quest;

    public QuestManager(Context context, LinearLayout bottomInfoLayout, LinearLayout questLayout, TextView questText, TextView hintText, Button acceptButton, Button hintButton, ImageButton closeButton) {
        this.context = context;
        this.preferencesManager = PreferencesManager.getInstance(context);
        this.bottomInfoLayout = bottomInfoLayout;
        this.questLayout = questLayout;
        this.questText = questText;
        this.hintText = hintText;
        this.acceptButton = acceptButton;
        this.hintButton = hintButton;
        this.closeButton = closeButton;
    }

    public void initializeQuestManager(Quest quest) {
        this.quest = quest;
        questText.setText(quest.getQuestion());
        initializeButtons();
        hintModal = new RegularModal(context) {
            @Override
            public void acceptButtonClicked() {
                int coins = preferencesManager.getPlayerCoins();
                if (coins - 100 >= 0) {
                    preferencesManager.setPlayerCoins(coins - 100);
                    hintText.setText(quest.getHint());
                    closePopup();
                    hintButton.setEnabled(false);
                } else {
                    setModalTextColour(Color.RED);
                    setModalText("You don't have enough coins to buy a hint!");
                }
            }
        };
        hintModal.setModalText("Do you want to buy a hint for 100 coins?");
        hintModal.setModalLocation(450);
    }

    private void initializeButtons() {
        acceptButton.setOnClickListener(v -> {
            String answer = ((EditText) questLayout.findViewById(R.id.answer_input)).getText().toString();
            if (isAnswerCorrect(answer)) {
                correctAnswerEntered();
                toggleQuestModal(false);
            } else {
                if (!isWrongAnswerShown) {
                    isWrongAnswerShown = true;
                    String previousText = hintText.getText().toString();
                    hintText.setText("Wrong answer! Try again.");
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        hintText.setText(previousText);
                        isWrongAnswerShown = false;
                    }, 5000);
                }
            }
        });

        closeButton.setOnClickListener(v -> {
            abandonQuest();
            toggleQuestModal(false);
        });

        hintButton.setOnClickListener(v -> {
            hintModal.openPopup();
        });
    }


    private boolean isAnswerCorrect(String answer) {
        String normalizedAnswer = normalizeString(answer);
        String normalizedCorrectAnswer = normalizeString(quest.getAnswer());
        return normalizedCorrectAnswer.equalsIgnoreCase(normalizedAnswer);
    }

    private String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public void toggleQuestModal(boolean changeViewToQuest) {
        if (changeViewToQuest) {
            bottomInfoLayout.setVisibility(View.GONE);
            questLayout.setVisibility(View.VISIBLE);
        } else {
            bottomInfoLayout.setVisibility(View.VISIBLE);
            questLayout.setVisibility(View.GONE);
        }
    }

    public abstract void correctAnswerEntered();

    public abstract void abandonQuest();


}

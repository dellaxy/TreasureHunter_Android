package com.example.wander_wise.services;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wander_wise.R;
import com.example.wander_wise.components.RegularModal;
import com.example.wander_wise.entities.ResourceManager;
import com.example.wander_wise.entities.puzzles.Quest;
import com.example.wander_wise.objects.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.Normalizer;
import java.util.ArrayList;
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
    private ChipGroup answerChipGroup;
    private EditText answerInput;
    private boolean hintUsed = false;


    public QuestManager(Context context, LinearLayout bottomInfoLayout, LinearLayout questLayout) {
        this.context = context;
        this.preferencesManager = PreferencesManager.getInstance(context);
        this.bottomInfoLayout = bottomInfoLayout;
        this.questLayout = questLayout;
        this.questText = questLayout.findViewById(R.id.question_text);
        this.hintText = questLayout.findViewById(R.id.hint_text);
        this.acceptButton = questLayout.findViewById(R.id.submit_button);
        this.hintButton = questLayout.findViewById(R.id.hint_button);
        this.closeButton = questLayout.findViewById(R.id.close_button);
        this.answerChipGroup = questLayout.findViewById(R.id.answer_chip_group);
        this.answerInput = questLayout.findViewById(R.id.answer_input);
    }

    public void initializeQuestManager(Quest quest) {
        this.quest = quest;
        questText.setText(quest.getQuestion());
        initializeAnswerSelection();
        initializeButtons();
        hintModal = new RegularModal(context) {
            @Override
            public void acceptButtonClicked() {
                int coins = preferencesManager.getPlayerCoins();
                if (coins - 100 >= 0) {
                    preferencesManager.setPlayerCoins(coins - 100);
                    hintText.setText(quest.getHint());
                    closePopup();
                    hintUsed = true;
                    hintButton.setEnabled(false);
                } else {
                    setModalTextColour(Color.RED);
                    setModalText(ResourceManager.getString(R.string.hintNotEnough));
                }
            }
        };
        hintModal.setModalText(ResourceManager.getString(R.string.hintQuestion));
        hintModal.setModalLocation(450);
    }

    public void initializeAnswerSelection() {
        ArrayList<String> answers = quest.getAnswers();
        if (answers.size() > 1) {
            answerInput.setVisibility(View.GONE);
            answerChipGroup.setVisibility(View.VISIBLE);
            for (String answer : quest.getAnswers()) {
                Chip chip = createChip();
                chip.setText(answer);
                answerChipGroup.addView(chip);
            }
            acceptButton.setOnClickListener(v -> {
                String answer = answerChipGroup.getCheckedChipId() != View.NO_ID ?
                        ((Chip) answerChipGroup.findViewById(answerChipGroup.getCheckedChipId())).getText().toString() : "";
                if (Utils.isNotNull(answer) && isAnswerCorrect(answer)) {
                    correctAnswerEntered();
                    clearLayout();
                    toggleQuestModal(false);
                } else {
                    setWrongAnswerShown();
                }
            });
        } else {
            answerInput.setVisibility(View.VISIBLE);
            answerChipGroup.setVisibility(View.GONE);
            acceptButton.setOnClickListener(v -> {
                String answer = answerInput.getText().toString();
                if (Utils.isNotNull(answer) && isAnswerCorrect(answer)) {
                    correctAnswerEntered();
                    clearLayout();
                    toggleQuestModal(false);
                } else {
                    setWrongAnswerShown();
                }
            });
        }
    }

    public boolean wasHintUsed() {
        return hintUsed;
    }

    private Chip createChip() {
        Chip chip = new Chip(context);
        chip.setCheckable(true);
        chip.setCheckedIconTintResource(R.color.primary);
        chip.setChipBackgroundColorResource(R.color.chip_background);
        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chip.setChipStartPadding(20);
        chip.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 160));
        chip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        return chip;
    }

    private void initializeButtons() {
        closeButton.setOnClickListener(v -> {
            abandonQuest();
            clearLayout();
            toggleQuestModal(false);
        });

        hintButton.setOnClickListener(v -> {
            hintModal.openPopup();
        });
    }

    private void setWrongAnswerShown() {
        if (!isWrongAnswerShown) {
            isWrongAnswerShown = true;
            String previousText = hintText.getText().toString();
            hintText.setText(ResourceManager.getString(R.string.wrongAnswer));
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                hintText.setText(previousText);
                isWrongAnswerShown = false;
            }, 5000);
        }
    }


    private boolean isAnswerCorrect(String answer) {
        String normalizedAnswer = normalizeString(answer);
        String normalizedCorrectAnswer = normalizeString(quest.getCorrectAnswer());
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

    public void clearLayout() {
        questText.setText("");
        hintText.setText("");
        answerInput.setText("");
        answerChipGroup.removeAllViews();
    }

    public abstract void correctAnswerEntered();

    public abstract void abandonQuest();

}

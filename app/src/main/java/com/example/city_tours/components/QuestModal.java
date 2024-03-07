package com.example.city_tours.components;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.city_tours.R;
import com.example.city_tours.entities.Quest;
import com.example.city_tours.services.PreferencesManager;

import java.text.Normalizer;
import java.util.regex.Pattern;

public abstract class QuestModal extends BaseModal {

    private Quest quest;
    private boolean isWrongAnswerShown = false;
    private PreferencesManager preferencesManager;

    public QuestModal(Context context, Quest quest) {
        super(context, R.layout.layout_quest_modal);
        this.quest = quest;
        onInit();
    }


    private void onInit() {
        preferencesManager = PreferencesManager.getInstance(context);
        TextView questionText = modalView.findViewById(R.id.question_text);
        TextView hintText = modalView.findViewById(R.id.hint_text);
        Button acceptButton = modalView.findViewById(R.id.submit_button);
        Button hintButton = modalView.findViewById(R.id.hint_button);
        RegularModal hintModal = new RegularModal(context) {
            @Override
            public void acceptButtonClicked() {
                int coins = preferencesManager.getPlayerCoins();
                if (coins - 100 >= 0) {
                    preferencesManager.setPlayerCoins(coins - 100);
                    hintText.setText(quest.getHint());
                    closePopup();
                } else {
                    setModalTextColour(Color.RED);
                    setModalText("You don't have enough coins to buy a hint!");
                }
            }
        };
        hintModal.setModalText("Do you want to buy a hint for 100 coins?");
        hintModal.setModalLocation(450);

        questionText.setText(quest.getQuestion());

        acceptButton.setOnClickListener(v -> {
            String answer = ((EditText) modalView.findViewById(R.id.answer_input)).getText().toString();
            if (isAnswerCorrect(answer)) {
                correctAnswerEntered();
                closePopup();
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

    public abstract void correctAnswerEntered();

    @Override
    public void beforeModalOpen() {

    }

    @Override
    public void beforeModalClose() {

    }
}

package com.example.city_tours.components;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.city_tours.R;
import com.example.city_tours.entities.Quest;

import java.text.Normalizer;
import java.util.regex.Pattern;

public abstract class QuestModal extends BaseModal {

    private Quest quest;
    private boolean isWrongAnswerShown = false;

    public QuestModal(Context context, Quest quest) {
        super(context, R.layout.layout_quest_modal);
        this.quest = quest;
        onInit();
    }


    private void onInit() {
        TextView questionText = modalView.findViewById(R.id.question_text);
        TextView hintText = modalView.findViewById(R.id.hint_text);
        Button acceptButton = modalView.findViewById(R.id.submit_button);
        Button hintButton = modalView.findViewById(R.id.hint_button);

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
            hintText.setText(quest.getHint());
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

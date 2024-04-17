package com.example.wander_wise.entities.puzzles;

import static com.example.wander_wise.entities.ConstantsCatalog.QUESTION;

import java.util.ArrayList;

public class Quest extends Puzzle {
    private final String question, hint;
    private String correctAnswer;
    private ArrayList<String> answers;

    public Quest(String text, String question, String hint) {
        super(text);
        this.question = question;
        this.hint = hint;
    }

    public String getQuestion() {
        return question;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public String getAnswer() {
        return correctAnswer;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String getPuzzleType() {
        return QUESTION;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

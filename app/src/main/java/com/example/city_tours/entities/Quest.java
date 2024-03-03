package com.example.city_tours.entities;

public class Quest {
    private final String question, answer, hint, text;

    public Quest(String question, String answer, String hint, String text) {
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.text = text;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getHint() {
        return hint;
    }

    public String getText() {
        return text;
    }
}

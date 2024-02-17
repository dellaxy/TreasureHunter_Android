package com.example.lovci_pokladov.components;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.entities.ConstantsCatalog.STARTING_TEXT;
import com.example.lovci_pokladov.services.PreferencesManager;
import com.example.lovci_pokladov.services.TextToSpeechService;


public class TutorialOverlay extends RelativeLayout {

    private TextView tutorialText;
    private int currentTextIndex = 0;
    private STARTING_TEXT[] texts = STARTING_TEXT.values();
    private TextToSpeechService textToSpeechService;
    private PreferencesManager preferencesManager;

    public TutorialOverlay(Context context) {
        super(context);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.layout_tutorial_window, this);
        texts = STARTING_TEXT.values();
        textToSpeechService = new TextToSpeechService(this.getContext());
        tutorialText = findViewById(R.id.tutorialTextView);

        showNextText();

        setOnClickListener(v -> {
            showNextText();
        });
    }

    private void showNextText(){
        if(currentTextIndex < texts.length){
            String text = texts[currentTextIndex].getText();
            textToSpeechService.synthesizeText(text);
            setTutorialText(text);
            currentTextIndex++;
        } else {
            textToSpeechService.cancel();
            setVisibility(GONE);
            setTutorialAsSeen();
        }
    }

    private void setTutorialAsSeen() {
        preferencesManager = PreferencesManager.getInstance(getContext());
        preferencesManager.setTutorialSeen();
    }


    public void setTutorialText(String text){
        tutorialText.setText(text);
    }


}

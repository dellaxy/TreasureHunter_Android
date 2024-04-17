package com.example.wander_wise.components;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wander_wise.R;
import com.example.wander_wise.services.PreferencesManager;
import com.example.wander_wise.services.tts_services.TextToSpeechService;


public class TutorialOverlay extends RelativeLayout {

    public enum STARTING_TEXT {
        INTRODUCTION("Welcome to Wanderwise, your ultimate guide to immersive tours around the world!\n" +
                "I'm your virtual tour guide, capable of speaking any language and adopting any voice.\n" +
                "Whether you're exploring historic landmarks or uncovering hidden treasures, I'll be your companion on this journey.\n" +
                "Let's get started with a quick tutorial to familiarize you with the app."),
        TOUR_SELECTION("To begin, all you have to do is to select a tour from the map. All available tours are visible, and you can filter them by regions by clicking on the menu in the top left corner.\n" +
                "Once you've chosen a tour, you'll see all the details about it. If you're unsure how to get to the starting point, you can toggle the navigation option, which will guide you there."),

        STARTING_ZONE("When you arrive at the starting zone, make sure you are standing inside it. Once you do, you'll see a popup window.\n" +
                "Simply click on the 'Accept' option, and your tour will begin from that point onward."),

        TOUR_START("Once your tour begins, you'll be guided by voice from zone to zone, receiving detailed information about each point of interest along the way.\n" +
                "You won't need to worry about carrying your phone around, feel free to put it in your pocket as you explore."),

        TOUR("If you happen to miss any information, don't worry! You'll have access to all the details in text form on the tour page. Simply open your phone at any time to review.\n" +
                "Additionally, a mini-map will be available in case you need to check the remaining checkpoints."),

        INTERACTION("To make the tours more engaging, there will be occasional quizzes and mini-games to complete before reaching your destination.\n" +
                "By successfully completing these interactive elements, you'll earn points and a badge based on your performance.\n" +
                " Achieve all the interactive challenges for the best badge, or receive a participation badge if you choose not to participate or encounter difficulties along the way.\n" +
                "So what are you waiting for? Let's get started!");


        private final String text;

        STARTING_TEXT(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }


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

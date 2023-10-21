package com.example.lovci_pokladov.services;

import android.os.AsyncTask;

import com.example.lovci_pokladov.BuildConfig;

import darren.googlecloudtts.GoogleCloudTTS;
import darren.googlecloudtts.GoogleCloudTTSFactory;
import darren.googlecloudtts.model.VoicesList;
import darren.googlecloudtts.parameter.AudioConfig;
import darren.googlecloudtts.parameter.AudioEncoding;
import darren.googlecloudtts.parameter.VoiceSelectionParams;

public class TextToSpeechService extends AsyncTask<String, Void, Void> {
    GoogleCloudTTS googleCloudTTS;
    VoicesList voicesList;
    String languageCode, voiceName;
    public TextToSpeechService() {
        googleCloudTTS = GoogleCloudTTSFactory.create(BuildConfig.TTS_API_KEY);
    }

    @Override
    protected Void doInBackground(String... strings) {
        voicesList = googleCloudTTS.load();
        languageCode = voicesList.getLanguageCodes()[14];
        voiceName = voicesList.getVoiceNames(languageCode)[6];
        googleCloudTTS.setVoiceSelectionParams(new VoiceSelectionParams(languageCode, voiceName))
                .setAudioConfig(new AudioConfig(AudioEncoding.MP3, 1f , 0f));
        googleCloudTTS.start(strings[0]);
        return null;
    }

    private void getVoiceName(VoicesList voicesList, String languageCode) {
        String[] voiceNames = voicesList.getVoiceNames(languageCode);
        for (String voiceName : voiceNames) {

        }
    }
}

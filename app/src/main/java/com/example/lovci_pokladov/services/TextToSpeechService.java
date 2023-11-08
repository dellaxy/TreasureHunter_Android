package com.example.lovci_pokladov.services;

import static com.example.lovci_pokladov.objects.Utils.isNull;

import android.os.Handler;
import android.os.Looper;

import com.example.lovci_pokladov.BuildConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import darren.googlecloudtts.GoogleCloudTTS;
import darren.googlecloudtts.GoogleCloudTTSFactory;
import darren.googlecloudtts.model.VoicesList;
import darren.googlecloudtts.parameter.AudioConfig;
import darren.googlecloudtts.parameter.AudioEncoding;
import darren.googlecloudtts.parameter.VoiceSelectionParams;

public class TextToSpeechService{
    GoogleCloudTTS googleCloudTTS;
    VoicesList voicesList;
    String languageCode, voiceName;
    ExecutorService executorService;
    Handler handler;
    public TextToSpeechService() {
        googleCloudTTS = GoogleCloudTTSFactory.create(BuildConfig.TTS_API_KEY);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        startService();
    }

    protected void startService(){
        executorService.execute(() -> {
            if(isNull(voicesList)){voicesList = googleCloudTTS.load();}
            languageCode = voicesList.getLanguageCodes()[14];
            voiceName = voicesList.getVoiceNames(languageCode)[5];
            googleCloudTTS.setVoiceSelectionParams(new VoiceSelectionParams(languageCode, voiceName))
                    .setAudioConfig(new AudioConfig(AudioEncoding.MP3, 1f , 0f));
        });
    }

    public void synthesizeText(String text) {
        executorService.execute(() -> googleCloudTTS.start(text));
    }

    public void postTaskToMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void cancel(){
        executorService.shutdownNow();
        googleCloudTTS.stop();
    }
}

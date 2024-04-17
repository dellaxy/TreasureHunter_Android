package com.example.wander_wise.services.tts_services;

import static com.example.wander_wise.objects.Utils.isNull;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.wander_wise.BuildConfig;
import com.example.wander_wise.services.PreferencesManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import darren.googlecloudtts.model.VoicesList;
import darren.googlecloudtts.parameter.AudioConfig;
import darren.googlecloudtts.parameter.AudioEncoding;
import darren.googlecloudtts.parameter.VoiceSelectionParams;

public class TextToSpeechService {
    MyGoogleCloudTTS googleCloudTTS;
    VoicesList voicesList;
    String languageCode, voiceName;
    ExecutorService executorService;
    Handler handler;
    PreferencesManager profilePreferences;

    public TextToSpeechService(Context context) {
        googleCloudTTS = MyGoogleCloudTTSFactory.create(BuildConfig.TTS_API_KEY);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        profilePreferences = PreferencesManager.getInstance(context);

        startService();
    }

    public TextToSpeechService(Context context, String languageCode, String voiceName) {
        googleCloudTTS = MyGoogleCloudTTSFactory.create(BuildConfig.TTS_API_KEY);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        profilePreferences = PreferencesManager.getInstance(context);
        this.languageCode = languageCode;
        this.voiceName = voiceName;

        startService();
    }

    protected void startService() {
        executorService.execute(() -> {
            if (isNull(voicesList) && isNull(languageCode) && isNull(voiceName)) {
                voicesList = googleCloudTTS.load();
                languageCode = voicesList.getLanguageCodes()[15];
                voiceName = profilePreferences.getTTSVoice();
            }
            googleCloudTTS.setVoiceSelectionParams(new VoiceSelectionParams(languageCode, voiceName))
                    .setAudioConfig(new AudioConfig(AudioEncoding.MP3, .95f, 0f));
        });
    }

    public void synthesizeText(String text) {
        executorService.execute(() -> {
            try {
                googleCloudTTS.start(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void synthesizeTexts(String[] texts, int delay) {
        executorService.execute(() -> {
            try {
                for (String text : texts) {
                    googleCloudTTS.start(text);
                    while (googleCloudTTS.isPlaying()) {
                        Thread.sleep(delay);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void postTaskToMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void cancel() {
        executorService.shutdownNow();
        googleCloudTTS.stop();
    }
}

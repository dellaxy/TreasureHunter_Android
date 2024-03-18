package com.example.city_tours.services.tts_services;

import darren.googlecloudtts.GoogleCloudAPIConfig;
import darren.googlecloudtts.api.SynthesizeApi;
import darren.googlecloudtts.api.SynthesizeApiImpl;
import darren.googlecloudtts.api.VoicesApi;
import darren.googlecloudtts.api.VoicesApiImpl;

public class MyGoogleCloudTTSFactory {
    public static MyGoogleCloudTTS create(String apiKey) {
        GoogleCloudAPIConfig config = new GoogleCloudAPIConfig(apiKey);
        return create(config);
    }

    public static MyGoogleCloudTTS create(GoogleCloudAPIConfig config) {
        SynthesizeApi synthesizeApi = new SynthesizeApiImpl(config);
        VoicesApi voicesApi = new VoicesApiImpl(config);
        return new MyGoogleCloudTTS(synthesizeApi, voicesApi);
    }
}

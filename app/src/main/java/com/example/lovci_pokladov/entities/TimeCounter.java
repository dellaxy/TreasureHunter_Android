package com.example.lovci_pokladov.entities;

import android.os.Handler;
import android.widget.TextView;

public class TimeCounter {
    private int seconds = 0;
    private Handler handler = new Handler();
    private TextView timerTextView;

    public TimeCounter(TextView textView) {
        this.timerTextView = textView;
    }

    public void startTimer() {
        handler.postDelayed(timerRunnable, 1000);
    }

    public void stopTimer() {
        handler.removeCallbacks(timerRunnable);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;

            String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
            timerTextView.setText(time);

            handler.postDelayed(this, 1000);
        }
    };
}

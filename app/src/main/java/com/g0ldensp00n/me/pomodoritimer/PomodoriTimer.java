package com.g0ldensp00n.me.pomodoritimer;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

public class PomodoriTimer extends AppCompatActivity {

    private Handler handler = new Handler();
    private TextView timerOutput;
    private boolean runningTimer = false;
    private boolean breakTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodori_timer);

        getSupportActionBar().setElevation(0);
        Typeface sfFont = Typeface.createFromAsset(getAssets(), "fonts/sf_font.ttf");
        timerOutput = (TextView) findViewById(R.id.timerText);
        timerOutput.setTypeface(sfFont);
        startRepeatingTask();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask(){
        timerUpdate.run();
        progressBarUpdate.run();
    }

    void stopRepeatingTask(){
        handler.removeCallbacks(progressBarUpdate);
        handler.removeCallbacks(timerUpdate);
    }


    Runnable timerUpdate = new Runnable(){
        @Override
        public void run(){
                try {
                    if(runningTimer) {
                        String[] timerStrings = timerOutput.getText().toString().split(":");
                        if (Integer.parseInt(timerStrings[1]) == 0 && Integer.parseInt(timerStrings[0]) == 0) {
                            String output = breakTime ? "25:00" : "5:00";
                            timerOutput.setText(output);
                            breakTime = !breakTime;
                        } else if (Integer.parseInt(timerStrings[1]) == 0) {
                            String output = Integer.parseInt(timerStrings[0]) - 1 + ":59";
                            timerOutput.setText(output);
                        } else {
                            String output = timerStrings[0] + ":" + String.format("%02d", Integer.parseInt(timerStrings[1]) - 1);
                            timerOutput.setText(output);
                        }
                    }
                } finally {
                    handler.postDelayed(this, 1000);
                }
        }
    };

    Runnable progressBarUpdate = new Runnable() {
        int currentRemove = 0;
        int timeLast;
        @Override
        public void run() {
                try {
                    if(runningTimer) {
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        String[] timerStrings = timerOutput.getText().toString().split(":");
                        int timeCurrent = ((Integer.parseInt(timerStrings[0]) * 60) + Integer.parseInt(timerStrings[1]));
                        currentRemove = timeLast != timeCurrent ? 0 : currentRemove;
                        double currentTimerTime = timeCurrent - (currentRemove * 0.01);
                        double currentMax = breakTime ? 300.0 : 1500;
                        progressBar.setProgress((int) ((currentTimerTime / currentMax) * 10000.0), true);
                        timeLast = timeCurrent;
                        currentRemove++;
                    }
                } finally {
                    handler.postDelayed(this, 10);
                }
        }
    };

    public void pauseTimer(View view) {
        if(runningTimer){
            stopRepeatingTask();
            runningTimer = !runningTimer;
        } else {
            startRepeatingTask();
            runningTimer = !runningTimer;
        }

        if(view instanceof FloatingActionButton){
            FloatingActionButton button = (FloatingActionButton) view;
            if(runningTimer){
                button.setImageResource(R.drawable.ic_stop_primarycolor);
            } else {
                button.setImageResource(R.drawable.ic_play_primarycolor);
            }
        }
    }
}

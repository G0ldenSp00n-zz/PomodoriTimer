package com.g0ldensp00n.me.pomodoritimer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

public class PomodoriTimer extends AppCompatActivity {

    private Handler handler = new Handler();
    private TextView timerOutput;
    private boolean runningTimer = true;
    private boolean breakTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodori_timer);

        timerOutput = (TextView) findViewById(R.id.timerText);
        startRepeatingTask();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask(){
        timerUpdate.run();
    }

    void stopRepeatingTask(){
        handler.removeCallbacks(timerUpdate);
    }


    Runnable timerUpdate = new Runnable(){
        @Override
        public void run(){
            try {
                String[] timerStrings = timerOutput.getText().toString().split(":");
                if (Integer.parseInt(timerStrings[1]) == 0 && Integer.parseInt(timerStrings[0]) == 0) {
                    String output = breakTime ? "25:00" : "5:00";
                    timerOutput.setText(output);
                    breakTime = !breakTime;
                } else if(Integer.parseInt(timerStrings[1]) == 0) {
                    String output = Integer.parseInt(timerStrings[0]) - 1 + ":59";
                    timerOutput.setText(output);
                } else {
                    String output = timerStrings[0] + ":" + String.format("%02d", Integer.parseInt(timerStrings[1]) - 1);
                    timerOutput.setText(output);
                }
            }finally {
                handler.postDelayed(this, 1000);
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
    }
}

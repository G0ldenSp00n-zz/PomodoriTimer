package com.g0ldensp00n.me.pomodoritimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PomodoriTimer extends AppCompatActivity {

    private Handler handler = new Handler();
    private TextView timerOutput;
    private boolean runningTimer = true;
    private boolean breakTime = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Creates Initial View and Loads it to the Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodori_timer);

        //Setup Theme and UI
        setSelectedTheme();

        //Start the Timer
        startRepeatingTask();
    }

    private void setSelectedTheme() {
        //Setting the ActionBar to be parallel to the background
        getSupportActionBar().setElevation(0);

        //Set the Font of the Timer
        Typeface sfFont = Typeface.createFromAsset(getAssets(), "fonts/sf_font.ttf");
        timerOutput = (TextView) findViewById(R.id.timerText);
        timerOutput.setTypeface(sfFont);

        //Setup Theme from Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch(sharedPreferences.getString(getString(R.string.themePreferences), "Default")) {
            case "AMOLED Dark":
                setCurrentTheme(R.color.colorDark, R.color.colorDarkLight, R.color.colorDarkRing);
                break;
            case "Light":
                setCurrentTheme(R.color.colorLightMaterial, R.color.colorLightMaterialDark, R.color.colorLightMaterialRing);
                break;
            case "Green":
                setCurrentTheme(R.color.colorGreenMaterial, R.color.colorGreenMaterialDark, R.color.colorGreenMaterialRing);
                break;
            case "Dark":
                setCurrentTheme(R.color.colorDarkMaterial, R.color.colorDarkMaterialDark, R.color.colorDarkMaterialRing);
                break;
            default:
                setCurrentTheme(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorRing);
                break;
        }

        //Setup Preference Manager the Handle Theme Change
        sharedPreferences.registerOnSharedPreferenceChangeListener(spChanged);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Add the Menu to the ActionBar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Test for Clicks in Menu
        switch (item.getItemId()) {
            case R.id.settingsMenuItem:
                //If Clicked Settings Load Settings
                openSettingsView();
                return true;
            default:
                //If Not Found Do Nothing
                return false;
        }
    }

    private void openSettingsView() {
        //Make a New Intent to Load Settings Menu and Load It as the Current Activity
        Intent settingsIntent = new Intent(this, PomodoriPreferenceActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onDestroy(){
        //When App Closed Kill Processes
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask(){
        //Start the Timer
        timerUpdate.run();
        progressBarUpdate.run();
    }

    void stopRepeatingTask(){
        //Stop the Timer
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
                            String output = breakTime ?  sharedPreferences.getString("workTimeLength", "25")+":00" :  sharedPreferences.getString("breakTimeLength", "5") + ":00";
                            //Sets 25 minutes when not on break, and 5 minutes for the break
                            String output = breakTime ? "25:00" : "5:00";
                            //Set the Timer to the new time
                            timerOutput.setText(output);
                            breakTime = !breakTime;
                            if(breakTime) setCurrentTheme(R.color.colorBreak, R.color.colorBreakDark, R.color.colorBreakRing);
                            else setSelectedTheme();
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

    private void setCurrentTheme(int colorPrimary, int colorDark, int colorRing){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColorInt(colorPrimary)));
        getWindow().setStatusBarColor(getColorInt(colorDark));
        getWindow().setNavigationBarColor(getColorInt(colorPrimary));
        findViewById(R.id.mainBackground).setBackgroundColor(getColorInt(colorPrimary));
        Drawable stop = getDrawable(R.drawable.ic_stop_primarycolor);
        stop.setTint(getColorInt(colorPrimary));
        ((FloatingActionButton) findViewById(R.id.floatingActionButton)).setImageDrawable(stop);
        Drawable clockRing = getDrawable(R.drawable.clockring);;
        clockRing.setTint(getColorInt(colorRing));
        findViewById(R.id.progressBar).setBackground(clockRing);
    }

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
                        double currentMax = breakTime ? Integer.parseInt(sharedPreferences.getString("breakTimeLength", "5")) * 60 : Integer.parseInt(sharedPreferences.getString("workTimeLength", "25")) * 60;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBar.setProgress(Math.abs((breakTime ? -10000 : 0) + (int) ((currentTimerTime / currentMax) * 10000.0)), true);
                        }else {
                            progressBar.setProgress(Math.abs((breakTime ? -10000 : 0) + (int) ((currentTimerTime / currentMax) * 10000.0)));
                        }
                        timeLast = timeCurrent;
                        currentRemove++;
                    }
                } finally {
                    handler.postDelayed(this, 10);
                }
        }
    };

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(!breakTime){
                        //Refresh Theme
                        setSelectedTheme();

                        //Reset Timer
                        String output = breakTime ?  sharedPreferences.getString("workTimeLength", "25")+":00" :  sharedPreferences.getString("breakTimeLength", "5") + ":00";
                        timerOutput.setText(output);
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
                Drawable drawableToTint = getDrawable(R.drawable.ic_stop_primarycolor);
                drawableToTint.setTint(((ColorDrawable)findViewById(R.id.mainBackground).getBackground()).getColor());
                button.setImageDrawable(drawableToTint);
            } else {
                Drawable drawableToTint = getDrawable(R.drawable.ic_play_primarycolor);
                drawableToTint.setTint(((ColorDrawable)findViewById(R.id.mainBackground).getBackground()).getColor());
                button.setImageDrawable(drawableToTint);
            }
        }
    }

    private int getColorInt(int colorIn){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return getColor(colorIn);
        } else {
            Resources resources = getResources();
            return resources.getColor(colorIn);
        }
    }
}

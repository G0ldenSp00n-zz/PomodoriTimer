package com.g0ldensp00n.me.pomodoritimer;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PomodoriPreferenceActivity extends PreferenceActivity {

    private Toolbar bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceManager.registerOnSharedPreferenceChangeListener(spChanged);
        setSelectedTheme();
    }

    private void setSelectedTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch(sharedPreferences.getString(getString(R.string.themePreferences), "Default")) {
            case "Dark":
                //Set Settings Dark Theme
                setCurrentTheme(R.color.settingDarkThemeMain, R.color.settingDarkThemeDark, R.color.settingDarkThemeBackground, R.color.settingDarkThemeFont);
                break;
            default:
                //Set Settings Light Theme
                setCurrentTheme(R.color.settingLightThemeMain, R.color.settingLightThemeDark, R.color.settingLightThemeBackground, R.color.settingLightThemeFont);
                break;
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    setSelectedTheme();
                }
            };

    private void setCurrentTheme(int colorPrimary, int colorDark, int colorBackground, int fontColor){
        bar.getNavigationIcon().setTint(getColorInt(R.color.textColorPrimary));
        bar.setBackgroundColor(getColorInt(colorPrimary));
        bar.setTitleTextColor(getColorInt(R.color.textColorPrimary));
        getWindow().setStatusBarColor(getColorInt(colorDark));
        getWindow().setNavigationBarColor(getColorInt(colorPrimary));
        getWindow().setBackgroundDrawable(new ColorDrawable(getColorInt(colorBackground)));
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
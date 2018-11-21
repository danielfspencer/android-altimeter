package com.example.daniel.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ActivityTemplate extends AppCompatActivity {
    private int current_theme;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        current_theme = preferences.getInt("theme",R.style.DarkAppTheme);

        setTheme(current_theme);

        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (current_theme != preferences.getInt("theme",R.style.DarkAppTheme)) {
            recreate();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

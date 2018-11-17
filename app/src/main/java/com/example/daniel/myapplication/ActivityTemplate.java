package com.example.daniel.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ActivityTemplate extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setTheme(preferences.getInt("theme",R.style.DarkAppTheme));

        super.onCreate(savedInstanceState);
    }
}

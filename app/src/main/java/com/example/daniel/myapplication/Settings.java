package com.example.daniel.myapplication;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import android.content.SharedPreferences;

public class Settings extends ActivityTemplate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toast.makeText(this, "settings activity created", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
    }

}

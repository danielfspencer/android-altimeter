package com.example.daniel.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityTemplate extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.LightAppTheme);
        }
        super.onCreate(savedInstanceState);
    }
}

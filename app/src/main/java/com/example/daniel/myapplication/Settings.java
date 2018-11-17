package com.example.daniel.myapplication;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class Settings extends ActivityTemplate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
    }

}

package com.example.daniel.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Debug extends ActivityTemplate {
    private SensorManager sensorManager;
    private Sensor pressureSensor;

    private TextView ref_update_rate;
    private TextView ref_resolution;
    private TextView ref_vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ref_update_rate = findViewById(R.id.update_rate);
        ref_resolution = findViewById(R.id.resolution);
        ref_vendor = findViewById(R.id.vendor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        float update_rate = 1f / ((float) pressureSensor.getMinDelay() / 1000000);

        ref_update_rate.setText(String.format("%.2f Hz", update_rate));
        ref_resolution.setText(String.format("%f hPa", pressureSensor.getResolution()));
        ref_vendor.setText(pressureSensor.getVendor());
    }

}

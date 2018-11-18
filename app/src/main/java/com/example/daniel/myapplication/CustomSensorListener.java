package com.example.daniel.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

class CustomSensorListener implements SensorEventListener {
    private float pressure_hpa = -1;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        this.pressure_hpa = sensorEvent.values[0];
    }

    @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public float getPressure() {
        return this.pressure_hpa;
    }
}

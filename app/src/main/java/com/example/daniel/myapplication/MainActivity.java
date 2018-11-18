package com.example.daniel.myapplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends ActivityTemplate {
    int frame_number = 0;
    int sample_every_n_frames;
    int render_every_n_frames;

    private TextView pressure_txt;
    private TextView height_txt;
    private View mainView;

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private CustomSensorListener sensorEventListener = new CustomSensorListener();

    private float reference_pressure;
    private ArrayList<Float> pressure_history;
    private int pressure_history_max_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Toast.makeText(this, "main activity created", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        // bind references
        mainView = findViewById(android.R.id.content);
        pressure_txt = findViewById(R.id.pressure);
        height_txt = findViewById(R.id.height);
        Button zero_button = findViewById(R.id.zero);

        // init sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        // set button onClick handler
        zero_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               reference_pressure = getAveragedReading();
            }
        });

        // call drawFrame every screen refresh
        mainView.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                drawFrame();
                mainView.postOnAnimation(this);
            }
        });

        if(savedInstanceState == null){
            pressure_history = new ArrayList<Float>();
            reference_pressure = -1;
        } else {
            pressure_history = (ArrayList<Float>) savedInstanceState.getSerializable("pressure_history");
            reference_pressure = savedInstanceState.getFloat("reference_pressure");
        }

        readPreferences();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        readPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("reference_pressure",reference_pressure);
        outState.putSerializable("pressure_history",pressure_history);
    }

    private void readPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        sample_every_n_frames = preferences.getInt("sample_every_n_frames",4);
        render_every_n_frames = preferences.getInt("render_every_n_frames",4);

        if (preferences.getBoolean("filter_enabled",true)) {
            pressure_history_max_length = preferences.getInt("average_samples", 20);
        } else {
            pressure_history_max_length = 1;
        }
    }

    private float getAltitude(float pressure) {
        return SensorManager.getAltitude(reference_pressure,pressure);
    }

    private void drawFrame() {
        if (frame_number % sample_every_n_frames == 0) {
            addReading();
            drawInfo();
        }

        frame_number++;
    }

    private void addReading() {
        float pressure = sensorEventListener.getPressure();

        if (pressure == -1) {
            //sensor not ready
            return;
        }

        if (pressure_history.size() < pressure_history_max_length) {
            pressure_history.add(pressure);
        } else {
            pressure_history.remove(0);
            pressure_history.add(pressure);
        }
    }

    private float getAveragedReading() {
        int number_of_readings = pressure_history.size();
        float sum = 0;

        for (int i = 0; i < pressure_history.size(); i++) {
            sum += pressure_history.get(i);
        }

        return sum / number_of_readings;
    }

    private void drawInfo() {
        float pressure = getAveragedReading();

        pressure_txt.setText(String.format("%.2f", pressure));
        if (reference_pressure != -1) {
            height_txt.setText(String.format("%.2f", getAltitude(pressure)));
        }
    }
}
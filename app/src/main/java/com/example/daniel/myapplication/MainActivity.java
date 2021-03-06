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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import java.io.FileWriter;
import java.util.ArrayList;
import com.opencsv.CSVWriter;

public class MainActivity extends ActivityTemplate {
    private int frame_number = 0;
    private int sample_every_n_frames;
    private int render_every_n_frames;

    private TextView pressure_txt;
    private TextView height_txt;
    private ProgressBar buffer;
    private View mainView;
    private Switch log_switch;

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private CustomSensorListener sensorEventListener = new CustomSensorListener();

    private float reference_pressure;
    private ArrayList<Float> pressure_history;
    private int pressure_history_max_length;

    private ArrayList<Float> pressures = new ArrayList<Float>();
    private boolean log_pressures = false;

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
        buffer = findViewById(R.id.buffer);
        Button zero_button = findViewById(R.id.zero);
        log_switch = findViewById(R.id.log_switch);

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
            pressure_history = new ArrayList<Float>();
            reference_pressure = savedInstanceState.getFloat("reference_pressure");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                this.startActivity(intent);
                return true;
            case R.id.open_debug:
                Intent another_intent = new Intent(this, Debug.class);
                this.startActivity(another_intent);
                return true;
            case R.id.log_switch:
                log_pressures = !item.isChecked();
                item.setChecked(log_pressures);
                return true;
            case R.id.save_csv:
                writeToFile(pressures);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
        render_every_n_frames = preferences.getInt("render_every_n_frames",6);

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
        }

        if (frame_number % render_every_n_frames == 0) {
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

        buffer.setMax(pressure_history_max_length);
        buffer.setProgress(pressure_history.size());

        if (log_pressures) {
            pressures.add(pressure);
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

    private void writeToFile(ArrayList<Float> values) {
        try {
            String filePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            CSVWriter writer = new CSVWriter(new FileWriter(filePath+"/raw.csv"));

            String[] header = {"reading #", "pressure hPa"};
            writer.writeNext(header);

            String[] line = new String[2];

            for (int i = 0; i < values.size(); i++) {
                line[0] = Integer.toString(i);
                line[1] = String.format("%.4f", values.get(i));
                writer.writeNext(line);
            }

            writer.close();
            Toast.makeText(this, String.format("%d readings saved", values.size()), Toast.LENGTH_SHORT).show();
        } catch (java.io.IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
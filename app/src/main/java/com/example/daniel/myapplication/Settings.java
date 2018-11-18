package com.example.daniel.myapplication;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.text.TextWatcher;

public class Settings extends ActivityTemplate {
    private RadioGroup ref_theme_selected;
    private TextView ref_render_every_n_frames;
    private TextView ref_sample_every_n_frames;
    private TextView ref_average_samples;
    private Switch ref_filter_enabled;
    private Button ref_reset_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Toast.makeText(this, "settings activity created", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        // bind references to elements
        ref_theme_selected = findViewById(R.id.theme_selector);
        ref_render_every_n_frames = findViewById(R.id.render_every_frames);
        ref_sample_every_n_frames = findViewById(R.id.sample_every_frames);
        ref_average_samples = findViewById(R.id.average_samples);
        ref_filter_enabled = findViewById(R.id.filter_enabled);
        ref_reset_button = findViewById(R.id.set_defaults);

        // show the current values
        if (preferences.getInt("theme",R.style.DarkAppTheme) == R.style.DarkAppTheme) {
            ref_theme_selected.check(R.id.dark_theme);
        } else {
            ref_theme_selected.check(R.id.light_theme);
        }
        ref_render_every_n_frames.setText( Integer.toString(preferences.getInt("render_every_n_frames",4)) );
        ref_sample_every_n_frames.setText( Integer.toString(preferences.getInt("sample_every_n_frames",4)) );
        ref_average_samples.setText( Integer.toString(preferences.getInt("average_samples",20)) );
        ref_filter_enabled.setChecked( preferences.getBoolean("filter_enabled",true));


        // listen for changed values
        ref_theme_selected.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dark_theme) {
                    editor.putInt("theme",R.style.DarkAppTheme);
                } else {
                    editor.putInt("theme",R.style.LightAppTheme);
                }
                editor.apply();
                recreate();
            }
        });
        ref_render_every_n_frames.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int number = Integer.parseInt(s.toString());
                    if (number > 0) {
                        editor.putInt("render_every_n_frames",number);
                        editor.apply();
                    }
                } catch (java.lang.NumberFormatException e) {
                    // ignore because it is not valid
                    return;
                }
            }
        });
        ref_sample_every_n_frames.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int number = Integer.parseInt(s.toString());
                    if (number > 0) {
                        editor.putInt("sample_every_n_frames",number);
                        editor.apply();
                    }
                } catch (java.lang.NumberFormatException e) {
                    // ignore because it is not valid
                    return;
                }
            }
        });
        ref_average_samples.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int number = Integer.parseInt(s.toString());
                    if (number > 0) {
                        editor.putInt("average_samples",number);
                        editor.apply();
                    }
                } catch (java.lang.NumberFormatException e) {
                    // ignore because it is not valid
                    return;
                }
            }
        });
        ref_filter_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("filter_enabled",isChecked);
                editor.apply();
            }
        });

        // set defaults when button pressed
        ref_reset_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.putInt("render_every_n_frames",4);
                editor.putInt("sample_every_n_frames",4);
                editor.putInt("average_samples",20);
                editor.putBoolean("filter_enabled",true);
                editor.commit();
                recreate();
            }
        });
    }
}

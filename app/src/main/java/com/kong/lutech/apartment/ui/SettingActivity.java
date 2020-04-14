package com.kong.lutech.apartment.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kongtech.lutech.apartment.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("설정");
    }
}

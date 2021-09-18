package com.babuminiproject.diywormhole;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class helpAndSupportactivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_supportactivity);


        mToolbar = (Toolbar) findViewById(R.id.helpandsupport_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Help and Support");


    }
}
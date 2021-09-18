package com.babuminiproject.diywormhole;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;


public class About_activity extends AppCompatActivity {

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_activity);

        mToolbar = (Toolbar) findViewById(R.id.about_main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("About");



    }
}
package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class my_profile_activity extends AppCompatActivity {


    private Toolbar mToolbar;
    private ImageView editprofImagebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_activity);

        BottomNavigationView bottom_menu_nav = (BottomNavigationView) findViewById(R.id.nav_bottom_menu);
        bottom_menu_nav.setSelectedItemId(R.id.self_profile_navbottomid);

        bottom_menu_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                editprofImagebtn = (ImageView) findViewById(R.id.myprof_editprofilebtn);
                editprofImagebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendTosettings();
                    }
                });
                mToolbar = (Toolbar) findViewById(R.id.myprofile_app_bar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle("my profile");

                switch (item.getItemId()) {

                    case R.id.home_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.serach_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), search_activity.class));
                        break;
                    case R.id.add_posting_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), PostActivity.class));
                        break;

                    case R.id.chat_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), chat_list_activity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.self_profile_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), my_profile_activity.class));
                        overridePendingTransition(0, 0);
                        break;

                }
                return false;
            }
        });


    }//main override

    private void sendTosettings() {
        Intent ssact = new Intent(my_profile_activity.this,settings.class);
        ssact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ssact);
    }
}
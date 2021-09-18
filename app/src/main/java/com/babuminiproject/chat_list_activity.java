package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class chat_list_activity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_activity);

        mToolbar = (Toolbar) findViewById(R.id.chat_activity_click_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chats");

        BottomNavigationView bottom_menu_nav = (BottomNavigationView) findViewById(R.id.nav_bottom_menu);
        bottom_menu_nav.setSelectedItemId(R.id.chat_navbottomid);


        bottom_menu_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){

                    case R.id.home_navbottomid :
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        break;

                    case R.id.serach_navbottomid :
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(),search_activity.class));
                        overridePendingTransition(0,0);
                        break;

                    case R.id.add_posting_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), PostActivity.class));
                        break;

                    case R.id.chat_navbottomid :
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(),chat_list_activity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.self_profile_navbottomid :
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(),my_profile_activity.class));
                        overridePendingTransition(0,0);
                        break;

                }
                return false;
            }
        });







    }
}
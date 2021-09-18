package com.babuminiproject.diywormhole;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class splash_screen_activity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public int mykey = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_activity);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        startwork();


    }//on create

    private void startwork() {

        if (isConnected()) {//internet conn present

            if (currentUser != null) {


                if (currentUser != null) {
                    Intent k = new Intent(splash_screen_activity.this, MainActivity.class);
                    k.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(k);

                }

            } else {

                Intent i = new Intent(splash_screen_activity.this, login_activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }


        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            AlertDialog alertDialog1 = new AlertDialog.Builder(
                    splash_screen_activity.this).create();

            // Setting Dialog Title
            alertDialog1.setTitle("NO INTERNET CONNECTION");

            // Setting Dialog Message
            alertDialog1.setMessage("Check mobile data or WIFI turned on.....");

            // Setting OK Button
            alertDialog1.setButton("RETRY", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog
                    Intent h = new Intent(splash_screen_activity.this, splash_screen_activity.class);
                    startActivity(h);
                    // closed
                }
            });

            // Showing Alert Message
            alertDialog1.show();


        }


    }


    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;


    }


}





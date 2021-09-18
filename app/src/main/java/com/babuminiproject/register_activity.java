package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register_activity extends AppCompatActivity {

    private TextView movetologintext;
    private Button CreateAccBtn;
    private EditText Usermail , Userpassword, Userconfirmpassword;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        mAuth = FirebaseAuth.getInstance();

        Usermail = (EditText) findViewById(R.id.editText_TextEmail);
        Userpassword = (EditText) findViewById(R.id.editText_Password);
        Userconfirmpassword = (EditText) findViewById(R.id.editText_confirm_password);

        movetologintext = (TextView) findViewById(R.id.login_txt_btn);
        movetologintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senttologinactivity();
            }

        });

        CreateAccBtn = (Button) findViewById(R.id.create_acc_btn);
        CreateAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });




    }//main override


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser  = mAuth.getCurrentUser();

        if(currentUser != null){
            sendToMainActivity();
        }



    }

    private void sendToMainActivity() {

        Intent mac = new Intent(register_activity.this,MainActivity.class);
        mac.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mac);
        finish();

    }

    private void CreateNewAccount() {

        Toast.makeText(register_activity.this,"Please Wait...",Toast.LENGTH_SHORT).show();

        String email = Usermail.getText().toString();
        String password = Userpassword.getText().toString();
        String confirmpasswprd = Userconfirmpassword.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"enter your new password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmpasswprd)){
            Toast.makeText(this,"enter your confirm password",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmpasswprd)){
            Toast.makeText(this,"Passwords Doesn't match",Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(register_activity.this,"Account Created",Toast.LENGTH_SHORT).show();
                        sendtosetupact();

                    }
                    else {
                        String ermsg = task.getException().getMessage();
                        Toast.makeText(register_activity.this, ermsg ,Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }




    }

    private void sendtosetupact() {
        Intent hh = new Intent(register_activity.this,setup_activity.class);
        hh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(hh);
    }


    private void senttologinactivity() {
        Intent gooo = new Intent(register_activity.this,login_activity.class);
        startActivity(gooo);
        finish();

    }
}
package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

public class login_activity extends AppCompatActivity {

    private Button LoginButton ;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountlink;


    private FirebaseAuth mAuth;
    private String currentUser;

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG ="GOOGLE_SIGN_IN_TAG";
    private ImageView googlebtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        mAuth = FirebaseAuth.getInstance();

        NeedNewAccountlink = (TextView) findViewById(R.id.textView5);
        UserEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        UserPassword = (EditText) findViewById(R.id.editTextTextPassword);
        LoginButton = (Button) findViewById(R.id.login_button);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserLogin();
            }
        });



        //register activity redirect
        NeedNewAccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterACt();
            }
        });






        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        googlebtn = (ImageView) findViewById(R.id.GoogleSignInButton);
        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: begin Google Signin");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);

            }
        });
    }// on create end


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Log.d(TAG,"onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                Toast.makeText(login_activity.this,"go here",Toast.LENGTH_SHORT).show();

                firebaseAuthwithGAcc(account.getIdToken());
            }
            catch (Exception e ){
                Log.d(TAG,"onActivityResult"  + e.getMessage());
                Toast.makeText(login_activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        }
    }

    private void firebaseAuthwithGAcc(String idToken) {
        Log.d(TAG,"firebaseAuth begin with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG,"onSuccess : logged in");



                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                Log.d(TAG,"email"+ email);
                Log.d(TAG,"Uid" + uid);

                if(authResult.getAdditionalUserInfo().isNewUser()){
                    Toast.makeText(login_activity.this,"logged in Successfully",Toast.LENGTH_SHORT).show();
                    Intent gotosetupact = new Intent(login_activity.this,setup_activity.class);
                    gotosetupact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gotosetupact);

                }
                else {
                    Toast.makeText(login_activity.this,"problem",Toast.LENGTH_SHORT).show();
                    MoveToMainAct();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onfailure : loggin failed"+ e.getMessage());

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            MoveToMainAct();
        }


    }



    private void AllowUserLogin() {
        Toast.makeText(login_activity.this,"Please Wait...",Toast.LENGTH_SHORT).show();

        String mail = UserEmail.getText().toString();
         String pwd = UserPassword.getText().toString();

         if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(pwd)){
             Toast.makeText(login_activity.this,"Enter Required Values",Toast.LENGTH_SHORT).show();
         }
         else {

             mAuth.signInWithEmailAndPassword(mail,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {

                     if(task.isSuccessful()){
                         MoveToMainAct();
                         Toast.makeText(login_activity.this,"logged in Successfully",Toast.LENGTH_SHORT).show();
                         finish();
                     }

                     else {

                         String err = task.getException().getMessage();
                         Toast.makeText(login_activity.this,err,Toast.LENGTH_SHORT).show();
                     }



                 }
             });

         }




    }

    private void MoveToMainAct() {
        Intent ma = new Intent(login_activity.this,MainActivity.class);
        ma.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ma);
        finish();


    }





    //register act method
    private void SendUserToRegisterACt() {
        Intent re = new Intent(login_activity.this,register_activity.class);
        startActivity(re);

    }



}//on create override
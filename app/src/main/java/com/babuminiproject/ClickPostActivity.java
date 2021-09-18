package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClickPostActivity extends AppCompatActivity {

    private Button dltpostbtn, editpostbtn;
    private ImageView postimagee;
    private TextView titlename;
    private ReadMoreTextView desctextname;
    private String PostKey, currentUserid, databaseUserID, tle, desc, pimg;
    private DatabaseReference ClickpostRef;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickpostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        dltpostbtn = (Button) findViewById(R.id.delete_post_btn5656);
        editpostbtn = (Button) findViewById(R.id.edit_post_btn5656);
        postimagee = (ImageView) findViewById(R.id.onclick_post_image5656);
        titlename = (TextView) findViewById(R.id.post_title_onclick5656);
        desctextname = (ReadMoreTextView) findViewById(R.id.post_description_onclick5656);
        dltpostbtn.setVisibility(View.INVISIBLE);
        editpostbtn.setVisibility(View.INVISIBLE);


        mToolbar = (Toolbar) findViewById(R.id.activity_click_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ClickpostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    tle = snapshot.child("title").getValue().toString();
                    desc = snapshot.child("description").getValue().toString();
                    pimg = snapshot.child("postimage").getValue().toString();
                    databaseUserID = snapshot.child("uid").getValue().toString();

                    titlename.setText(tle);
                    desctextname.setText(desc);
                    Glide.with(ClickPostActivity.this).load(pimg).placeholder(R.drawable.greybox).error(R.drawable.greybox).into(postimagee);

                    if (databaseUserID.equals(currentUserid)) {
                        dltpostbtn.setVisibility(View.VISIBLE);
                        editpostbtn.setVisibility(View.VISIBLE);
                    }
                    editpostbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(tle,desc);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dltpostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderdel = new AlertDialog.Builder(ClickPostActivity.this);
                builderdel.setTitle("Are your sure to Delete This Post ?\n\n");
                builderdel.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeletePostm();

                    }
                });
                builderdel.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderdel.show();

            }
        });


    }

    private void EditCurrentPost(String tle, String desc) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("EDIT POST:");

        LinearLayout layout = new LinearLayout(ClickPostActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputFieldtle = new EditText(ClickPostActivity.this);
        inputFieldtle.setText(tle);
        layout.addView(inputFieldtle);

        final EditText inputFielddesc = new EditText(ClickPostActivity.this);
        inputFielddesc.setText(desc);
        layout.addView(inputFielddesc);

        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickpostRef.child("title").setValue(inputFieldtle.getText().toString());
                ClickpostRef.child("description").setValue(inputFielddesc.getText().toString());
                Toast.makeText(getApplicationContext(), "Post Updated", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.alertbgclr);

    }

    private void DeletePostm() {
        ClickpostRef.removeValue();
        SendUserToMainActivity();
        Toast.makeText(getApplicationContext(), "Post Deleted", Toast.LENGTH_SHORT).show();

    }

    private void SendUserToMainActivity() {
        Intent llffout = new Intent(ClickPostActivity.this, login_activity.class);
        llffout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(llffout);
        finish();
    }

}
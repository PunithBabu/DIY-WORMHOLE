package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class setup_activity extends AppCompatActivity {

    private Spinner cs;
    public String cy;
    private EditText UserName,FullName;
    private Button setupbtn;
    private CircularImageView circularImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private String current_user_id;
    private StorageReference UserProfilePic;

    final static int Gallery_pick = 1;
    public String imageprescene;
    Uri imageUri;
    public String realimage;
    public String currenidlatest;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_activity);



        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UserProfilePic = FirebaseStorage.getInstance().getReference().child("profileimage");


        UserName = (EditText) findViewById(R.id.editTextTextPersonName);
        FullName = (EditText) findViewById(R.id.editTextTextPersonName2);
        setupbtn = (Button) findViewById(R.id.button3342);
        circularImageView = (CircularImageView) findViewById(R.id.prof_civ);

        cs = (Spinner) findViewById(R.id.spinner_country_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ctry_listing, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cs.setAdapter(adapter);
        cs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getSelectedItem().toString();
                cy = choice;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(setup_activity.this,"Select Your Country",Toast.LENGTH_SHORT).show();

            }
        });

        setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUserInformation();
            }
        });


        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,Gallery_pick);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();



            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                StorageReference filepath = UserProfilePic.child("profileimage").child(current_user_id + ".jpg");

                //loading image

                Glide.with(setup_activity.this)
                        .asGif()
                        .load(R.drawable.loadingggifff)
                        .placeholder(ResourcesCompat.getDrawable(setup_activity.this.getResources(),
                                R.drawable.loadingggifff, null))
                        .fitCenter().centerCrop()
                        .into(new ImageViewTarget<GifDrawable>(circularImageView) {
                            @Override
                            protected void setResource(@Nullable GifDrawable resource) {
                                circularImageView.setImageDrawable(resource);
                            }
                        });

                //loading image


                currenidlatest = current_user_id;

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            UserProfilePic.child("profileimage").child(currenidlatest +".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if(task.isSuccessful()) {
                                        String asaprofile = task.getResult().toString();
                                        realimage = asaprofile;
                                        Glide.with(setup_activity.this).load(resultUri).skipMemoryCache(true).error(R.drawable.loadingggifff).placeholder(R.drawable.profileicon).into(circularImageView);
                                        imageprescene = "gwsdviwg90sv";
                                    }

                                    else {
                                        String eeeerrr = task.getException().getMessage();
                                        Toast.makeText(setup_activity.this,eeeerrr + "error is here" ,Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                        }

                    }
                });


            }
        }

    }

    private void SaveUserInformation() {
        String user_name = UserName.getText().toString();
        String user_full_name = FullName.getText().toString();
        String country_name = cy;
        String imgpre = imageprescene;


        if(imgpre == null ){
            Toast.makeText(setup_activity.this,"Set profile Image",Toast.LENGTH_SHORT).show();
        }
        else if (user_full_name.isEmpty()){
            Toast.makeText(setup_activity.this,"enter your full name",Toast.LENGTH_SHORT).show();
        }
        else if(user_name.isEmpty()){
            Toast.makeText(setup_activity.this,"Set your user name",Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap UserMAp = new HashMap();
            UserMAp.put("username",user_name);
            UserMAp.put("dob","none");
            UserMAp.put("fullname",user_full_name);
            UserMAp.put("country",country_name);
            UserMAp.put("profileimage",realimage);
            UserMAp.put("status","Am a good DIY hobbiest");
            UserMAp.put("gender","none");
            UserMAp.put("profession","none");


            UserRef.updateChildren(UserMAp). addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Toast.makeText(setup_activity.this,"Setup Successful",Toast.LENGTH_SHORT).show();
                        SendToMainAct();
                    }
                    else {
                        String err = task.getException().getMessage();
                        Toast.makeText(setup_activity.this,err,Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }

    }

    private void SendToMainAct() {
        Intent ma = new Intent(setup_activity.this,MainActivity.class);
        ma.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ma);
        finish();
    }


}



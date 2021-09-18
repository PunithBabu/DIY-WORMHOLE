package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

public class settings extends AppCompatActivity {

    EditText dobdateformat;
    int year, month, day;
    private Toolbar mToolbar;

    private EditText userName, userProfname, userAbout;     //value
    public String dateobirth, countryname, gender;             //value
    private CircularImageView civprof;                        //value
    private Button updatebttn;
    private Spinner cs;
    private RadioGroup rgroupgender;
    private RadioButton maleradio, femaleradio, otherradio;

    private DatabaseReference settingsUserref;
    private StorageReference UserProfilePic;
    private FirebaseAuth mAuth;
    private String currenuserID;
    final static int Gallery_pick = 1;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currenuserID = mAuth.getCurrentUser().getUid();
        settingsUserref = FirebaseDatabase.getInstance().getReference().child("Users").child(currenuserID);
        UserProfilePic = FirebaseStorage.getInstance().getReference().child("profileimage");

        civprof = (CircularImageView) findViewById(R.id.settings_profile_image);
        userName = (EditText) findViewById(R.id.settings_username);
        userProfname = (EditText) findViewById(R.id.settings_profilename);
        userAbout = (EditText) findViewById(R.id.settings_status);
        updatebttn = (Button) findViewById(R.id.settings_update_btn);
        rgroupgender = (RadioGroup) findViewById(R.id.genderradio_grp);
        maleradio = (RadioButton) findViewById(R.id.gender_maleradio);
        femaleradio = (RadioButton) findViewById(R.id.gender_femaleradio);
        otherradio = (RadioButton) findViewById(R.id.gender_othersradio);
        loadingBar = new ProgressDialog(settings.this);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        civprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, Gallery_pick);
            }
        });


        settingsUserref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String pimg = snapshot.child("profileimage").getValue().toString();
                    String uname = snapshot.child("username").getValue().toString();
                    String abt = snapshot.child("status").getValue().toString();
                    String pname = snapshot.child("fullname").getValue().toString();
                    String dobb = snapshot.child("dob").getValue().toString();
                    String gend = snapshot.child("gender").getValue().toString();
                    String ctry = snapshot.child("country").getValue().toString();

                    Glide.with(settings.this).load(pimg).placeholder(R.drawable.profileicon).into(civprof);
                    userName.setText(uname);
                    userProfname.setText(pname);
                    userAbout.setText(abt);
                    dobdateformat.setText(dobb);
                    cs.setSelection(16);
                    if (gend == "male") {           //for gender selection by default
                        rgroupgender.check(R.id.gender_maleradio);
                    }
                    if (gend == "female") {           //for gender selection by default
                        rgroupgender.check(R.id.gender_femaleradio);
                    }
                    if (gend == "others") {           //for gender selection by default
                        rgroupgender.check(R.id.gender_othersradio);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        rgroupgender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.gender_maleradio:
                        gender = "male";
                        break;
                    case R.id.gender_femaleradio:
                        gender = "female";
                        break;
                    case R.id.gender_othersradio:
                        gender = "others";
                        break;
                }
            }
        });


        cs = (Spinner) findViewById(R.id.setting_spinner_country_list);


        dobdateformat = (EditText) findViewById(R.id.settings_dob);
        dobdateformat.requestFocusFromTouch();
        Calendar calendar = Calendar.getInstance();
        dobdateformat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ctry_listing, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cs.setAdapter(adapter);
        cs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getSelectedItem().toString();
                countryname = choice;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(settings.this, "Select Your Country", Toast.LENGTH_SHORT).show();

            }
        });


        updatebttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

    } //main override


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();


            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Sprite doubleBounce = new DoubleBounce();
                loadingBar.setIndeterminateDrawable(doubleBounce);
                loadingBar.setMessage("please Wait...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();
                StorageReference filepath = UserProfilePic.child("profileimage").child(currenuserID + ".jpg");

                String currenidlatest = currenuserID;

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            UserProfilePic.child("profileimage").child(currenidlatest + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        String asaprofile = task.getResult().toString();
                                        Glide.with(settings.this).load(resultUri).skipMemoryCache(true).error(R.drawable.loadingggifff).placeholder(R.drawable.profileicon).into(civprof);
                                        loadingBar.dismiss();
                                    } else {
                                        String eeeerrr = task.getException().getMessage();
                                        Toast.makeText(settings.this, eeeerrr + "error is here", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }

                                }
                            });
                        }

                    }
                });


            }
        }

    }


    private void ValidateAccountInfo() {

        String usernamee = userName.getText().toString();
        String profilenamee = userProfname.getText().toString();
        String aboutmee = userAbout.getText().toString();
        String dobb = dateobirth;
        String genderr = gender;
        String countrynamee = countryname;

        if (TextUtils.isEmpty(usernamee) || TextUtils.isEmpty(profilenamee) || TextUtils.isEmpty(aboutmee) || TextUtils.isEmpty(dobb) || TextUtils.isEmpty(genderr) || TextUtils.isEmpty(countrynamee)) {
            Toast.makeText(settings.this, "Enter All Required Fields", Toast.LENGTH_SHORT).show();
        } else {
            Sprite doubleBounce = new DoubleBounce();
            loadingBar.setIndeterminateDrawable(doubleBounce);
            loadingBar.setMessage("please Wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateUserInfo(usernamee, profilenamee, aboutmee, dobb, genderr, countrynamee);
        }
    }

    private void UpdateUserInfo(String usernamee, String profilenamee, String aboutmee, String dobb, String genderr, String countrynamee) {

        HashMap userMap = new HashMap();
        userMap.put("username", usernamee);
        userMap.put("fullname", profilenamee);
        userMap.put("status", aboutmee);
        userMap.put("dob", dobb);
        userMap.put("gender", genderr);
        userMap.put("country", countrynamee);
        settingsUserref.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    MoveToMainAct();
                    Toast.makeText(settings.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                } else {

                    Toast.makeText(settings.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }
        });


    }

    private void MoveToMainAct() {
        Intent ma = new Intent(settings.this, MainActivity.class);
        ma.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ma);
        finish();


    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(settings.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month = monthOfYear + 1;
                String strMonth = "" + month;
                String strDayOfMonth = "" + dayOfMonth;

                if (month < 10) {
                    strMonth = "0" + strMonth;
                }
                if (dayOfMonth < 10) {
                    strDayOfMonth = "0" + strDayOfMonth;
                }
                String date = strDayOfMonth + "/" + strMonth + "/" + year;
                dobdateformat.setText(date);                     //date value is here
                dateobirth = date;
            }
        }, year, month, day);
        datePickerDialog.setTitle("Select Your Date Of Birth");
        datePickerDialog.show();

    }
}
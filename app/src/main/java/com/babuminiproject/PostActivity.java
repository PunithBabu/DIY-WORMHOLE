package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import java.sql.Time;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.SimpleTimeZone;

public class PostActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private Button postbtn;
    private ImageView postimage;
    final static int Gallery_pick = 1;
    private Uri ImageUri ;
    private EditText title,description;
    private EditText ideaprice , productprice;
    private Switch priceswitch;
    private int pflag = 0;
    private ProgressDialog loadingbar;
    private CircularImageView navimageprof;


    private String ptitle , pdesc , pideaprice , pproductprice;
    private String saveCurrentDate , saveCurrentTime , RandomPostName , DownloadURl;

    private StorageReference PostImageRef;
    private FirebaseAuth mAuth;
    private String currentUser;
    private DatabaseReference UserRef , PostRef , ListPostRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostImageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
         currentUser = mAuth.getCurrentUser().getUid();
         UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
         PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
         ListPostRef = FirebaseDatabase.getInstance().getReference().child("PostsList");

         loadingbar = new ProgressDialog(PostActivity.this);
        mToolbar = (Toolbar) findViewById(R.id.update_page_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Post");

        title = (EditText) findViewById(R.id.post_title_edittext);
        description = (EditText) findViewById(R.id.description_edittext_multiline);
        priceswitch = (Switch) findViewById(R.id.price_switch);
        ideaprice = (EditText) findViewById(R.id.idea_price_editext);
        productprice = (EditText) findViewById(R.id.product_price_edittext);

        ideaprice.setVisibility(View.GONE);
        productprice.setVisibility(View.GONE);

        priceswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    ideaprice.setVisibility(View.VISIBLE);
                    productprice.setVisibility(View.VISIBLE);
                    pflag = 1;
                }
                else{
                    ideaprice.setVisibility(View.GONE);
                    productprice.setVisibility(View.GONE);
                    pflag = 0;
                }

            }
        });







        postbtn = (Button) findViewById(R.id.post_btn);
        postimage = (ImageView) findViewById(R.id.new_post_image);
        postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToGallery();
            }
        });

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateUserPost();
            }
        });



    }//on create end

    private void ValidateUserPost() {

         ptitle = title.getText().toString();
         pdesc = description.getText().toString();
         pideaprice = ideaprice.getText().toString();
         pproductprice = productprice.getText().toString();

        if(ImageUri == null){
            Toast.makeText(PostActivity.this,"select image",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(ptitle)){
            Toast.makeText(PostActivity.this,"Enter post title",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(pdesc)){
            Toast.makeText(PostActivity.this,"Enter post description",Toast.LENGTH_SHORT).show();
        }
        else{
            Sprite doubleBounce = new DoubleBounce();
            loadingbar.setIndeterminateDrawable(doubleBounce);
            loadingbar.setMessage("please Wait...");
            loadingbar.show();
            StoreImageToFirebase();
        }



    }



    private void StoreImageToFirebase() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MM-YYYY");
        saveCurrentDate = currentdate.format(calForDate.getTime());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime  =  mdformat.format(calendar.getTime());


        RandomPostName = "Date_" + saveCurrentDate+ "Time_" + saveCurrentTime +"_"+ currentUser;

        StorageReference filepath = PostImageRef.child("post images").child(ImageUri.getLastPathSegment() +"-"+ RandomPostName + ".jpg");


        filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){

                    //fun starts here

                  task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            PostImageRef.child("post images").child(ImageUri.getLastPathSegment() +"-"+ RandomPostName+".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if(task.isSuccessful()){

                                    String asap = task.getResult().toString();
                                    SavePostInformationToDB(asap);
                                    }
                                    else {
                                        String eeeerrr = task.getException().getMessage();
                                        Toast.makeText(PostActivity.this,eeeerrr + "error is here" ,Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                        }
                    });


                }
                else {
                    String errrr = task.getException().getMessage();
                    Toast.makeText(PostActivity.this,errrr,Toast.LENGTH_SHORT).show();

                }

            }
        });



    }

    private void SavePostInformationToDB(String asapy) {

        UserRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String Ufullname = snapshot.child("fullname").getValue().toString();
                    String UprofileImage = snapshot.child("profileimage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("uid",currentUser);
                    postMap.put("date",saveCurrentDate);
                    postMap.put("time",saveCurrentTime);
                    postMap.put("title",ptitle);
                    postMap.put("description",pdesc);
                    postMap.put("postimage",asapy);
                    postMap.put("profileimage",UprofileImage);
                    postMap.put("fullname",Ufullname);
                    if(TextUtils.isEmpty(pideaprice) ||TextUtils.isEmpty(pproductprice)){
                        postMap.put("ideaprice","none");
                        postMap.put("productprice","none");
                    }
                    else{
                        postMap.put("ideaprice",pideaprice);
                        postMap.put("productprice",pproductprice);
                    }

                    PostRef.child(currentUser +"_"+ RandomPostName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){
                                Toast.makeText(PostActivity.this,"Posted Successfully",Toast.LENGTH_SHORT).show();
                                String postvalue = currentUser +"_"+ RandomPostName;
                                savePostinPostsList(postvalue);

                            } else{
                                String erer = task.getException().getMessage();
                                Toast.makeText(PostActivity.this,erer,Toast.LENGTH_SHORT).show();loadingbar.dismiss();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void savePostinPostsList(String postvalue) {
        HashMap listMap = new HashMap();
        listMap.put(postvalue,postvalue);
        ListPostRef.child(currentUser).updateChildren(listMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Intent mact = new Intent(PostActivity.this,MainActivity.class);
                    mact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mact);
                    loadingbar.dismiss();
                }
                else {
                    String erer = task.getException().getMessage();
                    Toast.makeText(PostActivity.this,erer,Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
                }

        });

    }


    private void GoToGallery() {  //gallery method

        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,Gallery_pick);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data != null){

            ImageUri = data.getData();
            postimage.setImageURI(ImageUri);

        }


    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            SenToMAinACt();
        }

        return super.onOptionsItemSelected(item);
    }

    private void SenToMAinACt() {
        Intent mact = new Intent(PostActivity.this,MainActivity.class);
        mact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mact);

    }
}
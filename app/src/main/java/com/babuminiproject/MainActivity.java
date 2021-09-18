package com.babuminiproject.diywormhole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, PostRef;
    private StorageReference storageReference;

    private NavigationView navigationView;
    private BottomNavigationView bottom_menu_nav;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle action_bar_toogle;
    private RecyclerView postlist;
    private Toolbar mtoolbar;
    private TextView full_name;
    public String currentUserID;
    private CircularImageView navimageprof;
    private ImageView pricingshowbtn;
    public int sm = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        bottom_menu_nav = (BottomNavigationView) findViewById(R.id.nav_bottom_menu);
        bottom_menu_nav.setSelectedItemId(R.id.home_navbottomid);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        action_bar_toogle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(action_bar_toogle);
        action_bar_toogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postlist = (RecyclerView) findViewById(R.id.all_users_post_list);
        postlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        full_name = (TextView) navView.findViewById(R.id.nav_full_name);
        navimageprof = (CircularImageView) navView.findViewById(R.id.navprofileiconcirlce);

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("fullname")) {
                    String fname = snapshot.child("fullname").getValue().toString();
                    full_name.setText("@" + fname);
                }
                if (snapshot.hasChild("profileimage")) {

                    String pimagee = snapshot.child("profileimage").getValue().toString();


                    try {
                        Glide.with(MainActivity.this).load(pimagee).placeholder(R.drawable.profileicon).into(navimageprof);

                    } catch (Exception e) {
                        e.printStackTrace();
                        String rtrtrt = e.getMessage();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "profile name or status not found", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMEnuSelector(item);
                return false;

            }
        });


        DisplyAllUsersPost(); //calls method having posts details


        bottom_menu_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.home_navbottomid:
                        item.setCheckable(true);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.serach_navbottomid:
                        item.setCheckable(true);
                        SendUsertoSearchAct();
                        overridePendingTransition(0, 0);
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


    }//main overrride


    private void SendUsertoSearchAct() {
        Intent sact = new Intent(MainActivity.this, search_activity.class);
        startActivity(sact);
    }


    private void DisplyAllUsersPost() {


        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(PostRef, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, postsviewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, postsviewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull postsviewHolder holder, int position, @NonNull Posts model) {

                        final String PostKey = getRef(position).getKey();

                        holder.setDate(model.getDate());
                        holder.setDescription(model.getDescription());
                        holder.setFullname(model.getFullname());
                        holder.setPostimage(getApplicationContext(), model.getPostimage());
                        holder.setTime(model.getTime());
                        holder.setTitle(model.getTitle());
                        holder.setProfileimage(getApplicationContext(), model.getProfileimage());
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent u = new Intent(MainActivity.this, ClickPostActivity.class);
                                u.putExtra("PostKey", PostKey);
                                startActivity(u);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public postsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_post_layout, parent, false);

                        return new postsviewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        postlist.setAdapter(firebaseRecyclerAdapter);

    }




    public class postsviewHolder extends RecyclerView.ViewHolder {

        View mView;

        public postsviewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
           ImageView pricingshowbtn = (ImageView) mView.findViewById(R.id.price_see_btn);
            pricingshowbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            MainActivity.this, R.style.BottomSheetDialogTheme
                    );
                    View bottomSheetView = LayoutInflater.from(getApplicationContext())
                            .inflate(
                                    R.layout.bottom_sheet_price, (LinearLayout) findViewById(R.id.bottomSheetContainer));
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();

                }
            });

        }

        public void setFullname(String fullname) {
            TextView pofullname = (TextView) mView.findViewById(R.id.user_name_post);
            pofullname.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircularImageView poprofimage = (CircularImageView) mView.findViewById(R.id.user_profile_image);
            Glide.with(ctx).load(profileimage).placeholder(R.drawable.profileicon).into(poprofimage);
        }

        public void setTime(String time) {
            TextView potime = (TextView) mView.findViewById(R.id.time_post);
            potime.setText("  .  " + time);
        }

        public void setDate(String date) {
            TextView podate = (TextView) mView.findViewById(R.id.date_post);
            podate.setText(date);
        }

        public void setDescription(String description) {
            TextView podate = (TextView) mView.findViewById(R.id.post_description);
            podate.setText(description);
        }

        public void setTitle(String title) {
            TextView potitle = (TextView) mView.findViewById(R.id.post_title_main);
            potitle.setText(title);
        }
        public void setPostimage(Context conxt, String postimage) {
            ImageView poimage = (ImageView) mView.findViewById(R.id.main_post_image);

            try {
                Glide.with(conxt).load(postimage).placeholder(R.drawable.greybox).into(poimage);

            } catch (Exception e) {
                e.printStackTrace();
                String rtrtrt = e.getMessage();

            }


        }


    }


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUserID == null) {
            sendToLoginActivity();
        } else {
            CheckUserAvailability();

        }

    }


    private void CheckUserAvailability() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.hasChild(current_user_id)) {
                    MoveToSetupAct();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String eerr = error.getMessage();
                Toast.makeText(MainActivity.this, eerr, Toast.LENGTH_SHORT).show();


            }
        });

    }


    private void MoveToSetupAct() {
        Intent setitup = new Intent(MainActivity.this, setup_activity.class);
        startActivity(setitup);

    }


    private void sendToLoginActivity() {

        Intent goaway = new Intent(MainActivity.this, login_activity.class);
        goaway.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goaway);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (action_bar_toogle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void UserMEnuSelector(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;

            case R.id.nav_add_post:
                SendUserToPostAct();
                break;


            case R.id.nav_chats:
                startActivity(new Intent(getApplicationContext(),chat_list_activity.class));
                break;

            case R.id.nav_likes:
                Toast.makeText(this, "no Likes available", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Intent setintent = new Intent(MainActivity.this, settings.class);
                startActivity(setintent);
                sm = 1;
                break;

            case R.id.nav_Help_support:
                Intent setintesdnt = new Intent(MainActivity.this, helpAndSupportactivity.class);
                startActivity(setintesdnt);
                break;

            case R.id.nav_about:
                Intent goabout = new Intent(MainActivity.this, About_activity.class);
                startActivity(goabout);
                break;

            case R.id.nav_logout:

                mAuth.signOut();
                SendUserToLoginACt();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                break;


        }


    }

    private void SendUserToPostAct() {
        Intent pact = new Intent(MainActivity.this, PostActivity.class);
        startActivity(pact);


    }


    private void SendUserToLoginACt() {
        Intent llout = new Intent(MainActivity.this, login_activity.class);
        llout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(llout);
        finish();
    }


}//main activity



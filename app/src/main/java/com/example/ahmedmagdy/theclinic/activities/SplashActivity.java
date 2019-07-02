package com.example.ahmedmagdy.theclinic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.DoctorHome;
import com.example.ahmedmagdy.theclinic.HospitalHome;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.PatientHome;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {
    ImageView myImageView;
    Animation myAnimation;
    private FirebaseAuth mAuth;
    private FirebaseUser fuser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference databaseChat;
    String comefrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        comefrom = getIntent().getStringExtra("comefrom");

/*
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);*/
        setContentView(R.layout.activity_splash);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        /*databaseChat.keepSynced(true);*/
        mAuth = FirebaseAuth.getInstance();
        //get my image
        myImageView = (ImageView) findViewById(R.id.splash_logo);
        /** fuser = FirebaseAuth.getInstance().getCurrentUser();
         if(fuser!= null){
         String token=FirebaseInstanceId.getInstance().getToken();
         // updateToken(FirebaseInstanceId.getInstance().getToken());
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
         Token token1 = new Token(token);
         reference.child(fuser.getUid()).setValue(token1);
         Toast.makeText(SplashActivity.this, token, Toast.LENGTH_SHORT).show();

         }**/
        // load the animation file (my_anim)
      /*  myAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myanime);
        myImageView.startAnimation(myAnimation);*/

        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* Thread.sleep(3000);*/
                    if (fuser == null) {
                        Thread.sleep(4000);
                    } else {
                        Thread.sleep(0000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                     initAuthStateListener();
              /*      Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.putExtra("comefrom", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);*/

                }
            }
        });
        timer.start();
    }

/*    private void initAuthStateListener() {

        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            getallData();
        } else {
            Intent intent = new Intent(SplashActivity.this, PatientHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            // User is signed out
            // Log.d(TAG, "onAuthStateChanged:signed_out");
        }

    }*/

   /* private void getallData() {


        // private void getallData();
        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);


                if (usertype.equals("User")) {
                    Intent iii = new Intent(SplashActivity.this, PatientHome.class);
                    startActivity(iii);
                    finish();
                } else if (usertype.equals("Doctor")) {
                    Intent iii = new Intent(SplashActivity.this, DoctorHome.class);
                    startActivity(iii);
                    finish();
                } else if (usertype.equals("Hospital")) {
                    Intent iii = new Intent(SplashActivity.this, HospitalHome.class);
                    startActivity(iii);
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat.addValueEventListener(postListener1);
    }*/

/*    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }*/
    private void initAuthStateListener() {
        /** mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();**/
        if (mAuth.getCurrentUser() != null) {
            // User is signed in

            // Toast.makeText(LoginActivity.this, "good", Toast.LENGTH_SHORT).show();

            //  fuser = FirebaseAuth.getInstance().getCurrentUser();
            String Token = FirebaseInstanceId.getInstance().getToken();
            //Toast.makeText(LoginActivity.this, Token, Toast.LENGTH_LONG).show();

            //updateToken(Token);

          /*  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token token1 = new Token(Token);
            reference.child(fuser.getUid()).setValue(token1);*/


            getallData();


        } else {

                Intent intent = new Intent(SplashActivity.this, PatientHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

        }
        // }
        // };
    }

    /**
     * @Override public void onStart() {
     * super.onStart();
     * mAuth.addAuthStateListener(mAuthListener);
     * }
     **/


    private void getallData() {


        //**************************************************//
        // private void getallData();

        if (!UtilClass.isNetworkConnected(SplashActivity.this)){
            Toast.makeText(SplashActivity.this, getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
        }

        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                if (mAuth.getCurrentUser() != null) {
                    String usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);

                    if (usertype == null) {
                        initAuthStateListener();
                    } else {
                        if (usertype.equals("User")) {
                            Intent iii = new Intent(SplashActivity.this, PatientHome.class);
                            startActivity(iii);
                            finish();
                        } else if (usertype.equals("Doctor")) {
                            Intent iii = new Intent(SplashActivity.this, DoctorHome.class);
                            startActivity(iii);
                            finish();
                        } else if (usertype.equals("Hospital")) {
                            Intent iii = new Intent(SplashActivity.this, HospitalHome.class);
                            startActivity(iii);
                            finish();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat.addValueEventListener(postListener1);

    }
}

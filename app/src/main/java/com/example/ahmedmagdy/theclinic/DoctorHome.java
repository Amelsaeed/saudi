package com.example.ahmedmagdy.theclinic;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ahmedmagdy.theclinic.DoctorFragments.AllDoctorFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.BookingFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DatabaseFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DoctorProfileFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.MoreFragment;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.PatientFragment.MoreFragmentPatient;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Locale;

public class DoctorHome extends AppCompatActivity {
    private DatabaseReference databaseDoctor, databaseChat, databaseDoctor1, databaseChat1;
    FirebaseUser fuser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        loadLocale();
        setContentView(R.layout.activity_doctor_home);
        final Button Stop = findViewById(R.id.btn_stop);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DoctorFirebaseClass user = dataSnapshot.getValue(DoctorFirebaseClass.class);

                /////////////////////////////////////////////  //////////////////////////////////////////////

                ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {


                        if (user.getStop()) {
                            Stop.setVisibility(View.VISIBLE);

                        } else {
                            Stop.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };
                databaseDoctor.addValueEventListener(postListener1);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BottomNavigationView navigationView = findViewById(R.id.dr_bottom_nav);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new AllDoctorFragment();
                        break;
                    case R.id.nav_database:
                        selectedFragment = new DatabaseFragment();
                        break;
                    case R.id.nav_reservation:
                        selectedFragment = new BookingFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new DoctorProfileFragment();
                        break;
                    case R.id.nav_menu:
                        selectedFragment = new MoreFragmentPatient();
                        break;

                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AllDoctorFragment()).commit();
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Setting", Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

    }

    public void loadLocale() {
        SharedPreferences pref = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang", "");
        setLocale(language);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fuser != null) {
            String Token = FirebaseInstanceId.getInstance().getToken();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token token1 = new Token(Token);
            reference.child(fuser.getUid()).setValue(token1);
        }
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseChat.child(fuser.getUid()).child("status").setValue(true);
        databaseDoctor.child(fuser.getUid()).child("status").setValue(true);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.exitapp);
        alertDialogBuilder
                .setMessage(R.string.click_yes_to_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseChat1 = FirebaseDatabase.getInstance().getReference("ChatRoom");
                                databaseDoctor1 = FirebaseDatabase.getInstance().getReference("Doctordb");
                                databaseChat1.child(fuser.getUid()).child("status").setValue(false);
                                databaseDoctor1.child(fuser.getUid()).child("status").setValue(false);

                                moveTaskToBack(true);
                                Process.killProcess(Process.myPid());
                                System.exit(1);

                            }
                        })

                .setNegativeButton(R.string.noo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
/*    @Override
    public void onResume() {
        super.onResume();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat.keepSynced(true);
        databaseDoctor.keepSynced(true);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseChat.child(fuser.getUid()).child("status").setValue(true);
        databaseDoctor.child(fuser.getUid()).child("status").setValue(true);

    }*/



  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseChat.keepSynced(false);
        databaseDoctor.keepSynced(false);
        databaseDoctor1 = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat1 = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat1.child(fuser.getUid()).child("status").setValue(false);
        databaseDoctor1.child(fuser.getUid()).child("status").setValue(false);
    }
*/


}

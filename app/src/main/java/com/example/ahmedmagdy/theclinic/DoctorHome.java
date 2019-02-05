package com.example.ahmedmagdy.theclinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.DoctorFragments.AllDoctorFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.BookingFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DatabaseFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DoctorProfileFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.MoreFragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.MoreFragmentPatient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorHome extends AppCompatActivity {
    private DatabaseReference databaseDoctor, databaseChat,databaseDoctor1, databaseChat1;
    FirebaseUser fuser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);


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

    @Override
    public void onResume() {
        super.onResume();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat.keepSynced(true);
        databaseDoctor.keepSynced(true);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseChat.child(fuser.getUid()).child("status").setValue(true);
        databaseDoctor.child(fuser.getUid()).child("status").setValue(true);

    }
    


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseChat.keepSynced(false);
        databaseDoctor.keepSynced(false);
        databaseDoctor1 = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat1 = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat1.child(fuser.getUid()).child("status").setValue(false);
        databaseDoctor1.child(fuser.getUid()).child("status").setValue(false);
    }
}

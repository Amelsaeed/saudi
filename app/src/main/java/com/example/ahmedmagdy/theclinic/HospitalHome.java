package com.example.ahmedmagdy.theclinic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.ahmedmagdy.theclinic.DoctorFragments.AllDoctorFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.MoreFragment;
import com.example.ahmedmagdy.theclinic.HospitalFragment.HospitalAllDoctorFragment;
import com.example.ahmedmagdy.theclinic.HospitalFragment.HospitalMyDoctorFragment;
import com.example.ahmedmagdy.theclinic.HospitalFragment.HospitalProfileFragment;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HospitalHome extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeLayout;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);
        frameLayout = (FrameLayout)findViewById(R.id.fragment_container_hospital);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(this);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hospital,
                    new HospitalAllDoctorFragment()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_home_hospital:
                            selectedFragment = new AllDoctorFragment();
                            break;
                        case R.id.navigation_my_doctor:
                            selectedFragment = new HospitalMyDoctorFragment();
                            break;
                        case R.id.navigation_profile_hpspetal:
                            selectedFragment = new HospitalProfileFragment();
                            break;
                        case R.id.navigation_menu_hospital:
                            selectedFragment = new MoreFragment();
                 /*           FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                            Intent intent = new Intent(HospitalHome.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();*/
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hospital,
                            selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void onRefresh() {

       startActivity(new Intent(this,HospitalHome.class));
        swipeLayout.setRefreshing(false);

    }
}

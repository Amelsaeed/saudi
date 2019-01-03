package com.example.ahmedmagdy.theclinic;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.HospitalFragment.HospitalAllDoctorFragment;
import com.example.ahmedmagdy.theclinic.HospitalFragment.HospitalMyDoctorFragment;

public class HospitalHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

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
                            selectedFragment = new HospitalAllDoctorFragment();
                            break;
                        case R.id.navigation_my_doctor:
                            selectedFragment = new HospitalMyDoctorFragment();
                            break;
                        case R.id.navigation_profile_hpspetal:
                            selectedFragment = new HospitalMyDoctorFragment();
                            break;
                        case R.id.navigation_menu_hospital:
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hospital,
                            selectedFragment).commit();

                    return true;
                }
            };

}

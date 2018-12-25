package com.example.ahmedmagdy.theclinic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.DoctorFragments.AllDoctorFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.BookingFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DatabaseFragment;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DoctorProfileFragment;

public class DoctorHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}

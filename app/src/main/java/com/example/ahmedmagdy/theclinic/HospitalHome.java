package com.example.ahmedmagdy.theclinic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import com.example.ahmedmagdy.theclinic.PatientFragment.MoreFragmentPatient;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class HospitalHome extends AppCompatActivity  {

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);
        frameLayout = (FrameLayout)findViewById(R.id.fragment_container);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
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
                            selectedFragment = new MoreFragmentPatient();
                 /*           FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                            Intent intent = new Intent(HospitalHome.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();*/
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Setting",Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();

    }
    public void  loadLocale(){
        SharedPreferences pref = getSharedPreferences("Setting",Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang","");
        setLocale(language);
    }
}

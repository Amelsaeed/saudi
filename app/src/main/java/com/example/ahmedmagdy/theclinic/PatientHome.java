package com.example.ahmedmagdy.theclinic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.ChatRoomFragments.LoginAFragment;
import com.example.ahmedmagdy.theclinic.ChatRoomFragments.RegisterFragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.AllDoctorfragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.DoctorBookingsFragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.FavFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PatientHome extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new AllDoctorfragment();

                    loadFragment(fragment);
                    return true;
                case R.id.navigation_favorite:
                    fragment = new FavFragment();

                    loadFragment(fragment);
                    return true;

                case R.id.navigation_profile:
                        fragment = new DoctorBookingsFragment();
                        loadFragment(fragment);
                    return true;
                case R.id.navigation_menu:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    new AllDoctorfragment()).commit();
        }
    }

    private void loadFragment(Fragment fragment)
    // load fragment
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}

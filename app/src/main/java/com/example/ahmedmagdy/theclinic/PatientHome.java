package com.example.ahmedmagdy.theclinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.PatientFragment.AllDoctorfragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.FavFragment;
import com.example.ahmedmagdy.theclinic.activities.CalenderActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.RegestrationPathActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.ahmedmagdy.theclinic.PatientFragment.LoginAFragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.UserBookingFragment;

public class PatientHome extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new AllDoctorfragment();

                    loadFragment(fragment);
                    return true;
                case R.id.navigation_favorite:
                    if (user == null) {
                        // fragment = new RegisterFragment();
                        // loadFragment(fragment);
                        Intent it = new Intent(PatientHome.this, LoginActivity.class);
                        startActivity(it);
                    } else {
                       fragment = new FavFragment();

                    loadFragment(fragment);}
                    return true;

                case R.id.navigation_profile:
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        // fragment = new RegisterFragment();
                       // loadFragment(fragment);
                        Intent it = new Intent(PatientHome.this, RegestrationPathActivity.class);
                        startActivity(it);
                    } else {
                          fragment = new LoginAFragment();
                        loadFragment(fragment);
                    }

                    return true;
                case R.id.navigation_menu:
                        fragment = new LoginAFragment();
                        loadFragment(fragment);

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

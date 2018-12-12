package com.example.ahmedmagdy.theclinic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.Fragments.LoginAFragment;
import com.example.ahmedmagdy.theclinic.Fragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;


    private ActionBar toolbar;
    BottomNavigationView navigationView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //  fragment = new AllDoctorActivity();

                    loadFragment(fragment);
                    return true;
                case R.id.navigation_favorite:
                    //      fragment = new FavActivity();

                    loadFragment(fragment);
                    return true;
                case R.id.navigation_search:
                    //   fragment = new FavActivity();

                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                         fragment = new RegisterFragment();
                        loadFragment(fragment);
                    } else {
                          fragment = new LoginAFragment();
                        loadFragment(fragment);
                    }
                    return true;
                case R.id.navigation_Dates:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private void loadFragment(Fragment fragment)
    // load fragment
    { FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();}






}

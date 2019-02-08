package com.example.ahmedmagdy.theclinic;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.PatientFragment.AllDoctorfragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.FavFragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.MoreFragmentPatient;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.RegisterDoctorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.ahmedmagdy.theclinic.PatientFragment.UserBookingFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PatientHome extends AppCompatActivity {
    ValueEventListener seenListener;
    private DatabaseReference databaseChat, databaseChat1,databaseChat2;
    FirebaseUser fuser;
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
                        Intent it = new Intent(PatientHome.this, LoginActivity.class);
                        it.putExtra("comefrom", "2");

                        startActivity(it);
                    } else {
                        fragment = new FavFragment();

                        loadFragment(fragment);
                    }
                    return true;

                case R.id.Reservations:
                    if (user == null) {
                        Intent it = new Intent(PatientHome.this, LoginActivity.class);
                        it.putExtra("comefrom", "2");

                        startActivity(it);
                    } else {
                        fragment = new UserBookingFragment();
                        loadFragment(fragment);
                    }

                    return true;
                case R.id.navigation_menu:

                    fragment = new MoreFragmentPatient();
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
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AllDoctorfragment()).commit();
        }
    }

    private void loadFragment(Fragment fragment)
    // load fragment
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void status(boolean status){
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        databaseChat.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fuser != null) {
            status(true);
        }

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
                                if (fuser !=null) {
                                    databaseChat1 = FirebaseDatabase.getInstance().getReference("ChatRoom");
                                    databaseChat1.child(fuser.getUid()).child("status").setValue(false);
                                }
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
/*    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    public void status(Boolean b) {
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", b);

        databaseChat.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fuser != null) {
            status(true);
        }
        // currentUser(userid);
    }*/
/*
    @Override
    protected void onPause() {
        super.onPause();

    }*/

/*    @Override
    public void onResume() {
        super.onResume();
        databaseChat.child(fuser.getUid()).child("status").setValue(true);
    }*/

/*    @Override
    protected void onStop() {
        super.onStop();

    }*/


 /*   @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(PatientHome.this, "Finsh", Toast.LENGTH_LONG).show();
//        databaseChat.removeEventListener(seenListener);
        databaseChat.keepSynced(false);
        status(false);
        currentUser("none");
    }*/

/*    @Override
    public void onPause() {
        super.onPause();
        databaseChat.child(fuser.getUid()).child("status").setValue(false);
    }*/

}

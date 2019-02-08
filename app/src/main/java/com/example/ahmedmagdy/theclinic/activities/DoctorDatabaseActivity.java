package com.example.ahmedmagdy.theclinic.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.DoctorDatabaseAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DoctorDatabaseActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databasePatient;
    private DatabaseReference databaseUserReg,databaseDoctor,databaseChat;

    String UserType;
    SearchView searchView;
    TextView usernamef;
    private ProgressBar progressBar;
    private List<BookingTimesClass> doctorList;
    ListView listViewpatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_database);



        usernamef=findViewById(R.id.user_name_data);
        progressBar =  findViewById(R.id.data_progress_bar);
        mAuth = FirebaseAuth.getInstance();
         databasePatient = FirebaseDatabase.getInstance().getReference("Doctorpatientdb")
                .child(mAuth.getCurrentUser().getUid()); databasePatient.keepSynced(true);

        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data"); databaseUserReg.keepSynced(true);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb"); databaseDoctor.keepSynced(true);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom"); databaseChat.keepSynced(true);


        listViewpatient= findViewById(R.id.list_view_data);
        searchView =  findViewById(R.id.searchdata);
        doctorList=new ArrayList<>();
        listViewpatient.setTextFilterEnabled(true);
        removeFocus();
        getusername();
    }
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        maketable();
    }
    private void maketable() {

        if (UtilClass.isNetworkConnected(DoctorDatabaseActivity.this)) {

            databasePatient.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    doctorList.clear();
                    for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                        BookingTimesClass bookingtimesclass=doctorSnapshot.getValue(BookingTimesClass.class);
                        final String PID=bookingtimesclass.getCtid();
                        final String LastBookingDate=bookingtimesclass.getCtdate();
                        // Toast.makeText(FavActivity.this, DID, Toast.LENGTH_LONG).show();
                        ///////////////////////////////////////////
                        final ValueEventListener postListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {

                                String Pname = dataSnapshot1.child(PID).child("cname").getValue(String.class);
                                String Pphone = dataSnapshot1.child(PID).child("cphone").getValue(String.class);
                                String Puri = dataSnapshot1.child(PID).child("cUri").getValue(String.class);

                                BookingTimesClass doctorclass = new BookingTimesClass(PID, Pname, LastBookingDate,Pphone, Puri);
                                doctorList.add(0,doctorclass);// i= 0  (index)to start from top

                                DoctorDatabaseAdapter adapter = new DoctorDatabaseAdapter(DoctorDatabaseActivity.this, doctorList);
                                listViewpatient.setAdapter(adapter);
                                setupSearchView();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                            }
                        };
                        databaseUserReg .addValueEventListener(postListener1);
                        //////////////////////////////////////////////////////



                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }else {
            Toast.makeText(this, getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }

    }

    private void getusername() {

if (UtilClass.isNetworkConnected(DoctorDatabaseActivity.this)){
        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String UserName = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                UserType = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);

                if(UserName != null) {
                    usernamef.setText(UserName);
                }else{usernamef.setText(R.string.name);}


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat .addValueEventListener(postListener1);
}else{
    Toast.makeText(this,  getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
}
    }
    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    listViewpatient.clearTextFilter();
                } else {
                    listViewpatient.setFilterText(newText);
                }
                return true;
            }
        });
    }

    private void removeFocus() {
        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
}

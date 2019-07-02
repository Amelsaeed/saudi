package com.example.ahmedmagdy.theclinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
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

public class FavActivity extends AppCompatActivity {
    ImageView favDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctorFav;
    private DatabaseReference databaseUserReg,databaseDoctor,databaseChat;
    Button alldoctors,book;

    String UserType;
    SearchView searchView;
     TextView usernamef;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);


        book = (Button) findViewById(R.id.book);
       // alldoctors = (Button) findViewById(R.id.all_doc_btn);
        favDoctorButton = (ImageView) findViewById(R.id.alldoctor);
        usernamef=findViewById(R.id.user_name);
        progressBar = (ProgressBar) findViewById(R.id.fav_progress_bar);
        mAuth = FirebaseAuth.getInstance();

        databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits")
                .child(mAuth.getCurrentUser().getUid());/*databaseDoctorFav.keepSynced(true);*/
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");/*databaseDoctor.keepSynced(true);*/
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");


        listViewDoctor= (ListView)findViewById(R.id.list_view_fav);
        searchView = (SearchView) findViewById(R.id.searchfav);
        doctorList=new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(true);
        removeFocus();
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FavActivity.this, UserBookingActivity.class);
                finish();
                startActivity(it);

            }
        });
    /**    alldoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FavActivity.this, AllDoctorActivity.class);
                finish();
                startActivity(it);

            }
        });**/
        favDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(  UserType.equals("User")){
                Intent it = new Intent(FavActivity.this, UserProfileActivity.class);
                startActivity(it);
                }else if(  UserType.equals("Doctor")){
                    Intent uIntent = new Intent(FavActivity.this, DoctorProfileActivity.class);
                    uIntent.putExtra("DoctorID",mAuth.getCurrentUser().getUid());
                    uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(uIntent);
                }
            }
        });
        getusername();
    }
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        maketable();
    }


    private void maketable() {

       if (UtilClass.isNetworkConnected(FavActivity.this)) {
           // databaseDoctorFav.keepSynced(true);
           // databaseDoctor.keepSynced(true);

            databaseDoctorFav.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    doctorList.clear();
                    for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                        DoctorFirebaseClass doctorclass=doctorSnapshot.getValue(DoctorFirebaseClass.class);
                        final String DID=doctorclass.getcId();
                        final boolean checked=doctorclass.getChecked();
                       // Toast.makeText(FavActivity.this, DID, Toast.LENGTH_LONG).show();
                        ///////////////////////////////////////////
                        final ValueEventListener postListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {

                                String DName = dataSnapshot1.child(DID).child("cName").getValue(String.class);
                                String DSpecialty = dataSnapshot1.child(DID).child("cSpecialty").getValue(String.class);
                                String DCity = dataSnapshot1.child(DID).child("cCity").getValue(String.class);
                                String DUri = dataSnapshot1.child(DID).child("cUri").getValue(String.class);

                                String DInsurance = dataSnapshot1.child(DID).child("cInsurance").getValue(String.class);
                                String DPrice = dataSnapshot1.child(DID).child("cPrice").getValue(String.class);
                                String DDegree = dataSnapshot1.child(DID).child("cDegree").getValue(String.class);
                                String HospitalID = dataSnapshot1.child(DID).child("cHospitalID").getValue(String.class);

                                String DType = dataSnapshot1.child(DID).child("cType").getValue(String.class);
                                DoctorFirebaseClass doctorclass = new DoctorFirebaseClass(DID, DName, DSpecialty, DCity, DUri,DInsurance,DDegree,DPrice,checked,HospitalID,DType,DType,false,false);
                                doctorList.add(doctorclass);// i= 0  (index)to start from top
                                DoctorAdapter adapter = new DoctorAdapter(FavActivity.this, doctorList);
                                listViewDoctor.setAdapter(adapter);
                                setupSearchView();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                            }
                        };
                        databaseDoctor .addValueEventListener(postListener1); //databaseDoctor.keepSynced(true);
                        //////////////////////////////////////////////////////



                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });databaseDoctorFav.keepSynced(true);

       }else
       {
           Toast.makeText(this, getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
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
                    listViewDoctor.clearTextFilter();
                } else {
                    listViewDoctor.setFilterText(newText);
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
    private void getusername() {

if (UtilClass.isNetworkConnected(FavActivity.this)){
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
}
    }
    @Override
    public void onBackPressed() {
        Intent it = new Intent(FavActivity.this, AllDoctorActivity.class);
        startActivity(it);
    }
}

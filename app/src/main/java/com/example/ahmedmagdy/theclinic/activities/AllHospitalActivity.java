package com.example.ahmedmagdy.theclinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AllHospitalActivity extends AppCompatActivity {
    ImageView addDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctorFav;
    DatabaseReference databaseDoctor;
    FirebaseUser fuser;
    private ImageView btnproceed;
	
    SearchView searchView;
    private Filter filter;
    private boolean isSearching = false;
   // Button addTrampButton;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;
    private List<DoctorFirebaseClass> favList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctor);

        addDoctorButton = (ImageView) findViewById(R.id.adddoctor);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.home_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseDoctor.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        listViewDoctor= (ListView)findViewById(R.id.list_view_doctor);
        searchView = (SearchView) findViewById(R.id.search);
        doctorList=new ArrayList<>();
        favList=new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(true);
        removeFocus();
        btnproceed= (ImageView) findViewById(R.id.map);

        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AllHospitalActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuser != null) {
                    Intent it = new Intent(AllHospitalActivity.this, FavActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(AllHospitalActivity.this, "You should log in firstly", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(AllHospitalActivity.this, LoginActivity.class);
                    startActivity(it);
                }
            }
        });
//        updateToken(FirebaseInstanceId.getInstance().getToken());
    }
  /**  private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }**/
    @Override
    protected void onStart() {
        super.onStart();

        if (isSearching){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits").child(mAuth.getCurrentUser().getUid());
            databaseDoctorFav.keepSynced(true);

            maketableoffav();
        } else {
            maketableofall();
        }


    }

    private void maketableofall() {

      if (!UtilClass.isNetworkConnected(getApplicationContext())) {
          Toast.makeText(this, getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
      }

        databaseDoctor.orderByChild("cType").equalTo("Hospital").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        doctorList.clear();
                        for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                        DoctorFirebaseClass doctorclass=doctorSnapshot.getValue(DoctorFirebaseClass.class);


                            if (isFavourite(doctorclass)) {
                                doctorclass.checked = true;
                            }

                            doctorList.add(0,doctorclass);/// i= 0  (index)to start from top
                          }

                        DoctorAdapter adapter = new DoctorAdapter(AllHospitalActivity.this, doctorList);
                        //adapter.notifyDataSetChanged();
                        filter = adapter.getFilter();
                        listViewDoctor.setAdapter(adapter);
                        setupSearchView();
                        progressBar.setVisibility(View.GONE);
                        // listViewTramp.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


      /**  } else {
            Toast.makeText(AllDoctorActivity.this, "please check the network connection", Toast.LENGTH_LONG).show();
        }**/
    }
    private void maketableoffav() {

       if (UtilClass.isNetworkConnected(AllHospitalActivity.this)) {

        databaseDoctorFav.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        DoctorFirebaseClass doctorclass = doctorSnapshot.getValue(DoctorFirebaseClass.class);
                        favList.add(0, doctorclass);// i= 0  (index)to start from top
                    }
                }
                maketableofall();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

       }
        /**  } else {
         Toast.makeText(AllDoctorActivity.this, "please check the network connection", Toast.LENGTH_LONG).show();
         }**/
    }


    private boolean isFavourite(DoctorFirebaseClass doctorclass) {
        for (DoctorFirebaseClass f : favList) {
            if (f.cId.equals(doctorclass.cId)) {
                return true;
            }
        }
        return false;
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
                    filter.filter("");
                    isSearching = false;
                } else {
                    filter.filter(newText);
                    isSearching = true;
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

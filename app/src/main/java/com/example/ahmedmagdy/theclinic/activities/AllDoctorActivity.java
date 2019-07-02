package com.example.ahmedmagdy.theclinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.example.ahmedmagdy.theclinic.map.DoctorMapFrag;
import com.example.ahmedmagdy.theclinic.map.UserLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AllDoctorActivity extends AppCompatActivity {
    ImageView addDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor,databaseDoctorFav;
    FirebaseUser fuser;
    private ImageView btnproceed;
	
    SearchView searchView;
    Filter filter;
    private boolean isSearching = false;
   // Button addTrampButton;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;
    private List<DoctorFirebaseClass> favList;
    //gps
    public static ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    //gps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctor);

        addDoctorButton = (ImageView) findViewById(R.id.adddoctor);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.home_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        /*databaseDoctor.keepSynced(true);*/
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        listViewDoctor= (ListView)findViewById(R.id.list_view_doctor);
        searchView = (SearchView) findViewById(R.id.search);
        doctorList=new ArrayList<>();
        favList=new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(false);
        removeFocus();
        btnproceed= (ImageView) findViewById(R.id.map);

        //get all data
        getAllDoctorsMap();

        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorMapFrag fragment = DoctorMapFrag.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("intent_user_locs",mUserLocations);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container_alldoctorsfrag, fragment , "User List");
                transaction.addToBackStack("User List");
                transaction.commit();
                /*Intent i=new Intent(AllDoctorActivity.this,MapsActivity.class);
                startActivity(i);*/
            }
        });

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuser != null) {
                    Intent it = new Intent(AllDoctorActivity.this, FavActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(AllDoctorActivity.this, R.string.you_should_log_in_firstly, Toast.LENGTH_LONG).show();
                    Intent it = new Intent(AllDoctorActivity.this, LoginActivity.class);
                    startActivity(it);
                }
            }
        });
//        updateToken(FirebaseInstanceId.getInstance().getToken());
    }
    //get all doc data
    private void getAllDoctorsMap() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("DoctorMap");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("qerrrrrrrrrrry Count " + "" + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserLocation post = postSnapshot.getValue(UserLocation.class);
                    mUserLocations.add(post);
                    System.out.println("qerrrrrrrrrrry Get Data" + post.getCmname());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);


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
            /*databaseDoctorFav.keepSynced(true);*/

            maketableoffav();
        } else {
            maketableofall();
        }


    }

    private void maketableofall() {

       if (UtilClass.isNetworkConnected(getApplicationContext())) {

          databaseDoctor.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        doctorList.clear();
                        for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                        DoctorFirebaseClass doctorclass=doctorSnapshot.getValue(DoctorFirebaseClass.class);


                            if (isFavourite(doctorclass)) {
                                doctorclass.checked = true;
                            }

                            doctorList.add(doctorclass);/// i= 0  (index)to start from top
                          }

                        DoctorAdapter adapter = new DoctorAdapter(AllDoctorActivity.this, doctorList);
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

           }
      /**  } else {
            Toast.makeText(AllDoctorActivity.this, "please check the network connection", Toast.LENGTH_LONG).show();
        }**/
    }
    private void maketableoffav() {

       if (UtilClass.isNetworkConnected(getApplicationContext())) {

        databaseDoctorFav.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        DoctorFirebaseClass doctorclass = doctorSnapshot.getValue(DoctorFirebaseClass.class);
                        favList.add( doctorclass);// i= 0  (index)to start from top
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

package com.example.ahmedmagdy.theclinic.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class AllHospitalFragment extends Fragment {
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

    public AllHospitalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        addDoctorButton = (ImageView) view.findViewById(R.id.adddoctor);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = (ProgressBar) view.findViewById(R.id.home_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        /*databaseDoctor.keepSynced(true);*/
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        listViewDoctor = (ListView) view.findViewById(R.id.list_view_doctor);
        searchView = (SearchView) view.findViewById(R.id.search);
        doctorList = new ArrayList<>();
        favList = new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(true);
        removeFocus();
        btnproceed = (ImageView) view.findViewById(R.id.map);

        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
            }
        });

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuser != null) {
                    Intent it = new Intent(getActivity(), FavActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(getActivity(), R.string.you_should_log_in_firstly, Toast.LENGTH_LONG).show();
                    Intent it = new Intent(getActivity(), LoginActivity.class);
                    startActivity(it);
                }
            }
        });
//        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    /**
     * private void updateToken(String token){
     * DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
     * Token token1 = new Token(token);
     * reference.child(fuser.getUid()).setValue(token1);
     * }
     **/
    @Override
    public void onStart() {
        super.onStart();

        if (isSearching) {
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
        if (getActivity() == null) {
            return;
        }
        if (UtilClass.isNetworkConnected(getActivity().getApplicationContext())) {
            Toast.makeText(getActivity(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }

        databaseDoctor.orderByChild("cType").equalTo("Hospital").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctorList.clear();
                for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                    DoctorFirebaseClass doctorclass = doctorSnapshot.getValue(DoctorFirebaseClass.class);


                    if (isFavourite(doctorclass)) {
                        doctorclass.checked = true;
                    }

                    doctorList.add( doctorclass);/// i= 0  (index)to start from top
                }

                DoctorAdapter adapter = new DoctorAdapter(getActivity(), doctorList);
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

        if (UtilClass.isNetworkConnected(getActivity())) {

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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

}

package com.example.ahmedmagdy.theclinic.HospitalFragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.FavActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.MapsActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HospitalAllDoctorFragment extends Fragment implements View.OnClickListener{
    ImageView addDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor,databaseDoctorFav;
    FirebaseUser fuser;
    private ImageView btnproceed;

    SearchView searchView;
    // Button addTrampButton;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;
    private List<DoctorFirebaseClass> favList;

    public HospitalAllDoctorFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_all_doctor, container, false);
        addDoctorButton = (ImageView) rootView.findViewById(R.id.adddoctor);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = (ProgressBar)  rootView.findViewById(R.id.home_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseDoctor.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        listViewDoctor= (ListView) rootView.findViewById(R.id.list_view_doctor);
        searchView = (SearchView)  rootView.findViewById(R.id.search);
        doctorList=new ArrayList<>();
        favList=new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(true);
        removeFocus();
        btnproceed= (ImageView)  rootView.findViewById(R.id.map);
        btnproceed.setOnClickListener(this);
        return rootView;
    }

    /**  private void updateToken(String token){
     DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
     Token token1 = new Token(token);
     reference.child(fuser.getUid()).setValue(token1);
     }**/
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits").child(mAuth.getCurrentUser().getUid());
            databaseDoctorFav.keepSynced(true);

            maketableoffav();
        } else {
            maketableofall();
        }


    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        } else {
            return false;
        }
    }

    private void maketableofall() {

        // if (isNetworkConnected()) {

        databaseDoctor.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctorList.clear();
                for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                    DoctorFirebaseClass doctorclass=doctorSnapshot.getValue(DoctorFirebaseClass.class);


                    if (isFavourite(doctorclass)) {
                        doctorclass.checked = true;
                    }

                    doctorList.add(0,doctorclass);// i= 0  (index)to start from top
                }

                DoctorAdapter adapter = new DoctorAdapter(getActivity(), doctorList);
                //adapter.notifyDataSetChanged();
                listViewDoctor.setAdapter(adapter);
                setupSearchView();
                progressBar.setVisibility(View.GONE);
                // listViewTramp.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //  }// network
        /**  } else {
         Toast.makeText(AllDoctorActivity.this, "please check the network connection", Toast.LENGTH_LONG).show();
         }**/
    }
    private void maketableoffav() {

        // if (isNetworkConnected()) {

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

        //  }// network
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentTransaction replace = transaction.replace(R.id.frame_container, new MapsActivity());
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }
}
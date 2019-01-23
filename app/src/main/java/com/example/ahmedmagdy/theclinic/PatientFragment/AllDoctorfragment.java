package com.example.ahmedmagdy.theclinic.PatientFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AllDoctorfragment extends Fragment implements View.OnClickListener{
    ImageView addDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor,databaseDoctorFav;
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
    //gps
    public static ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    //gps

    public AllDoctorfragment() {
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
        listViewDoctor.setTextFilterEnabled(false);
        removeFocus();
        btnproceed= (ImageView)  rootView.findViewById(R.id.map);
        //get all data
        getAllDoctorsMap();

        btnproceed.setOnClickListener(this);
        return rootView;


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
    public void onStart() {
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

    if (!UtilClass.isNetworkConnected(getContext())) {
        Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
    }

        databaseDoctor.orderByChild("cType").equalTo("Doctor").addListenerForSingleValueEvent(new ValueEventListener() {

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

        //  }// network

    }
    private void maketableoffav() {
        if (!UtilClass.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map:
                DoctorMapFrag fragment = DoctorMapFrag.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("intent_user_locs",mUserLocations);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container_alldoctorsfrag, fragment , "User List");
                transaction.addToBackStack("User List");
                transaction.commit();
                break;
    }
}
}
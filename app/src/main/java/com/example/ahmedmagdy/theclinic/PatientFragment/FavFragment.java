package com.example.ahmedmagdy.theclinic.PatientFragment;



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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.DoctorProfileActivity;
import com.example.ahmedmagdy.theclinic.activities.UserProfileActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment {
    ImageView favDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctorFav,databaseHospital;
    private DatabaseReference databaseUserReg,databaseDoctor,databaseChat;
    Button alldoctors,book;
    View rootView;

    String UserType;
    SearchView searchView;
    TextView usernamef;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = getLayoutInflater().inflate(R.layout.activity_fav, container, false);

        //book = (Button) getActivity().findViewById(R.id.book);
        //  alldoctors = (Button) getActivity().findViewById(R.id.all_doc_btn);
        favDoctorButton = (ImageView) rootView.findViewById(R.id.alldoctor);
        usernamef=rootView.findViewById(R.id.user_name);
        progressBar = (ProgressBar)rootView. findViewById(R.id.fav_progress_bar);
        mAuth = FirebaseAuth.getInstance();


        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");databaseDoctor.keepSynced(true);
        databaseHospital = FirebaseDatabase.getInstance().getReference("Hospitaldb");databaseHospital.keepSynced(true);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");


        listViewDoctor= (ListView)rootView.findViewById(R.id.list_view_fav);
        TextView noDataMsg = rootView.findViewById(R.id.no_data_msg);
        listViewDoctor.setTextFilterEnabled(true);
        listViewDoctor.setEmptyView(noDataMsg);
        searchView = (SearchView) rootView.findViewById(R.id.searchfav);
        doctorList=new ArrayList<>();
        removeFocus();
        /**  book.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        //  ft.replace(R.id.container2, new UserBookingFragment());
        ft.commit();



        }
        });**/

        favDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(  UserType.equals("User")){
                    Intent it = new Intent(getActivity(), UserProfileActivity.class);
                    startActivity(it);
                }else if(  UserType.equals("Doctor")){
                    Intent uIntent = new Intent(getActivity(), DoctorProfileActivity.class);
                    uIntent.putExtra("DoctorID",mAuth.getCurrentUser().getUid());
                    uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(uIntent);
                }
            }
        });
        getusername();
        return rootView;
    }



    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);

            maketable();

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

    private void maketable() {
      //  if (mAuth.getCurrentUser().getUid() != null) {
        // if (isNetworkConnected()) {
        // databaseDoctorFav.keepSynced(true);
        // databaseDoctor.keepSynced(true);
        databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits")
                .child(mAuth.getCurrentUser().getUid());databaseDoctorFav.keepSynced(true);
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
                            DoctorFirebaseClass doctorclass = new DoctorFirebaseClass(DID, DName, DSpecialty, DCity, DUri,DInsurance,DDegree,DPrice,checked,HospitalID,DType,DType);
                            doctorList.add(0,doctorclass);// i= 0  (index)to start from top

                            DoctorAdapter adapter = new DoctorAdapter(getActivity(), doctorList);
                            listViewDoctor.setAdapter(adapter);
                            setupSearchView();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressBar.setVisibility(View.GONE);                        }

                    };
                    databaseDoctor.addValueEventListener(postListener1); //databaseDoctor.keepSynced(true);
                    progressBar.setVisibility(View.GONE);
                    //////////////////////////////////////////////////////
               /**
                    final ValueEventListener postListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {

                            String DName = dataSnapshot2.child(DID).child("cName").getValue(String.class);
                            String DSpecialty = dataSnapshot2.child(DID).child("cSpecialty").getValue(String.class);
                            String DCity = dataSnapshot2.child(DID).child("cCity").getValue(String.class);
                            String DUri = dataSnapshot2.child(DID).child("cUri").getValue(String.class);

                            String DInsurance = dataSnapshot2.child(DID).child("cInsurance").getValue(String.class);
                            String DPrice = dataSnapshot2.child(DID).child("cPrice").getValue(String.class);
                            String DDegree = dataSnapshot2.child(DID).child("cDegree").getValue(String.class);
                            String HospitalID = dataSnapshot2.child(DID).child("cHospitalID").getValue(String.class);

                            DoctorFirebaseClass doctorclass = new DoctorFirebaseClass(DID, DName, DSpecialty, DCity, DUri,DInsurance,DDegree,DPrice,checked,HospitalID);
                            doctorList.add(0,doctorclass);// i= 0  (index)to start from top

                            DoctorAdapter adapter = new DoctorAdapter(getActivity(), doctorList);
                            listViewDoctor.setAdapter(adapter);
                            setupSearchView();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                        }
                    };
                    databaseHospital .addValueEventListener(postListener2);**/
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });databaseDoctorFav.keepSynced(true);
        progressBar.setVisibility(View.GONE);
     //  }

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
    private void getusername() {


        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String UserName = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                UserType = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);

                if(UserName != null) {
                    usernamef.setText(UserName);
                }else{usernamef.setText("Name");}


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat .addValueEventListener(postListener1);
    }

}

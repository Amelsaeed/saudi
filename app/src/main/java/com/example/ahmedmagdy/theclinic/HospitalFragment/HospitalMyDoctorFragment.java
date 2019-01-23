package com.example.ahmedmagdy.theclinic.HospitalFragment;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.RegisterDoctorActivity;
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


public class HospitalMyDoctorFragment extends Fragment {
    ImageButton adddoctor;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseHospital, databaseChat;


    private DatabaseReference databaseDoctorFav;
    private DatabaseReference databaseUserReg,databaseDoctor;
    String UserType;
    SearchView searchView1;
    private Filter filter;
    private boolean isSearching = false;
    TextView usernamef;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hospital_my_doctor, container, false);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        databaseHospital = FirebaseDatabase.getInstance().getReference("Hospitaldb");
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");




        databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits")
                .child(mAuth.getCurrentUser().getUid());databaseDoctorFav.keepSynced(true);
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");databaseDoctor.keepSynced(true);


        listViewDoctor= (ListView)rootView.findViewById(R.id.list_view_fav);
       searchView1 = (SearchView) rootView.findViewById(R.id.search_doctor);
        doctorList=new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(false);
       removeFocus();


        progressBar = (ProgressBar) rootView.findViewById(R.id.fav_progress_bar);
        adddoctor = rootView.findViewById(R.id.add_doctor);

        adddoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        ////////////////////////////////////
                            final ValueEventListener postListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot1) {

                                    String HName = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cName").getValue(String.class);
                                    String HID = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cId").getValue(String.class);
                                    String HPassword = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cUriID").getValue(String.class);
                                    String HEmail = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cEmail").getValue(String.class);

                                    Intent intent = new Intent(getContext(),RegisterDoctorActivity.class);
                                    intent.putExtra("ComeFrom", "Hospital");
                                    intent.putExtra("HName", HName);
                                    intent.putExtra("HospitalID", HID);
                                    intent.putExtra("HospitalPassword", HPassword);
                                    intent.putExtra("HospitalEmail", HEmail);


                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                }
                            };
                databaseDoctor .addValueEventListener(postListener1); //databaseDoctor.keepSynced(true);
                            //////////////////////////////////////////////////////

            }
        });


        return rootView;
    }

    public void onStart() {
        super.onStart();

        if (isSearching){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        maketable();

    }

    private void maketable() {
        //  if (mAuth.getCurrentUser().getUid() != null) {
        if (UtilClass.isNetworkConnected(getContext())) {
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
                            if (DID ==null){return;}
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
                            filter = adapter.getFilter();
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
        });

         }else {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }

    }

    private void setupSearchView() {
        searchView1.setIconifiedByDefault(false);

        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        searchView1.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView1.getWindowToken(), 0);
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

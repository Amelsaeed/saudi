package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class DatabaseFragment extends Fragment {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databasePatient;
    private DatabaseReference databaseUserReg, databaseDoctor, databaseChat;
    private ValueEventListener mEventListener, patientEventListener;

    String UserType;
    SearchView searchView;
    TextView usernamef;
    private ProgressBar progressBar;
    private List<BookingTimesClass> doctorList;
    ListView listViewpatient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = getLayoutInflater().inflate(R.layout.activity_doctor_database, container, false);


        usernamef = rootView.findViewById(R.id.user_name_data);
        progressBar = rootView.findViewById(R.id.data_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        databasePatient = FirebaseDatabase.getInstance().getReference("Doctorpatientdb")
                .child(mAuth.getCurrentUser().getUid());

        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");


        listViewpatient = rootView.findViewById(R.id.list_view_data);
        searchView = rootView.findViewById(R.id.searchdata);
        doctorList = new ArrayList<>();
        listViewpatient.setTextFilterEnabled(true);
        removeFocus();
        getUserName();
        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        makeTable();
    }

    private void makeTable() {

        if (UtilClass.isNetworkConnected(getContext())) {

            patientEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    doctorList.clear();
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        BookingTimesClass bookingtimesclass = doctorSnapshot.getValue(BookingTimesClass.class);
                        final String PID = bookingtimesclass.getCtid();
                        final String LastBookingDate = bookingtimesclass.getCtdate();
                        // Toast.makeText(FavActivity.this, DID, Toast.LENGTH_LONG).show();
                        ///////////////////////////////////////////
                        mEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {

                                String Pname = dataSnapshot1.child(PID).child("cname").getValue(String.class);
                                String Pphone = dataSnapshot1.child(PID).child("cphone").getValue(String.class);
                                String Puri = dataSnapshot1.child(PID).child("cUri").getValue(String.class);

                                BookingTimesClass doctorclass = new BookingTimesClass(PID, Pname, LastBookingDate, Pphone, Puri);
                                doctorList.add(0, doctorclass);// i= 0  (index)to start from top

                                DoctorDatabaseAdapter adapter = new DoctorDatabaseAdapter(getActivity(), doctorList);
                                listViewpatient.setAdapter(adapter);
                                setupSearchView();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                            }
                        };
                        databaseUserReg.addValueEventListener(mEventListener);
                        //////////////////////////////////////////////////////


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            databasePatient.addListenerForSingleValueEvent(patientEventListener);
        }

    }

    private void getUserName() {


        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String UserName = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                UserType = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);

                if (UserName != null) {
                    usernamef.setText(UserName);
                } else {
                    usernamef.setText("Name");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat.addValueEventListener(postListener1);
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mEventListener != null) {
            databaseUserReg.removeEventListener(mEventListener);
        }
        if (patientEventListener != null) {
            databasePatient.removeEventListener(patientEventListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mEventListener != null) {
            databaseUserReg.removeEventListener(mEventListener);
        }
        if (patientEventListener != null) {
            databasePatient.removeEventListener(patientEventListener);
        }
    }
}

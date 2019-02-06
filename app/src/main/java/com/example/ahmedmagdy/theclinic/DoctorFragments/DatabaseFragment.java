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
import android.widget.Filter;
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

public class DatabaseFragment extends Fragment {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private Filter filter;
    private boolean isSearching = false;
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
        databaseUserReg.keepSynced(true);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseDoctor.keepSynced(true);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");


        listViewpatient = rootView.findViewById(R.id.list_view_data);
        searchView = rootView.findViewById(R.id.searchdata);
        doctorList = new ArrayList<>();
        TextView noDataMsg = rootView.findViewById(R.id.no_data_msg);
        listViewpatient.setTextFilterEnabled(false);
        listViewpatient.setEmptyView(noDataMsg);
        removeFocus();
        getUserName();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (isSearching){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        makeTable();

    }

    private void makeTable() {

        if (!UtilClass.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
            patientEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (getActivity() == null){
                        return;
                    }

                    doctorList.clear();
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        BookingTimesClass bookingtimesclass = doctorSnapshot.getValue(BookingTimesClass.class);
                        final String PID = bookingtimesclass.getCtid();
                        final String LastBookingDate = bookingtimesclass.getCtdate();
                        String Pname = bookingtimesclass.getCtname();
                        String PAge = bookingtimesclass.getCtage();
                        String Puri = bookingtimesclass.getCtpicuri();

                        BookingTimesClass doctorclass = new BookingTimesClass(PID, Pname, LastBookingDate, PAge, Puri);
                        doctorList.add(0, doctorclass);// i= 0  (index)to start from top

                        DoctorDatabaseAdapter adapter = new DoctorDatabaseAdapter(getActivity(), doctorList);
                        filter = adapter.getFilter();
                        listViewpatient.setAdapter(adapter);

                        setupSearchView();
                        progressBar.setVisibility(View.GONE);


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                }
            };
            databasePatient.addListenerForSingleValueEvent(patientEventListener);
            progressBar.setVisibility(View.GONE);


    }

    private void getUserName() {

        if (!UtilClass.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {
                    if (getActivity() == null){
                        return;
                    }

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
                    filter.filter("");
                    isSearching = false;
                } else {
                    filter.filter(newText);
                    isSearching= true;
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
 /*   @Override
    public void onResume() {
        super.onResume();
        databaseDoctor.child(mAuth.getCurrentUser().getUid()).child("status").setValue(true);
        databaseChat.child(mAuth.getCurrentUser().getUid()).child("status").setValue(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseDoctor.child(mAuth.getCurrentUser().getUid()).child("status").setValue(false);
        databaseChat.child(mAuth.getCurrentUser().getUid()).child("status").setValue(false);
    }*/
}

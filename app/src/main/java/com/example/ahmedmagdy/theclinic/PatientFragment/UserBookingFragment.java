package com.example.ahmedmagdy.theclinic.PatientFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.example.ahmedmagdy.theclinic.Adapters.PatientBookingAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
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

public class UserBookingFragment extends Fragment {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference bookforuser;
    private DatabaseReference databaseUserReg,databaseDoctor,databaseChat;

    String UserType;
    SearchView searchView;
    TextView usernamef;
    private ProgressBar progressBar;
    private List<BookingTimesClass> doctorList;
    ListView listViewuserbook;

    public UserBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_user_booking, container, false);
        usernamef = rootView.findViewById(R.id.user_name_book);
        progressBar = (ProgressBar) rootView.findViewById(R.id.data_progress_bar);
        mAuth = FirebaseAuth.getInstance();

        bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser").child(mAuth.getCurrentUser().getUid());
        bookforuser.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseDoctor.keepSynced(true);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");


        listViewuserbook = (ListView) rootView.findViewById(R.id.list_view_user_book);
        searchView = (SearchView) rootView.findViewById(R.id.searchuserbooking);
        doctorList = new ArrayList<>();
        listViewuserbook.setTextFilterEnabled(true);
        removeFocus();
        getusername();

        return rootView;
    }
        @Override
        public void onStart() {
            super.onStart();
            progressBar.setVisibility(View.VISIBLE);
            maketable();
        }
        private void maketable() {

            // if (isNetworkConnected()) {

            bookforuser.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    doctorList.clear();
                    for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                        BookingTimesClass bookingtimesclass=doctorSnapshot.getValue(BookingTimesClass.class);
                        final String DID=bookingtimesclass.getCtid();
                        final String Daddress=bookingtimesclass.getCtAddress();
                        final String Ddate=bookingtimesclass.getCtbookingdate();
                        final String timeID=bookingtimesclass.getCtPeriod();
                        final String LastBookingDate=bookingtimesclass.getCtdate();


                        final String Darrange=bookingtimesclass.getCtArrangement();
//(String ctid, String ctname, String ctdate,String ctAddress, String ctPeriod, String ctpicuri,String ctbookingdate, String ctArrangement)
                        // Toast.makeText(FavActivity.this, DID, Toast.LENGTH_LONG).show();
                        ///////////////////////////////////////////
                        final ValueEventListener postListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {

                                String DName = dataSnapshot1.child(DID).child("cName").getValue(String.class);
                                String DSpecialty = dataSnapshot1.child(DID).child("cSpecialty").getValue(String.class);
                                String DUri = dataSnapshot1.child(DID).child("cUri").getValue(String.class);
                                BookingTimesClass bookingtimesclass = new BookingTimesClass(DID, DName,LastBookingDate, Daddress,timeID, DUri,Ddate,Darrange, DSpecialty);

                                doctorList.add(0,bookingtimesclass);// i= 0  (index)to start from top

                                PatientBookingAdapter adapter = new PatientBookingAdapter(getActivity(), doctorList);
                                listViewuserbook.setAdapter(adapter);
                                setupSearchView();
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                            }
                        };
                        databaseDoctor .addValueEventListener(postListener1);
                        //////////////////////////////////////////////////////



                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            // }

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
                        listViewuserbook.clearTextFilter();
                    } else {
                        listViewuserbook.setFilterText(newText);
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


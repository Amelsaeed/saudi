package com.example.ahmedmagdy.theclinic.PatientFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.example.ahmedmagdy.theclinic.Adapters.PatientBookingAdapter;
import com.example.ahmedmagdy.theclinic.PatientHome;
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

public class UserBookingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference bookforuser;
    private DatabaseReference databaseUserReg,databaseDoctor,databaseChat;
    private SwipeRefreshLayout swipeLayout;
    String UserType;
    SearchView searchView;
    private Filter filter;
    private boolean isSearching = false;
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
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout2);
        swipeLayout.setOnRefreshListener(this);
        bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser").child(mAuth.getCurrentUser().getUid());
        bookforuser.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseUserReg.keepSynced(true);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseDoctor.keepSynced(true);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");


        listViewuserbook = (ListView) rootView.findViewById(R.id.list_view_user_book);
        searchView = (SearchView) rootView.findViewById(R.id.searchuserbooking);
        TextView noDataMsg = rootView.findViewById(R.id.no_data_msg);
        listViewuserbook.setTextFilterEnabled(false);
        listViewuserbook.setEmptyView(noDataMsg);
        doctorList = new ArrayList<>();
        removeFocus();
        getusername();

        return rootView;
    }
    @Override
    public void onRefresh() {
      startActivity(new Intent(getActivity(),PatientHome.class));
        swipeLayout.setRefreshing(false);
    }
        @Override
        public void onStart() {
            super.onStart();

            if (isSearching){
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            maketable();
        }
        private void maketable() {

             if (!UtilClass.isNetworkConnected(getContext())) {
                 Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
             }

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
                                filter = adapter.getFilter();
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
            progressBar.setVisibility(View.GONE);


        }

        private void getusername() {

if (!UtilClass.isNetworkConnected(getContext())){
    Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
}


            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {

                    String UserName = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                    UserType = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);

                    if(UserName != null) {
                        usernamef.setText(UserName);
                    }else{usernamef.setText(R.string.name);}


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


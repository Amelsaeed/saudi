package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class AllDoctorFragment extends Fragment {
    ImageView addDoctorButton;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor;
    FirebaseUser fuser;
    private ImageView btnproceed;
	
    SearchView searchView;
   // Button addTrampButton;
    private ProgressBar progressBar;

    ListView listViewDoctor;
    private List<DoctorFirebaseClass> doctorList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View rootView = getLayoutInflater().inflate(R.layout.activity_all_doctor,container,false);

        addDoctorButton = rootView.findViewById(R.id.adddoctor);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = rootView.findViewById(R.id.home_progress_bar);
        mAuth = FirebaseAuth.getInstance();

        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        listViewDoctor= rootView.findViewById(R.id.list_view_doctor);
        searchView = rootView.findViewById(R.id.search);
        doctorList=new ArrayList<>();
        listViewDoctor.setTextFilterEnabled(true);
        removeFocus();
        btnproceed= rootView.findViewById(R.id.map);

        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),MapsActivity.class);
                startActivity(i);
            }
        });

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuser != null) {
                    Intent it = new Intent(getContext(), FavActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(getContext(), "You should log in firstly", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    startActivity(it);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
  /**  private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }**/
    @Override
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

        if (isNetworkConnected()) {
         //   if(country != null &&  type != null) {


              /**  if (type.equals("User")){
                    addTrampButton.setVisibility(View.GONE);
                } else {
                    addTrampButton.setVisibility(View.VISIBLE);
                }**/
                //databaseTramp.child(country).child("Individual").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                databaseDoctor/**.child(country).child(type).child("users")**/.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        doctorList.clear();
                        for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                           DoctorFirebaseClass doctorclass=doctorSnapshot.getValue(DoctorFirebaseClass.class);
                            doctorList.add(0,doctorclass);// i= 0  (index)to start from top



                    }
                        // }
                        //}
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

            }
      /**  } else {
            Toast.makeText(AllDoctorActivity.this, "please check the network connection", Toast.LENGTH_LONG).show();
        }**/
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

}

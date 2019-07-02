package com.example.ahmedmagdy.theclinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.NoteAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.NoteClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity  extends AppCompatActivity {

    // declare var's
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<NoteClass> mList;
    private ListView mListView;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private String iduser,iddoctor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        // get intent data
        Intent intent = getIntent();
        iduser = intent.getStringExtra("iduser");
        String name = intent.getStringExtra("nameuser");
        iddoctor = intent.getStringExtra("iddoctor");
        // init var's
        mDatabase = FirebaseDatabase.getInstance().getReference("Notes");/*mDatabase.keepSynced(true);*/
        mAuth = FirebaseAuth.getInstance();
        mList = new ArrayList<>();
        mListView = findViewById(R.id.list_view_note);
        mSearchView = findViewById(R.id.search_view_note);
        mProgressBar = findViewById(R.id.progress_bar_note);
        mTextView = findViewById(R.id.user_name_note);
        mTextView.setText(name);
        // update list filter status
        mListView.setTextFilterEnabled(true);
        // remove search focus
        mSearchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // show progress bar
        mProgressBar.setVisibility(View.VISIBLE);
        // get data from database
        prepareData();
    }

    /**
     * Get Data from database and init adapter with list view
     */
    private void prepareData() {

        if (UtilClass.isNetworkConnected(this)) {
        } else{
            Toast.makeText(this, R.string.network_connection_msg, Toast.LENGTH_SHORT).show();}
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mList.clear();
                    for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                        NoteClass note = doctorSnapshot.getValue(NoteClass.class);
                        if((iduser.equals(note.getcUserId()))&&(iddoctor.equals(note.getcDoctorId())))
                            mList.add(note);
                    }
                    NoteAdapter adapter = new NoteAdapter(NoteActivity.this, mList);
                    mListView.setAdapter(adapter);
                    searchInit();
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


    }


    /**
     * Search init
     */
    private void searchInit() {
        mSearchView.setIconifiedByDefault(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mListView.clearTextFilter();
                } else {
                    mListView.setFilterText(newText);
                }
                return true;
            }
        });
    }
}

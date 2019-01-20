package com.example.ahmedmagdy.theclinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.Adapters.InsuranceAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.OneWordClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsuranceListActivity extends AppCompatActivity {
String InsuranceListString,DoctorName;
    TextView doctornamei;
    SearchView searchView;
    GridView listView;
    ArrayList<OneWordClass> minsurances = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_list);

        doctornamei=findViewById(R.id.user_name);
        searchView = findViewById(R.id.searchfav);

        Intent intent = getIntent();
        DoctorName = intent.getStringExtra("DoctorName");
        InsuranceListString = intent.getStringExtra("InsuranceList");

        doctornamei.setText(DoctorName);
     // ArrayList<String> InsuranceList = (ArrayList<String>)Arrays.asList(InsuranceListString.split(","));
        final List<String> InsuranceList = Arrays.asList(InsuranceListString.split(","));

        for (int i = 0; i < InsuranceList.size(); i++) {
            //  Toast.makeText(context, userInsurancetype, Toast.LENGTH_LONG).show();
            minsurances.add(i,new OneWordClass(InsuranceList.get(i)));
               }
      //  List<String> InsuranceList = Arrays.asList(InsuranceListString.split(","));

        InsuranceAdapter   itemsAdapter =new InsuranceAdapter(this, minsurances);
        listView=findViewById(R.id.insurance_list);
        listView.setTextFilterEnabled(true);
        listView.setAdapter(itemsAdapter);
        setupSearchView();
        removeFocus();
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

                    listView.clearTextFilter();
                } else {
                    listView.setFilterText(newText);
                }
                return true;
            }
        });
    }

    private void removeFocus() {
        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
}

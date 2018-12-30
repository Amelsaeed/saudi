package com.example.ahmedmagdy.theclinic.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    GridView listview;
    ArrayList<OneWordClass> minsurances = new ArrayList<OneWordClass>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_list);

        doctornamei=findViewById(R.id.user_name);

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

        InsuranceAdapter itemsAdapter =new InsuranceAdapter(this, minsurances);
        listview=findViewById(R.id.insurance_list);
        listview.setAdapter(itemsAdapter);
    }
}

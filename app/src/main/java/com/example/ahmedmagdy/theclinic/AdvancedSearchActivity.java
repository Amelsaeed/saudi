package com.example.ahmedmagdy.theclinic;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AdvancedSearchActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private Spinner mSpecialty, mHospital, mDegree, mInsurance, mCity;
    private String mSpecialtyText, mHospitalText, mDegreeText, mInsuranceText, mCityText;

    private RecyclerView mResultList;

    private DatabaseReference mDoctorDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        mDoctorDatabase = FirebaseDatabase.getInstance().getReference("Doctordb");

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSpecialty =  findViewById(R.id.spinner_specialty);
        mHospital =  findViewById(R.id.spinner_hospital_clinic);
        mDegree =  findViewById(R.id.spinner_degree);
        mInsurance =  findViewById(R.id.spinner_insurance);
        mCity =  findViewById(R.id.spinner_city);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        // Specialty Spinner
        ArrayAdapter<CharSequence> specialtyArray = ArrayAdapter.createFromResource(
                this, R.array.spiciality_array, android.R.layout.simple_spinner_item
        );
        specialtyArray.setDropDownViewResource(R.layout.spinner_list_item);
        mSpecialty.setAdapter(specialtyArray);
        mSpecialty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpecialtyText = mSpecialty.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSpecialtyText = "";
            }
        });

        // Hospital Spinner
        ArrayAdapter<CharSequence> hospitalArray = ArrayAdapter.createFromResource(
                this, R.array.HC_array, android.R.layout.simple_spinner_item
        );
        hospitalArray.setDropDownViewResource(R.layout.spinner_list_item);
        mHospital.setAdapter(hospitalArray);
        mHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mHospitalText = mHospital.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mHospitalText = "";
            }
        });

        // Degree Spinner
        ArrayAdapter<CharSequence> degreeArray = ArrayAdapter.createFromResource(
                this, R.array.Degree_array, android.R.layout.simple_spinner_item
        );
        degreeArray.setDropDownViewResource(R.layout.spinner_list_item);
        mDegree.setAdapter(degreeArray);
        mDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDegreeText = mDegree.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDegreeText = "";
            }
        });

        // Insurance Spinner
        ArrayAdapter<CharSequence> insuranceArray = ArrayAdapter.createFromResource(
                this, R.array.spiciality_array, android.R.layout.simple_spinner_item
        );
        insuranceArray.setDropDownViewResource(R.layout.spinner_list_item);
        mInsurance.setAdapter(insuranceArray);
        mInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mInsuranceText = mInsurance.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mInsuranceText = "";
            }
        });

        // City Spinner
        ArrayAdapter<CharSequence> cityArray = ArrayAdapter.createFromResource(
                this, R.array.spiciality_array, android.R.layout.simple_spinner_item
        );
        cityArray.setDropDownViewResource(R.layout.spinner_list_item);
        mCity.setAdapter(cityArray);
        mCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCityText = mCity.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mCityText = "";
            }
        });

        // search button
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Doctor Name
                String searchText = mSearchField.getText().toString();

                startSearch(searchText);

            }
        });

    }

    private void startSearch(String searchText) {

        Toast.makeText(AdvancedSearchActivity.this, "Getting Data...", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mDoctorDatabase.orderByChild("cName").startAt(searchText).endAt(searchText + "\uf8ff")
                                                   .orderByChild("cSpecialty").startAt(mSpecialtyText).endAt(searchText + "\uf8ff")
                                                   .orderByChild("cHospitalName").startAt(mHospitalText).endAt(searchText + "\uf8ff")
                                                   .orderByChild("cDegree").startAt(mDegreeText).endAt(searchText + "\uf8ff")
                                                   .orderByChild("cCity").startAt(mCityText).endAt(searchText + "\uf8ff")
                                                   .orderByChild("cInsurance").startAt(mInsuranceText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<DoctorFirebaseClass, ResultViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DoctorFirebaseClass, ResultViewHolder>(

                DoctorFirebaseClass.class,
                R.layout.list_layout_doctors,
                ResultViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(ResultViewHolder viewHolder, DoctorFirebaseClass model, int position) {

                viewHolder.setDetails(getApplicationContext(), model);

            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);

    }

    // View Holder Class

    public static class ResultViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ResultViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(Context ctx, DoctorFirebaseClass doctorData){

            /*TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView user_status = (TextView) mView.findViewById(R.id.status_text);
            ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);

            user_name.setText(userName);
            user_status.setText(userStatus);

            Glide.with(ctx).load(userImage).into(user_image);*/

        }

    }
}

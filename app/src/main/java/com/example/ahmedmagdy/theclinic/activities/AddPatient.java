package com.example.ahmedmagdy.theclinic.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddPatient extends AppCompatActivity {

    EditText patientName, patientAge;
    private FirebaseAuth mAuth;
    private ValueEventListener  mBookingAddressListener, mBookTimeListener;
    private DatabaseReference mBookingRef, mBookingAddressRef, mBookTimesRef;
    ArrayList<String> addressesList;
    ArrayList<BookingClass> bookTimeList;
    String bookingTimeId, selectedAddress, selectedTime,name,age;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        patientName = findViewById(R.id.pat_name_et);
        patientAge = findViewById(R.id.pat_age_et);



        mAuth = FirebaseAuth.getInstance();


        mBookingRef = FirebaseDatabase.getInstance().getReference("bookingtimes").child(mAuth.getCurrentUser().getUid());
        mBookingAddressRef = FirebaseDatabase.getInstance().getReference("bookingdb").child(mAuth.getCurrentUser().getUid());
        mBookTimesRef = mBookingAddressRef;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // remove listeners when the activity is destroyed
        if (mBookingAddressListener != null) {
            mBookingAddressRef.removeEventListener(mBookingAddressListener);
        }

        if (mBookTimeListener != null) {
            mBookTimesRef.removeEventListener(mBookTimeListener);
        }


    }


}

package com.example.ahmedmagdy.theclinic.PatientFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.ahmedmagdy.theclinic.R;


    public class DoctorBookingsFragment extends Fragment {


        public DoctorBookingsFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_doctor_bookings, container, false);
        }


    }

package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.BookingExpandableListAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.BookingListActivity;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BookingFragment extends Fragment {

    View rootView;
    BookingExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    RelativeLayout calendarPick;
    ProgressBar progressBar;
    TextView dateView, dayView;
    FloatingActionButton addPatient;
    private DatabaseReference mBookingRef;
    private ValueEventListener mBookingListener;
    private FirebaseAuth mAuth;
    String selectedDay, selectedDate, vDate, doctorId;
    ArrayList<BookingTimesClass> mBookingsGroupList;
    HashMap<BookingTimesClass, List<BookingTimesClass>> mBookingsChildList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = getLayoutInflater().inflate(R.layout.activity_doctor_bookings, container, false);

        expandableListView = rootView.findViewById(R.id.booking_expandable_list);
        calendarPick = rootView.findViewById(R.id.calendar_pick);


        progressBar = rootView.findViewById(R.id.dr_booking_pb);
        dateView = rootView.findViewById(R.id.booking_date);
        dayView = rootView.findViewById(R.id.booking_day);
        addPatient = rootView.findViewById(R.id.add_patient_fb);
        mBookingsGroupList = new ArrayList<>();
        mBookingsChildList = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        doctorId = mAuth.getCurrentUser().getUid();
        mBookingRef = FirebaseDatabase.getInstance().getReference("bookingtimes").child(mAuth.getCurrentUser().getUid());
        mBookingRef.keepSynced(true);
        selectedDate = UtilClass.getInstanceDate();
        vDate = UtilClass.dateFormat(UtilClass.getInstanceDate());

        try {
            selectedDay = UtilClass.getDayNameFromDate(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateView.setText(vDate);
        dayView.setText(selectedDay);

        calendarPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendarPicker();

            }
        });


        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPatientDialog();

            }
        });
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // display a dialog to add a new patient
    private void openAddPatientDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_patient_fragment);
        dialog.setTitle("Add new patient");
        dialog.setCanceledOnTouchOutside(false);
        final EditText nameField = dialog.findViewById(R.id.pat_name_et);
        final EditText ageField = dialog.findViewById(R.id.pat_age_et);
        TextView submit = dialog.findViewById(R.id.sub_tv);
        TextView cancel = dialog.findViewById(R.id.canc_tv);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameField.getText().toString();
                String age = ageField.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(age)) {

                        if (UtilClass.isNetworkConnected(getContext())) {
                            Intent intent = new Intent(getContext(), BookingListActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("age", age);
                            intent.putExtra("DoctorID", doctorId);
                            dialog.dismiss();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ageField.setError(getString(R.string.empty_name_field_msg));
                    }
                } else {
                    nameField.setError(getString(R.string.empty_age_field_msg));
                }
            }
        });


        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        makeTable();
    }


    private void makeTable() {

        if (!UtilClass.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
            mBookingListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (getActivity() == null){
                        return;
                    }

                    mBookingsChildList.clear();
                    mBookingsGroupList.clear();

                    ArrayList<Long> childrenCount = new ArrayList<>();
                    ArrayList<BookingTimesClass> list = new ArrayList<>();
                    BookingTimesClass drBooking = new BookingTimesClass();

                    for (DataSnapshot booking : dataSnapshot.getChildren()) {


                        if (booking.child(selectedDate).exists()) {
                            String tempTime = "";
                            mBookingsChildList.clear();
                            DataSnapshot data = booking.child(selectedDate);

                            childrenCount.add(data.getChildrenCount());

                            for (DataSnapshot mData : data.getChildren()) {
                                drBooking = mData.getValue(BookingTimesClass.class);

                                String timeId = drBooking.getCttimeid();

                                if (tempTime.equals("")) {
                                    tempTime = timeId;

                                    mBookingsGroupList.add(drBooking);
                                    list.add(drBooking);

                                } else {
                                    if (timeId.equals(tempTime)) {
                                        list.add(drBooking);
                                    }
                                }
                            }
                        }
                    }

                    ArrayList<BookingTimesClass> tempList = new ArrayList<>();
                    for (int x = 0; x < mBookingsGroupList.size(); x++) {
                        tempList = new ArrayList<>();
                        tempList.clear();
                        for (int y = 0; y < childrenCount.get(x); y++) {
                            tempList.add(list.get(0));
                            // sort the bookings list
                            Collections.sort(tempList, new Comparator<BookingTimesClass>() {
                                @Override
                                public int compare(BookingTimesClass bookingTimesClass, BookingTimesClass t1) {
                                    return bookingTimesClass.getCtposition() - t1.getCtposition();
                                }
                            });
                            mBookingsChildList.put(mBookingsGroupList.get(x), tempList);
                            list.remove(0);
                        }

                    }

                    expandableListAdapter = new BookingExpandableListAdapter(getContext(), mBookingsGroupList, mBookingsChildList);
                    expandableListView.setAdapter(expandableListAdapter);
                    progressBar.setVisibility(View.GONE);

                    TextView emptyListView = rootView.findViewById(R.id.empty_list_tv);
                    expandableListView.setEmptyView(emptyListView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

            mBookingRef.addListenerForSingleValueEvent(mBookingListener);

//.orderByChild("ctdate")




    }

    private void openCalendarPicker() {
        ImageGenerator mImageGenerator = new ImageGenerator(getContext());

// Set the icon size to the generated in dip.
        mImageGenerator.setIconSize(50, 50);

// Set the size of the date and month font in dip.
        mImageGenerator.setDateSize(30);
        mImageGenerator.setMonthSize(10);

// Set the position of the date and month in dip.
        mImageGenerator.setDatePosition(42);
        mImageGenerator.setMonthPosition(14);

// Set the color of the font to be generated
        mImageGenerator.setDateColor(Color.parseColor("#3c6eaf"));
        mImageGenerator.setMonthColor(Color.WHITE);

        // abookingphoto.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //   public void onClick(View v) {
        final Calendar mCurrentDate = Calendar.getInstance();
        int year = mCurrentDate.get(Calendar.YEAR);
        int month = mCurrentDate.get(Calendar.MONTH);
        int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mPickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {

                String month = String.valueOf(Month + 1);
                if (Integer.parseInt(month) < 10) {
                    month = "0" + month;
                }
                mCurrentDate.set(Year, ((Month + 1)), Day);

                selectedDate = Year + "_" + month + "_" + Day;


                vDate = Day + "-" + month + "-" + Year;
                try {
                    selectedDay = UtilClass.getDayNameFromDate(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateView.setText(vDate);
                dayView.setText(selectedDay);


                progressBar.setVisibility(View.VISIBLE);
                makeTable();


            }
        }, year, month, day);
        mPickerDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBookingListener != null) {
            mBookingRef.removeEventListener(mBookingListener);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mBookingListener != null) {
            mBookingRef.removeEventListener(mBookingListener);
        }
    }
}

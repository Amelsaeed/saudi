package com.example.ahmedmagdy.theclinic.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.Adapters.BookingExpandableListAdapter;
import com.example.ahmedmagdy.theclinic.R;
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
import java.util.HashMap;
import java.util.List;

public class DoctorBookingsActivity extends AppCompatActivity {


    BookingExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    Button calendarButton, alldoctorsbtn;
    ProgressBar progressBar;
    TextView dateView,dayView;
    private DatabaseReference mBookingRef;
    private ValueEventListener mBookingListener;
    private FirebaseAuth mAuth;
    String selectedDay;
    String selectedDate;
    ArrayList<BookingTimesClass> mBookingsGroupList;
    HashMap<BookingTimesClass, List<BookingTimesClass>> mBookingsChildList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_bookings);

        expandableListView = (ExpandableListView) findViewById(R.id.booking_expandable_list);
        calendarButton = (Button) findViewById(R.id.calendar_btn);
        alldoctorsbtn = (Button) findViewById(R.id.all_btn);

        progressBar = (ProgressBar) findViewById(R.id.dr_booking_pb);
        dateView = (TextView) findViewById(R.id.booking_date);
        dayView = (TextView) findViewById(R.id.booking_day);

        mBookingsGroupList= new ArrayList<>();
        mBookingsChildList = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();

        mBookingRef = FirebaseDatabase.getInstance().getReference("bookingtimes").child(mAuth.getCurrentUser().getUid());
        mBookingRef.keepSynced(true);
        selectedDate = UtilClass.getInstanceDate();
        try {
            selectedDay = UtilClass.getDayNameFromDate(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateView.setText(selectedDate);
        dayView.setText(selectedDay);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              openCalendarPicker();

            }
        });
        alldoctorsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(DoctorBookingsActivity.this, AllDoctorActivity.class);
                finish();
                startActivity(it);

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
       progressBar.setVisibility(View.VISIBLE);
     makeTable();
    }


    private void makeTable() {

        if (UtilClass.isNetworkConnected(DoctorBookingsActivity.this)) {

          mBookingListener =  new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mBookingsChildList.clear();
                    mBookingsGroupList.clear();

                    ArrayList<Long> childrenCount = new ArrayList<>();
                    ArrayList<BookingTimesClass> list = new ArrayList<>();
                    BookingTimesClass drBooking = new BookingTimesClass();

                    for (DataSnapshot booking : dataSnapshot.getChildren()){


                        if (booking.child(selectedDate).exists()) {

                            String tempAddress = "";
                            mBookingsChildList.clear();
                            DataSnapshot data = booking.child(selectedDate);

                            childrenCount.add(data.getChildrenCount());

                            for (DataSnapshot mData : data.getChildren()) {
                                drBooking = mData.getValue(BookingTimesClass.class);

                                String address = drBooking.getCtAddress();

                                if (tempAddress.equals("")) {
                                    tempAddress = address;

                                    mBookingsGroupList.add(drBooking);
                                    list.add(drBooking);

                                } else {
                                    if (address.equals(tempAddress)) {
                                        list.add(drBooking);
                                    }
                                }
                            }
                        }
                    }

                    ArrayList<BookingTimesClass> tempList = new ArrayList<>();
                    for (int x = 0 ; x < mBookingsGroupList.size(); x++){
                        tempList = new ArrayList<>();
                        tempList.clear();
                        for (int y = 0 ; y < childrenCount.get(x); y++){
                            tempList.add(list.get(0));
                           mBookingsChildList.put(mBookingsGroupList.get(x), tempList);
                           list.remove(0);
                        }
                    }

                    expandableListAdapter = new BookingExpandableListAdapter(DoctorBookingsActivity.this, mBookingsGroupList,mBookingsChildList);
                    expandableListView.setAdapter(expandableListAdapter);
                    progressBar.setVisibility(View.GONE);

                    TextView emptyListView = (TextView) findViewById(R.id.empty_list_tv);
                    expandableListView.setEmptyView(emptyListView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mBookingRef.addListenerForSingleValueEvent(mBookingListener);

//.orderByChild("ctdate")
        }

    }

    private void openCalendarPicker() {
        ImageGenerator mImageGenerator = new ImageGenerator(DoctorBookingsActivity.this);

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
        int year=mCurrentDate.get(Calendar.YEAR);
        int month=mCurrentDate.get(Calendar.MONTH);
        int day=mCurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mPickerDialog =  new DatePickerDialog(DoctorBookingsActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {

                mCurrentDate.set(Year, ((Month+1)),Day);

                selectedDate = Year+"_"+ (Month+1)+"_"+Day;
                try {
                    selectedDay = UtilClass.getDayNameFromDate(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateView.setText(selectedDate);
                dayView.setText(selectedDay);


                progressBar.setVisibility(View.VISIBLE);
                makeTable();



            }
        }, year, month, day);
        mPickerDialog.show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBookingListener != null){
            mBookingRef.removeEventListener(mBookingListener);
        }

    }
}

package com.example.ahmedmagdy.theclinic.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddPatient extends AppCompatActivity {

    EditText patientName, patientAge;
    TextView bookingDay, errorMessage, bookingAddress;
    Spinner bookAddress, bookTime;
    private FirebaseAuth mAuth;
    private ValueEventListener  mBookingAddressListener, mBookTimeListener;
    private DatabaseReference mBookingRef, mBookingAddressRef, mBookTimesRef;
    ArrayList<String> addressesList;
    ArrayList<BookingClass> bookTimeList;
    String bookingTimeId, selectedAddress, selectedTime;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        patientName = findViewById(R.id.pat_name_et);
        patientAge = findViewById(R.id.pat_age_et);
        bookAddress = findViewById(R.id.address_sp);
        bookTime = findViewById(R.id.booking_time_sp);
        bookingDay = findViewById(R.id.booking_day_tv);
        errorMessage= findViewById(R.id.error_msg_tv);



        bookingDay.setText(UtilClass.getInstanceDate());

        mAuth = FirebaseAuth.getInstance();


        mBookingRef = FirebaseDatabase.getInstance().getReference("bookingtimes").child(mAuth.getCurrentUser().getUid());
        mBookingAddressRef = FirebaseDatabase.getInstance().getReference("bookingdb").child(mAuth.getCurrentUser().getUid());
        mBookTimesRef = mBookingAddressRef;

        bookAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getData(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bookTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bookingTimeId = bookTimeList.get(i).getCbid();
                selectedTime = bookTimeList.get(i).getCbtime();
                Toast.makeText(AddPatient.this, bookingTimeId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



      bookingDay.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openCalendarPicker();
          }
      });



    }

// save patient data
    private void savePatient(){
        errorMessage.setVisibility(View.GONE);
        String name = patientName.getText().toString();
        String age = patientAge.getText().toString();
        String address = selectedAddress;
        String time = selectedTime;
        String day = bookingDay.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(age)) {
                if (!TextUtils.isEmpty(address)) {
                    if (!TextUtils.isEmpty(time)){
                        if (!TextUtils.isEmpty(day)){
                            addNewPaitent(name, age, address, day);
                        }else {
                            bookingDay.setError(getString(R.string.empty_day_field_msg));
                        }
                    }else{
                        Toast.makeText(AddPatient.this, R.string.empty_time_field_msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPatient.this, R.string.empty_address_field_msg, Toast.LENGTH_SHORT).show();
                }
            } else {
                patientAge.setError(getString(R.string.empty_age_field_msg));
            }
        } else {
            patientName.setError(getString(R.string.empty_name_field_msg));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        errorMessage.setVisibility(View.GONE);
        if (addressesList == null) {
            getAddressData();
        }
    }

// get booking addresses for the doctor
    private void getAddressData() {

        if (UtilClass.isNetworkConnected(AddPatient.this)) {
            addressesList = new ArrayList<>();
            addressesList.clear();

            mBookingAddressListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        BookingClass bookingClass = data.getValue(BookingClass.class);
                        if (!addressesList.contains(bookingClass.getCbaddress())) {
                            addressesList.add(bookingClass.getCbaddress());
                        }

                    }

                    ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(AddPatient.this, R.layout.spinner_list_item, addressesList);

                    bookAddress.setAdapter(addressAdapter);
                    if (addressesList.isEmpty()){
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(getString(R.string.no_working_hours_msg));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mBookingAddressRef.addValueEventListener(mBookingAddressListener);

            // if addressList is null call the getAddressData method again
            if (addressesList == null) {
                if (count < 4) {
                    count++;
                    getAddressData();
                } else {
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();

                }
            } else {
                count = 0;
            }


        } else {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.network_connection_msg));
        }


    }

    //
    private void getData(int position) {
        bookTimeList = new ArrayList<>();
        bookTimeList.clear();
        final ArrayList<String> list = new ArrayList<>();
        selectedAddress = addressesList.get(position);

        if (UtilClass.isNetworkConnected(AddPatient.this)){
        mBookTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    BookingClass bookClass = data.getValue(BookingClass.class);
                    if (bookClass.getCbaddress().equals(selectedAddress)) {
                        bookTimeList.add(bookClass);
                        list.add(bookClass.getCbtime());
                    }

                    ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(AddPatient.this, R.layout.spinner_list_item, list);
                    bookTime.setAdapter(timeAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mBookTimesRef.addValueEventListener(mBookTimeListener);


        }else{
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.network_connection_msg));
        }


    }

    private void addNewPaitent(String name, String age, String address, String day) {

        if (UtilClass.isNetworkConnected(AddPatient.this)){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(calendar.getTime());

        ////to do/////////-------------------------------------------------------------
        DatabaseReference reference = mBookingRef.push();

        String key = reference.getKey();

        String uId = mAuth.getCurrentUser().getUid();

        BookingTimesClass bookingtimesclass = new BookingTimesClass(key, name, age, date, address,selectedTime, "0", false, bookingTimeId,day);

        // Database for Account Activity
        mBookingRef.child(bookingTimeId).child(day)
                .child(key).setValue(bookingtimesclass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                }
            }
        });

        }else {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.network_connection_msg));
        }
    }

    private void openCalendarPicker() {
        ImageGenerator mImageGenerator = new ImageGenerator(AddPatient.this);

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
        DatePickerDialog mPickerDialog =  new DatePickerDialog(AddPatient.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {

                mCurrentDate.set(Year, ((Month+1)),Day);

              String  selectedDate = Year+"_"+ (Month+1)+"_"+Day;

                bookingDay.setText(selectedDate);



            }
        }, year, month, day);
        mPickerDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_patient_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_save){
            savePatient();
        }else if (id == android.R.id.home){
            // when the user select the home item menu the activity will be destroyed and go back.
            finish();
        }

        return super.onOptionsItemSelected(item);
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

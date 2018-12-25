package com.example.ahmedmagdy.theclinic.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.BookingAdapter;
import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.Notifications.Client;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;
import com.example.ahmedmagdy.theclinic.classes.MapClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingListActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    Button paddbook;
    TextView dname;
    String DoctorID;
    String address, idm;
    boolean satstate,sunstate,monstate,tusstate,wedstate,thustate,fristate;
    double latitude;
    double longitude;
    String arrange;
    final int theRequestCodeForLocation = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Boolean isPermissionGranted;

    APIService apiService;
    EditText dialogAddress;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor,databaseChat,databaseMap;
    private DatabaseReference databaseUserReg;
    private DatabaseReference databasetimeBooking;
    private FirebaseUser fuser;
    ProgressBar progressBarBooking;
    ListView listViewBooking;
    private List<BookingClass> bookingList;
    String startTime,endingTime;
    int startHour,endingHour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        listViewBooking= (ListView)findViewById(R.id.list_view_booking);
        bookingList=new ArrayList<BookingClass>();

        mAuth = FirebaseAuth.getInstance();



        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");databaseDoctor.keepSynced(true);
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");databaseUserReg.keepSynced(true);
        databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes");databasetimeBooking.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");databaseChat.keepSynced(true);
        databaseMap = FirebaseDatabase.getInstance().getReference("mapdb");databaseMap.keepSynced(true);
        DatabaseReference reference = databaseMap.push();
        idm = reference.getKey();

        paddbook = (Button) findViewById(R.id.book);
        progressBarBooking = (ProgressBar) findViewById(R.id.booking_progress_bar);

        Intent intent = getIntent();
        DoctorID = intent.getStringExtra("DoctorID");
        String DoctorName = intent.getStringExtra("DoctorName");
        dname=findViewById(R.id.d_name);
        dname.setText(DoctorName);
        // Toast.makeText(DoctorProfileActivity.this, DoctorID, Toast.LENGTH_LONG).show();

        if(!DoctorID.equals(mAuth.getCurrentUser().getUid())){paddbook.setVisibility(View.GONE);}


        listViewBooking.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {

            BookingClass bookingclass = bookingList.get(position);
            final String timeID = bookingclass.getCbid();
            String comefrom="1";


            Intent intent = new Intent(BookingListActivity.this,CalenderActivity.class);
            intent.putExtra("comefrom", comefrom);
            intent.putExtra("DoctorID", DoctorID);
            intent.putExtra("TimeID", timeID);
            intent.putExtra("StartingTime", bookingclass.getCbtimestart());
            intent.putExtra("EndingTime", bookingclass.getCbtimeend());
            intent.putExtra("DoctorAddress", bookingclass.getCbaddress());

            intent.putExtra("Satchecked", bookingclass.getSatchecked());
            intent.putExtra("Sunchecked", bookingclass.getSunchecked());
            intent.putExtra("Monchecked", bookingclass.getMonchecked());
            intent.putExtra("Tuschecked", bookingclass.getTuschecked());
            intent.putExtra("Wedchecked", bookingclass.getWedchecked());
            intent.putExtra("Thuchecked", bookingclass.getThuchecked());
            intent.putExtra("Frichecked", bookingclass.getFrichecked());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            return true;
            }
        });





        /** listViewBooking.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             @Override
             public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                 // TODO Auto-generated method stub


                     BookingClass bookingclass = bookingList.get(position);
                     final String timeID = bookingclass.getCbid();


                 Intent intent = new Intent(BookingListActivity.this,CalenderActivity.class);
                 intent.putExtra("DoctorID", DoctorID);
                 intent.putExtra("TimeID", timeID);

                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
                     //////////////////////////////////////////////////

             }
         });**/


        //--------Gps---------------------
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();

        paddbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--------Gps---------------------
                if(isPermissionGranted){

                    getLocation();
                }else{
                    requestPermission();
                    if(isPermissionGranted){
                        //We have it, Get the location.
                        getLocation();
                    }
                    else {
                        Toast.makeText(BookingListActivity.this, "Please Give us permission so you can use the app", Toast.LENGTH_SHORT).show();
                    }
                }
                //--------------------------------------
                //String whatdata = "Ex:from Sat to Mon in address at 15:00 clock";
                editDialogbook();
            }
        });

        ////////////////////////////////
        //--------Gps---------------------
    }

    /***-------------------------------------------------***/
    private void editDialogbook() {

        final DatabaseReference databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);
        databaseBooking.keepSynced(true);

        final Dialog dialog = new Dialog(BookingListActivity.this);
        dialog.setContentView(R.layout.booking_data_dialig);
        //dialog.setTitle("Edit your data");
        dialog.setCanceledOnTouchOutside(false);
        getLocation();
        //Toast.makeText(this, address, Toast.LENGTH_LONG).show();

        dialogAddress = (EditText) dialog.findViewById(R.id.dialog_address);
        dialogAddress.setEnabled(true);
        dialogAddress.setText(address);
        dialogAddress.setEnabled(false);

        final EditText dialogstarttime = dialog.findViewById(R.id.start_time);
        final EditText dialogendingtime = dialog.findViewById(R.id.ending_time);

        final ImageView dialogstarttimelogo = dialog.findViewById(R.id.timer);
        final ImageView dialogendingtimelogo = dialog.findViewById(R.id.timer_off);

        dialogstarttimelogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(BookingListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
///////////*************************to get am-pm value**************************/////////
                    /**    String am_pm = "";

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";**/
///////////*************************to get am-pm value**************************/////////

                        startTime=selectedHour + ":" + selectedMinute;
                        startHour=selectedHour;
                        dialogstarttime.setEnabled(true);
                        dialogstarttime.setText( selectedHour + ":" + selectedMinute);
                        dialogstarttime.setEnabled(false);

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        dialogendingtimelogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker1 = new TimePickerDialog(BookingListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour1, int selectedMinute1) {
///////////*************************to get am-pm value**************************/////////
                      /**  String am_pm1 = "";

                        Calendar datetime1 = Calendar.getInstance();
                        datetime1.set(Calendar.HOUR_OF_DAY, selectedHour1);
                        datetime1.set(Calendar.MINUTE, selectedMinute1);

                        if (datetime1.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm1 = "AM";
                        else if (datetime1.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm1 = "PM";**/
///////////*************************to get am-pm value**************************/////////
                        endingTime=selectedHour1 + ":" + selectedMinute1;
                        endingHour=selectedHour1;

                        dialogendingtime.setEnabled(true);
                        dialogendingtime.setText( selectedHour1 + ":" + selectedMinute1);
                        dialogendingtime.setEnabled(false);

                    }
                }, hour1, minute1, false);//Yes 24 hour time
                mTimePicker1.setTitle("Select Time");
                mTimePicker1.show();

            }
        });


        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_e);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_e);

        CheckBox dsatcheckbox = (CheckBox) dialog.findViewById(R.id.sat);
        CheckBox dsuncheckbox = (CheckBox) dialog.findViewById(R.id.sun);
        CheckBox dmoncheckbox = (CheckBox) dialog.findViewById(R.id.mon);
        CheckBox dtuscheckbox = (CheckBox) dialog.findViewById(R.id.tus);
        CheckBox dwedcheckbox = (CheckBox) dialog.findViewById(R.id.wed);
        CheckBox dthucheckbox = (CheckBox) dialog.findViewById(R.id.thu);
        CheckBox dfricheckbox = (CheckBox) dialog.findViewById(R.id.fri);
        //////////////////////////
        dsatcheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {satstate =true; } else { satstate =false;}
            }
        });
        dsuncheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {sunstate =true; } else { sunstate =false;}
            }
        });//satstate,sunstate,monstate,tusstate,wedstate,thustate,fristate
        dmoncheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {monstate =true; } else { monstate =false;}
            }
        });
        dtuscheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {tusstate =true; } else { tusstate =false;}
            }
        });
        dwedcheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {wedstate =true; } else { wedstate =false;}
            }
        });
        dthucheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {thustate =true; } else { thustate =false;}
            }
        });
        dfricheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {fristate =true; } else { fristate =false;}
            }
        });
        ////////////////////





        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String getaddress = dialogAddress.getText().toString().trim();
                final String getstartingtime = dialogstarttime.getText().toString().trim();
                final String getendingtime = dialogendingtime.getText().toString().trim();


                if (getaddress.isEmpty()) {
                    dialogAddress.setError("Please fill the address");
                    dialogAddress.requestFocus();
                    return;}
                if (getstartingtime.isEmpty()) {
                    dialogstarttime.setError("Please fill starting time");
                    dialogstarttime.requestFocus();
                    return;}
                if (getendingtime.isEmpty()) {
                    dialogendingtime.setError("Please fill ending times");
                    dialogendingtime.requestFocus();
                    return;}
                if (endingHour<=startHour) {
                    dialogendingtime.setError("Ending time must be after starting time /n and in the same day");
                    dialogendingtime.requestFocus();
                    dialogstarttime.setError("Ending time must be after starting time /n and in the same day");
                    dialogstarttime.requestFocus();
                    return;}


                DatabaseReference reference = databaseBooking.push();
                String id = reference.getKey();
                //Log.v("Data"," 2-User id :"+ mUserId);
                BookingClass bookingclass = new BookingClass(id, getstartingtime,getendingtime, getaddress,DoctorID,String.valueOf(latitude),String.valueOf(longitude),satstate,sunstate,monstate,tusstate,wedstate,thustate,fristate);
                // BookingAdapter myAdapter = new BookingAdapter(DoctorProfileActivity.this, bookingList, id, DoctorID);
                // Database for Account Activity
                databaseBooking.child(id).setValue(bookingclass);
                //////////////////////////////////////
                final ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {

                        String DoctorName = dataSnapshot1.child(DoctorID).child("cName").getValue(String.class);
                        String DoctorSpecialty = dataSnapshot1.child(DoctorID).child("cSpecialty").getValue(String.class);
                        String DoctorPic = dataSnapshot1.child(DoctorID).child("cUri").getValue(String.class);
                        if (DoctorPic == null){DoctorPic= "https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=d3108b95-4e16-4549-99b6-f0fa466e0d11";}
                        // DatabaseReference reference = databaseMap.push();
                        // String idm = reference.getKey();
                        //Log.v("Data"," 2-User id :"+ mUserId);
                        MapClass mapclass = new MapClass(DoctorID,String.valueOf(latitude),String.valueOf(longitude),DoctorName,DoctorSpecialty,DoctorPic);
                        // BookingAdapter myAdapter = new BookingAdapter(DoctorProfileActivity.this, bookingList, id, DoctorID);
                        // Database for Account Activity
                        databaseMap.child(idm).setValue(mapclass);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };
                databaseDoctor .addValueEventListener(postListener1);

                ///////////////////////////////////////////////

                dialog.dismiss();
                //to refresh activity as you need to go back activity and return
                finish();
                startActivity(getIntent());

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }




    //-----------------------------add Gps----------------------------------
    /**
     * Request the Location Permission
     */
    private void requestPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        }
        else{
            isPermissionGranted = true;
        }

    }

    /**
     * this method gets the location of the user then
     * Calls the showAddress Method and pass to it the Latitude
     * and the Longitude
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    showAddress(latitude,longitude);
                }else{
                    Toast.makeText(BookingListActivity.this, "Error we didn't get the Location\n Please try again after Few seconds", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * this method shows the User's address to the screen, it calls the getAddress which returns a string
     * contains the address then changes the TextView text to it.
     * @param latitude is the latitude of the location
     * @param longitude is the longitude of the location
     */
    private void showAddress(double latitude, double longitude){
        String msg = "";

        try {
            msg = getAddress(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pcity.setText(msg);
//
    }


    /**
     * this method is called by android system after we request a permission
     * and the system pass the result of our request to this method so we can check if we got
     * the permission or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode){
            case theRequestCodeForLocation:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isPermissionGranted = true;
                }else{
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    isPermissionGranted = false;
                }
                break;
        }

    }

    /**
     * this method takes the longitude and latitude of the location then convert them into real address
     * and return it as string
     * @param latitude the Latitude
     * @param longitude the Longitude
     * @return the address as String
     * @throws IOException
     */
    private String getAddress(double latitude, double longitude) throws IOException {

        //Geocoder class helps us to convert longitude and latitude into Address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        address =  addresses.get(0).getAddressLine(0);
        String city = "City: " + addresses.get(0).getLocality();
        String state = "State:" + addresses.get(0).getAdminArea();
        String country = "Country: " + addresses.get(0).getCountryName();
        // String wholeAddress = address + "\n" + city + "\n" + state + "\n" + country;
        String wholeAddress = address ;
        // pcity.setText(address);

        return  wholeAddress;

    }
    protected void onStart() {
        super.onStart();
        progressBarBooking.setVisibility(View.VISIBLE);
        // getRegData();
        // if (isNetworkConnected()) {
        final DatabaseReference databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);

        //databaseTramp.child(country).child("Individual").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
        databaseBooking.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookingList.clear();
                for(DataSnapshot doctorSnapshot: dataSnapshot.getChildren()){
                    BookingClass bookingclass=doctorSnapshot.getValue(BookingClass.class);
                    bookingList.add(bookingclass);// i= 0  (index)to start from top



                }
                // }
                //}
                BookingAdapter adapter = new BookingAdapter(BookingListActivity.this, bookingList);
                //adapter.notifyDataSetChanged();
                listViewBooking.setAdapter(adapter);
                progressBarBooking.setVisibility(View.GONE);
                // listViewTramp.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        /**  } else {
         Toast.makeText(DoctorProfileActivity.this, "please check the network connection", Toast.LENGTH_LONG).show();
         }**/
    }
}

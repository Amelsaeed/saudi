package com.example.ahmedmagdy.theclinic.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.ahmedmagdy.theclinic.Adapters.BookingAdapter;
import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.Notifications.Client;
import com.example.ahmedmagdy.theclinic.Notifications.Data;
import com.example.ahmedmagdy.theclinic.Notifications.MyResponse;
import com.example.ahmedmagdy.theclinic.Notifications.Sender;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.MapClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingListActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    Button paddbook;
    TextView dname;
    String DoctorID, address, idm, startTime, endingTime, arrange, patientName, patientAge, MaxNo;
    String DoctorName;
    boolean satstate, sunstate, monstate, tusstate, wedstate, thustate, fristate;
    double latitude;
    double longitude;
    final int theRequestCodeForLocation = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Boolean isPermissionGranted;
    boolean notify = false;
    APIService apiService;
    EditText dialogAddress;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor, databaseChat, databaseMap;
    private DatabaseReference databaseUserReg;
    private DatabaseReference databasetimeBooking;
    private FirebaseUser fuser;
    ProgressBar progressBarBooking;
    ListView listViewBooking;
    private List<BookingClass> bookingList;
    int startHour, endingHour;
    String userid;
    Boolean BookingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        Toolbar toolbar = findViewById(R.id.toolbarbook);

        listViewBooking = (ListView) findViewById(R.id.list_view_booking);
        TextView noDataMsg = findViewById(R.id.no_appoint_msg);
        listViewBooking.setEmptyView(noDataMsg);
        bookingList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        fuser = mAuth.getInstance().getCurrentUser();
        userid = mAuth.getCurrentUser().getUid();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        /*databaseDoctor.keepSynced(true);*/
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        /*databaseUserReg.keepSynced(true);*/
        databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes");
        /*databasetimeBooking.keepSynced(true);*/
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        /*databaseChat.keepSynced(true);*/
        databaseMap = FirebaseDatabase.getInstance().getReference("mapdb");
        /*databaseMap.keepSynced(true);*/
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DatabaseReference reference = databaseMap.push();
        idm = reference.getKey();

        paddbook = (Button) findViewById(R.id.book);
        progressBarBooking = (ProgressBar) findViewById(R.id.booking_progress_bar);

        Intent intent = getIntent();
        DoctorID = intent.getStringExtra("DoctorID");
       //  DoctorName = intent.getStringExtra("DoctorName");
        patientName = intent.getStringExtra("name");
        patientAge = intent.getStringExtra("age");
        BookingType = getIntent().getExtras().getBoolean("BookingType");
        MaxNo = intent.getStringExtra("MaxNo");
        // Toast.makeText(BookingListActivity.this, MaxNo+" ", Toast.LENGTH_LONG).show();

        dname = findViewById(R.id.d_name);


        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                 DoctorName = dataSnapshot1.child(DoctorID).child("cName").getValue(String.class);
              //  MaxNo = dataSnapshot1.child(DoctorID).child("cMaxno").getValue(String.class);
               // BookingType = dataSnapshot1.child(DoctorID).child("cbookingtypestate").getValue(boolean.class);

                if (DoctorName != null) {
                    dname.setText(DoctorName);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseDoctor.addValueEventListener(postListener1);

        // Toast.makeText(DoctorProfileActivity.this, DoctorID, Toast.LENGTH_LONG).show();

        if (!DoctorID.equals(mAuth.getCurrentUser().getUid())) {
            paddbook.setVisibility(View.GONE);
        }

//----------------------------------------------------------------
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (getSupportActionBar() != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }
//=============================================================
        /**      listViewBooking.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
        if(BookingType){
        BookingClass bookingclass = bookingList.get(position);
        final String timeID = bookingclass.getCbid();
        String comefrom = "1";


        Intent intent = new Intent(BookingListActivity.this, CalenderActivity.class);
        intent.putExtra("comefrom", comefrom);
        intent.putExtra("DoctorID", DoctorID);
        intent.putExtra("TimeID", timeID);
        intent.putExtra("StartingTime", bookingclass.getCbtimestart());
        intent.putExtra("EndingTime", bookingclass.getCbtimeend());
        intent.putExtra("DoctorAddress", bookingclass.getCbaddress());
        if (bookingclass.getSteptime() != null) {
        intent.putExtra("StepTime", bookingclass.getSteptime());
        } else {
        intent.putExtra("StepTime", "15");
        }

        if (patientName != null) {
        intent.putExtra("patientName", patientName);
        }
        if (patientAge != null) {
        intent.putExtra("patientAge", patientAge);
        }
        intent.putExtra("Satchecked", bookingclass.getSatchecked());
        intent.putExtra("Sunchecked", bookingclass.getSunchecked());
        intent.putExtra("Monchecked", bookingclass.getMonchecked());
        intent.putExtra("Tuschecked", bookingclass.getTuschecked());
        intent.putExtra("Wedchecked", bookingclass.getWedchecked());
        intent.putExtra("Thuchecked", bookingclass.getThuchecked());
        intent.putExtra("Frichecked", bookingclass.getFrichecked());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        }else{
        ///////////////////rearrange booking/////////////////
        if (mAuth.getCurrentUser() == null) {
        Toast.makeText(BookingListActivity.this, "Please log in first", Toast.LENGTH_LONG).show();
        } else{

        BookingClass bookingclass = bookingList.get(position);
        final String timeID = bookingclass.getCbid();

        openClenderAction(timeID, position);

        }

        ////////////////////////rearrange booking///////////////////////////////
        }

        return true;
        }
        });**/


        /** listViewBooking.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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
//adding new booking period
        paddbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final DatabaseReference databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);
                databaseBooking.keepSynced(true);

                final Dialog dialog = new Dialog(BookingListActivity.this);
                dialog.setContentView(R.layout.booking_data_dialig);
                dialog.setTitle(R.string.edit_your_data);
                dialog.setCanceledOnTouchOutside(false);
                //
                //
                //--------get location from Gps---------------------
                if (isPermissionGranted) {

                    getLocation(dialog);
                } else {
                    requestPermission();
                    if (isPermissionGranted) {
                        //We have it, Get the location.
                        getLocation(dialog);
                    } else {
                        Toast.makeText(BookingListActivity.this, R.string.please_give_us_permission_so_you_can_use_the_app, Toast.LENGTH_SHORT).show();
                    }
                }
                //--------------------------------------
                //
                // getLocation();
                //Toast.makeText(this, address, Toast.LENGTH_LONG).show();


                final EditText dialogstarttime = dialog.findViewById(R.id.start_time);
                final EditText dialogendingtime = dialog.findViewById(R.id.ending_time);

                final ImageView dialogstarttimelogo = dialog.findViewById(R.id.timer);
                final ImageView dialogendingtimelogo = dialog.findViewById(R.id.timer_off);
                final Spinner dialogspinnerstep = dialog.findViewById(R.id.step_time);
                final Spinner dialogspinnerHC = dialog.findViewById(R.id.hospital_clinic);

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
                                //////
                                String selectedHour00 = String.valueOf(selectedHour);
                                if (Integer.parseInt(selectedHour00) < 10) {
                                    selectedHour00 = "0" + selectedHour00;
                                }
                                String selectedMinute00 = String.valueOf(selectedMinute);
                                if (Integer.parseInt(selectedMinute00) < 10) {
                                    selectedMinute00 = "0" + selectedMinute00;
                                }

                                ///////////////////////////

                                startTime = selectedHour00 + ":" + selectedMinute00;
                                startHour = selectedHour;

                                dialogstarttime.setEnabled(true);
                                dialogstarttime.setText(startTime);
                                dialogstarttime.setEnabled(false);

                            }
                        }, hour, minute, false);//Yes 24 hour time
                        mTimePicker.setTitle(getString(R.string.select_time));
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

                                //////
                                String selectedHour100 = String.valueOf(selectedHour1);
                                if (Integer.parseInt(selectedHour100) < 10) {
                                    selectedHour100 = "0" + selectedHour100;
                                }
                                String selectedMinute100 = String.valueOf(selectedMinute1);
                                if (Integer.parseInt(selectedMinute100) < 10) {
                                    selectedMinute100 = "0" + selectedMinute100;
                                }

                                ///////////////////////////
                                endingTime = selectedHour100 + ":" + selectedMinute100;
                                endingHour = selectedHour1;

                                dialogendingtime.setEnabled(true);

                                dialogendingtime.setText(endingTime);
                                dialogendingtime.setEnabled(false);

                            }
                        }, hour1, minute1, false);//Yes 24 hour time
                        mTimePicker1.setTitle(R.string.select_time);
                        mTimePicker1.show();

                    }
                });


                TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_e);
                TextView refresh = (TextView) dialog.findViewById(R.id.refresh_tv_e);
                TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_e);

                final CardView dsatcardview = (CardView) dialog.findViewById(R.id.sat);
                final CardView dsuncardview = (CardView) dialog.findViewById(R.id.sun);
                final CardView dmoncardview = (CardView) dialog.findViewById(R.id.mon);
                final CardView dtuscardview = (CardView) dialog.findViewById(R.id.tus);
                final CardView dwedcardview = (CardView) dialog.findViewById(R.id.wed);
                final CardView dthucardview = (CardView) dialog.findViewById(R.id.thu);
                final CardView dfricardview = (CardView) dialog.findViewById(R.id.fri);

                // spinner for Hospital or Clinic
                ArrayAdapter<CharSequence> adapterHC = ArrayAdapter.createFromResource(
                        BookingListActivity.this, R.array.HC_array, android.R.layout.simple_spinner_item);
                adapterHC.setDropDownViewResource(R.layout.spinner_list_item);
                dialogspinnerHC.setAdapter(adapterHC);

                dialogspinnerHC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
                        //String minsurance = spinnerinsurance.getSelectedItem().toString().trim();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // spinner for insurance
                ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(
                        BookingListActivity.this, R.array.step_array, android.R.layout.simple_spinner_item);
                adapters.setDropDownViewResource(R.layout.spinner_list_item);
                dialogspinnerstep.setAdapter(adapters);

                dialogspinnerstep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
                        //String minsurance = spinnerinsurance.getSelectedItem().toString().trim();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

/**
 CheckBox dsatcheckbox = (CheckBox) dialog.findViewById(R.id.sat);
 CheckBox dsuncheckbox = (CheckBox) dialog.findViewById(R.id.sun);
 CheckBox dmoncheckbox = (CheckBox) dialog.findViewById(R.id.mon);
 CheckBox dtuscheckbox = (CheckBox) dialog.findViewById(R.id.tus);
 CheckBox dwedcheckbox = (CheckBox) dialog.findViewById(R.id.wed);
 CheckBox dthucheckbox = (CheckBox) dialog.findViewById(R.id.thu);
 CheckBox dfricheckbox = (CheckBox) dialog.findViewById(R.id.fri);
 //////////////////////////
 dsatcheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {satstate =true; } else { satstate =false;}
}
});
 dsuncheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {sunstate =true; } else { sunstate =false;}
}
});//satstate,sunstate,monstate,tusstate,wedstate,thustate,fristate
 dmoncheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {monstate =true; } else { monstate =false;}
}
});
 dtuscheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {tusstate =true; } else { tusstate =false;}
}
});
 dwedcheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {wedstate =true; } else { wedstate =false;}
}
});
 dthucheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {thustate =true; } else { thustate =false;}
}
});
 dfricheckbox.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View v) {
//is chkIos checked?
if (((CheckBox) v).isChecked()) {fristate =true; } else { fristate =false;}
}
});**/

                dsatcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dsatcardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dsatcardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            satstate = true;
                        } else {
                            dsatcardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            satstate = false;
                        }
                    }
                });
                dsuncardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dsuncardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dsuncardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            sunstate = true;
                        } else {

                            dsuncardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            satstate = false;
                        }
                    }
                });
                dmoncardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dmoncardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dmoncardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            monstate = true;
                        } else {

                            dmoncardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            monstate = false;
                        }
                    }
                });
                dtuscardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dtuscardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dtuscardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            tusstate = true;
                        } else {

                            dtuscardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            tusstate = false;
                        }
                    }
                });
                dwedcardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dwedcardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dwedcardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            wedstate = true;
                        } else {

                            dwedcardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            wedstate = false;
                        }
                    }
                });
                dthucardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dthucardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dthucardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            thustate = true;
                        } else {

                            dthucardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            thustate = false;
                        }
                    }
                });
                dfricardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dfricardview.getCardBackgroundColor().getDefaultColor() == -1) {
                            dfricardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            fristate = true;
                        } else {

                            dfricardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                            fristate = false;
                        }
                    }
                });
                ////////////////////


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String getaddress = dialogAddress.getText().toString().trim();
                        final String getstartingtime = dialogstarttime.getText().toString().trim();
                        final String getendingtime = dialogendingtime.getText().toString().trim();
                        final String getsteptime = dialogspinnerstep.getSelectedItem().toString().trim();
                        final String getHC = dialogspinnerHC.getSelectedItem().toString().trim();


                        if (getaddress.isEmpty()) {
                            dialogAddress.setError(getString(R.string.please_fill_the_address));
                            dialogAddress.requestFocus();
                            return;
                        }
                        if (getstartingtime.isEmpty()) {
                            dialogstarttime.setError(getString(R.string.please_fill_starting_time));
                            dialogstarttime.requestFocus();
                            return;
                        }
                        if (getendingtime.isEmpty()) {
                            dialogendingtime.setError(getString(R.string.please_fill_ending_times));
                            dialogendingtime.requestFocus();
                            return;
                        }
                        if (endingHour <= startHour) {
                            dialogendingtime.setError(getString(R.string.ending_time_must_be_after_starting_time_and_in_the_same_day));
                            dialogendingtime.requestFocus();
                            dialogstarttime.setError(getString(R.string.ending_time_must_be_after_starting_time_and_in_the_same_day));
                            dialogstarttime.requestFocus();
                            return;
                        }


                        DatabaseReference reference = databaseBooking.push();
                        final String id = reference.getKey();
                        //Log.v("Data"," 2-User id :"+ mUserId);
                        BookingClass bookingclass = new BookingClass(id, getstartingtime, getendingtime, getaddress, DoctorID, String.valueOf(latitude), String.valueOf(longitude), getsteptime, satstate, sunstate, monstate, tusstate, wedstate, thustate, fristate);
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
                                String DoctorGander = dataSnapshot1.child(DoctorID).child("cGandr").getValue(String.class);
                                String DoctorType = dataSnapshot1.child(DoctorID).child("cType").getValue(String.class);
                                if (DoctorPic == null) {
                                    DoctorPic = "https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=d3108b95-4e16-4549-99b6-f0fa466e0d11";
                                }
                                // DatabaseReference reference = databaseMap.push();
                                // String idm = reference.getKey();
                                //Log.v("Data"," 2-User id :"+ mUserId);
                                MapClass mapclass = new MapClass(DoctorID, String.valueOf(latitude), String.valueOf(longitude), DoctorName, DoctorSpecialty, DoctorPic, getHC, DoctorGander, DoctorType);
                                // BookingAdapter myAdapter = new BookingAdapter(DoctorProfileActivity.this, bookingList, id, DoctorID);
                                // Database for Account Activity
                                databaseMap.child(id).setValue(mapclass);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                            }
                        };
                        databaseDoctor.addValueEventListener(postListener1);

                        ///////////////////////////////////////////////

                        dialog.dismiss();
                        //to refresh activity as you need to go back activity and return
                        finish();
                        startActivity(getIntent());

                    }
                });
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLocation(dialog);
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

                //String whatdata = "Ex:from Sat to Mon in address at 15:00 clock";
                // editDialogbook();
            }
        });

        ////////////////////////////////
        //--------Gps---------------------
    }

    /***-------------------------------------------------***/
    /**
     private void editDialogbook() {

     final DatabaseReference databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);
     databaseBooking.keepSynced(true);

     final Dialog dialog = new Dialog(BookingListActivity.this);
     dialog.setContentView(R.layout.booking_data_dialig);
     //dialog.setTitle("Edit your data");
     dialog.setCanceledOnTouchOutside(false);
     getLocation(dialog);
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
    @Override public void onClick(View v) {
    //is chkIos checked?
    Calendar mcurrentTime = Calendar.getInstance();
    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    int minute = mcurrentTime.get(Calendar.MINUTE);

    TimePickerDialog mTimePicker;
    mTimePicker = new TimePickerDialog(BookingListActivity.this, new TimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


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
    @Override public void onClick(View v) {
    //is chkIos checked?
    Calendar mcurrentTime1 = Calendar.getInstance();
    int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
    int minute1 = mcurrentTime1.get(Calendar.MINUTE);
    TimePickerDialog mTimePicker1;
    mTimePicker1 = new TimePickerDialog(BookingListActivity.this, new TimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(TimePicker timePicker, int selectedHour1, int selectedMinute1) {


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

     final CardView dsatcardview = (CardView) dialog.findViewById(R.id.sat);
     final CardView dsuncardview = (CardView) dialog.findViewById(R.id.sun);
     final CardView dmoncardview = (CardView) dialog.findViewById(R.id.mon);
     final CardView dtuscardview = (CardView) dialog.findViewById(R.id.tus);
     final CardView dwedcardview = (CardView) dialog.findViewById(R.id.wed);
     final CardView dthucardview = (CardView) dialog.findViewById(R.id.thu);
     final CardView dfricardview = (CardView) dialog.findViewById(R.id.fri);

     /**
     CheckBox dsatcheckbox = (CheckBox) dialog.findViewById(R.id.sat);
     CheckBox dsuncheckbox = (CheckBox) dialog.findViewById(R.id.sun);
     CheckBox dmoncheckbox = (CheckBox) dialog.findViewById(R.id.mon);
     CheckBox dtuscheckbox = (CheckBox) dialog.findViewById(R.id.tus);
     CheckBox dwedcheckbox = (CheckBox) dialog.findViewById(R.id.wed);
     CheckBox dthucheckbox = (CheckBox) dialog.findViewById(R.id.thu);
     CheckBox dfricheckbox = (CheckBox) dialog.findViewById(R.id.fri);
     //////////////////////////
     dsatcheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {satstate =true; } else { satstate =false;}
    }
    });
     dsuncheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {sunstate =true; } else { sunstate =false;}
    }
    });//satstate,sunstate,monstate,tusstate,wedstate,thustate,fristate
     dmoncheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {monstate =true; } else { monstate =false;}
    }
    });
     dtuscheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {tusstate =true; } else { tusstate =false;}
    }
    });
     dwedcheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {wedstate =true; } else { wedstate =false;}
    }
    });
     dthucheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {thustate =true; } else { thustate =false;}
    }
    });
     dfricheckbox.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    //is chkIos checked?
    if (((CheckBox) v).isChecked()) {fristate =true; } else { fristate =false;}
    }
    });

     dsatcardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dsatcardview.getCardBackgroundColor().getDefaultColor()==-1){
    dsatcardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    satstate =true;
    }else{
    dsatcardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    satstate =false;
    }
    }
    });
     dsuncardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dsuncardview.getCardBackgroundColor().getDefaultColor()==-1){
    dsuncardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    sunstate =true;
    }else{

    dsuncardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    satstate =false;
    }
    }
    });
     dmoncardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dmoncardview.getCardBackgroundColor().getDefaultColor()==-1){
    dmoncardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    monstate =true;
    }else{

    dmoncardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    monstate =false;
    }
    }
    });
     dtuscardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dtuscardview.getCardBackgroundColor().getDefaultColor()==-1){
    dtuscardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    tusstate =true;
    }else{

    dtuscardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    tusstate =false;
    }
    }
    });
     dwedcardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dwedcardview.getCardBackgroundColor().getDefaultColor()==-1){
    dwedcardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    wedstate =true;
    }else{

    dwedcardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    wedstate =false;
    }
    }
    });
     dthucardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dthucardview.getCardBackgroundColor().getDefaultColor()==-1){
    dthucardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    thustate =true;
    }else{

    dthucardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    thustate =false;
    }
    }
    });
     dfricardview.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    if(dfricardview.getCardBackgroundColor().getDefaultColor()==-1){
    dfricardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
    fristate =true;
    }else{

    dfricardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    fristate =false;
    }
    }
    });
     ////////////////////





     submit.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
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
    @Override public void onDataChange(DataSnapshot dataSnapshot1) {

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

    @Override public void onCancelled(DatabaseError databaseError) {
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
    @Override public void onClick(View v) {
    dialog.dismiss();
    }
    });

     dialog.setCanceledOnTouchOutside(false);

     dialog.show();
     }
     **/


    //-----------------------------add Gps----------------------------------

    /**
     * Request the Location Permission
     */
    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        } else {
            isPermissionGranted = true;
        }

    }

    /**
     * this method gets the location of the user then
     * Calls the showAddress Method and pass to it the Latitude
     * and the Longitude
     *
     * @param dialog
     */
    private void getLocation(final Dialog dialog) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    showAddress(latitude, longitude, dialog);
                } else {
                    Toast.makeText(BookingListActivity.this, R.string.error_we_didnot_get_the_location_please_try_again_after_few_seconds, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * this method shows the User's address to the screen, it calls the getAddress which returns a string
     * contains the address then changes the TextView text to it.
     *
     * @param latitude  is the latitude of the location
     * @param longitude is the longitude of the location
     * @param dialog
     */
    private void showAddress(double latitude, double longitude, Dialog dialog) {
        String msg = "";

        try {
            msg = getAddress(latitude, longitude, dialog);
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
        switch (requestCode) {
            case theRequestCodeForLocation:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                } else {
                    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
                    isPermissionGranted = false;
                }
                break;
        }

    }

    /**
     * this method takes the longitude and latitude of the location then convert them into real address
     * and return it as string
     *
     * @param latitude  the Latitude
     * @param longitude the Longitude
     * @return the address as String
     * @throws IOException
     */
    private String getAddress(double latitude, double longitude, Dialog dialog) throws IOException {

        //Geocoder class helps us to convert longitude and latitude into Address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = "Country: " + addresses.get(0).getCountryName();
        // String wholeAddress = address + "\n" + city + "\n" + state + "\n" + country;
        String wholeAddress = address;
        // pcity.setText(address);
        dialogAddress = (EditText) dialog.findViewById(R.id.dialog_address);
        dialogAddress.setEnabled(true);
        dialogAddress.setText(state+" - " + city);
        dialogAddress.setEnabled(false);
        return state + " - " + city;

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void onStart() {
        super.onStart();
        progressBarBooking.setVisibility(View.VISIBLE);
        // getRegData();
        if (UtilClass.isNetworkConnected(BookingListActivity.this)) {
        } else {
            Toast.makeText(BookingListActivity.this, getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
        }
            final DatabaseReference databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);

            //databaseTramp.child(country).child("Individual").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            databaseBooking.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bookingList.clear();
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        BookingClass bookingclass = doctorSnapshot.getValue(BookingClass.class);
                        bookingList.add(bookingclass);// i= 0  (index)to start from top


                    }
                    //}
                    //}
                    BookingAdapter adapter = new BookingAdapter(BookingListActivity.this, bookingList, DoctorID, patientName, patientAge, BookingType, MaxNo);
                    //adapter.notifyDataSetChanged();
                    listViewBooking.setAdapter(adapter);
                    progressBarBooking.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });



    }

/**
 private void openClenderAction(final String timeID , final int position) {
 ImageGenerator mImageGenerator = new ImageGenerator(BookingListActivity.this);

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
 DatePickerDialog mPickerDialog =  new DatePickerDialog(BookingListActivity.this, new DatePickerDialog.OnDateSetListener() {
@Override public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
String month1 = String.valueOf(Month + 1);
if (Integer.parseInt(month1) < 10) {
month1 = "0" + month1;
}
String datedmy= Year+"_"+ month1+"_"+Day;
try {
String dayname=getDayNameFromDate( datedmy);
//Toast.makeText(DoctorProfileActivity.this, dayname, Toast.LENGTH_LONG).show();
BookingClass bookingclass = bookingList.get(position);
String a,b,c,d,e,f,g;
if( bookingclass.getSatchecked()){ a="Saturday";}else{a="no";}
if( bookingclass.getSunchecked()){ b="Sunday";}else{b="no";}
if( bookingclass.getMonchecked()){ c="Monday";}else{c="no";}
if( bookingclass.getTuschecked()){ d="Tuesday";}else{d="no";}
if(bookingclass.getWedchecked()){ e="Wednesday";}else{e="no";}
if(bookingclass.getThuchecked()){ f="Thursday";}else{f="no";}
if( bookingclass.getFrichecked()){ g="Friday";}else{g="no";}
if(dayname.equalsIgnoreCase(a)||dayname.equalsIgnoreCase(b)||dayname.equalsIgnoreCase(c)||dayname.equalsIgnoreCase(d)
||dayname.equalsIgnoreCase(e)||dayname.equalsIgnoreCase(f)||dayname.equalsIgnoreCase(g) ){
makepatientbooking(timeID, datedmy, position);
Toast.makeText(BookingListActivity.this, "is booked", Toast.LENGTH_LONG).show();
}else{Toast.makeText(BookingListActivity.this, "Not match", Toast.LENGTH_LONG).show();}
} catch (ParseException e) {
e.printStackTrace();
}
//  Toast.makeText(DoctorProfileActivity.this, datedmy, Toast.LENGTH_LONG).show();
// Toast.makeText(context, id+doctorID, Toast.LENGTH_LONG).show();

//editTextcal.setText(Year+"_"+ ((Month/10)+1)+"_"+Day);
mCurrentDate.set(Year, ((Month+1)),Day);
//   mImageGenerator.generateDateImage(mCurrentDate, R.drawable.empty_calendar);
}
}, year, month, day);
 mPickerDialog.show();
 }**/
    /**
     * private void makepatientbooking(final String timeID, final String datedmy, final int position) {
     * <p>
     * ///// /*************************************
     * final ValueEventListener postListener = new ValueEventListener() {
     *
     * @Override public void onDataChange(DataSnapshot dataSnapshot) {
     * final String  picuri,mDate;
     * final String patientName = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
     * final String patientBirthday = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cbirthday").getValue(String.class);
     * <p>
     * final BookingClass currentBooking = bookingList.get(position);
     * String patientpic = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cpatentphoto").getValue(String.class);
     * if(patientpic != null){
     * picuri=patientpic;
     * }else{picuri="https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/user_logo_m.jpg?alt=media&token=ff53fa61-0252-43a4-8fa3-0eb3a3976ee5";}
     * // Toast.makeText(DoctorProfileActivity.this, picuri, Toast.LENGTH_LONG).show();
     * <p>
     * Calendar calendar = Calendar.getInstance();
     * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     * mDate = sdf.format(calendar.getTime());
     * <p>
     * ////**************for user********************
     * <p>
     * databasetimeBooking.child(DoctorID).child(timeID) .child(datedmy).addListenerForSingleValueEvent(new ValueEventListener() {
     * @Override public void onDataChange(DataSnapshot dataSnapshot) {
     * // for (DataSnapshot snap: dataSnapshot.getChildren()) {
     * //  dataSnapshot.getChildrenCount();
     * // Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
     * arrange =String.valueOf( dataSnapshot.getChildrenCount()+1 );
     * Toast.makeText(BookingListActivity.this ,"your Arrangement is the"+arrange, Toast.LENGTH_LONG).show();
     * //  databasetimeBooking.child(DoctorID).child(timeID).child(datedmy).child(mAuth.getCurrentUser().getUid()).child("ctArrangement").setValue(String.valueOf( dataSnapshot.getChildrenCount() ));
     * //String.valueOf( arrange )
     * DatabaseReference bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser");
     * DatabaseReference referencea = bookforuser.push();
     * String randomid = referencea.getKey();
     * <p>
     * BookingTimesClass bookingtimes = new BookingTimesClass( DoctorID,  mDate, currentBooking.getCbaddress(),timeID , datedmy,arrange);
     * bookforuser.child(userid).child(DoctorID+datedmy).setValue(bookingtimes);
     * ///***********for adapt arange in user booking activity*************
     * //  databasetimeBooking.child(DoctorID).child(timeID).child(datedmy).child(mAuth.getCurrentUser().getUid()).child("rangementid").setValue(randomid);
     * //String.valueOf( arrange )
     * ////to do/////////fordoctor***********************
     * <p>
     * DatabaseReference reference1 = databasetimeBooking.push();
     * //final DatabaseReference databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes").child(DoctorID).child(timeID).child(datedmy);
     * // DatabaseReference reference = databasetimeBooking.push();
     * String timesid = reference1.getKey();
     * <p>
     * //Log.v("Data"," 2-User id :"+ mUserId);
     * <p>
     * // get age from birthday
     * //  String patientAge = UtilClass.calculateAgeFromDate(patientBirthday);
     * <p>
     * <p>
     * BookingTimesClass  bookingtimesclass = new BookingTimesClass(userid, patientName, patientBirthday, mDate, currentBooking.getCbaddress(),currentBooking.getCbtimestart(),currentBooking.getCbtimeend() , arrange ,picuri,timeID,datedmy,Integer.valueOf(arrange) );
     * // Database for Account Activity
     * databasetimeBooking.child(DoctorID).child(timeID)
     * .child(datedmy)
     * .child(userid).setValue(bookingtimesclass).addOnCompleteListener(
     * new OnCompleteListener<Void>() {
     * @Override public void onComplete(@NonNull Task<Void> task) {
     * notify = true;
     * if (notify && userid == mAuth.getCurrentUser().getUid()) {
     * System.out.println("databasetimebooking listner: pName:" +
     * patientName + " ,, Doctor ID:" + DoctorID +
     * ",, user id : " + userid);
     * <p>
     * sendNotifiaction(DoctorID, patientName, "Booking time with you");
     * }
     * notify = false;
     * }
     * }
     * );
     * //////////////////////***fordoctor****
     * }
     * @Override public void onCancelled(DatabaseError databaseError) {
     * <p>
     * }
     * });
     * <p>
     * ////**************for user********************
     * <p>
     * <p>
     * <p>
     * <p>
     * }
     * @Override public void onCancelled(DatabaseError databaseError) {
     * // Getting Post failed, log a message
     * }
     * };
     * databaseUserReg .addValueEventListener(postListener);
     * <p>
     * /////////////////////*************************************
     * <p>
     * }
     **/
    public static String getDayNameFromDate(String date) throws ParseException {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date dt = inFormat.parse(date);
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String dayName = outFormat.format(dt);
        return dayName;
    }

    private void sendNotifiaction(final String receiver, final String username, final String message, final String datedmy) {

        final String rec = receiver;

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Token token = snapshot.getValue(Token.class);
                    /*Data data = new Data(fuser.getUid(), R.drawable.ic_stat_name,
                            username + ": " + message, "Booking",
                            receiver);*/
                    Data data = new Data(fuser.getUid(),  R.drawable.ic_stat_name,
                            username + ": " + message, "Booking", receiver,"b", datedmy);
                    Sender sender = new Sender(data, token.getToken());
                    // Sender sender = new Sender(data, token.getToken());

                    System.out.println("D push noti method: token :" + token.getToken() +
                            ",,,  userid:" + userid +
                            ",,,  reciever: " + rec +
                            ",,, fuser-sender : " + fuser.getUid()
                    );

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(BookingListActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

package com.example.ahmedmagdy.theclinic.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.Notifications.Client;
import com.example.ahmedmagdy.theclinic.Notifications.Data;
import com.example.ahmedmagdy.theclinic.Notifications.MyResponse;
import com.example.ahmedmagdy.theclinic.Notifications.Sender;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.PatientFragment.AllDoctorfragment;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.CalenderActivity;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.MapClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.example.ahmedmagdy.theclinic.map.DoctorMapFrag;
import com.example.ahmedmagdy.theclinic.map.UserLocation;
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
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ahmedmagdy.theclinic.activities.BookingListActivity.getDayNameFromDate;

/**
 * Created by AHMED MAGDY on 10/23/2018.
 */

public class BookingAdapter extends ArrayAdapter<BookingClass> {


    private Activity context;
    List<BookingClass> bookingList;
    private FirebaseAuth mAuth;
    private DatabaseReference databasetimeBooking,databaseBooking,databaseMap;
    private DatabaseReference databaseUserReg,databaseDoctor,databaseChat;

    private FirebaseUser fuser;
    String userid,arrange,startTime, endingTime,address;
    boolean notify = false;
    APIService apiService;
    int startHour, endingHour;
    boolean satstate, sunstate, monstate, tusstate, wedstate, thustate, fristate;
    EditText dialogAddress;

    double latitude , longitude;
    double docLatitude , docLongitude;
    final int theRequestCodeForLocation = 1;
    Boolean isPermissionGranted;
    private FusedLocationProviderClient mFusedLocationClient;
    public static ArrayList<UserLocation> mUserLocations = new ArrayList<>();


    /**public String id;
    public String doctorID;

    public BookingAdapter(DoctorProfileActivity context , List<BookingClass> bookingList, String id, String doctorID) {
        super((Context) context, R.layout.list_layout_booking, bookingList);

        this.context = context;
        this.bookingList = bookingList;
        this.id = id;
        this.doctorID = doctorID;
    }**/
    private String DoctorID;
    private String patientName;
    private String patientAge;
    private Boolean BookingType;
    private String MaxNo;

    public BookingAdapter( Activity context, List<BookingClass> bookingList,String DoctorID,String patientName,String patientAge,Boolean BookingType, String MaxNo) {
        super((Context) context, R.layout.list_layout_booking, bookingList);

        this.context = context;
        this.bookingList = bookingList;
        this.DoctorID = DoctorID;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.BookingType = BookingType;
        this.MaxNo = MaxNo;
    }



    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.list_layout_booking, null, true);

        fuser = mAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes");databasetimeBooking.keepSynced(true);
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");databaseUserReg.keepSynced(true);
        databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);databaseBooking.keepSynced(true);
        databaseMap = FirebaseDatabase.getInstance().getReference("mapdb");databaseMap.keepSynced(true);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");databaseDoctor.keepSynced(true);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");databaseChat.keepSynced(true);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        final TextView abookingtimestart = (TextView) listViewItem.findViewById(R.id.time_book_start);
        final TextView abookingtimeend = (TextView) listViewItem.findViewById(R.id.time_book_end);
        final TextView abookingaddress = (TextView) listViewItem.findViewById(R.id.Adress_book);

        CardView dbookcardview = (CardView) listViewItem.findViewById(R.id.book_card_1);
        CardView dlocationcardview = (CardView) listViewItem.findViewById(R.id.location_card_1);
        CardView deditcardview = (CardView) listViewItem.findViewById(R.id.edit_card_1);


        CardView dsatcardview = (CardView) listViewItem.findViewById(R.id.sat1);
        CardView dsuncardview = (CardView) listViewItem.findViewById(R.id.sun1);
        CardView dmoncardview = (CardView) listViewItem.findViewById(R.id.mon1);
        CardView dtuscardview = (CardView) listViewItem.findViewById(R.id.tus1);
        CardView dwedcardview = (CardView) listViewItem.findViewById(R.id.wed1);
        CardView dthucardview = (CardView) listViewItem.findViewById(R.id.thu1);
        CardView dfricardview = (CardView) listViewItem.findViewById(R.id.fri1);

        TextView dsattextview = (TextView) listViewItem.findViewById(R.id.sat1_text);
        TextView dsuntextview = (TextView) listViewItem.findViewById(R.id.sun1_text);
        TextView dmontextview = (TextView) listViewItem.findViewById(R.id.mon1_text);
        TextView dtustextview = (TextView) listViewItem.findViewById(R.id.tus1_text);
        TextView dwedtextview = (TextView) listViewItem.findViewById(R.id.wed1_text);
        TextView dthutextview = (TextView) listViewItem.findViewById(R.id.thu1_text);
        TextView dfritextview = (TextView) listViewItem.findViewById(R.id.fri1_text);


        // final ImageView abookingphoto = (ImageView) listViewItem.findViewById(R.id.image_book);

        final BookingClass bookingclass = bookingList.get(position);

        dlocationcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateDocMapFragment();
            }
        });
        if (!DoctorID.equals(mAuth.getCurrentUser().getUid())) {
            deditcardview.setVisibility(View.GONE);
        }
        deditcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BookingClass bookingclass = bookingList.get(position);

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.edit_delete_dialog);
                dialog.setTitle(R.string.choose_what_you_need);
                dialog.setCanceledOnTouchOutside(false);
                final Button editbtn = dialog.findViewById(R.id.edit_select_dialog);
                final Button deletebtn = dialog.findViewById(R.id.delete_select_dialog);
                final Button cancelbtn = dialog.findViewById(R.id.cancel_select_dialog);


                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BookingClass bookingclass = bookingList.get(position);
                        databaseMap.child(DoctorID+bookingclass.getCbid()).setValue(null);
                        databaseBooking.child(bookingclass.getCbid()).setValue(null);
                        dialog.dismiss();

                    }
                });
                //--------Gps---------------------
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                requestPermission();
                editbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

////////////////////////////////syart of edit box/////////////////////

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.booking_data_dialig);
                        dialog.setTitle(R.string.edit_your_data);
                        dialog.setCanceledOnTouchOutside(false);
                        //
                        //
                        //--------get location from Gps---------------------
                        if(isPermissionGranted){

                            getLocation(dialog);
                        }else{
                            requestPermission();
                            if(isPermissionGranted){
                                //We have it, Get the location.
                                getLocation(dialog);
                            }
                            else {
                                Toast.makeText(context, R.string.please_give_us_permission_so_you_can_use_the_app, Toast.LENGTH_SHORT).show();
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
                        final Spinner dialogspinnerHC= dialog.findViewById(R.id.hospital_clinic);

                        dialogstarttimelogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //is chkIos checked?
                                Calendar mcurrentTime = Calendar.getInstance();
                                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                int minute = mcurrentTime.get(Calendar.MINUTE);

                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
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

                                        startTime=selectedHour00 + ":" + selectedMinute00;
                                        startHour=selectedHour;

                                        dialogstarttime.setEnabled(true);
                                        dialogstarttime.setText( startTime);
                                        dialogstarttime.setEnabled(false);

                                    }
                                }, hour, minute, false);//Yes 24 hour time
                                mTimePicker.setTitle(R.string.select_time);
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
                                mTimePicker1 = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
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
                                        endingTime=selectedHour100 + ":" + selectedMinute100;
                                        endingHour=selectedHour1;

                                        dialogendingtime.setEnabled(true);

                                        dialogendingtime.setText( endingTime);
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
                                context, R.array.HC_array, android.R.layout.simple_spinner_item);
                        adapterHC.setDropDownViewResource(R.layout.spinner_list_item);
                        dialogspinnerHC.setAdapter(adapterHC);

                        dialogspinnerHC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(context.getResources().getColor(R.color.colorText));
                                //String minsurance = spinnerinsurance.getSelectedItem().toString().trim();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        // spinner for insurance
                        ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(
                                context, R.array.step_array, android.R.layout.simple_spinner_item);
                        adapters.setDropDownViewResource(R.layout.spinner_list_item);
                        dialogspinnerstep.setAdapter(adapters);

                        dialogspinnerstep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(context.getResources().getColor(R.color.colorText));
                                //String minsurance = spinnerinsurance.getSelectedItem().toString().trim();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        dsatcardview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
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
                            @Override
                            public void onClick(View v) {
                                final String getaddress = dialogAddress.getText().toString().trim();
                                final String getstartingtime = dialogstarttime.getText().toString().trim();
                                final String getendingtime = dialogendingtime.getText().toString().trim();
                                final String getsteptime = dialogspinnerstep.getSelectedItem().toString().trim();
                                final String  getHC= dialogspinnerHC.getSelectedItem().toString().trim();



                                if (getaddress.isEmpty()) {
                                    dialogAddress.setError(context.getString(R.string.please_fill_the_address));
                                    dialogAddress.requestFocus();
                                    return;}
                                if (getstartingtime.isEmpty()) {
                                    dialogstarttime.setError(context.getString(R.string.please_fill_starting_time));
                                    dialogstarttime.requestFocus();
                                    return;}
                                if (getendingtime.isEmpty()) {
                                    dialogendingtime.setError(context.getString(R.string.please_fill_ending_times));
                                    dialogendingtime.requestFocus();
                                    return;}
                                if (endingHour<=startHour) {
                                    dialogendingtime.setError(context.getString(R.string.ending_time_must_be_after_starting_time_and_in_the_same_day));
                                    dialogendingtime.requestFocus();
                                    dialogstarttime.setError(context.getString(R.string.ending_time_must_be_after_starting_time_and_in_the_same_day));
                                    dialogstarttime.requestFocus();
                                    return;}


                                BookingClass bookingclass1 = new BookingClass(bookingclass.getCbid(), getstartingtime,getendingtime, getaddress,DoctorID,String.valueOf(latitude),String.valueOf(longitude),getsteptime,satstate,sunstate,monstate,tusstate,wedstate,thustate,fristate);
                                // BookingAdapter myAdapter = new BookingAdapter(DoctorProfileActivity.this, bookingList, id, DoctorID);
                                // Database for Account Activity
                                databaseBooking.child(bookingclass.getCbid()).setValue(bookingclass1);
                                //////////////////////////////////////
                                final ValueEventListener postListener1 = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot1) {

                                        String DoctorName = dataSnapshot1.child(DoctorID).child("cName").getValue(String.class);
                                        String DoctorSpecialty = dataSnapshot1.child(DoctorID).child("cSpecialty").getValue(String.class);
                                        String DoctorPic = dataSnapshot1.child(DoctorID).child("cUri").getValue(String.class);
                                        String DoctorGander = dataSnapshot1.child(DoctorID).child("cGandr").getValue(String.class);
                                        String DoctorType = dataSnapshot1.child(DoctorID).child("cType").getValue(String.class);
                                        if (DoctorPic == null){DoctorPic= "https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=d3108b95-4e16-4549-99b6-f0fa466e0d11";}
                                        // DatabaseReference reference = databaseMap.push();
                                        // String idm = reference.getKey();
                                        //Log.v("Data"," 2-User id :"+ mUserId);
                                        MapClass mapclass = new MapClass(DoctorID,String.valueOf(latitude),String.valueOf(longitude),DoctorName,DoctorSpecialty,DoctorPic,getHC,DoctorGander,DoctorType);
                                        // BookingAdapter myAdapter = new BookingAdapter(DoctorProfileActivity.this, bookingList, id, DoctorID);
                                        // Database for Account Activity
                                        databaseMap.child(DoctorID+bookingclass.getCbid()).setValue(mapclass);
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
                                context.finish();
                                context.startActivity(context.getIntent());

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
                        /////////////////////////end of editbox//////////////////////////////
                    }
                });


                dialog.show();
            }
        });

        dbookcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(BookingType){
                    BookingClass bookingclass = bookingList.get(position);
                    final String timeID = bookingclass.getCbid();
                    String comefrom = "1";


                    Intent intent = new Intent(context, CalenderActivity.class);
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
                    context.startActivity(intent);
                }else{
                    ///////////////////************rearrange booking*******************//////////////////
                    if (mAuth.getCurrentUser() == null) {
                        Toast.makeText(context, R.string.you_should_log_in_firstly, Toast.LENGTH_LONG).show();
                    } else{

                        BookingClass bookingclass = bookingList.get(position);
                        final String timeID = bookingclass.getCbid();

                        openClenderAction(timeID, position);

                    }

                    ////////////////////////**rearrange booking***////////////////////////////////
                }
            }
        });

        ///***********************checkbox**********************************************//

        abookingtimestart.setText(bookingclass.getCbtimestart());
        abookingtimeend.setText(bookingclass.getCbtimeend());

        abookingaddress.setText(bookingclass.getCbaddress());


        Boolean csatcheckbox=(bookingclass.getSatchecked());
        Boolean csuncheckbox=(bookingclass.getSunchecked());
        Boolean cmoncheckbox=(bookingclass.getMonchecked());
        Boolean ctuscheckbox=(bookingclass.getTuschecked());
        Boolean cwedcheckbox=(bookingclass.getWedchecked());
        Boolean cthucheckbox=(bookingclass.getThuchecked());
        Boolean cfricheckbox=(bookingclass.getFrichecked());

        if (csatcheckbox){

            dsatcardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
            }else{
            dsatcardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dsattextview.setTextColor(Color.parseColor("#ffffff"));
            }
            //////////////////***********************
        if (csuncheckbox){

            dsuncardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
        }else{
            dsuncardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dsuntextview.setTextColor(Color.parseColor("#ffffff"));
        }
        //////////////////***********************

        if (cmoncheckbox){

            dmoncardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
        }else{
            dmoncardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dmontextview.setTextColor(Color.parseColor("#ffffff"));
        }
        //////////////////***********************

        if (ctuscheckbox){

            dtuscardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
        }else{
            dtuscardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dtustextview.setTextColor(Color.parseColor("#ffffff"));
        }
        //////////////////***********************
        if (cwedcheckbox){

            dwedcardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
        }else{
            dwedcardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dwedtextview.setTextColor(Color.parseColor("#ffffff"));
        }
        //////////////////***********************
        if (cthucheckbox){

            dthucardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
        }else{
            dthucardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dthutextview.setTextColor(Color.parseColor("#ffffff"));
        }
        //////////////////***********************

        if (cfricheckbox){

            dfricardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
        }else{
            dfricardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            dfritextview.setTextColor(Color.parseColor("#ffffff"));
        }
        //////////////////***********************




       // dsatcheckbox.setEnabled(false); dsuncheckbox.setEnabled(false); dmoncheckbox.setEnabled(false); dtuscheckbox.setEnabled(false);
      //  dwedcheckbox.setEnabled(false); dthucheckbox.setEnabled(false); dfricheckbox.setEnabled(false);

        Animation animation = AnimationUtils.loadAnimation(context,R.anim.scale);
        listViewItem.startAnimation(animation);
        return listViewItem;
    }

    private void openClenderAction(final String timeID , final int position) {
        ImageGenerator mImageGenerator = new ImageGenerator(context);

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
        DatePickerDialog mPickerDialog =  new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
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
                    }else{Toast.makeText(context, R.string.not_match, Toast.LENGTH_LONG).show();}
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
    }

    private void makepatientbooking(final String timeID, final String datedmy, final int position) {

        /*************************************/
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String  picuri,mDate;
                final String patientName = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                final String patientBirthday = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cbirthday").getValue(String.class);

                final BookingClass currentBooking = bookingList.get(position);
                String patientpic = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cpatentphoto").getValue(String.class);
                if(patientpic != null){
                    picuri=patientpic;
                }else{picuri="https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/user_logo_m.jpg?alt=media&token=ff53fa61-0252-43a4-8fa3-0eb3a3976ee5";}
                // Toast.makeText(DoctorProfileActivity.this, picuri, Toast.LENGTH_LONG).show();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mDate = sdf.format(calendar.getTime());

                ////**************for user*********************/

                databasetimeBooking.child(DoctorID).child(timeID) .child(datedmy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // for (DataSnapshot snap: dataSnapshot.getChildren()) {
                        //  dataSnapshot.getChildrenCount();
                        // Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
                        arrange = String.valueOf(dataSnapshot.getChildrenCount() + 1);
                      //  if(MaxNo == null){
                        if ((MaxNo == null)||((dataSnapshot.getChildrenCount() + 1) <= (Integer.parseInt(MaxNo)) )) {
                            Toast.makeText(context, context.getString(R.string.your_arrangement_is_the) + arrange, Toast.LENGTH_LONG).show();
                            //  databasetimeBooking.child(DoctorID).child(timeID).child(datedmy).child(mAuth.getCurrentUser().getUid()).child("ctArrangement").setValue(String.valueOf( dataSnapshot.getChildrenCount() ));
                            //String.valueOf( arrange )
                            DatabaseReference bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser");
                            DatabaseReference referencea = bookforuser.push();
                            String randomid = referencea.getKey();

                            BookingTimesClass bookingtimes = new BookingTimesClass(DoctorID, mDate, currentBooking.getCbaddress(), timeID, datedmy, arrange);
                            bookforuser.child(userid).child(DoctorID + datedmy).setValue(bookingtimes);
                            Toast.makeText(context, R.string.is_booked, Toast.LENGTH_LONG).show();

                            ///***********for adapt arange in user booking activity**************/
                            //  databasetimeBooking.child(DoctorID).child(timeID).child(datedmy).child(mAuth.getCurrentUser().getUid()).child("rangementid").setValue(randomid);
                            //String.valueOf( arrange )
                            ////to do/////////-------------------fordoctor------------------------------------------

                            DatabaseReference reference1 = databasetimeBooking.push();
                            //final DatabaseReference databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes").child(DoctorID).child(timeID).child(datedmy);
                            // DatabaseReference reference = databasetimeBooking.push();
                            String timesid = reference1.getKey();

                            //Log.v("Data"," 2-User id :"+ mUserId);

                            // get age from birthday
                            //  String patientAge = UtilClass.calculateAgeFromDate(patientBirthday);


                            BookingTimesClass bookingtimesclass = new BookingTimesClass(userid, patientName, patientBirthday, mDate, currentBooking.getCbaddress(), currentBooking.getCbtimestart(), currentBooking.getCbtimeend(), arrange, picuri, timeID, datedmy, Integer.valueOf(arrange));
                            // Database for Account Activity
                            databasetimeBooking.child(DoctorID).child(timeID)
                                    .child(datedmy)
                                    .child(userid).setValue(bookingtimesclass).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            notify = true;
                                            if (notify && userid == mAuth.getCurrentUser().getUid()) {

                                                // mDate or datedmy // currentBooking.getCbtimestart() // arrange

                                                String datedmy2 = datedmy.replaceAll("_", "-");
                                                System.out.println("booking_databasetimebooking_2"+",, datedmy: " + datedmy2+",, mDate: "
                                                        +mDate +",, getCbtimestart: " + currentBooking.getCbtimestart()+",, arrange: " + arrange);

                                                String msg = context.getString(R.string.booking_time_with_you)+" , on "+datedmy2+" - "+
                                                                currentBooking.getCbtimestart()+" , No. "+arrange;
                                                sendNotifiaction(DoctorID, patientName, msg);
                                            }
                                            notify = false;
                                        }
                                    }
                            );
                        }else{Toast.makeText(context, R.string.doctor_reach_to_the_max_no_of_bookings_in_this_time_try_with_another_time, Toast.LENGTH_LONG).show();
                        }//here

                        //////////////////////***fordoctor****-----------------
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                ////**************for user*********************/




            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat .addValueEventListener(postListener);

        /*************************************/
    }
    private void sendNotifiaction(final String receiver, final String username, final String message) {

        final String rec = receiver;

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),  R.drawable.ic_stat_name,
                            username + ": " + message, "Booking", receiver,"b");
                    Sender sender = new Sender(data,token.getToken());
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
                                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
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







    //-----------------------------add Gps----------------------------------
    /**
     * Request the Location Permission
     */
    private void requestPermission(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        }
        else{
            isPermissionGranted = true;
        }

    }

    /**
     * this method gets the location of the user then
     * Calls the showAddress Method and pass to it the Latitude
     * and the Longitude
     * @param dialog
     */
    private void getLocation(final Dialog dialog) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    showAddress(latitude,longitude,dialog);
                }else{
                    Toast.makeText(context, R.string.error_we_didnot_get_the_location_please_try_again_after_few_seconds, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * this method shows the User's address to the screen, it calls the getAddress which returns a string
     * contains the address then changes the TextView text to it.
     * @param latitude is the latitude of the location
     * @param longitude is the longitude of the location
     * @param dialog
     */
    private void showAddress(double latitude, double longitude, Dialog dialog){
        String msg = "";

        try {
            msg = getAddress(latitude, longitude,dialog);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pcity.setText(msg);
//
    }

    /**
     * this method takes the longitude and latitude of the location then convert them into real address
     * and return it as string
     * @param latitude the Latitude
     * @param longitude the Longitude
     * @return the address as String
     * @throws IOException
     */
    private String getAddress(double latitude, double longitude,Dialog dialog) throws IOException {

        //Geocoder class helps us to convert longitude and latitude into Address
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        address =  addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state =  addresses.get(0).getAdminArea();
        String country = "Country: " + addresses.get(0).getCountryName();
        // String wholeAddress = address + "\n" + city + "\n" + state + "\n" + country;
        String wholeAddress = address ;
        // pcity.setText(address);
        dialogAddress = (EditText) dialog.findViewById(R.id.dialog_address);
        dialogAddress.setEnabled(true);
        dialogAddress.setText(state+" - "+city);
        dialogAddress.setEnabled(false);
        return  state+" - "+city;

    }


    /**
     * this method is called by android system after we request a permission
     * and the system pass the result of our request to this method so we can check if we got
     * the permission or not
     */
    /**
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode){
            case theRequestCodeForLocation:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isPermissionGranted = true;
                }else{
                    Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
                    isPermissionGranted = false;
                }
                break;
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        context.finish();
        return true;
    }**/
    @SuppressLint("ResourceType")
    private void inflateDocMapFragment() {
        getAllDoctorsMap();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((AppCompatActivity)
                context).getSupportFragmentManager().beginTransaction();

        DoctorMapFrag fragment = new DoctorMapFrag();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("intent_user_locs", mUserLocations);
        //bundle.putString("YourKey1", "YourValue");
        //docLatitude , docLongitude
        System.out.println("BookingAdapterList : lat: "+docLatitude+" ,lng: "+docLongitude);
        bundle.putDouble("lat", docLatitude);
        bundle.putDouble("lng", docLongitude);
        fragment.setArguments(bundle);

        fragmentTransaction.add(R.id.activity_booking_list_frame_id, fragment,"User List");
        fragmentTransaction.addToBackStack("User List");
        fragmentTransaction.commit();
    }
    //get all doc data
    private void getAllDoctorsMap() {
        if (UtilClass.isNetworkConnected(getContext())) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child("mapdb");
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("qerrrrrrrrrrry Count " + "" + dataSnapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserLocation post = postSnapshot.getValue(UserLocation.class);
                        mUserLocations.add(post);
                        System.out.println("qerrrrrrrrrrry Get Data" + post.getCmname());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            query.addValueEventListener(valueEventListener);

        } else {
            Toast.makeText(getContext(), context.getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.ahmedmagdy.theclinic.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.OneWordAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.OneWordClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class CalenderActivity extends AppCompatActivity {
    private HorizontalCalendar horizontalCalendar;
    private TextView timef;
    String updatedTimeprinted;
    GridView listview;
    String DoctorID;
    String TimeID,DoctorAddress,patientName,patientAge;
    String selectedDateStr,selectedDateStrForFirebase;
    Boolean Satchecked,Sunchecked,Monchecked,Tuschecked,Wedchecked,Thuchecked,Frichecked;
    Boolean dayAvaliable;
    int month1=-1;
    int day1=-1;
    int year1=-1;
    int come=-1;
    String StartTime,Endtime;   int step = 15;
    ArrayList<OneWordClass> mtimes = new ArrayList<OneWordClass>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        Toolbar toolbar = findViewById(R.id.toolbar);

//----------------------------------------------------------------
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if(getSupportActionBar() != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }
//=============================================================


        Intent intent = getIntent();
        come = Integer.parseInt(intent.getStringExtra("comefrom"));
        DoctorID = intent.getStringExtra("DoctorID");
        TimeID = intent.getStringExtra("TimeID");
        StartTime = intent.getStringExtra("StartingTime");
        Endtime = intent.getStringExtra("EndingTime");
         DoctorAddress = intent.getStringExtra("DoctorAddress");
        step =  Integer.parseInt(intent.getStringExtra("StepTime")) ;
         patientName = intent.getStringExtra("patientName");
         patientAge = intent.getStringExtra("patientAge");

         Satchecked = intent.getBooleanExtra("Satchecked",false);
         Sunchecked = intent.getBooleanExtra("Sunchecked",false);
         Monchecked = intent.getBooleanExtra("Monchecked",false);
         Tuschecked = intent.getBooleanExtra("Tuschecked",false);
         Wedchecked = intent.getBooleanExtra("Wedchecked",false);
         Thuchecked = intent.getBooleanExtra("Thuchecked",false);
         Frichecked = intent.getBooleanExtra("Frichecked",false);


        /* start 2 months ago from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        /* end after 2 months from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        // Default Date set to Today.
        final Calendar defaultSelectedDate = Calendar.getInstance();
      //  defaultSelectedDate.setTime(date);
        if(come==0) {
        Intent intent1 = getIntent();
            year1 = Integer.parseInt(intent1.getStringExtra("year"));
            month1 = Integer.parseInt(intent1.getStringExtra("month"));
            day1 = Integer.parseInt(intent1.getStringExtra("day"));

    if((month1 != -1) && (day1 != -1)&& (year1 != -1)) {

      //  Toast.makeText(CalenderActivity.this, day1+ " ***", Toast.LENGTH_SHORT).show();

    defaultSelectedDate.set(Calendar.YEAR, year1);
    defaultSelectedDate.set(Calendar.MONTH, month1-1);

        defaultSelectedDate.set(Calendar.DAY_OF_MONTH, day1);
}}

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                .end()
                .defaultSelectedDate(defaultSelectedDate)
                .build();
// caltext=Year+"_"+ (Month+1)+"_"+Day;
        selectedDateStr= DateFormat.format("EEE, MMM d, yyyy", defaultSelectedDate).toString();
        String dayname = DateFormat.format("EEE", defaultSelectedDate).toString();
        selectedDateStrForFirebase = DateFormat.format("yyyy_MM_d", defaultSelectedDate).toString();

       // Toast.makeText(CalenderActivity.this, selectedDateStr+ " selected!", Toast.LENGTH_SHORT).show();
        Toast.makeText(CalenderActivity.this, selectedDateStrForFirebase+ getString(R.string.selected), Toast.LENGTH_SHORT).show();
        checkdate(dayname);
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

            }
            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,int dx, int dy) {
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                selectedDateStrForFirebase = DateFormat.format("yyyy_MM_d", date).toString();

                // SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy");
                //Toast.makeText(CalenderActivity.this, formatter+ " selected!", Toast.LENGTH_SHORT).show();
                Log.i("onDateSelected", selectedDateStr + " - Position = " + position);
                Toast.makeText(CalenderActivity.this, selectedDateStrForFirebase+ getString(R.string.selected), Toast.LENGTH_SHORT).show();

                String dayname = DateFormat.format("EEE", date).toString();
                // month = Integer.parseInt(DateFormat.format("MMM", defaultSelectedDate).toString());
                // day = Integer.parseInt(DateFormat.format("d", defaultSelectedDate).toString());
                String year = DateFormat.format("yyyy", date).toString();
                String month = DateFormat.format("MM", date).toString();
                String day = DateFormat.format("d", date).toString();

                String comefrom="0";
                Intent intent = new Intent(CalenderActivity.this,CalenderActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);

                intent.putExtra("comefrom", comefrom);

                intent.putExtra("DoctorID", DoctorID);
                intent.putExtra("TimeID", TimeID);
                intent.putExtra("StartingTime", StartTime);
                intent.putExtra("EndingTime", Endtime);
                intent.putExtra("DoctorAddress", DoctorAddress);
                intent.putExtra("patientName",patientName);
                intent.putExtra("patientAge",patientAge);

                intent.putExtra("StepTime", String.valueOf(step));


                intent.putExtra("Satchecked", Satchecked);
                intent.putExtra("Sunchecked", Sunchecked);
                intent.putExtra("Monchecked", Monchecked);
                intent.putExtra("Tuschecked", Tuschecked);
                intent.putExtra("Wedchecked", Wedchecked);
                intent.putExtra("Thuchecked", Thuchecked);
                intent.putExtra("Frichecked", Frichecked);


                finish();
                //startActivity(getIntent());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
            }

        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horizontalCalendar.goToday(false);
            }
        });

         //////////////////////*********************************************************
/////////////*******************************///
      /**  databasetimeBooking.child(DoctorID).child(TimeID)
                .child(selectedDateStr).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                positioncolorList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        bookingtimesclass  = doctorSnapshot.getValue(BookingTimesClass.class);
                        positioncolorList.add(0, bookingtimesclass);// i= 0  (index)to start from top
                        //Toast.makeText(getContext(), bookingtimesclass.getCtPeriod(), Toast.LENGTH_LONG).show();

                        //final String DID = bookingtimesclass.getcId();
                        // final boolean checked = bookingtimesclass.getChecked();
                        // Toast.makeText(FavActivity.this, DID, Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });**/
        ////***************************************************/////
        //**********************start time********************************
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
        Date date1 = null;
        try {
            date1 = formatter1.parse(StartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
       // cal2.add(Calendar.MINUTE, 30);
       // SimpleDateFormat printTimeFormat = new SimpleDateFormat("HH:mm");
        //String updatedTime=printTimeFormat.format(cal2.getTime());

        SimpleDateFormat printTimeFormath1 = new SimpleDateFormat("HH");
        String updatedTimeh1=printTimeFormath1.format(cal1.getTime());
        int hourstart= Integer.parseInt(updatedTimeh1);

        SimpleDateFormat printTimeFormatm1 = new SimpleDateFormat("mm");
        String updatedTimem1=printTimeFormatm1.format(cal1.getTime());
        int minstart= Integer.parseInt(updatedTimem1);
/**
        SimpleDateFormat printTimeFormata1 = new SimpleDateFormat("a");
        String updatedTimea1=printTimeFormata1.format(cal1.getTime());
        String aastart= updatedTimea1+"";
        Toast.makeText(CalenderActivity.this, ""+aastart, Toast.LENGTH_LONG).show();**/

        //**********************end time********************************
        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
        Date date2 = null;
        try {
            date2 = formatter2.parse(Endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date2);
      
        SimpleDateFormat printTimeFormath2 = new SimpleDateFormat("HH");
        String updatedTimeh2 = printTimeFormath2.format(cal1.getTime());
        int hourend = Integer.parseInt(updatedTimeh2);

        SimpleDateFormat printTimeFormatm2 = new SimpleDateFormat("mm");
        String updatedTimem2 = printTimeFormatm2.format(cal1.getTime());
        int minend = Integer.parseInt(updatedTimem2);
/**
        SimpleDateFormat printTimeFormata2 = new SimpleDateFormat("a");
        String updatedTimea2=printTimeFormata2.format(cal1.getTime());
        String aastart2= updatedTimea2+"";
        Toast.makeText(CalenderActivity.this, ""+aastart2, Toast.LENGTH_LONG).show();**/

        //**********************loop time********************************
        int noOfSteps = (((hourend * 60 + minend) - (hourstart * 60 + minstart)) / step) + 1;
        //int noOfSteps = 20;

        String updatedTimeprinted = StartTime;
        String updatedTime0 = StartTime;
        mtimes.add(0,new OneWordClass(StartTime));
        for (int i = 1; i < noOfSteps; i++) {

            SimpleDateFormat formatter0 = new SimpleDateFormat("HH:mm");
            Date date0 = null;
            try {
                date0 = formatter0.parse(updatedTime0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal0 = Calendar.getInstance();
            cal0.setTime(date0);
            cal0.add(Calendar.MINUTE, step);
            SimpleDateFormat printTimeFormat0 = new SimpleDateFormat("HH:mm");
            updatedTime0 = printTimeFormat0.format(cal0.getTime());
            //update time with step
            mtimes.add(i,new OneWordClass(updatedTime0));

            // if (i != mInsuranceItems.size() - 1) {item = item + ", ";}
            updatedTimeprinted = updatedTimeprinted + " ," + updatedTime0 ;

        }
       // timef.setText(updatedTimeprinted);
        OneWordAdapter itemsAdapter = null;
if (patientName == null){
     itemsAdapter =new OneWordAdapter(this, mtimes/**,R.color.colorCardDefault**/,DoctorID,TimeID,DoctorAddress,selectedDateStrForFirebase,dayAvaliable,StartTime,Endtime);
}else {
     itemsAdapter =new OneWordAdapter(this, mtimes/**,R.color.colorCardDefault**/,DoctorID,TimeID,DoctorAddress,selectedDateStrForFirebase,dayAvaliable,patientName,patientAge, StartTime, Endtime);
}


         listview=findViewById(R.id.timelist);
        listview.setAdapter(itemsAdapter);


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void checkdate(String dayname) {


        String a,b,c,d,e,f,g;
        if( Satchecked){ a="Sat";}else{a="no";}
        if( Sunchecked){ b="Sun";}else{b="no";}
        if( Monchecked){ c="Mon";}else{c="no";}
        if( Tuschecked){ d="Tue";}else{d="no";}
        if(Wedchecked){ e="Wed";}else{e="no";}
        if(Thuchecked){ f="Thu";}else{f="no";}
        if(Frichecked){ g="Fri";}else{g="no";}
        if(dayname.equalsIgnoreCase(a)||dayname.equalsIgnoreCase(b)||dayname.equalsIgnoreCase(c)||dayname.equalsIgnoreCase(d)
                ||dayname.equalsIgnoreCase(e)||dayname.equalsIgnoreCase(f)||dayname.equalsIgnoreCase(g) ){
            // makepatientbooking(timeID, datedmy, position);
            Toast.makeText(CalenderActivity.this, R.string.doctor_is_on, Toast.LENGTH_LONG).show();
            dayAvaliable=true;
        }else{
            Toast.makeText(CalenderActivity.this, R.string.doctor_is_off, Toast.LENGTH_LONG).show();
            dayAvaliable=false;

        }


    }
}


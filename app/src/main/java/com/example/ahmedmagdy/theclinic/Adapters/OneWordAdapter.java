package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.Notifications.Client;
import com.example.ahmedmagdy.theclinic.Notifications.Data;
import com.example.ahmedmagdy.theclinic.Notifications.MyResponse;
import com.example.ahmedmagdy.theclinic.Notifications.Sender;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.CalenderActivity;
import com.example.ahmedmagdy.theclinic.activities.DoctorProfileActivity;
import com.example.ahmedmagdy.theclinic.activities.FavActivity;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.OneWordClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AHMED MAGDY on 10/23/2018.
 */

public class OneWordAdapter extends ArrayAdapter<OneWordClass> {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUserReg;
    private DatabaseReference databasetimeBooking;
    DatabaseReference bookforuser;
    String   picuri,mDate;
    String userid;
    private FirebaseUser fuser;
    boolean notify = false;
    APIService apiService;


    private String DoctorID;
    private String TimeID;
    private String DoctorAddress;
    private String datedmy;
    private Boolean dayAvaliable;
    // private int colorResourceID;
    private Activity context;
    List<OneWordClass> timingList;
    //private List<String> positioncolorList;
    //private List<BookingTimesClass> positioncolorList;

    BookingTimesClass bookingtimesclass;

    public OneWordAdapter(Activity context, List<OneWordClass> timingList/**,int colorResourceID**/, String DoctorID, String TimeID, String DoctorAddress, String datedmy,Boolean dayAvaliable) {
        super((Context) context, R.layout.time_grid_item, timingList);

        this.context = context;
        this.timingList = timingList;
       // this.colorResourceID = colorResourceID;
        this.DoctorID = DoctorID;
        this.TimeID = TimeID;
        this.DoctorAddress = DoctorAddress;
        this.datedmy = datedmy;
        this.dayAvaliable = dayAvaliable;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.time_grid_item, null, true);

        final TextView atime = (TextView) listViewItem.findViewById(R.id.tv);
        final CardView cardview= (CardView) listViewItem.findViewById(R.id.cv);

        mAuth = FirebaseAuth.getInstance();
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");databaseUserReg.keepSynced(true);
        databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes");databasetimeBooking.keepSynced(true);
        userid = mAuth.getCurrentUser().getUid();
        fuser = mAuth.getInstance().getCurrentUser();
         bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser");
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);




        final OneWordClass onewordclass = timingList.get(position);
        atime.setText(onewordclass.getWord());

///****************************past date*************************

        String bookingdate=datedmy+" "+onewordclass.getWord();//"2018_12_27 16:15:51";2019_01_1 12:00

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy_MM_dd HH:mm");
        Date date5 = null;
        try {
            date5 = formatter1.parse(bookingdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date5);


        Calendar cal2 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH:mm");
        String currentdate = sdf.format(cal2.getTime());

        int timecomp = cal2.compareTo(cal1);
        // Toast.makeText(context, timecomp+"you ", Toast.LENGTH_LONG).show();
        if (timecomp > 0) {
            cardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));
            atime.setTextColor(Color.parseColor("#ffffff"));
        } else {
           // cardviewbook.setCardBackgroundColor(Color.parseColor("#fd0101"));
        }

///****************************past date*************************
        if(dayAvaliable) {
        /////////////*******************************///
        databasetimeBooking.child(DoctorID).child(TimeID)
                .child(datedmy).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                         bookingtimesclass  = doctorSnapshot.getValue(BookingTimesClass.class);
                        final String CtPeriod = bookingtimesclass.getCtPeriod();
                       // final String ctbookingdate = bookingtimesclass.getCtbookingdate();
                        final String userIdForAll = bookingtimesclass.getCtid();
                       // positioncolorList.add(0, CtPeriod);// i= 0  (index)to start from top
                      //  if (positioncolorList.get(0).equals(onewordclass.getWord())){

                        if (CtPeriod.equals(onewordclass.getWord())){
                            if (userIdForAll.equals(userid)){
                                cardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                            }else{
                                cardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

                                atime.setTextColor(Color.parseColor("#ffffff"));
                            }

                        }

                        //final String DID = bookingtimesclass.getcId();
                        // final boolean checked = bookingtimesclass.getChecked();
                        // Toast.makeText(FavActivity.this, DID, Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
////////////////////////////////////////////
        }else {
            cardview.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));//"#79d1c0",FFDFDBDB

            atime.setTextColor(Color.parseColor("#ffffff"));}
        ////***************************************************/////

        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    OneWordClass onewordclass =timingList.get(position);
                if(cardview.getCardBackgroundColor().getDefaultColor()==-1){
                    if(dayAvaliable) {
                        cardview.setCardBackgroundColor(Color.parseColor("#1c71b6"));
                        //datedmy=booking day= 15-1-2018
                        makepatientbooking(TimeID, datedmy, position);
                        Toast.makeText(context, "Is booked", Toast.LENGTH_LONG).show();




                        /**  Intent intent = new Intent(context,CalenderActivity.class);
                          context.finish();
                          context.startActivity(intent);**/
                    }else {Toast.makeText(context, "Doctor is off this day", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(cardview.getCardBackgroundColor().getDefaultColor()==Color.parseColor("#FFDFDBDB")){
                  //  if (userIdForAll.equals(userid)){
                        Toast.makeText(context, "Another one select this period", Toast.LENGTH_LONG).show();

                    }else{
                        cardview.setCardBackgroundColor(Color.parseColor("#ffffff"));
                        databasetimeBooking.child(DoctorID).child(TimeID)
                                .child(datedmy)
                                .child(userid).setValue(null);
                       bookforuser.child(userid).child(DoctorID+datedmy).setValue(null);
                        Toast.makeText(context, "Removed", Toast.LENGTH_LONG).show();
                    }


                }
                ////////////////delay for refresh adapter
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        notifyDataSetChanged();
                    }
                }, 300);
            }
        });



        return listViewItem;
    }










    private void makepatientbooking(final String TimeID, final String datedmy, final int position) {
        final OneWordClass onewordclass = timingList.get(position);


        /*************************************/
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String patientName = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                String patientBirthday = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cbirthday").getValue(String.class);

                //final BookingClass currentBooking = bookingList.get(position);
                String patientpic = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("cpatentphoto").getValue(String.class);
                if(patientpic != null){
                    picuri=patientpic;
                }else{picuri="https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/user_logo_m.jpg?alt=media&token=ff53fa61-0252-43a4-8fa3-0eb3a3976ee5";}
                // Toast.makeText(DoctorProfileActivity.this, picuri, Toast.LENGTH_LONG).show();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mDate = sdf.format(calendar.getTime());
////////////////***************user booking table**********************

               // DatabaseReference referencea = bookforuser.push();
               // String randomid = referencea.getKey();

                BookingTimesClass bookingtimes = new BookingTimesClass( DoctorID,  mDate, DoctorAddress,TimeID,datedmy ,onewordclass.getWord());

                bookforuser.child(userid).child(DoctorID+datedmy).setValue(bookingtimes);

                ////to do/////////------------doctor booking table-------------------------------------------------

                DatabaseReference reference1 = databasetimeBooking.push();
                //final DatabaseReference databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes").child(DoctorID).child(timeID).child(datedmy);
                // DatabaseReference reference = databasetimeBooking.push();
                String timesid = reference1.getKey();

                //Log.v("Data"," 2-User id :"+ mUserId);

                // get age from birthday
//                String patientAge = UtilClass.calculateAgeFromDate(patientBirthday);

                BookingTimesClass bookingtimesclass = new BookingTimesClass(userid, patientName, patientBirthday, mDate, DoctorAddress,onewordclass.getWord() , picuri,TimeID,datedmy,position);

                // Database for Account Activity
                databasetimeBooking.child(DoctorID).child(TimeID)
                        .child(datedmy)
                        .child(userid).setValue(bookingtimesclass).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notify = true;
                                if (notify && userid == mAuth.getCurrentUser().getUid()) {
                                    System.out.println("databasetimebooking listner: pName:" +
                                            patientName + " ,, Doctor ID:" + DoctorID +
                                            ",, user id : " + userid);

                                    sendNotifiaction(DoctorID, patientName, "Booking time with you");
                                }
                                notify = false;
                            }
                        }
                );
              //  databasetimeBooking.child(DoctorID).child(timeID).child(datedmy).child(userid).child("checked").setValue(true);
                //////////////////////*******-----------------
         /**       databasetimeBooking.child(DoctorID).child(timeID) .child(datedmy)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                //  dataSnapshot.getChildrenCount();
                                Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
                                arrange =String.valueOf( dataSnapshot.getChildrenCount() );
                                Toast.makeText(DoctorProfileActivity.this ,"your Arrangement is the"+dataSnapshot.getChildrenCount(), Toast.LENGTH_LONG).show();
                                databasetimeBooking.child(DoctorID).child(timeID)
                                        .child(datedmy)
                                        .child(mAuth.getCurrentUser().getUid()).child("ctArrangement").setValue(String.valueOf( dataSnapshot.getChildrenCount() ));
                                //String.valueOf( arrange )
                                DatabaseReference bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser");
                                DatabaseReference referencea = bookforuser.push();
                                String randomid = referencea.getKey();

                                BookingTimesClass bookingtimes = new BookingTimesClass( DoctorID,  mDate, currentBooking.getCbaddress(),currentBooking.getCbtime() , datedmy,arrange);

                                bookforuser.child(userid).child(randomid).setValue(bookingtimes);
////***********for adapt arange in user booking activity///////*******************
                                databasetimeBooking.child(DoctorID).child(timeID)
                                        .child(datedmy)
                                        .child(mAuth.getCurrentUser().getUid()).child("rangementid").setValue(randomid);
                                //String.valueOf( arrange )
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });**/

                /***********************************/

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseUserReg .addValueEventListener(postListener);

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
                    Data data = new Data(fuser.getUid(), R.drawable.ic_stat_name,
                            username + ": " + message, "Booking",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

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
                                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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

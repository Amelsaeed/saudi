package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingClass;

import java.util.List;

/**
 * Created by AHMED MAGDY on 10/23/2018.
 */

public class BookingAdapter extends ArrayAdapter<BookingClass> {


    private Activity context;
    List<BookingClass> bookingList;
    /**public String id;
    public String doctorID;

    public BookingAdapter(DoctorProfileActivity context , List<BookingClass> bookingList, String id, String doctorID) {
        super((Context) context, R.layout.list_layout_booking, bookingList);

        this.context = context;
        this.bookingList = bookingList;
        this.id = id;
        this.doctorID = doctorID;
    }**/

    public BookingAdapter( Activity context, List<BookingClass> bookingList) {
        super((Context) context, R.layout.list_layout_booking, bookingList);

        this.context = context;
        this.bookingList = bookingList;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.list_layout_booking, null, true);

        final TextView abookingtimestart = (TextView) listViewItem.findViewById(R.id.time_book_start);
        final TextView abookingtimeend = (TextView) listViewItem.findViewById(R.id.time_book_end);

        final TextView abookingaddress = (TextView) listViewItem.findViewById(R.id.Adress_book);


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

        BookingClass bookingclass = bookingList.get(position);

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


        return listViewItem;
    }
/**
    private void getbookdata() {
        final DatabaseReference databaseBooking = FirebaseDatabase.getInstance().getReference("bookingdb").child(DoctorID);

        ////import data of country and tope
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    type = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
                    country = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ccountry").getValue(String.class);
                    maketable();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            databaseReg .addValueEventListener(postListener);
    }**/

}

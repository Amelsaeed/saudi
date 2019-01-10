package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.PatientFragment.UserBookingFragment;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatientBookingAdapter extends ArrayAdapter<BookingTimesClass> implements Filterable {
    List<BookingTimesClass> doctorList;
    private Activity context;
    private List<BookingTimesClass> mSearchList;
    private String a1;



    public PatientBookingAdapter(Activity context, List<BookingTimesClass> doctorList) {
        super(context, R.layout.list_layout_patient_book, doctorList);
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.list_layout_patient_book, null, true);

        final TextView adname = (TextView) listViewItem.findViewById(R.id.doctor_name_b);
        final TextView apspecialty = (TextView) listViewItem.findViewById(R.id.doctor_specialty_b);
        final TextView apaddress = (TextView) listViewItem.findViewById(R.id.doctor_city_b);
        final TextView apdate = (TextView) listViewItem.findViewById(R.id.doctor_date_b);
        final TextView apperiod = (TextView) listViewItem.findViewById(R.id.doctor_period_b);
        final TextView apcurrentdate = (TextView) listViewItem.findViewById(R.id.doctor_current_date_b);
        final TextView aparrange = (TextView) listViewItem.findViewById(R.id.arrange);
        final CardView cardviewbook= (CardView) listViewItem.findViewById(R.id.book123);
        final CardView cardviewcancel= (CardView) listViewItem.findViewById(R.id.book_cancel);




        final ImageView apphoto = (ImageView) listViewItem.findViewById(R.id.doctor_photo_b);

        final BookingTimesClass doctorclass = doctorList.get(position);
        //asize = trampList.size();
       // String bookingdate=doctorclass.getCtbookingdate()+" "+doctorclass.getCtPeriod();
        if(doctorclass.getCtbookingdate()!= null && doctorclass.getCtArrangement()!=null){
        String bookingdate=doctorclass.getCtbookingdate()+" "+doctorclass.getCtArrangement();//"2018_12_27 16:15:51";2019_01_1 12:00

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
                cardviewbook.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));
                cardviewcancel.setCardBackgroundColor(Color.parseColor("#FFDFDBDB"));
            } else {
                cardviewbook.setCardBackgroundColor(Color.parseColor("#fd0101"));
            }
        }

        adname.setText(doctorclass.getCtname());
        apspecialty.setText(doctorclass.getCtSpc());
        apaddress.setText(doctorclass.getCtAddress());
        apdate.setText(doctorclass.getCtbookingdate());
        apperiod.setText(doctorclass.getCtPeriod());
        apcurrentdate.setText(doctorclass.getCtdate());
        aparrange.setText(doctorclass.getCtArrangement());
        apaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + doctorclass.getCtAddress()));
                context.startActivity(intent);
            }
        });
        cardviewcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardviewcancel.getCardBackgroundColor().getDefaultColor()==Color.parseColor("#FFDFDBDB")){
                    //  if (userIdForAll.equals(userid)){
                    Toast.makeText(context, "This date has passed", Toast.LENGTH_LONG).show();

                }else {
                    final BookingTimesClass doctorclass = doctorList.get(position);
                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    String userid = mAuth.getCurrentUser().getUid();

                    DatabaseReference databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes");
                    DatabaseReference bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser");


                    bookforuser.child(userid).child(doctorclass.getCtid() + doctorclass.getCtbookingdate()).setValue(null);
                    //doctorclass.getCtPeriod()= time date ID
                    databasetimeBooking.child(doctorclass.getCtid()).child(doctorclass.getCtPeriod())
                            .child(doctorclass.getCtbookingdate())
                            .child(userid).setValue(null);
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show();
                }
            }
        });


        // favcheckbox.setChecked(doctorclass.getChecked());//normal code retrive status of checkbox from firebase


        a1 = doctorclass.getCtpicuri();
        if (a1 != null) {

            /** RequestOptions requestOptions = new RequestOptions();
             requestOptions = requestOptions.transforms(new RoundedCorners(16));**/
            //requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

            Glide.with(context)
                    .load(a1)
                    .apply(RequestOptions.circleCropTransform())
                    // .apply(requestOptions)
                    .into(apphoto);
        } else {
            Glide.with(context)
                    .load("https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/user_logo_m.jpg?alt=media&token=ff53fa61-0252-43a4-8fa3-0eb3a3976ee5")
                    .apply(RequestOptions.circleCropTransform())
                    // .apply(requestOptions)
                    .into(apphoto);
        }

        return listViewItem;
    }

    @Override
    public int getCount() {
        return doctorList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults resultsFilter = new FilterResults();
                final List<BookingTimesClass> resultsList = new ArrayList<>();
                if (mSearchList == null)
                    mSearchList = doctorList;
                if (constraint != null) {
                    if (mSearchList != null && mSearchList.size() > 0) {
                        for (final BookingTimesClass tramp : mSearchList) {
                            if (tramp.getCtphone().toLowerCase()
                                    .contains(constraint.toString()) ||
                                    tramp.getCtname().toLowerCase()
                                            .contains(constraint.toString()) ||
                                    tramp.getCtdate().toLowerCase()
                                            .contains(constraint.toString()))
                                resultsList.add(tramp);

                        }
                    }
                    resultsFilter.values = resultsList;
                }
                return resultsFilter;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                doctorList = (ArrayList<BookingTimesClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;

import java.util.ArrayList;
import java.util.List;

public class DoctorDatabaseAdapter extends ArrayAdapter<BookingTimesClass> implements Filterable {
    List<BookingTimesClass> doctorList;
    private Activity context;
    private List<BookingTimesClass> mSearchList;
    private String a1;



    public DoctorDatabaseAdapter(Activity context, List<BookingTimesClass> doctorList) {
        super(context, R.layout.list_layout_patient, doctorList);
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.list_layout_patient, null, true);

        final TextView apname =  listViewItem.findViewById(R.id.p_name);
        final TextView apAge =  listViewItem.findViewById(R.id.p_age);
        final TextView apdate = listViewItem.findViewById(R.id.p_date);



        final ImageView apphoto =  listViewItem.findViewById(R.id.p_photo);

        BookingTimesClass doctorclass = doctorList.get(position);
        //asize = trampList.size();




        apname.setText(doctorclass.getCtname());

        String patAge = doctorclass.getCtage();
        if (patAge != null){
            if (patAge.contains("_")){
                patAge = UtilClass.calculateAgeFromDate(patAge);

            }
            apAge.setText(patAge);
        }

        apdate.setText(doctorclass.getCtdate());
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

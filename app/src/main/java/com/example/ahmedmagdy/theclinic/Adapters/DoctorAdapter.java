package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.DoctorProfileActivity;
import com.example.ahmedmagdy.theclinic.activities.FavActivity;
import com.example.ahmedmagdy.theclinic.activities.MessageActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AHMED MAGDY on 10/21/2018.
 */

public class DoctorAdapter extends ArrayAdapter<DoctorFirebaseClass> implements Filterable {
    List<DoctorFirebaseClass> doctorList;
    private Activity context;
    private List<DoctorFirebaseClass> mSearchList;
    private FirebaseAuth mAuth;

    private String a1;

    public DoctorAdapter(Activity context, List<DoctorFirebaseClass> doctorList) {
        super(context, R.layout.list_layout_doctors, doctorList);
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.list_layout_doctors, null, true);

        final TextView adoctorname = (TextView) listViewItem.findViewById(R.id.doctor_name);
        final TextView adoctorspecialty = (TextView) listViewItem.findViewById(R.id.doctor_specialty);
        final TextView adoctorcity = (TextView) listViewItem.findViewById(R.id.doctor_city);
        final ImageView Book = (ImageView) listViewItem.findViewById(R.id.book123);
        final ImageView ChatRoom = (ImageView) listViewItem.findViewById(R.id.chatroom);


        final CheckBox favcheckbox = (CheckBox) listViewItem.findViewById(R.id.fav_checkbox);
        mAuth = FirebaseAuth.getInstance();


        final ImageView adoctorphoto = (ImageView) listViewItem.findViewById(R.id.doctor_photo);

        DoctorFirebaseClass doctorclass = doctorList.get(position);
        //asize = trampList.size();
        favcheckbox.setChecked(doctorclass.getChecked());
        favcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(context, "Please log in first", Toast.LENGTH_LONG).show();
                    buttonView.setChecked(false);
                } else {
                    DatabaseReference databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
                    DatabaseReference databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits")
                            .child(mAuth.getCurrentUser().getUid());
                    DoctorFirebaseClass doctorclass = doctorList.get(position);
                    if (isChecked) {
                        // update database
                        // databaseDoctor.child(doctorclass.getcId()).child("checked").setValue(isChecked);
                        databaseDoctorFav.child(doctorclass.getcId()).child("cId").setValue(doctorclass.getcId());
                        databaseDoctorFav.child(doctorclass.getcId()).child("checked").setValue(isChecked);
                        // Toast.makeText(context,doctorclass.getcId() , Toast.LENGTH_LONG).show();
                        // databaseDoctorFav.setValue(doctorclass);
                        //databaseDoctorFav.child("checked").setValue(isChecked);

                        Toast.makeText(context, doctorclass.getcName() + " is added to your fav.", Toast.LENGTH_LONG).show();
                        // Intent intent = new Intent(context, FavActivity.class);
                        //context.startActivity(intent);
//return;
                    } else {
                        // databaseDoctor.child(doctorclass.getcId()).child("checked").setValue(isChecked);
                        databaseDoctorFav.child(doctorclass.getcId()).setValue(null);

                        Toast.makeText(context, "Removed", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, FavActivity.class);
                        context.startActivity(intent);
                    }
                }
            }
        });

        Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mAuth.getCurrentUser() == null)) {
                    Toast.makeText(context, "Please log in first", Toast.LENGTH_LONG).show();
                } else {
                    DoctorFirebaseClass doctorclass = doctorList.get(position);
                    Intent uIntent = new Intent(context, DoctorProfileActivity.class);
                    uIntent.putExtra("DoctorID", doctorclass.getcId());

                    uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(uIntent);
                    // context.finish();
                }
            }
        });

        ChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(context, "Please log in first", Toast.LENGTH_LONG).show();
                } else {
                    DoctorFirebaseClass doctorclasss = doctorList.get(position);
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("userid", doctorclasss.getcId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }


            }
        });

        adoctorname.setText(doctorclass.getcName());
        adoctorspecialty.setText(doctorclass.getcSpecialty());
        adoctorcity.setText(doctorclass.getcCity());
        // favcheckbox.setChecked(doctorclass.getChecked());//normal code retrive status of checkbox from firebase


        a1 = doctorclass.getcUri();
        if (a1 != null) {

            /** RequestOptions requestOptions = new RequestOptions();
             requestOptions = requestOptions.transforms(new RoundedCorners(16));**/
            //requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

            Glide.with(context)
                    .load(a1)
                    .apply(RequestOptions.circleCropTransform())
                    // .apply(requestOptions)
                    .into(adoctorphoto);
        } else {
            Glide.with(context)
                    .load("https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=2cbb0305-145f-4568-99a2-b76d8011f287")
                    .apply(RequestOptions.circleCropTransform())
                    // .apply(requestOptions)
                    .into(adoctorphoto);
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
                final List<DoctorFirebaseClass> resultsList = new ArrayList<>();
                if (mSearchList == null)
                    mSearchList = doctorList;
                if (constraint != null) {
                    if (mSearchList != null && mSearchList.size() > 0) {
                        for (final DoctorFirebaseClass tramp : mSearchList) {
                            if (tramp.getcCity().toLowerCase()
                                    .contains(constraint.toString()) ||
                                    tramp.getcName().toLowerCase()
                                            .contains(constraint.toString()) ||
                                    tramp.getcSpecialty().toLowerCase()
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
                doctorList = (ArrayList<DoctorFirebaseClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}

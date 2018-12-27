package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.DoctorHome;
import com.example.ahmedmagdy.theclinic.HospitalHome;
import com.example.ahmedmagdy.theclinic.PatientHome;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.DoctorProfileActivity;
import com.example.ahmedmagdy.theclinic.activities.FavActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.MessageActivity;
import com.example.ahmedmagdy.theclinic.activities.SplashActivity;
import com.example.ahmedmagdy.theclinic.activities.StartCahtRoom;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.MapClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private FirebaseUser fuser;

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
        final TextView adoctorsalary = (TextView) listViewItem.findViewById(R.id.doctor_salary);
        final ImageView Book = (ImageView) listViewItem.findViewById(R.id.book123);
        final ImageView ChatRoom = (ImageView) listViewItem.findViewById(R.id.chatroom);
     //   GridView listview=(GridView)listViewItem.findViewById(R.id.in_list);
        final TableLayout tableLayout = (TableLayout) listViewItem.findViewById(R.id.in_list);
        final TextView TypeList = (TextView) listViewItem.findViewById(R.id.type_list);
        final CheckBox favcheckbox = (CheckBox) listViewItem.findViewById(R.id.fav_checkbox);
        final RelativeLayout relativeLayoutbook = listViewItem.findViewById(R.id.rilative_book);
        final RelativeLayout relativeLayoutfav = listViewItem.findViewById(R.id.rilative_fav);
        mAuth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final ImageView adoctorphoto = (ImageView) listViewItem.findViewById(R.id.doctor_photo);
        final DoctorFirebaseClass doctorclass = doctorList.get(position);
        favcheckbox.setChecked(doctorclass.getChecked());
      /**  final Button singout = (Button) listViewItem.findViewById(R.id.singout);
        singout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                context.startActivity(new Intent(context,LoginActivity.class));
            }
        });**/

    /*    if (fuser != null) {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Doctordb");
            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {
                    String usertype = dataSnapshot1.child(fuser.getUid()).child("cType").getValue(String.class);

                    if (usertype.equals("Hospital")) {
                        relativeLayoutbook.setVisibility(View.GONE);
                        relativeLayoutfav.setVisibility(View.GONE);
                        ChatRoom.setVisibility(View.GONE);
                    } else if (usertype.equals("Doctor")) {
                        relativeLayoutbook.setVisibility(View.GONE);
                        relativeLayoutfav.setVisibility(View.GONE);
                        ChatRoom.setVisibility(View.GONE);
                    }else {
                        relativeLayoutbook.setVisibility(View.VISIBLE);
                        relativeLayoutfav.setVisibility(View.VISIBLE);
                        ChatRoom.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            database.addValueEventListener(postListener1);

        }*/
        favcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAuth.getCurrentUser() == null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    context.finish();
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
                    context.startActivity(new Intent(context, LoginActivity.class));
                    context.finish();
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
                    context.startActivity(new Intent(context, LoginActivity.class));
                    context.finish();
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
        adoctorcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + doctorclass.getcCity()));
                context.startActivity(intent);
            }
        });
        // favcheckbox.setChecked(doctorclass.getChecked());//normal code retrive status of checkbox from firebase

        TypeList.setText(doctorclass.getcType());
        adoctorname.setText(doctorclass.getcName());
        adoctorspecialty.setText(doctorclass.getcSpecialty());
        adoctorcity.setText(doctorclass.getcCity());
        adoctorsalary.setText(doctorclass.getcPrice());
       String InsuranceList=doctorclass.getcInsurance();
       String[] items = InsuranceList.split(",");
  ////////////***********************************************
    /**    for (int i = 0; i < items.length; i++) {
            // Creation row
            final TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // Creation textView
            final TextView text = new TextView(context);
            text.setText(items[i]);
            text.setGravity(Gravity.CENTER);
            text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.addView(text);
          //  tableRow.addView(button);

            tableLayout.addView(tableRow);
        }**/
////////////***********************************************
        /** int totalno= items.length;
         int r= 7;
         int c= totalno % r;**/
        int a = 0;
        for (int i = 0; i < 7; i++) {
            // Creation row
            final TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < 5; j++) {

                final TextView text = new TextView(context);

                if (a < items.length) {
                    text.setText("* "+ items[a]);
                    text.setTextColor(Color.parseColor("#FFFFFF"));
                    text.setTextSize(10);
                } else {
                    text.setText(" ");
                }

                a++;
                //text.setGravity(Gravity.CENTER);
                text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                text.setPadding(10, 0, 10, 1);
                tableRow.addView(text);

            }
            tableLayout.addView(tableRow);

        }
/////////////////************************************
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

package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.BookingListActivity;
import com.example.ahmedmagdy.theclinic.activities.InsuranceListActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.MessageActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
    private DatabaseReference databaseDoctor;

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
        de.hdodenhof.circleimageview.CircleImageView StatusDoctcr = (de.hdodenhof.circleimageview.CircleImageView) listViewItem.findViewById(R.id.statis_doctor);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");

        DoctorFirebaseClass doctorclasss = doctorList.get(position);


        if (doctorclasss.getstatus()) {

            StatusDoctcr.setVisibility(View.VISIBLE);
        } else {
            StatusDoctcr.setVisibility(View.GONE);
        }


        final TextView adoctorname = (TextView) listViewItem.findViewById(R.id.doctor_name);
        final TextView adoctorspecialty = (TextView) listViewItem.findViewById(R.id.doctor_specialty);
        final TextView adoctorcity = (TextView) listViewItem.findViewById(R.id.doctor_city);
        final TextView adoctorsalary = (TextView) listViewItem.findViewById(R.id.doctor_salary);
        final TextView doctorDiscPrice = listViewItem.findViewById(R.id.doctor_disc_salary);
        final ImageView Book = (ImageView) listViewItem.findViewById(R.id.book123);
        final ImageView ChatRoom = (ImageView) listViewItem.findViewById(R.id.chatroom);

        final TextView adoctordegree = (TextView) listViewItem.findViewById(R.id.doctor_degree);
        final TextView adoctorinsurance = (TextView) listViewItem.findViewById(R.id.doctor_Insurance);

        final TextView ahospitalname = (TextView) listViewItem.findViewById(R.id.hospital_name);
        final ImageView ahospitalpic = (ImageView) listViewItem.findViewById(R.id.hospital_pic);
        final RelativeLayout relativeLayout = (RelativeLayout) listViewItem.findViewById(R.id.relativeFav);
        final LinearLayout lineardoctorsalary = listViewItem.findViewById(R.id.linear_doctor_salary);
        final LinearLayout lineardoctordegree = listViewItem.findViewById(R.id.linear_doctor_degree);
        //   GridView listview=(GridView)listViewItem.findViewById(R.id.in_list);
        // final TableLayout tableLayout = (TableLayout) listViewItem.findViewById(R.id.in_list);
        final TextView TypeList = (TextView) listViewItem.findViewById(R.id.type_list);
        final CheckBox favcheckbox = (CheckBox) listViewItem.findViewById(R.id.fav_checkbox);
        final RelativeLayout relativeLayoutbook = listViewItem.findViewById(R.id.rilative_book);
        final RelativeLayout relativeLayoutfav = listViewItem.findViewById(R.id.rilative_fav);
        mAuth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final CircleImageView adoctorphoto = (CircleImageView) listViewItem.findViewById(R.id.doctor_photo);
        final DoctorFirebaseClass doctorclass = doctorList.get(position);
        favcheckbox.setChecked(doctorclass.getChecked());
        ImageView Insuranceall = (ImageView) listViewItem.findViewById(R.id.doctor_Insurance_all);
        ImageView specialtyDetail = (ImageView) listViewItem.findViewById(R.id.doctor_specialty_all);


        /**  final Button singout = (Button) listViewItem.findViewById(R.id.singout);
         singout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
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
                if (fuser == null) {
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

                    }
                }
            }
        });

        Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((fuser == null)) {
                    Toast.makeText(context, "Please log in first", Toast.LENGTH_LONG).show();

                } else {
                    DoctorFirebaseClass doctorclass = doctorList.get(position);
                    Intent uIntent = new Intent(context, BookingListActivity.class);
                    uIntent.putExtra("DoctorID", doctorclass.getcId());
                    uIntent.putExtra("DoctorName", doctorclass.getcName());
                    uIntent.putExtra("BookingType", doctorclass.getCbookingtypestate());
                    uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(uIntent);

                }
            }
        });

        ChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuser == null) {
                    Toast.makeText(context, "Please log in first", Toast.LENGTH_LONG).show();

                } else {
                    DoctorFirebaseClass doctorclasss = doctorList.get(position);
                    if ((doctorclasss.getcChatstart() != null) && (doctorclasss.getcChatend() != null)) {
                        ///////////////current time chat////cal 3**********************
                        Calendar caldef = Calendar.getInstance();
                        SimpleDateFormat formatterdef = new SimpleDateFormat("yyyy_MM_dd");
                        String currentdatedef = formatterdef.format(caldef.getTime());

                        Calendar cal2 = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH:mm");
                        String currentdate = sdf.format(cal2.getTime());

//****************************start time chat////cal 1*************************
                        //  String a="17:00";  String b="18:00";
                        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy_MM_dd HH:mm");

                        Date date5 = null;
                        try {
                            date5 = formatter1.parse(currentdatedef + " " + doctorclasss.getcChatstart());

                            // date5 = formatter1.parse(a);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(date5);
                        ///////////////*ending time chat////cal 3**********************

                        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy_MM_dd HH:mm");
                        Date date6 = null;
                        try {
                            date6 = formatter2.parse(currentdatedef + " " + doctorclasss.getcChatend());
                            //   date6 = formatter2.parse(b);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal3 = Calendar.getInstance();
                        cal3.setTime(date6);


                        int timecomp1 = cal2.compareTo(cal1);
                        int timecomp2 = cal2.compareTo(cal3);

                        if ((timecomp1 >= 0) && (timecomp2 <= 0)) {
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("userid", doctorclasss.getcId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Doctor is available from " + doctorclasss.getcChatstart() + " To " + doctorclasss.getcChatend(), Toast.LENGTH_LONG).show();
                        }

///****************************past date*************************
                    } else {
                        Toast.makeText(context, "Doctor Don't activate chat times yet", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        Insuranceall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoctorFirebaseClass doctorclasss = doctorList.get(position);
                Intent intent = new Intent(context, InsuranceListActivity.class);
                intent.putExtra("InsuranceList", doctorclass.getcInsurance());
                intent.putExtra("DoctorName", doctorclass.getcName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }
        });
        specialtyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoctorFirebaseClass doctorclasss = doctorList.get(position);
                // Toast.makeText(context, doctorclasss.getcAbout(), Toast.LENGTH_LONG).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Specialization Details");
                if (doctorclasss.getcAbout() != null) {
                    alert.setMessage(doctorclasss.getcAbout());
                } else {
                    alert.setMessage("Nothing to show");
                }
// Create TextView
                final TextView input = new TextView(context);
                alert.setView(input);


                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();


            }
        });
  /*      adoctorcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + doctorclass.getcCity()));
                context.startActivity(intent);
            }
        });*/
        // favcheckbox.setChecked(doctorclass.getChecked());//normal code retrive status of checkbox from firebase


        if ((doctorclass.getcType()).equals("Hospital")) {

            relativeLayout.setVisibility(View.GONE);
            Book.setVisibility(View.GONE);
            ChatRoom.setVisibility(View.GONE);
            lineardoctorsalary.setVisibility(View.GONE);
            lineardoctordegree.setVisibility(View.GONE);
        } else {
            Book.setVisibility(View.VISIBLE);
            lineardoctorsalary.setVisibility(View.VISIBLE);
            lineardoctordegree.setVisibility(View.VISIBLE);
        }

        TypeList.setText(doctorclass.getcType());
        adoctorname.setText(doctorclass.getcName());
        adoctorspecialty.setText(doctorclass.getcSpecialty());
        adoctorcity.setText(doctorclass.getcCity());
        if (doctorclass.getcPrice() != null) {
            String docPrice = doctorclass.getcPrice().trim().replace(" SAR", "");
            double price = Double.parseDouble(docPrice);
            DecimalFormat df1 = new DecimalFormat("###.##");
            String fPrice = df1.format(price);

            String mDiscount = doctorclass.getcDiscount().trim().replace(" SAR", "");
            double ds = Double.parseDouble(mDiscount);
            DecimalFormat d = new DecimalFormat("###.##");
            String disc = d.format(ds);
            if (disc.equals("0")) {
                adoctorsalary.setText(fPrice + " SAR");
            } else {
                adoctorsalary.setText(fPrice + " SAR");
                adoctorsalary.setTextColor(Color.RED);
                adoctorsalary.setPaintFlags(adoctorsalary.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                //calculate price after discount
                double discountPrice = price - (price * ds / 100);
                DecimalFormat df2 = new DecimalFormat("###.##");
                String disPrice = df2.format(discountPrice);
                //  String disPrice = String.format(Locale.ENGLISH,"%.2f",discountPrice);
                doctorDiscPrice.setText(disPrice + " SAR");
            }


        } else {
            adoctorsalary.setText("price not detected");
        }

        if (doctorclass.getcDegree() != null) {
            adoctordegree.setText(doctorclass.getcDegree());
        } else {
            adoctordegree.setText("Degree not detected");
        }
        String InsuranceList = doctorclass.getcInsurance();
        final List<String> items = Arrays.asList(InsuranceList.split(","));
        // Toast.makeText(context, doctorclass.getcHospitalID(), Toast.LENGTH_LONG).show();
        if (doctorclass.getcHospitalID() != null) {
            if (!doctorclass.getcHospitalID().equalsIgnoreCase("non")) {
                DatabaseReference databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
                final ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {

                        String HospitalName = dataSnapshot1.child(doctorclass.getcHospitalID()).child("cName").getValue(String.class);
                        String Hospitalpic = dataSnapshot1.child(doctorclass.getcHospitalID()).child("cUri").getValue(String.class);
                        ahospitalname.setText(HospitalName);
                        //  Toast.makeText(context, HospitalName+ "/"+Hospitalpic, Toast.LENGTH_LONG).show();
                        if (!context.isFinishing()) {
                            Glide.with(context)
                                    .load(Hospitalpic)
                                    .apply(RequestOptions.circleCropTransform())
                                    // .apply(requestOptions)
                                    .into(ahospitalpic);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };
                databaseDoctor.addValueEventListener(postListener1);
                // adoctordegree.setText(doctorclass.getcDegree());
            }//else{adoctordegree.setText("Degree not detected");}
        }
        //  final ArrayList<String> items = (ArrayList<String>)Arrays.asList(InsuranceList.split(","));
        //final String[] items = InsuranceList.split(",");
        ////////////***********************************************
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");

            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {
                    String userInsurancetype = dataSnapshot1.child(fuser.getUid()).child("cInsurance").getValue(String.class);
                    for (int i = 0; i < items.size(); i++) {
                        //  Toast.makeText(context, userInsurancetype, Toast.LENGTH_LONG).show();

                        if (items.get(i).equalsIgnoreCase(userInsurancetype)) {
                            //  Toast.makeText(context, userInsurancetype+"/"+items.get(i), Toast.LENGTH_LONG).show();

                            adoctorinsurance.setText(items.get(i));
                            return;
                        } else {
                            adoctorinsurance.setText("Insurance");
                            //   Toast.makeText(context, userInsurancetype+"/"+items.get(i), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            databaseChat.addValueEventListener(postListener1);

        } else {
            adoctorinsurance.setText("Not detected");
        }
        /////*//////////////**************************/////
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
        /**   int a = 0;
         for (int i = 0; i < 7; i++) {
         // Creation row
         final TableRow tableRow = new TableRow(context);
         tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

         for (int j = 0; j < 5; j++) {

         final TextView text = new TextView(context);

         if (a < items.size()) {
         text.setText("* "+ items.get(a));
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

         }**/
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
                                            .contains(constraint.toString()) ||
                                    tramp.getcPhone().toLowerCase()
                                            .contains(constraint.toString()) ||
                                    tramp.getcInsurance().toLowerCase().contains(constraint.toString())
                                    )
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

package com.example.ahmedmagdy.theclinic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.activities.BookingListActivity;
import com.example.ahmedmagdy.theclinic.activities.InsuranceListActivity;
import com.example.ahmedmagdy.theclinic.activities.MessageActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdvancedSearchActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private Spinner mSpecialty, mHospital, mDegree, mInsurance, mCity;
    private String mSpecialtyText, mHospitalText, mDegreeText, mInsuranceText, mCityText;

    private RecyclerView mResultList;
    private FirebaseRecyclerAdapter<DoctorFirebaseClass, ResultViewHolder> mRecyclerAdapter;

    private DatabaseReference mDoctorDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        mDoctorDatabase = FirebaseDatabase.getInstance().getReference("Doctordb");

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSpecialty =  findViewById(R.id.spinner_specialty);
        mHospital =  findViewById(R.id.spinner_hospital_clinic);
        mDegree =  findViewById(R.id.spinner_degree);
        mInsurance =  findViewById(R.id.spinner_insurance);
        mCity =  findViewById(R.id.spinner_city);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        // Specialty Spinner
        final ArrayAdapter<CharSequence> specialtyArray = ArrayAdapter.createFromResource(
                this, R.array.spiciality_array, android.R.layout.simple_spinner_item
        );
        specialtyArray.setDropDownViewResource(R.layout.spinner_list_item);
        mSpecialty.setAdapter(specialtyArray);
        mSpecialty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpecialtyText = mSpecialty.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSpecialtyText = specialtyArray.getItem(0).toString();
            }
        });

        // Hospital Spinner
        final ArrayAdapter<CharSequence> hospitalArray = ArrayAdapter.createFromResource(
                this, R.array.HC_array, android.R.layout.simple_spinner_item
        );
        hospitalArray.setDropDownViewResource(R.layout.spinner_list_item);
        mHospital.setAdapter(hospitalArray);
        mHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mHospitalText = mHospital.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mHospitalText = hospitalArray.getItem(0).toString();
            }
        });

        // Degree Spinner
        final ArrayAdapter<CharSequence> degreeArray = ArrayAdapter.createFromResource(
                this, R.array.Degree_array, android.R.layout.simple_spinner_item
        );
        degreeArray.setDropDownViewResource(R.layout.spinner_list_item);
        mDegree.setAdapter(degreeArray);
        mDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDegreeText = mDegree.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDegreeText = degreeArray.getItem(0).toString();
            }
        });

        // Insurance Spinner
        final ArrayAdapter<CharSequence> insuranceArray = ArrayAdapter.createFromResource(
                this, R.array.spiciality_array, android.R.layout.simple_spinner_item
        );
        insuranceArray.setDropDownViewResource(R.layout.spinner_list_item);
        mInsurance.setAdapter(insuranceArray);
        mInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mInsuranceText = mInsurance.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mInsuranceText = insuranceArray.getItem(0).toString();
            }
        });

        // City Spinner
        final ArrayAdapter<CharSequence> cityArray = ArrayAdapter.createFromResource(
                this, R.array.spiciality_array, android.R.layout.simple_spinner_item
        );
        cityArray.setDropDownViewResource(R.layout.spinner_list_item);
        mCity.setAdapter(cityArray);
        mCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCityText = mCity.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mCityText = cityArray.getItem(0).toString();
            }
        });

        // search button
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Doctor Name
                String searchText = mSearchField.getText().toString().trim();

                startSearch(searchText);

            }
        });

    }

    private void startSearch(String searchText) {

        Toast.makeText(AdvancedSearchActivity.this, "Getting Data...", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mDoctorDatabase.orderByChild("cName").startAt(searchText).endAt(searchText + "\uf8ff");

        mRecyclerAdapter = new FirebaseRecyclerAdapter<DoctorFirebaseClass, ResultViewHolder>(

                DoctorFirebaseClass.class,
                R.layout.list_layout_doctors,
                ResultViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(ResultViewHolder viewHolder, DoctorFirebaseClass model, int position) {
                if (model.getcSpecialty().equals(mSpecialtyText) && model.getcHospitalName().equals(mHospitalText)
                        && model.getcDegree().equals(mDegreeText) && model.getcCity().equals(mCityText)
                        && model.getcInsurance().equals(mInsuranceText)
                )
                    viewHolder.setDetails(getApplicationContext(), model);

            }
        };

        mResultList.setAdapter(mRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // View Holder Class
    public class ResultViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ResultViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(final Context ctx, DoctorFirebaseClass doctorData) {

            de.hdodenhof.circleimageview.CircleImageView StatusDoctcr = mView.findViewById(R.id.statis_doctor);

            final DoctorFirebaseClass doctorclass = doctorData;

            if (doctorclass.getstatus()) {
                StatusDoctcr.setVisibility(View.VISIBLE);
            } else {
                StatusDoctcr.setVisibility(View.GONE);
            }

            final TextView adoctorname = (TextView) mView.findViewById(R.id.doctor_name);
            final TextView adoctorspecialty = (TextView) mView.findViewById(R.id.doctor_specialty);
            final TextView adoctorcity = (TextView) mView.findViewById(R.id.doctor_city);
            final TextView adoctorsalary = (TextView) mView.findViewById(R.id.doctor_salary);
            final TextView doctorDiscPrice = mView.findViewById(R.id.doctor_disc_salary);
            final RelativeLayout Book = (RelativeLayout) mView.findViewById(R.id.book123);
            final RelativeLayout ChatRoom = (RelativeLayout) mView.findViewById(R.id.chatroom);
            final TextView adoctordegree = (TextView) mView.findViewById(R.id.doctor_degree);
            final TextView adoctorinsurance = (TextView) mView.findViewById(R.id.doctor_Insurance);
            final TextView ahospitalname = (TextView) mView.findViewById(R.id.hospital_name);
            final ImageView ahospitalpic = (ImageView) mView.findViewById(R.id.hospital_pic);
            final RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.relativeFav);
            final RelativeLayout lineardoctorsalary = mView.findViewById(R.id.linear_doctor_salary);
            final LinearLayout lineardoctordegree = mView.findViewById(R.id.linear_doctor_degree);
            final TextView TypeList = (TextView) mView.findViewById(R.id.type_list);
            final CheckBox favcheckbox = (CheckBox) mView.findViewById(R.id.fav_checkbox);
            final RelativeLayout relativeLayoutbook = mView.findViewById(R.id.rilative_book);
            final RelativeLayout relativeLayoutfav = mView.findViewById(R.id.rilative_fav);
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
            final CircleImageView adoctorphoto = (CircleImageView) mView.findViewById(R.id.doctor_photo);
            favcheckbox.setChecked(doctorclass.getChecked());
            final ImageView Insuranceall = (ImageView) mView.findViewById(R.id.doctor_Insurance_all);
            ImageView specialtyDetail = (ImageView) mView.findViewById(R.id.doctor_specialty_all);

            adoctorphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Display display = getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    int height = display.getHeight();
                    loadPhoto(ctx, adoctorphoto, width, height);
                }
            });
            ahospitalpic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Display display = getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    int height = display.getHeight();
                    loadPhoto(ctx, ahospitalpic, width, height);
                }
            });

            favcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (fuser == null) {
                        Toast.makeText(ctx, R.string.you_should_log_in_firstly, Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);

                    } else {
                        DatabaseReference databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits")
                                .child(mAuth.getCurrentUser().getUid());

                        if (isChecked) {
                            // update database
                            databaseDoctorFav.child(doctorclass.getcId()).child("cId").setValue(doctorclass.getcId());
                            databaseDoctorFav.child(doctorclass.getcId()).child("checked").setValue(true);
                            Toast.makeText(ctx, doctorclass.getcName() + ctx.getString(R.string.is_added_to_your_fav), Toast.LENGTH_LONG).show();
                        } else {
                            databaseDoctorFav.child(doctorclass.getcId()).setValue(null);
                            Toast.makeText(ctx, R.string.removed, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            Book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((fuser == null)) {
                        Toast.makeText(ctx, R.string.you_should_log_in_firstly, Toast.LENGTH_LONG).show();

                    } else {
                        Intent uIntent = new Intent(ctx, BookingListActivity.class);
                        uIntent.putExtra("DoctorID", doctorclass.getcId());
                        uIntent.putExtra("BookingType", doctorclass.getCbookingtypestate());
                        uIntent.putExtra("MaxNo", doctorclass.getcMaxno());
                        uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(uIntent);
                    }
                }
            });

            ChatRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fuser == null) {
                        Toast.makeText(ctx, R.string.you_should_log_in_firstly, Toast.LENGTH_LONG).show();

                    } else {
                        if ((doctorclass.getcChatstart() != null) && (doctorclass.getcChatend() != null)) {
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
                                date5 = formatter1.parse(currentdatedef + " " + doctorclass.getcChatstart());

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
                                date6 = formatter2.parse(currentdatedef + " " + doctorclass.getcChatend());
                                //   date6 = formatter2.parse(b);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal3 = Calendar.getInstance();
                            cal3.setTime(date6);
                            int timecomp1 = cal2.compareTo(cal1);
                            int timecomp2 = cal2.compareTo(cal3);

                            if ((timecomp1 >= 0) && (timecomp2 <= 0)) {
                                Intent intent = new Intent(ctx, MessageActivity.class);
                                intent.putExtra("userid", doctorclass.getcId());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(intent);
                            } else {
                                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ctx);
                                alertDialogBuilder.setTitle(R.string.attention);
                                alertDialogBuilder
                                        .setIcon(R.drawable.ic_attention)
                                        .setMessage(ctx.getString(R.string.doctor_is_available_from) + doctorclass.getcChatstart() + ctx.getString(R.string.to) + doctorclass.getcChatend())
                                        .setCancelable(true);

                                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } else {
                            Toast.makeText(ctx, R.string.doctor_donot_activate_chat_times_yet, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            Insuranceall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, InsuranceListActivity.class);
                    intent.putExtra("InsuranceList", doctorclass.getcInsurance());
                    intent.putExtra("DoctorName", doctorclass.getcName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            });
            specialtyDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, doctorclasss.getcAbout(), Toast.LENGTH_LONG).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                    alert.setTitle(R.string.specialization_details);
                    if (doctorclass.getcAbout() != null) {
                        alert.setMessage(doctorclass.getcAbout());
                    } else {
                        alert.setMessage(R.string.nothing_to_show);
                    }
                    // Create TextView
                    final TextView input = new TextView(ctx);
                    alert.setView(input);

                    alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });
                    alert.show();

                }
            });

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
                String docPrice = doctorclass.getcPrice().trim().replace("SAR", "");
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
                adoctorsalary.setText(R.string.price_not_detected);
            }

            if (doctorclass.getcDegree() != null) {
                adoctordegree.setText(doctorclass.getcDegree());
            } else {
                adoctordegree.setText(R.string.degree_not_detected);
            }
            String InsuranceList = doctorclass.getcInsurance();
            final List<String> items = Arrays.asList(InsuranceList.split(","));
            // Toast.makeText(context, doctorclass.getcHospitalID(), Toast.LENGTH_LONG).show();
            if (doctorclass.getcHospitalID() != null) {
                if (!doctorclass.getcHospitalID().equalsIgnoreCase("non")) {
                    final ValueEventListener postListener1 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {

                            String HospitalName = dataSnapshot1.child(doctorclass.getcHospitalID()).child("cName").getValue(String.class);
                            String Hospitalpic = dataSnapshot1.child(doctorclass.getcHospitalID()).child("cUri").getValue(String.class);
                            ahospitalname.setText(HospitalName);
                            //  Toast.makeText(context, HospitalName+ "/"+Hospitalpic, Toast.LENGTH_LONG).show();
                            if (!isFinishing()) {
                                Glide.with(ctx)
                                        .load(Hospitalpic)
                                        .into(ahospitalpic);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                        }
                    };
                    mDoctorDatabase.addValueEventListener(postListener1);
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
                            if (items.get(i).equalsIgnoreCase(userInsurancetype)) {
                                adoctorinsurance.setText(items.get(i));
                            } else {
                                adoctorinsurance.setText(R.string.insurance);
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
                adoctorinsurance.setText(R.string.not_detected);
            }
            // set doctor profile image
            if (doctorclass.getcUri() != null) {
                Glide.with(ctx)
                        .load(doctorclass.getcUri())
                        .apply(RequestOptions.circleCropTransform())
                        .into(adoctorphoto);
            } else {
                Glide.with(ctx)
                        .load("https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=2cbb0305-145f-4568-99a2-b76d8011f287")
                        .apply(RequestOptions.circleCropTransform())
                        .into(adoctorphoto);
            }

        }

        /////////// show photo profile ////////////////////////
        private void loadPhoto(Context ctx, ImageView imageView, int width, int height) {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.custom_fullimage_dialoge,
                    (ViewGroup) mView.findViewById(R.id.layout_root));
            de.hdodenhof.circleimageview.CircleImageView image = mView.findViewById(R.id.fullimage);
            image.setImageDrawable(imageView.getDrawable());
            image.getLayoutParams().height = height;
            image.getLayoutParams().width = width;
            image.requestLayout();
            dialog.setContentView(layout);
            dialog.show();
        }
    }
}

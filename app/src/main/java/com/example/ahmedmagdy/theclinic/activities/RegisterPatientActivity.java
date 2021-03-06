package com.example.ahmedmagdy.theclinic.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.util.Calendar;
import java.util.Locale;

public class RegisterPatientActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback{
    private ImageView callogo;

    private TextView singIn, signUp;
    private EditText editTextEmail, editTextPassword, editTextCPassword,editTextAddress,editTextName, editTextcal,specialtyEditText,editTextPhone;
    private ProgressBar progressBar;
    private Spinner spinnercity, spinnerinsurance,spinnerspecialty;
    DatabaseReference databaseUserReg;
    DatabaseReference databaseDoctor;
    DatabaseReference databaseChat;
    //DatabaseReference databaseDoctorReg;
    FirebaseAuth mAuth;

    final int theRequestCodeForLocation = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Boolean isPermissionGranted;
    String caltext;
    Calendar mCurrentDate;
    int year,month,day;
    String mDate;
    FirebaseUser fuser;
    String mtype;

//Bitmap mgeneratedateicon;
//ImageGenerator imageGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (fuser != null){
            loadLocale();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);


        mAuth = FirebaseAuth.getInstance();
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        //databaseDoctorReg = FirebaseDatabase.getInstance().getReference("doctor_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
//        updateToken(FirebaseInstanceId.getInstance().getToken());
        spinnercity = findViewById(R.id.spinner_country);
        //spinnerType = findViewById(R.id.spinner_type);
        spinnerinsurance= findViewById(R.id.spinner_insurance);
        progressBar = findViewById(R.id.progressbar);


        editTextPhone = findViewById(R.id.edit_phone);
        callogo = findViewById(R.id.calender_logo);

        editTextEmail = findViewById(R.id.edit_email);
        editTextPassword = findViewById(R.id.edit_password);
        editTextCPassword = findViewById(R.id.edit_c_password);
        editTextAddress = findViewById(R.id.edit_city);
        editTextName = findViewById(R.id.edit_name);
        signUp = findViewById(R.id.getstarted);
        singIn = (TextView) findViewById(R.id.login);

        editTextName= findViewById(R.id.edit_name);
        editTextcal= findViewById(R.id.calender);
        specialtyEditText = (EditText) findViewById(R.id.specialty_reg);
        final LinearLayout linearCalender=(LinearLayout)this.findViewById(R.id.linear_calender);
        final LinearLayout linearSpecialty=(LinearLayout)this.findViewById(R.id.linear_specialty);
        final LinearLayout linearAddress=(LinearLayout)this.findViewById(R.id.linaddress);
        ///**************user type*******************//
        Intent intent = getIntent();
        mtype = intent.getStringExtra("selector");
        /**if (mtype.equalsIgnoreCase("User")) {
            // specialtyEditText.setVisibility(View.GONE);
            linearSpecialty.setVisibility(LinearLayout.GONE);
            linearCalender.setVisibility(LinearLayout.VISIBLE);
            linearAddress.setVisibility(LinearLayout.GONE);
            spinnerspecialty.setVisibility(LinearLayout.GONE);
            spinnerspecialty.setVisibility(LinearLayout.GONE);





        } else if (mtype.equalsIgnoreCase("Doctor")) {
            // editTextcal.setVisibility(View.GONE);
            linearCalender.setVisibility(LinearLayout.GONE);
            linearSpecialty.setVisibility(LinearLayout.VISIBLE);
            linearAddress.setVisibility(LinearLayout.VISIBLE);




        } else if(mtype .equalsIgnoreCase ("Hospital") ){
            // editTextcal.setVisibility(View.GONE);
            linearCalender.setVisibility(LinearLayout.GONE);
            linearSpecialty.setVisibility(LinearLayout.VISIBLE);
            linearAddress.setVisibility(LinearLayout.VISIBLE);




        }else {}**/

        // Toast.makeText(DoctorProfileActivity.this, DoctorID, Toast.LENGTH_LONG).show();

        //if(!type.equals("User")){paddbook.setVisibility(View.GONE);}

        /////////////////********************************//

        ///////////////Calender//////////////////


        // Create an object of ImageGenerator class in your activity
// and pass the context as the parameter
        ImageGenerator mImageGenerator = new ImageGenerator(this);

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

        callogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentDate = Calendar.getInstance();
                year=mCurrentDate.get(Calendar.YEAR);
                month=mCurrentDate.get(Calendar.MONTH);
                day=mCurrentDate.get(Calendar.DAY_OF_MONTH);
                //final String abc= getAge(year, month, day);

                DatePickerDialog mPickerDialog =  new DatePickerDialog(RegisterPatientActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        caltext=Year+"_"+ (Month+1)+"_"+Day;
                        editTextcal.setText(caltext);
                        //  Toast.makeText(RegisterPatientActivity.this,"Your age= "+abc, Toast.LENGTH_LONG).show();

                        mCurrentDate.set(Year, (Month+1),Day);
                        //   mImageGenerator.generateDateImage(mCurrentDate, R.drawable.empty_calendar);
                    }
                }, year, month, day);
                mPickerDialog.show();
            }
        });///////////////////*Calender////////////////////---------------------



        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RegisterPatientActivity.this, LoginActivity.class);
                finish();
                startActivity(it);

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

        // spinner for cities
        ArrayAdapter<CharSequence> adapterc = ArrayAdapter.createFromResource(
                RegisterPatientActivity.this, R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterc.setDropDownViewResource(R.layout.spinner_list_item);
        spinnercity.setAdapter(adapterc);

        spinnercity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
               // String mcity = spinnercity.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // spinner for insurance
        ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(
                RegisterPatientActivity.this, R.array.insurance_array, android.R.layout.simple_spinner_item);
        adapters.setDropDownViewResource(R.layout.spinner_list_item);
        spinnerinsurance.setAdapter(adapters);

        spinnerinsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
                //String minsurance = spinnerinsurance.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    private void registerUser() {
        final String mEmail = editTextEmail.getText().toString().trim();
        String mPassword = editTextPassword.getText().toString().trim();
        String mCPassword = editTextCPassword.getText().toString().trim();
        final String mName = editTextName.getText().toString().trim();
        final String mCity = spinnercity.getSelectedItem().toString().trim();
        final String mInsurance = spinnerinsurance.getSelectedItem().toString().trim();
        final String mBirthDayCalender = editTextcal.getText().toString().trim();
        //final String mSpecialty = spinnerspecialty.getSelectedItem().toString().trim();
      //  Toast.makeText(RegisterPatientActivity.this, mBirthDayCalender, Toast.LENGTH_SHORT).show();


        if (mName.isEmpty()) {
            editTextName.setError(getString(R.string.name_is_required));
            editTextName.requestFocus();
            return;
        }



        if (mEmail.isEmpty()) {
            editTextEmail.setError(getString(R.string.email_is_required));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            editTextEmail.setError(getString(R.string.please_enter_a_valid_email));
            editTextEmail.requestFocus();
            return;
        }

        if (mPassword.length() < 6) {
            editTextPassword.setError(getString(R.string.minimum_length_of_password_should_be_6));
            editTextPassword.requestFocus();
            return;
        }

        if (mPassword.isEmpty()) {
            editTextPassword.setError(getString(R.string.password_is_required));
            editTextPassword.requestFocus();
            return;
        }

        if (mCPassword.isEmpty()) {
            editTextCPassword.setError(getString(R.string.you_should_confirm_your_password));
            editTextCPassword.requestFocus();
            return;
        }

        if (!mCPassword.equals(mPassword)) {
            editTextCPassword.setError(getString(R.string.it_must_be_the_same_as_password));
            editTextCPassword.requestFocus();
            return;
        }

            if (mBirthDayCalender.isEmpty()) {
                editTextcal.setError("Birthdar is required");
                editTextcal.requestFocus();
                return;
            }

      /**  if (mtype.equalsIgnoreCase("Doctor")  ||  mtype.equalsIgnoreCase("Hospital")) {
            if (mCity.isEmpty()) {
                editTextAddress.setError("City is required");
                editTextAddress.requestFocus();
                return;
            }
        }


        if (mtype.equalsIgnoreCase("Doctor")  ||  mtype.equalsIgnoreCase("Hospital")) {
            if (mSpecialty.isEmpty()) {
                specialtyEditText.setError("Specialty is required");
                specialtyEditText.requestFocus();
                return;
            }
        }**/


        progressBar.setVisibility(View.VISIBLE);
        if (UtilClass.isNetworkConnected(RegisterPatientActivity.this)) {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        /* updateToken(FirebaseInstanceId.getInstance().getToken());*/
                        Toast.makeText(RegisterPatientActivity.this, R.string.user_created, Toast.LENGTH_SHORT).show();
                        String Id = mAuth.getCurrentUser().getUid();

                        RegisterClass usersChat = new RegisterClass(Id,mName, mInsurance, mCity, mEmail, mtype,false);
                        databaseChat.child(Id).setValue(usersChat);
                        databaseChat.child(Id).child("cbirthday").setValue(mBirthDayCalender);


                            RegisterClass regdatauser = new RegisterClass(Id,mName, mInsurance, mCity,mBirthDayCalender , mEmail, mtype);
                            databaseUserReg.child(mAuth.getCurrentUser().getUid()).setValue(regdatauser);


                        Intent intend = new Intent(RegisterPatientActivity.this, LoginActivity.class);
                        intend.putExtra("comefrom", "2");
                        intend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intend);

                    } else {
                        //Log.e(TAG, task.getException().getMessage());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterPatientActivity.this, R.string.you_are_already_registered, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterPatientActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }


    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Setting",Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();

    }
    public void  loadLocale(){
        SharedPreferences pref = getSharedPreferences("Setting",Activity.MODE_PRIVATE);
        String language = pref.getString("My_Lang","");
        setLocale(language);
    }
}
package com.example.ahmedmagdy.theclinic.ChatRoomFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
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

public class RegisterFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ImageView callogo;

    private TextView singIn, signUp;
    private EditText editTextEmail, editTextPassword, editTextCPassword,editTextAddress,editTextName, editTextcal,specialtyEditText,editTextPhone;
    private ProgressBar progressBar;
    private Spinner spinnerCountry, spinnerType;
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_register_patient, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        //databaseDoctorReg = FirebaseDatabase.getInstance().getReference("doctor_data");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
//        updateToken(FirebaseInstanceId.getInstance().getToken());


        editTextPhone = getActivity().findViewById(R.id.edit_phone);
        callogo = getActivity().findViewById(R.id.calender_logo);

        editTextEmail = getActivity().findViewById(R.id.edit_email);
        editTextPassword = getActivity().findViewById(R.id.edit_password);
        editTextCPassword = getActivity().findViewById(R.id.edit_c_password);
        editTextAddress = getActivity().findViewById(R.id.edit_city);
        editTextName = getActivity().findViewById(R.id.edit_name);
        signUp = getActivity().findViewById(R.id.getstarted);
        singIn = (TextView) getActivity().findViewById(R.id.login);

        editTextName= getActivity().findViewById(R.id.edit_name);
        editTextcal= getActivity().findViewById(R.id.calender);
        specialtyEditText = (EditText) getActivity().findViewById(R.id.specialty_reg);
        final LinearLayout linearCalender=(LinearLayout)getActivity().findViewById(R.id.linear_calender);
        final LinearLayout linearSpecialty=(LinearLayout)getActivity().findViewById(R.id.linear_specialty);
        final LinearLayout linearAddress=(LinearLayout)getActivity().findViewById(R.id.linaddress);


        ///////////////Calender//////////////////


        // Create an object of ImageGenerator class in your activity
// and pass the context as the parameter
        ImageGenerator mImageGenerator = new ImageGenerator(getActivity());

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

                DatePickerDialog mPickerDialog =  new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

        spinnerCountry = getActivity().findViewById(R.id.spinner_country);
       // spinnerType = getActivity().findViewById(R.id.spinner_type);

        progressBar = getActivity().findViewById(R.id.progressbar);


        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent it = new Intent(RegisterPatientActivity.this, LoginActivity.class);
                finish();
                startActivity(it);
**/
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

        //--------Gps---------------------
/**
 mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
 requestPermission();

 if(isPermissionGranted){

 getLocation();
 }else{
 requestPermission();
 if(isPermissionGranted){
 //We have it, Get the location.
 getLocation();
 }
 else {
 Toast.makeText(RegisterPatientActivity.this, "Please Give us permission so you can use the app", Toast.LENGTH_SHORT).show();
 }
 }
 **/
        //--------------------------------------
/**
 // spinner for countries
 ArrayAdapter<CharSequence> adapterc = ArrayAdapter.createFromResource(
 RegisterPatientActivity.this, R.array.countries_array, android.R.layout.simple_spinner_item);
 adapterc.setDropDownViewResource(R.layout.spinner_list_item);
 spinnerCountry.setAdapter(adapterc);
 spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
@Override
public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
}
@Override
public void onNothingSelected(AdapterView<?> parent) {
}
});**/

        // spinner for type
        ArrayAdapter<CharSequence> adaptert = ArrayAdapter.createFromResource(
                getActivity(), R.array.type_array, android.R.layout.simple_spinner_item);
        adaptert.setDropDownViewResource(R.layout.spinner_list_item);
        spinnerType.setAdapter(adaptert);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
                String mtype = spinnerType.getSelectedItem().toString().trim();
                if (mtype.equalsIgnoreCase("User")) {
                    // specialtyEditText.setVisibility(View.GONE);
                    linearSpecialty.setVisibility(LinearLayout.GONE);
                    linearCalender.setVisibility(LinearLayout.VISIBLE);
                    linearAddress.setVisibility(LinearLayout.GONE);




                } else if (mtype.equalsIgnoreCase("Doctor")) {
                    // editTextcal.setVisibility(View.GONE);
                    linearCalender.setVisibility(LinearLayout.GONE);
                    linearSpecialty.setVisibility(LinearLayout.VISIBLE);
                    linearAddress.setVisibility(LinearLayout.VISIBLE);




                } else {// (mtype .equalsIgnoreCase ("Hospital") )
                    // editTextcal.setVisibility(View.GONE);
                    linearCalender.setVisibility(LinearLayout.GONE);
                    linearSpecialty.setVisibility(LinearLayout.VISIBLE);
                    linearAddress.setVisibility(LinearLayout.VISIBLE);




                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                linearSpecialty.setVisibility(LinearLayout.GONE);
                linearCalender.setVisibility(LinearLayout.VISIBLE);
                linearAddress.setVisibility(LinearLayout.GONE);

            }
        });
    }


    private void registerUser() {
        final String mEmail = editTextEmail.getText().toString().trim();
        String mPassword = editTextPassword.getText().toString().trim();
        String mCPassword = editTextCPassword.getText().toString().trim();
        final String mPhone = editTextPhone.getText().toString().trim();
        final String mName = editTextName.getText().toString().trim();
        final String mCity = editTextAddress.getText().toString().trim();
        final String mBirthDayCalender = editTextcal.getText().toString().trim();
        final String mSpecialty = specialtyEditText.getText().toString().trim();
        //final String mCountry = spinnerCountry.getSelectedItem().toString().trim();
        final String mtype = spinnerType.getSelectedItem().toString().trim();
        //final String mBirthDayCalender = editTextcal.getText().toString().trim();caltext
        Toast.makeText(getActivity(), mBirthDayCalender, Toast.LENGTH_SHORT).show();


        if (mName.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (mtype.equalsIgnoreCase("Doctor")  ||  mtype.equalsIgnoreCase("Hospital")) {
            if (mPhone.isEmpty()) {
                editTextPhone.setError("Phone is required");
                editTextPhone.requestFocus();
                return;
            }
        }


        if (mEmail.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (mPassword.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        if (mPassword.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (mCPassword.isEmpty()) {
            editTextCPassword.setError("you should confirm your password");
            editTextCPassword.requestFocus();
            return;
        }

        if (!mCPassword.equals(mPassword)) {
            editTextCPassword.setError("it must be the same as password");
            editTextCPassword.requestFocus();
            return;
        }
        if (mtype.equalsIgnoreCase("Doctor")  ||  mtype.equalsIgnoreCase("Hospital")) {
            if (mCity.isEmpty()) {
                editTextAddress.setError("City is required");
                editTextAddress.requestFocus();
                return;
            }
        }
        if (mtype.equalsIgnoreCase("User")) {
            if (mBirthDayCalender.isEmpty()) {
                editTextcal.setError("Birthdar is required");
                editTextcal.requestFocus();
                return;
            }
        }

        if (mtype.equalsIgnoreCase("Doctor")  ||  mtype.equalsIgnoreCase("Hospital")) {
            if (mSpecialty.isEmpty()) {
                specialtyEditText.setError("Specialty is required");
                specialtyEditText.requestFocus();
                return;
            }
        }


        progressBar.setVisibility(View.VISIBLE);
        if (isNetworkConnected()) {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        /* updateToken(FirebaseInstanceId.getInstance().getToken());*/
                        Toast.makeText(getActivity(), "USER CREATED", Toast.LENGTH_SHORT).show();
                        String Id = mAuth.getCurrentUser().getUid();

                        RegisterClass usersChat = new RegisterClass(Id,mName, mPhone, mCity, mEmail, mtype);
                        databaseChat.child(Id).setValue(usersChat);

                        if (mtype.equalsIgnoreCase("User")) {

                            RegisterClass regdatauser = new RegisterClass(Id,mName, mPhone, mCity,mBirthDayCalender , mEmail, mtype);
                            databaseUserReg.child(mAuth.getCurrentUser().getUid()).setValue(regdatauser);

                        }else {
                            // DatabaseReference reference = databaseDoctor.push();
                            // String id = reference.getKey();

                            DoctorFirebaseClass doctorfirebaseclass = new DoctorFirebaseClass(Id,mName, mPhone, mCity, mSpecialty, mEmail, mtype);
                            databaseDoctor.child(Id).setValue(doctorfirebaseclass);
                            // databaseDoctorReg.child(mAuth.getCurrentUser().getUid()).setValue(regdatadoctor);
                        }
                        /**
                         Intent intend = new Intent(getActivity(), SplashActivity.class);
                         intend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         finish();
                         startActivity(intend);
                         **/
                    } else {
                        //Log.e(TAG, task.getException().getMessage());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getActivity(), "you are already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "REGISTER ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "please check the network connection", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    //  check if network is connected
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        } else {
            return false;
        }
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

}
package com.example.ahmedmagdy.theclinic.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class RegisterDoctorActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback{
    private ImageView callogo;

    private TextView singIn, signUp,word,textInsurance,wordid;
    private EditText editTextEmail, editTextPassword, editTextCPassword,editTextName,editTextPhone;
    private ImageView profilePoto,IDphoto,workPermitphoto;
    private ProgressBar progressBar;
    private Spinner spinnercity, spinnergander,spinnerspecialty;
    DatabaseReference databaseUserReg;
    private DatabaseReference databaseDoctor;
    DatabaseReference databaseHospital;
    DatabaseReference databaseChat;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;
    private Uri imagePath;
    //  int GALLERY_REQUEST_CODE;
    //  int CAMERA_REQUEST_CODE ;

    byte[] byteImageDataPP;byte[] byteImageDataID;byte[] byteImageDataWP;
    String mdoctorPhotoUrl = "";
    String mdoctorIDUrl = "";
    String mdoctorWPUrl = "";

    FirebaseUser fuser;
    String mtype,HospitalID,HospitalName,ComeFrom,HospitalPassword,HospitalEmail;

    String getmInsuranceItems="";
    String[] listItems;
    Boolean[] checkedItems;
    ArrayList<Integer> mInsuranceItems = new ArrayList<>();
//Bitmap mgeneratedateicon;
//ImageGenerator imageGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor);

        mAuth = FirebaseAuth.getInstance();
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseHospital = FirebaseDatabase.getInstance().getReference("Hospitaldb");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
//        updateToken(FirebaseInstanceId.getInstance().getToken());
        spinnercity = findViewById(R.id.spinner_country);
        spinnergander = findViewById(R.id.spinner_gander);
        spinnerspecialty= findViewById(R.id.spinner_specialty);
        progressBar = findViewById(R.id.progressbar);





        editTextPhone = findViewById(R.id.edit_phone);
        callogo = findViewById(R.id.calender_logo);

        editTextEmail = findViewById(R.id.edit_email);
        editTextPassword = findViewById(R.id.edit_password);
        editTextCPassword = findViewById(R.id.edit_c_password);
        editTextName = findViewById(R.id.edit_name);
        signUp = findViewById(R.id.getstarted);
        singIn = (TextView) findViewById(R.id.login);
        word= (TextView) findViewById(R.id.word);
        wordid= (TextView) findViewById(R.id.wordid);
        editTextName= findViewById(R.id.edit_name);
        profilePoto= findViewById(R.id.logo);
        IDphoto= findViewById(R.id.id_photo);
        workPermitphoto= findViewById(R.id.work_permit_photo);



        ///**************user type*******************//
        Intent intent = getIntent();
        ComeFrom=intent.getStringExtra("ComeFrom");//, "LogIn");
        if(ComeFrom.equals("LogIn")) {
            mtype = intent.getStringExtra("selector");
        }else{
            mtype = "Doctor";
            HospitalName = intent.getStringExtra("HName");
            HospitalID = intent.getStringExtra("HospitalID");
            HospitalPassword = intent.getStringExtra("HospitalPassword");
            HospitalEmail = intent.getStringExtra("HospitalEmail");


        }

        profilePoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image
                final int GALLERY_REQUEST_CODE = 11;
                final int CAMERA_REQUEST_CODE = 21;


                displayImportImageDialog(CAMERA_REQUEST_CODE, GALLERY_REQUEST_CODE);
            }
        });
        IDphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image
                final int GALLERY_REQUEST_CODE = 12;
                final int CAMERA_REQUEST_CODE = 22;


                displayImportImageDialog(CAMERA_REQUEST_CODE, GALLERY_REQUEST_CODE);
            }
        });
        workPermitphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image
                final int GALLERY_REQUEST_CODE = 13;
                final int CAMERA_REQUEST_CODE = 23;
                displayImportImageDialog(CAMERA_REQUEST_CODE, GALLERY_REQUEST_CODE);
            }
        });
        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RegisterDoctorActivity.this, LoginActivity.class);
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
        final LinearLayout linearInsurance=(LinearLayout)this.findViewById(R.id.spinner_insurance);
        textInsurance= findViewById(R.id.text_insurance);
        listItems= getResources().getStringArray(R.array.insurance_array);
        checkedItems = new Boolean[listItems.length];

        linearInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterDoctorActivity.this);
                mBuilder.setTitle("SELECT Insurance");


                String insur = textInsurance.getText().toString();
                String[] insurances = insur.split(",");
                final ArrayList<Boolean> checkedList = new ArrayList<>();
                final boolean[] checkedItems = new boolean[listItems.length];
                boolean checked;
                for (int x = 0; x < listItems.length; x++) {
                    checked = false;
                    for (int y = 0; y < insurances.length; y++) {
                        if (listItems[x].equals(insurances[y])) {
                            checked = true;
                        }
                    }

                    checkedItems[x] = checked;
                }


                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                      getmInsuranceItems = "";

                        if(isChecked){
                            checkedItems[position] = true;
                        }else{
                            checkedItems[position] = false;
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                       getmInsuranceItems = "";
                        for (int x = 0; x < listItems.length; x++) {
                            if (checkedItems[x]) {
                                if (getmInsuranceItems.equals("")) {
                                    getmInsuranceItems = listItems[x];
                                } else {
                                    getmInsuranceItems = getmInsuranceItems + "," + listItems[x];
                                }

                            }
                        }

                            textInsurance.setText(getmInsuranceItems);

                    }
                });


                mBuilder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
/**
 mBuilder.setNeutralButton("clear_all", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialogInterface, int which) {
for (int i = 0; i < checkedItems.length; i++) {
checkedItems[i] = false;
mInsuranceItems.clear();
textInsurance.setText("");
}
}
});

 **/
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        // spinner for gander
        ArrayAdapter<CharSequence> adapterg = ArrayAdapter.createFromResource(
                RegisterDoctorActivity.this, R.array.gander_array, android.R.layout.simple_spinner_item);
        adapterg.setDropDownViewResource(R.layout.spinner_list_item);
        spinnergander.setAdapter(adapterg);

        spinnergander.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
                // String mcity = spinnercity.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // spinner for cities
        ArrayAdapter<CharSequence> adapterc = ArrayAdapter.createFromResource(
                RegisterDoctorActivity.this, R.array.countries_array, android.R.layout.simple_spinner_item);
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
        // spinner for spiciality
        ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(
                RegisterDoctorActivity.this, R.array.spiciality_array, android.R.layout.simple_spinner_item);
        adapters.setDropDownViewResource(R.layout.spinner_list_item);
        spinnerspecialty.setAdapter(adapters);

        spinnerspecialty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
               // String mSpecialty = spinnerspecialty.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

/**
// spinner for insurance
        ArrayAdapter<CharSequence> adapteri = ArrayAdapter.createFromResource(
                RegisterDoctorActivity.this, R.array.insurance_array, android.R.layout.simple_spinner_item);
        adapteri.setDropDownViewResource(R.layout.spinner_list_item);
        spinnerinsurance.setAdapter(adapteri);
        spinnerinsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorText));
                //String minsurance = spinnerinsurance.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });**/
    }


    private void registerUser() {
        final String mEmail = editTextEmail.getText().toString().trim();
        String mPassword = editTextPassword.getText().toString().trim();
        String mCPassword = editTextCPassword.getText().toString().trim();
        final String mPhone = editTextPhone.getText().toString().trim();
        final String mName = editTextName.getText().toString().trim();
        final String mgander= spinnergander.getSelectedItem().toString().trim();

        final String mSpecialty = spinnerspecialty.getSelectedItem().toString().trim();
        final String mCity = spinnercity.getSelectedItem().toString().trim();
        final String mInsurance = getmInsuranceItems;
//        final String mInsurance = spinnerinsurance.getSelectedItem().toString().trim();
/**
 if(mdoctorPhotoUrl.equals("")){
 Toast.makeText(RegisterDoctorActivity.this, "Personal photo is required", Toast.LENGTH_LONG).show();

 return;
 }**/

        if (mName.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (mPhone.isEmpty()) {
            editTextPhone.setError("phone NO. is required");
            editTextPhone.requestFocus();
            return;}
        if (mPhone.length() != 10) {
            editTextPhone.setError("Invalid phone NO.");
            editTextPhone.requestFocus();
            return;}

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
       //  =
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
        /**      if (mtype.equalsIgnoreCase("Doctor")  ||  mtype.equalsIgnoreCase("Hospital")) {
         if (mCity.isEmpty()) {
         editTextAddress.setError("City is required");
         editTextAddress.requestFocus();
         return;
         }
         }
         **/

        if (getmInsuranceItems.equals("")) {
            textInsurance.setError("Please insert list of Insurance");
            textInsurance.requestFocus();
            return;
        }


        // if (mdoctorPhotoUrl.equals("")) {
        if (byteImageDataPP == null) {

            Toast.makeText(RegisterDoctorActivity.this, "Personal photo is required", Toast.LENGTH_LONG).show();

            word.setError("Personal photo is required");
            word.requestFocus();
            return;
        }

        if (byteImageDataID == null) {
            // if (mdoctorIDUrl.equals("")) {
            Toast.makeText(RegisterDoctorActivity.this, "ID photo is required", Toast.LENGTH_LONG).show();

            wordid.setError("ID photo is required");
            wordid.requestFocus();
            return;
        }

        if (byteImageDataWP == null) {
            //if (mdoctorWPUrl.equals("")) {
            Toast.makeText(RegisterDoctorActivity.this, "Work permit photo is required", Toast.LENGTH_LONG).show();

            wordid.setError("Work permit photo is required");
            wordid.requestFocus();
            return;
        }
        uploadImagePP(mEmail, mPassword,mName, mInsurance, mCity, mSpecialty,  mPhone,mgander);
    }
    private void makeauth(final String mEmail, String mPassword, final String mName, final String mInsurance, final String mCity, final String mSpecialty, final String mPhone, final String mgander) {
        progressBar.setVisibility(View.VISIBLE);
        if((!mdoctorIDUrl.equals(""))&&(!mdoctorPhotoUrl.equals(""))&&(!mdoctorWPUrl.equals(""))){
        if (UtilClass.isNetworkConnected(RegisterDoctorActivity.this)) {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {


                        /* updateToken(FirebaseInstanceId.getInstance().getToken());*/
                        Toast.makeText(RegisterDoctorActivity.this, "USER CREATED", Toast.LENGTH_SHORT).show();
                        final String Id = mAuth.getCurrentUser().getUid();

                        RegisterClass usersChat = new RegisterClass(Id, mName, mInsurance, mPhone, mCity, mEmail, mtype, mdoctorPhotoUrl);
                        databaseChat.child(Id).setValue(usersChat);
///////////////////////////////////******ComeFrom*************/////////////////////////////////////////////////
                       // Toast.makeText(RegisterDoctorActivity.this, HospitalID, Toast.LENGTH_SHORT).show();
                        if(ComeFrom.equals("LogIn")) {

                            String HospName="non";
                            String HospID="non";
                            DoctorFirebaseClass doctorfirebaseclass = new DoctorFirebaseClass(Id, mName, mInsurance, mCity, mSpecialty, mEmail, mtype, mPhone, mdoctorPhotoUrl, mdoctorIDUrl, mdoctorWPUrl,HospName,HospID,mgander,false);
                            databaseDoctor.child(Id).setValue(doctorfirebaseclass);
                            databaseDoctor.child(Id).child("cDiscount").setValue("0");
                            // databaseDoctorReg.child(mAuth.getCurrentUser().getUid()).setValue(regdatadoctor);

                            Intent intend = new Intent(RegisterDoctorActivity.this, LoginActivity.class);
                            intend.putExtra("comefrom", "2");

                            intend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intend);
                        }else{
                            DoctorFirebaseClass doctorfirebaseclass = new DoctorFirebaseClass(Id, mName, mInsurance, mCity, mSpecialty, mEmail, mtype, mPhone, mdoctorPhotoUrl, mdoctorIDUrl, mdoctorWPUrl,HospitalName,HospitalID,mgander,false);
                            databaseDoctor.child(Id).setValue(doctorfirebaseclass);
                            databaseDoctor.child(Id).child("cDiscount").setValue("0");
                            //mAuth.signOut();


                            mAuth.signInWithEmailAndPassword(HospitalEmail, HospitalPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        DatabaseReference databaseDoctorFav = FirebaseDatabase.getInstance().getReference("Favourits")
                                                .child(HospitalID);

                                            // update database
                                            // databaseDoctor.child(doctorclass.getcId()).child("checked").setValue(isChecked);
                                            databaseDoctorFav.child(Id).child("cId").setValue(Id);
                                            databaseDoctorFav.child(Id).child("checked").setValue(true);

                                        Intent intend = new Intent(RegisterDoctorActivity.this, LoginActivity.class);
                                        intend.putExtra("comefrom", "2");

                                        intend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intend);}
                                }
                            });
                        }



                    } else {
                        //Log.e(TAG, task.getException().getMessage());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterDoctorActivity.this, "you are already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterDoctorActivity.this, "REGISTER ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_connection_msg, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }else{registerUser();}
    }



    private void displayImportImageDialog(final int CAMERA_REQUEST_CODE, final int GALLERY_REQUEST_CODE) {

        final Dialog dialog = new Dialog(RegisterDoctorActivity.this);
        dialog.setContentView(R.layout.import_image_dialog);
        dialog.setTitle("Import image from:");
        dialog.setCanceledOnTouchOutside(false);

        TextView gallery = (TextView) dialog.findViewById(R.id.gallery_tv);
        TextView openCamera = (TextView) dialog.findViewById(R.id.open_camera_tv);
        TextView cancel = (TextView) dialog.findViewById(R.id.dismiss_dialog);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGalleryAction( GALLERY_REQUEST_CODE);
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCameraAction(CAMERA_REQUEST_CODE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }
    private void openCameraAction(int CAMERA_REQUEST_CODE) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

    }

    private void openGalleryAction(int GALLERY_REQUEST_CODE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if ((requestCode == 11)||(requestCode == 12)||(requestCode == 13)) {
                if (data.getData() != null) {
                    imagePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                        Bitmap compressedBitmap = getScaledBitmap(bitmap);
                        if (requestCode == 11){
                        profilePoto.setImageBitmap(compressedBitmap);
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        if (requestCode == 11){
                            byteImageDataPP = baos.toByteArray();
                        } else if(requestCode == 12){ byteImageDataID = baos.toByteArray();
                            IDphoto.setImageResource(R.drawable.ic_ok);
                        }else if(requestCode == 13){ byteImageDataWP = baos.toByteArray();
                            workPermitphoto.setImageResource(R.drawable.ic_ok);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                   // addDoctorTextView.setEnabled(true);
                }

            } else  if ((requestCode == 21)||(requestCode == 22)||(requestCode == 23)) {
                // if (data.getData() != null) {
                // imagePath = data.getData();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                Bitmap compressedBitmap = getScaledBitmap(bitmap);
                if (requestCode == 21){
                profilePoto.setImageBitmap(compressedBitmap);}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                if (requestCode == 21){
                    byteImageDataPP = baos.toByteArray();
                } else if(requestCode == 22){ byteImageDataID = baos.toByteArray();
                    IDphoto.setImageResource(R.drawable.ic_ok);
                }else if(requestCode == 23){ byteImageDataWP = baos.toByteArray();
                    workPermitphoto.setImageResource(R.drawable.ic_ok);
                }

               // addDoctorTextView.setEnabled(true);
               }
        }
    }
    //resize image
    private Bitmap getScaledBitmap(Bitmap bm) {

        int width = 0;

        try {
            width = bm.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bm.getHeight();
        int bounding = dpToPx(250);

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) profilePoto.getLayoutParams();
        params.width = width;
        params.height = height;
       // profilePoto.setLayoutParams(params);

        return scaledBitmap;

    }

    // convert dp to pixel
    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void uploadImagePP(final String mEmail, final String mPassword, final String mName, final String mInsurance, final String mCity, final String mSpecialty, final String mPhone, final String mgander) {

        if (UtilClass.isNetworkConnected(RegisterDoctorActivity.this)) {

            if (byteImageDataPP != null) {

                    progressBar.setVisibility(View.VISIBLE);


                    StorageReference trampsRef = mStorageRef.child("docregister").child(System.currentTimeMillis()+ ".jpg");
//mStorageRef.child("doctorPic").child("pp"+mAuth.getCurrentUser().getUid()+ ".jpg");
                    trampsRef.putBytes(byteImageDataPP)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegisterDoctorActivity.this, "Personal photo is uploaded", Toast.LENGTH_LONG).show();

                                    mdoctorPhotoUrl = taskSnapshot.getDownloadUrl().toString();
                                    uploadImageID(mEmail, mPassword,mName, mInsurance, mCity, mSpecialty,  mPhone,mgander);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(RegisterDoctorActivity.this, "an error occurred while  uploading image", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    // addDoctorTextView.setEnabled(true);

                                }
                            });

            }
        } else {
            Toast.makeText(RegisterDoctorActivity.this, getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImageID(final String mEmail, final String mPassword, final String mName, final String mInsurance, final String mCity, final String mSpecialty, final String mPhone,final String mgander) {

        if (UtilClass.isNetworkConnected(RegisterDoctorActivity.this)) {

            if (byteImageDataID != null) {

                progressBar.setVisibility(View.VISIBLE);


                StorageReference trampsRef = mStorageRef.child("docregister").child(System.currentTimeMillis()+ ".jpg");

                trampsRef.putBytes(byteImageDataID)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterDoctorActivity.this, "ID Photo is uploaded", Toast.LENGTH_LONG).show();

                                mdoctorIDUrl = taskSnapshot.getDownloadUrl().toString();
                                uploadImageWP(mEmail, mPassword,mName, mInsurance, mCity, mSpecialty,  mPhone,mgander);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(RegisterDoctorActivity.this, "an error occurred while  uploading image", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                // addDoctorTextView.setEnabled(true);

                            }
                        });

            }
        } else {
            Toast.makeText(RegisterDoctorActivity.this, "Please check the network connection", Toast.LENGTH_LONG).show();
        }
    }
    private void uploadImageWP(final String mEmail, final String mPassword, final String mName, final String mInsurance, final String mCity, final String mSpecialty, final String mPhone, final String mgander) {

        if (UtilClass.isNetworkConnected(RegisterDoctorActivity.this)) {

            if (byteImageDataWP != null) {

                    progressBar.setVisibility(View.VISIBLE);


                    StorageReference trampsRef = mStorageRef.child("docregister").child(System.currentTimeMillis()+ ".jpg");
// mStorageRef.child("docregister").child(System.currentTimeMillis()+ ".jpg");
                    trampsRef.putBytes(byteImageDataWP)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegisterDoctorActivity.this, "Work permit Photo is uploaded", Toast.LENGTH_LONG).show();

                                    mdoctorWPUrl = taskSnapshot.getDownloadUrl().toString();
                                    makeauth(mEmail, mPassword,mName, mInsurance, mCity, mSpecialty,  mPhone,mgander);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(RegisterDoctorActivity.this, "an error occurred while  uploading image", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    // addDoctorTextView.setEnabled(true);

                                }
                            });

            }
        } else {
            Toast.makeText(RegisterDoctorActivity.this, getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
        }
    }
}
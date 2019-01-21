package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.BookingListActivity;
import com.example.ahmedmagdy.theclinic.activities.InsuranceListActivity;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

//import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;

public class DoctorProfileFragment extends Fragment  {
    ImageView ppicuri, editName, editCity, editPhone, editDegree, editSpeciality, editPrice, insuranceEdit;
    TextView pname, pcity, pspeciality, pdegree, pphone, pprice, ptime, drEmail, insuranceView;
    EditText peditbox;
    CheckBox  bookingtypecheck;
    private ProgressBar progressBarImage;

    private Uri imagePath;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private boolean disChecked = false;
    String  idm ;
    String mTrampPhotoUrl = "";
    String doctorId,drDiscountPrice="0";
    Boolean bookingtype;
    String DoctorName, insuranceItems = "";
    byte[] byteImageData;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor, databaseChat, databaseMap;
    private FirebaseUser fUser;
    private ValueEventListener doctorEventListener;


    String[] listCityItems;
    String[] listSpecialityItems;
    String[] listDegreeItems;
    //gps
    //create user location to save all doctor locations
    private static final String TAG = "DoctorProfileFragment";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = getLayoutInflater().inflate(R.layout.activity_doctor_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        if (fUser != null) {
            doctorId = fUser.getUid();
        }
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseMap = FirebaseDatabase.getInstance().getReference("mapdb");
        DatabaseReference reference = databaseMap.push();
        idm = reference.getKey();

        progressBarImage = rootView.findViewById(R.id.progressbar_image);


        editName = rootView.findViewById(R.id.name_edit);
        editCity = rootView.findViewById(R.id.city_edit);
        editPhone = rootView.findViewById(R.id.phone_edit);
        editDegree = rootView.findViewById(R.id.degree_edit);
        insuranceView = rootView.findViewById(R.id.insur);
        insuranceEdit = rootView.findViewById(R.id.insur_edit);
        editSpeciality = rootView.findViewById(R.id.speciality_edit);
        editPrice = rootView.findViewById(R.id.price_edit);
        drEmail = rootView.findViewById(R.id.doctor_email_tv);
        pname = rootView.findViewById(R.id.pname);

        pcity = rootView.findViewById(R.id.ppcity);
        pspeciality = rootView.findViewById(R.id.pspeciality);
        pdegree = rootView.findViewById(R.id.pdegree);
        pphone = rootView.findViewById(R.id.pphone);
        pprice = rootView.findViewById(R.id.pprice);
        ptime = rootView.findViewById(R.id.ptime);

        peditbox = rootView.findViewById(R.id.peditbox);
        ppicuri = rootView.findViewById(R.id.edit_photo);
        bookingtypecheck= rootView.findViewById(R.id.checkBox1);


        editName.setVisibility(View.VISIBLE);
        editPhone.setVisibility(View.VISIBLE);
        editDegree.setVisibility(View.VISIBLE);
        editSpeciality.setVisibility(View.VISIBLE);
        editCity.setVisibility(View.VISIBLE);
        editPrice.setVisibility(View.VISIBLE);
        insuranceEdit.setVisibility(View.VISIBLE);
        drEmail.setVisibility(View.VISIBLE);


        Button workingHours = rootView.findViewById(R.id.working_hours_btn);

        drEmail.setText(fUser.getEmail());


        getallData();
        bookingtypecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    databaseDoctor.child(doctorId).child("cbookingtypestate").setValue(true);
                } else {
                    databaseDoctor.child(doctorId).child("cbookingtypestate").setValue(false);
                }
            }
        });
        workingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookingListActivity.class);
                intent.putExtra("DoctorID", doctorId);
                intent.putExtra("DoctorName", DoctorName);
                intent.putExtra("BookingType", bookingtype);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });


        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatData = "Name";
                editDialog(whatData);
            }
        });
/**
 editCity.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View view) {
String whatData = "State/ City/ Region";
editDialog(whatData);
}
});**/

        editPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatData = "Detection price";
                editDialog(whatData);
            }
        });

        /**   editSpeciality.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        String whatData = "Specialty";
        editDialog(whatData);
        }
        });        editDegree.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        String whatData = "Degree";
        editDialog(whatData);
        }
        });**/


        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatData = "Phone Number";
                editDialog(whatData);
            }
        });

        insuranceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String whatData = "medical insurance";
                // editDialog(whatData);
                displayInsurancesDialog();

            }
        });
        ppicuri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image
                displayImportImageDialog();
            }
        });
        listCityItems = getResources().getStringArray(R.array.countries_array);
        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("SELECT City");

                mBuilder.setSingleChoiceItems(listCityItems, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO Auto-generated method stub
                        databaseDoctor.child(doctorId).child("cCity").setValue(listCityItems[i]);
                        databaseChat.child(doctorId).child("cCity").setValue(listCityItems[i]);
                        pcity.setText(listCityItems[i]);

                    }
                });


                mBuilder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        listSpecialityItems = getResources().getStringArray(R.array.spiciality_array);
        editSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("SELECT Specialty");

                mBuilder.setSingleChoiceItems(listSpecialityItems, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO Auto-generated method stub

                        databaseDoctor.child(doctorId).child("cSpecialty").setValue(listSpecialityItems[i]);
                        databaseChat.child(doctorId).child("cSpecialty").setValue(listSpecialityItems[i]);
                        databaseMap.child(idm).child("cmdoctorspecialty").setValue(listSpecialityItems[i]);
                        pspeciality.setText(listSpecialityItems[i]);

                    }
                });


                mBuilder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        insuranceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getContext(), InsuranceListActivity.class);
                intent.putExtra("InsuranceList", insuranceView.getText().toString());
                intent.putExtra("DoctorName", pname.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

        listDegreeItems = getResources().getStringArray(R.array.Degree_array);
        editDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("SELECT Degree");

                mBuilder.setSingleChoiceItems(listDegreeItems, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO Auto-generated method stub

                        databaseDoctor.child(doctorId).child("cDegree").setValue(listDegreeItems[i]);
                        databaseChat.child(doctorId).child("cDegree").setValue(listDegreeItems[i]);
                        pdegree.setText(listDegreeItems[i]);
                    }
                });


                mBuilder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        //Performing action on button click
     /**   pphone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = pphone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
           startActivity(callIntent);
            }

        });**/

        pphone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Creat a call");
                alert.setMessage("Are you sure, you want to dialling "+pname.getText().toString()+"?");
// Create TextView
                final TextView input = new TextView (getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String number = pphone.getText().toString();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + number));
                        startActivity(callIntent);
                        // Do something with value!
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
            }

        });
       // if(!doctorId.equals(uid)){peditbox.setEnabled(false);}
        //--------------------------------------
        peditbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
               // if(doctorId.equals(uid)) {

                    final String about1 = peditbox.getText().toString().trim();
                    databaseDoctor.child(doctorId).child("cAbout").setValue(about1);
              //  }else{peditbox.setEnabled(false);}
                /**else {
                 //////////////////////////////
                 Toast.makeText(DoctorProfileActivity.this, "You can't change it", Toast.LENGTH_LONG).show();
                 final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {
                String DoctorAbout = dataSnapshot1.child(DoctorID).child("cAbout").getValue(String.class);
                if (DoctorAbout != null) {peditbox.setText(DoctorAbout);}
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                }
                };
                 databaseDoctor.addValueEventListener(postListener1);
                 ////////////////////////////////////
                 }**/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });
        return rootView;
    }




    private void displayImportImageDialog() {

        final Dialog dialog = new Dialog(getContext());
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
                openGalleryAction();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCameraAction();
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

    private void openCameraAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

    }

    private void openGalleryAction() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (data.getData() != null) {
                    imagePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                        Bitmap compressedBitmap = getScaledBitmap(bitmap);
                        ppicuri.setImageBitmap(compressedBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byteImageData = baos.toByteArray();
                        uploadImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // addDoctorTextView.setEnabled(true);
                }

            } else if (requestCode == CAMERA_REQUEST_CODE) {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Bitmap compressedBitmap = getScaledBitmap(bitmap);
                ppicuri.setImageBitmap(compressedBitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byteImageData = baos.toByteArray();
                uploadImage();
            }
        }
    }

    private void uploadImage() {

        if (UtilClass.isNetworkConnected(getContext())) {

            if (byteImageData != null) {
                progressBarImage.setVisibility(View.VISIBLE);


                StorageReference trampsRef = mStorageRef.child("homelesspic/" + System.currentTimeMillis() + ".jpg");

                // StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("profilepics/pro.jpg");


                trampsRef.putBytes(byteImageData)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBarImage.setVisibility(View.GONE);

                                mTrampPhotoUrl = taskSnapshot.getDownloadUrl().toString();
                                databaseDoctor.child(fUser.getUid()).child("cUri").setValue(mTrampPhotoUrl);
                                databaseChat.child(fUser.getUid()).child("curi").setValue(mTrampPhotoUrl);
                                databaseChat.child(fUser.getUid()).child("cUri").setValue(mTrampPhotoUrl);
                                databaseMap.child(idm).child("cmdoctorpic").setValue(mTrampPhotoUrl);
                                if (!mTrampPhotoUrl.equals("")) {
                                    Log.v("Image", "Upload end");
                                    Toast.makeText(getContext(), "Upload end", Toast.LENGTH_LONG).show();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressBarImage.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "an error occurred while  uploading image", Toast.LENGTH_LONG).show();

                            }
                        });
            }
        } else {
            Toast.makeText(getContext(), "please check the network connection", Toast.LENGTH_LONG).show();
        }
    }

    ////////////////////////////////////////////
    private void editDialog(final String whatdata) {


        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_data_dialig);
        // dialog.setTitle("Edit your data");
        dialog.setCanceledOnTouchOutside(false);

        final EditText editField = (EditText) dialog.findViewById(R.id.edit_data_tv_e);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_e);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_e);
        final EditText discountInput = dialog.findViewById(R.id.discount_et);
        final CheckBox discountCheckBox = dialog.findViewById(R.id.discount_cb);
        final LinearLayout linear = dialog.findViewById(R.id.linear_discount);
        editField.setHint(whatdata);


        if (whatdata.equals("Phone Number")){
            editField.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_DATETIME_VARIATION_NORMAL);
            editField.setText(pphone.getText().toString().trim());
        }


        if (whatdata.equals("Detection price")){

            discountCheckBox.setVisibility(View.VISIBLE);
            editField.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

           if (!pprice.getText().toString().equals("Detection price")){
               editField.setText(pprice.getText().toString().trim().replace("$",""));
           }
            if (!drDiscountPrice.equals("0")&&!drDiscountPrice.equals("0.0") && drDiscountPrice!= null){
                linear.setVisibility(View.VISIBLE);
                discountCheckBox.setChecked(true);
                disChecked = true;
                discountInput.setText(drDiscountPrice);
            }
            discountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked){
                        linear.setVisibility(View.VISIBLE);
                        disChecked = true;
                    }else {
                        linear.setVisibility(View.GONE);
                        disChecked = false;
                    }
                }
            });

        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editfield1 = editField.getText().toString().trim();
                double pers =0;

                if (editfield1.isEmpty()) {
                    editField.setError(getString(R.string.empty_field_msg));
                    editField.requestFocus();
                    return;
                }
                if (whatdata.equals("Detection price")){

                    if (disChecked){

                        if (TextUtils.isEmpty(discountInput.getText().toString())){
                            discountInput.setError(getString(R.string.empty_field_msg));
                            discountInput.requestFocus();
                            return;
                        }else{
                            pers = Double.parseDouble(discountInput.getText().toString());
                            if (pers< 0 || pers> 100){
                                discountInput.setError(getString(R.string.invalid_number_msg));
                                discountInput.requestFocus();
                                return;
                            }
                        }
                    }
                }

                getRegData(editfield1, whatdata,pers);
                dialog.dismiss();

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

    private void getRegData(final String editfield1, final String whatdata, double discount) {

        if (whatdata.equals("Name")) {
            databaseDoctor.child(doctorId).child("cName").setValue(editfield1);
            databaseChat.child(doctorId).child("cName").setValue(editfield1);
            databaseMap.child(idm).child("cmname").setValue(editfield1);
            pname.setText(editfield1);

        } else if (whatdata.equals("Phone Number")) {
            databaseDoctor.child(doctorId).child("cPhone").setValue(editfield1);
            databaseChat.child(doctorId).child("cPhone").setValue(editfield1);
            pphone.setText(editfield1);

        } else if (whatdata.equals("Detection price")) {

           databaseDoctor.child(doctorId).child("cPrice").setValue(editfield1);
           databaseDoctor.child(doctorId).child("cDiscount").setValue(String.valueOf(discount));
           pprice.setText(editfield1);
        } else if (whatdata.equals("Average detection time in min")) {
            databaseDoctor.child(doctorId).child("cTime").setValue(editfield1);
        } else if (whatdata.equals("medical insurance")) {
            databaseDoctor.child(doctorId).child("cInsurance").setValue(editfield1);
        }

        //**************************************************//

        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                DoctorName = dataSnapshot1.child(doctorId).child("cName").getValue(String.class);
                String DoctorCity = dataSnapshot1.child(doctorId).child("cCity").getValue(String.class);
                String DoctorSpecialty = dataSnapshot1.child(doctorId).child("cSpecialty").getValue(String.class);
                String DoctorDegree = dataSnapshot1.child(doctorId).child("cDegree").getValue(String.class);
                String DoctorPhone = dataSnapshot1.child(doctorId).child("cPhone").getValue(String.class);
                String DoctorPrice = dataSnapshot1.child(doctorId).child("cPrice").getValue(String.class);
                String DoctorTime = dataSnapshot1.child(doctorId).child("cTime").getValue(String.class);
                String medInsurance = dataSnapshot1.child(doctorId).child("cInsurance").getValue(String.class);
                if (DoctorName != null) {
                    pname.setText(DoctorName);
                } else {
                    pname.setText("Name");
                }
                if (DoctorCity != null) {
                    pcity.setText(DoctorCity);
                } else {
                    pcity.setText("State/ City/ Region");
                }
                if (DoctorSpecialty != null) {
                    pspeciality.setText(DoctorSpecialty);
                } else {
                    pspeciality.setText("Specialty");
                }
                if (DoctorDegree != null) {
                    pdegree.setText(DoctorDegree);
                } else {
                    pdegree.setText("Degree");
                }
                if (DoctorPhone != null) {
                    pphone.setText(DoctorPhone);
                } else {
                    pphone.setText("Phone Number");
                }
                if (DoctorPrice != null) {
                    pprice.setText(DoctorPrice);
                } else {
                    pprice.setText("Detection price");
                }
                if (DoctorTime != null) {
                    ptime.setText(DoctorTime + "min.");
                } else {
                    ptime.setText("Not yet");
                }
                if (medInsurance != null) {
                    insuranceView.setText(medInsurance);
                } else {
                    insuranceView.setText("Not yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        //  databaseDoctor .addValueEventListener(postListener1);
    }

    /***-------------------------------------------------***/



    private void getallData() {

        //**************************************************//

        if (UtilClass.isNetworkConnected(getContext())){
        doctorEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                if (getActivity() == null){
                    return;
                }
                String DoctorName = dataSnapshot1.child(doctorId).child("cName").getValue(String.class);
                String DoctorCity = dataSnapshot1.child(doctorId).child("cCity").getValue(String.class);
                String DoctorSpecialty = dataSnapshot1.child(doctorId).child("cSpecialty").getValue(String.class);
                String DoctorDegree = dataSnapshot1.child(doctorId).child("cDegree").getValue(String.class);
                String DoctorPhone = dataSnapshot1.child(doctorId).child("cPhone").getValue(String.class);
                String DoctorPrice = dataSnapshot1.child(doctorId).child("cPrice").getValue(String.class);
                String DoctorTime = dataSnapshot1.child(doctorId).child("cTime").getValue(String.class);
                String DoctorAbout = dataSnapshot1.child(doctorId).child("cAbout").getValue(String.class);
                String DoctorPic = dataSnapshot1.child(doctorId).child("cUri").getValue(String.class);
                String medInsurance = dataSnapshot1.child(doctorId).child("cInsurance").getValue(String.class);
                drDiscountPrice = dataSnapshot1.child(doctorId).child("cDiscount").getValue(String.class);
                bookingtype = dataSnapshot1.child(doctorId).child("cbookingtypestate").getValue(boolean.class);

                if (bookingtype != null) {
                bookingtypecheck.setChecked(bookingtype);}

                if (DoctorName != null) {
                    pname.setText(DoctorName);
                } else {
                    pname.setText("Name");
                }
                if (DoctorCity != null) {
                    pcity.setText(DoctorCity);
                } else {
                    pcity.setText("State/ City/ Region");
                }
                if (DoctorSpecialty != null) {
                    pspeciality.setText(DoctorSpecialty);
                } else {
                    pspeciality.setText("Specialty");
                }
                if (DoctorDegree != null) {
                    pdegree.setText(DoctorDegree);
                } else {
                    pdegree.setText("Degree");
                }
                if (DoctorPhone != null) {
                    pphone.setText(DoctorPhone);
                } else {
                    pphone.setText("Phone Number");
                }
                if (DoctorPrice != null) {
                    pprice.setText(DoctorPrice + "$");
                } else {
                    pprice.setText("Detection price");
                }
                if (DoctorTime != null) {
                    ptime.setText(DoctorTime + "min.");
                } else {
                    ptime.setText("Not yet");
                }
                if (medInsurance != null) {
                    insuranceView.setText(medInsurance);
                } else {
                    insuranceView.setText("Nothing");
                }
                if(DoctorAbout != null) {
                    peditbox.setText(DoctorAbout);
                }

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new RoundedCorners(16));
                if (DoctorPic != null) {
                  if ( getActivity()!= null) {
                        Glide.with(getActivity())
                                .load(DoctorPic)
                                .apply(requestOptions)
                                .into(ppicuri);
                    }
                } else {
                  //  if (!getActivity().isFinishing()) {
                        Glide.with(getActivity())
                                .load("https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=d3108b95-4e16-4549-99b6-f0fa466e0d11")
                                .apply(requestOptions)
                                .into(ppicuri);
                   // }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseDoctor.addValueEventListener(doctorEventListener);
        }else {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ppicuri.getLayoutParams();
        params.width = width;
        params.height = height;
        ppicuri.setLayoutParams(params);

        return scaledBitmap;

    }

    // convert dp to pixel
    private int dpToPx(int dp) {
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    private void displayInsurancesDialog() {
        final String[] insuranceList = getResources().getStringArray(R.array.insurance_array);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        String insur = insuranceView.getText().toString();
        String[] insurances = insur.split(",");
        final boolean[] checkedItems = new boolean[insuranceList.length];
        boolean checked;
        for (int x = 0; x < insuranceList.length; x++) {
            checked = false;
            for (int y = 0; y < insurances.length; y++) {
                if (insuranceList[x].equals(insurances[y])) {
                    checked = true;
                }
            }
            checkedItems[x] = checked;

        }

        mBuilder.setTitle("Select insurance");

        mBuilder.setMultiChoiceItems(insuranceList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                insuranceItems = "";
                if (isChecked) {
                    checkedItems[position] = true;
                } else {
                    checkedItems[position] = false;
                }
            }
        });

        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                insuranceItems = "";
                for (int x = 0; x < insuranceList.length; x++) {
                    if (checkedItems[x]) {
                        if (insuranceItems.equals("")) {
                            insuranceItems = insuranceList[x];
                        } else {
                            insuranceItems = insuranceItems + "," + insuranceList[x];
                        }

                    }
                }
                if (insuranceItems.equals("")) {
                    insuranceView.setText("Nothing");
                } else {
                    insuranceView.setText(insuranceItems);
                }

                databaseDoctor.child(doctorId).child("cInsurance").setValue(insuranceItems);
            }
        });

        mBuilder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }



}
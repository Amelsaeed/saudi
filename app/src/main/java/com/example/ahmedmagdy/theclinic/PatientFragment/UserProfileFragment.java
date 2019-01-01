package com.example.ahmedmagdy.theclinic.PatientFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.UserProfileActivity;
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
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class UserProfileFragment extends Fragment {
    private Uri imagePath;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    TextView nameEditUser,phoneEditUser,birthdayEditUser, edit1,edit2, edit3;

    ImageView photoEdit;
    private ProgressBar progressBarUser;
    byte[] byteImageData;
    String PhotoUrl = "",Userid;


    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseUserReg,databaseChat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_user_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");databaseUserReg.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");databaseChat.keepSynced(true);
        Userid=mAuth.getCurrentUser().getUid();

        edit1 = (TextView) rootView.findViewById(R.id.edit1);
        edit2 = (TextView) rootView.findViewById(R.id.edit2);
        edit3 = (TextView) rootView.findViewById(R.id.edit3);
        nameEditUser= rootView.findViewById(R.id.user_name);
        phoneEditUser= rootView.findViewById(R.id.user_phone);
        birthdayEditUser= rootView.findViewById(R.id.user_birthday);



        photoEdit=(ImageView) rootView.findViewById(R.id.user_photo);
        progressBarUser= rootView.findViewById(R.id.progressbar_user);


        photoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayImportImageDialog();
            }
        });

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String whatdata = "Name";
                editDialog(whatdata);            }
        });

        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatdata = "Phone Number";
                editDialog(whatdata);
            }
        });

        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
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

                // abookingphoto.setOnClickListener(new View.OnClickListener() {
                //  @Override
                //   public void onClick(View v) {
                final Calendar mCurrentDate = Calendar.getInstance();
                int year=mCurrentDate.get(Calendar.YEAR);
                int month=mCurrentDate.get(Calendar.MONTH);
                int day=mCurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mPickerDialog =  new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        String datedmy= Year+"_"+ (Month+1)+"_"+Day;
                        Toast.makeText(getActivity(), datedmy, Toast.LENGTH_LONG).show();
                        databaseUserReg.child(Userid).child("cbirthday").setValue(datedmy);
                        databaseChat.child(Userid).child("cbirthday").setValue(datedmy);

                        birthdayEditUser.setText(datedmy);
                        // Toast.makeText(context, id+doctorID, Toast.LENGTH_LONG).show();
                        //makepatientbooking(timeID, datedmy);
                        //editTextcal.setText(Year+"_"+ ((Month/10)+1)+"_"+Day);
                        mCurrentDate.set(Year, ((Month+1)),Day);
                        //   mImageGenerator.generateDateImage(mCurrentDate, R.drawable.empty_calendar);
                    }
                }, year, month, day);
                mPickerDialog.show();
            }
        });

        loadUserInfo();
        return rootView;


    }

    private void loadUserInfo() {
        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String userName = dataSnapshot1.child(Userid).child("cname").getValue(String.class);
                String userPhone = dataSnapshot1.child(Userid).child("cphone").getValue(String.class);
                String userPic = dataSnapshot1.child(Userid).child("cUri").getValue(String.class);
                String userbithdar = dataSnapshot1.child(Userid).child("cbirthday").getValue(String.class);


                if(userName != null) {
                    nameEditUser.setText(userName);
                }else{nameEditUser.setText("Your name");}
                if(userPhone != null) {
                    phoneEditUser.setText(userPhone);
                }else{phoneEditUser.setText("your phone");}
                if(userbithdar != null) {
                    birthdayEditUser.setText(userbithdar);
                }else{birthdayEditUser.setText("Your birthday");}

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new RoundedCorners(16));
                if(userPic != null) {
                    Glide.with(getActivity())
                            .load(userPic)
                            .apply(requestOptions)
                            .into(photoEdit);
                }else{
                    Glide.with(getActivity())
                            .load("https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/user_logo_m.jpg?alt=media&token=ff53fa61-0252-43a4-8fa3-0eb3a3976ee5")
                            .apply(requestOptions)
                            .into(photoEdit);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseUserReg .addValueEventListener(postListener1);
    }



    private void saveuserinfo() {
        final String name = nameEditUser.getText().toString().trim();
        final String phone = phoneEditUser.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null ) {
            if (name.isEmpty()) {
                nameEditUser.setError("User name is required");
                nameEditUser.requestFocus();
                return;
            }
            databaseUserReg.child(Userid).child("cname").setValue(name);
            databaseUserReg.child(Userid).child("cphone").setValue(phone);
            databaseChat.child(Userid).child("cname").setValue(name);
            databaseChat.child(Userid).child("cphone").setValue(phone);
        }
    }
    private void displayImportImageDialog() {

        final Dialog dialog = new Dialog(getActivity());
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

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (data.getData() != null) {
                imagePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                    Bitmap compressedBitmap = getScaledBitmap(bitmap);
                    photoEdit.setImageBitmap(compressedBitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byteImageData = baos.toByteArray();
                    uploadImage();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } else if (requestCode == CAMERA_REQUEST_CODE) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap compressedBitmap = getScaledBitmap(bitmap);
            photoEdit.setImageBitmap(compressedBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byteImageData = baos.toByteArray();
            uploadImage();


        }



    }
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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoEdit.getLayoutParams();
        params.width = width;
        params.height = height;
        photoEdit.setLayoutParams(params);

        return scaledBitmap;

    }
    private int dpToPx(int dp) {
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        } else {
            return false;
        }
    }
    private void uploadImage(){
        if (isNetworkConnected()){

            if (isNetworkConnected()) {

                if (byteImageData != null) {
                    progressBarUser.setVisibility(View.VISIBLE);


                    StorageReference trampsRef = mStorageRef.child( "userPic/" + ".jpg");

                    trampsRef.putBytes(byteImageData)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBarUser.setVisibility(View.GONE);

                                    PhotoUrl = taskSnapshot.getDownloadUrl().toString();
                                    databaseUserReg.child(Userid).child("cUri").setValue(PhotoUrl);
                                    databaseChat.child(Userid).child("cUri").setValue(PhotoUrl);

                                    if (!PhotoUrl.equals("")) {
                                        Toast.makeText(getActivity(), "Upload end", Toast.LENGTH_LONG).show();

                                    }


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressBarUser.setVisibility(View.GONE);

                                    Toast.makeText(getActivity(), "an error occurred while  uploading image", Toast.LENGTH_LONG).show();

                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), "please check the network connection", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void editDialog(final String whatdata) {


        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_data_dialig);
        dialog.setTitle("Edit your data");
        dialog.setCanceledOnTouchOutside(false);

        final EditText editfield = (EditText) dialog.findViewById(R.id.edit_data_tv_e);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_e);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_e);
        editfield.setHint(whatdata);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editfield1 = editfield.getText().toString().trim();

                if (editfield1.isEmpty()) {
                    editfield.setError("Please fill the field");
                    editfield.requestFocus();
                    return;}
                getRegData(editfield1, whatdata);
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
    private void getRegData(final String editfield1, final String whatdata) {

        if (whatdata.equals("Name")) {
            databaseUserReg.child(Userid).child("cname").setValue(editfield1);
            databaseChat.child(Userid).child("cname").setValue(editfield1);

        } else if (whatdata.equals("Phone Number")) {
            databaseUserReg.child(Userid).child("cphone").setValue(editfield1);
            databaseChat.child(Userid).child("cphone").setValue(editfield1);

        }

        //**************************************************//
        // private void getallData();
        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String DoctorName = dataSnapshot1.child(Userid).child("cname").getValue(String.class);

                String DoctorPhone = dataSnapshot1.child(Userid).child("cphone").getValue(String.class);

                if(DoctorName != null) {
                    nameEditUser.setText(DoctorName);
                }else{nameEditUser.setText("Name");}

                if(DoctorPhone != null) {
                    phoneEditUser.setText(DoctorPhone);
                }else{phoneEditUser.setText("Phone Number");}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseUserReg .addValueEventListener(postListener1);
    }

}

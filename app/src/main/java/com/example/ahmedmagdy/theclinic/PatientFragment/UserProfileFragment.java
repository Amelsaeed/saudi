package com.example.ahmedmagdy.theclinic.PatientFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahmedmagdy.theclinic.R;
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
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class UserProfileFragment extends Fragment {
    private Uri imagePath;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    TextView nameEditUser, phoneEditUser, birthdayEditUser,  insuranceEditUser, Email;
    ImageView edit1, edit2, edit3, edit4;
    CircleImageView photoEdit;
    private ProgressBar progressBarUser;
    byte[] byteImageData;
    String PhotoUrl = "", Userid;
    String[] listInsuranceItem;
    private FirebaseUser FUser;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseUserReg, databaseChat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_user_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FUser = mAuth.getCurrentUser();
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databaseUserReg.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat.keepSynced(true);
        Userid = mAuth.getCurrentUser().getUid();
        Email = (TextView) rootView.findViewById(R.id.email_user);
        Email.setText(FUser.getEmail());
        edit1 =  rootView.findViewById(R.id.edit1);
        edit2 =  rootView.findViewById(R.id.edit2);
        edit3 =  rootView.findViewById(R.id.edit3);
        edit4 =  rootView.findViewById(R.id.edit4);
        nameEditUser = rootView.findViewById(R.id.user_name);
        phoneEditUser = rootView.findViewById(R.id.user_phone);
        birthdayEditUser = rootView.findViewById(R.id.user_birthday);
        insuranceEditUser = rootView.findViewById(R.id.user_insurance);


        photoEdit = (CircleImageView) rootView.findViewById(R.id.user_photo);
        progressBarUser = rootView.findViewById(R.id.progressbar_user);


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
                editDialog(whatdata);
            }
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
                int year = mCurrentDate.get(Calendar.YEAR);
                int month = mCurrentDate.get(Calendar.MONTH);
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mPickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        String datedmy = Year + "_" + (Month + 1) + "_" + Day;
                        Toast.makeText(getActivity(), datedmy, Toast.LENGTH_LONG).show();
                        databaseUserReg.child(Userid).child("cbirthday").setValue(datedmy);
                        databaseChat.child(Userid).child("cbirthday").setValue(datedmy);

                        birthdayEditUser.setText(datedmy);
                        // Toast.makeText(context, id+doctorID, Toast.LENGTH_LONG).show();
                        //makepatientbooking(timeID, datedmy);
                        //editTextcal.setText(Year+"_"+ ((Month/10)+1)+"_"+Day);
                        mCurrentDate.set(Year, ((Month + 1)), Day);
                        //   mImageGenerator.generateDateImage(mCurrentDate, R.drawable.empty_calendar);
                    }
                }, year, month, day);
                mPickerDialog.show();
            }
        });

        listInsuranceItem = getResources().getStringArray(R.array.insurance_array);
        edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(R.string.select_degree);

                mBuilder.setSingleChoiceItems(listInsuranceItem, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO Auto-generated method stub
                        databaseUserReg.child(Userid).child("cInsurance").setValue(listInsuranceItem[i]);
                        databaseChat.child(Userid).child("cInsurance").setValue(listInsuranceItem[i]);
                        insuranceEditUser.setText(listInsuranceItem[i]);
                    }
                });


                mBuilder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


        loadUserInfo();
        return rootView;


    }

    private void loadUserInfo() {

        if (!UtilClass.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {

                    String userName = dataSnapshot1.child(Userid).child("cname").getValue(String.class);
                    String userPhone = dataSnapshot1.child(Userid).child("cphone").getValue(String.class);
                    String userPic = dataSnapshot1.child(Userid).child("cUri").getValue(String.class);
                    String userbithdar = dataSnapshot1.child(Userid).child("cbirthday").getValue(String.class);
                    String userinsurance = dataSnapshot1.child(Userid).child("cInsurance").getValue(String.class);

                    if (userName != null) {
                        nameEditUser.setText(userName);
                    } else {
                        nameEditUser.setText(getString(R.string.your_name));
                    }
                    if (userPhone != null) {
                        phoneEditUser.setText(userPhone);
                    } else {
                        phoneEditUser.setText(getString(R.string.your_phone));
                    }
                    if (userbithdar != null) {
                        birthdayEditUser.setText(userbithdar);
                    } else {
                        birthdayEditUser.setText(getString(R.string.your_birthday));
                    }
                    if (userinsurance != null) {
                        insuranceEditUser.setText(userinsurance);
                    } else {
                        insuranceEditUser.setText(R.string.your_insurance);
                    }

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new RoundedCorners(16));
                    if (userPic != null) {
                        if ( getActivity()!= null) {
                        Glide.with(getActivity())
                                .load(userPic)
                                .apply(requestOptions)
                                .into(photoEdit);}
                    } else {
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
            databaseUserReg.addValueEventListener(postListener1);

    }


    private void saveuserinfo() {
        final String name = nameEditUser.getText().toString().trim();
        final String phone = phoneEditUser.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (name.isEmpty()) {
                nameEditUser.setError(getString(R.string.user_name_is_required));
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
        dialog.setTitle(R.string.import_image_from);
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
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), GALLERY_REQUEST_CODE);
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


    private void uploadImage() {


            if (UtilClass.isNetworkConnected(getContext())) {

                if (byteImageData != null) {
                    progressBarUser.setVisibility(View.VISIBLE);


                    StorageReference trampsRef = mStorageRef.child("userPic").child(mAuth.getCurrentUser().getUid()+ ".jpg");

                    trampsRef.putBytes(byteImageData)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBarUser.setVisibility(View.GONE);

                                    PhotoUrl = taskSnapshot.getDownloadUrl().toString();
                                    databaseUserReg.child(Userid).child("cUri").setValue(PhotoUrl);
                                    databaseChat.child(Userid).child("cUri").setValue(PhotoUrl);

                                    if (!PhotoUrl.equals("")) {
                                        Toast.makeText(getActivity(), R.string.upload_end, Toast.LENGTH_LONG).show();

                                    }


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressBarUser.setVisibility(View.GONE);

                                    Toast.makeText(getActivity(), R.string.an_error_occurred_while_uploading_image, Toast.LENGTH_LONG).show();

                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), R.string.please_check_the_network_connection, Toast.LENGTH_LONG).show();
            }

    }

    private void editDialog(final String whatdata) {


        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_data_dialig);
        dialog.setTitle("Edit your data");
        dialog.setCanceledOnTouchOutside(false);

        final EditText editfield = (EditText) dialog.findViewById(R.id.edit_data_tv_e);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_et);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_et);
        editfield.setHint(whatdata);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editfield1 = editfield.getText().toString().trim();

                if (editfield1.isEmpty()) {
                    editfield.setError(getString(R.string.please_fill_the_field));
                    editfield.requestFocus();
                    return;
                }
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
        if (UtilClass.isNetworkConnected(getContext())) {
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

                    if (DoctorName != null) {
                        nameEditUser.setText(DoctorName);
                    } else {
                        nameEditUser.setText(R.string.name);
                    }

                    if (DoctorPhone != null) {
                        phoneEditUser.setText(DoctorPhone);
                    } else {
                        phoneEditUser.setText(R.string.phone_number);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            databaseUserReg.addValueEventListener(postListener1);
        } else {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
    }

}

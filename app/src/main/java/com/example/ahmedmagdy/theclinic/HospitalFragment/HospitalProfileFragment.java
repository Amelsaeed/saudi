package com.example.ahmedmagdy.theclinic.HospitalFragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
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


public class HospitalProfileFragment extends Fragment {
    private FirebaseUser FUser;
    private FirebaseAuth mAuth;
    private Uri imagePath;
    byte[] byteImageData;
    String mTrampPhotoUrl = "";
    ProgressBar progressBarImage;
    private StorageReference mStorageRef;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    TextView Email, Name, City, NumberPhone, Insurance;
    DatabaseReference reference;
    ImageView Photo, EditCity, EditPhone, EditInsurance,EditName;
    CircleImageView ProfileHospital;
    String[] listCityItems;
    String doctorId;
    private DatabaseReference databaseDoctor, databaseChat;
    String  insuranceItems = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hospital_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FUser = mAuth.getCurrentUser();
        if (FUser != null) {
            doctorId = FUser.getUid();
        }

        ProfileHospital = (CircleImageView) view.findViewById(R.id.profile_hospital);
        EditCity = (ImageView) view.findViewById(R.id.edit_city_hospital);
        EditPhone = (ImageView) view.findViewById(R.id.edit_phone_hospital);
        EditInsurance = (ImageView) view.findViewById(R.id.edit_insurance_hospital);
        Email = (TextView) view.findViewById(R.id.email_hospital);
        Photo = (ImageView) view.findViewById(R.id.profile_hospital);
        City = (TextView) view.findViewById(R.id.hospital_city);
        EditName = (ImageView)view.findViewById(R.id.edit_name_hospital);
        progressBarImage = (ProgressBar)view.findViewById(R.id.proo);
        Email.setText(FUser.getEmail());
        Name = (TextView) view.findViewById(R.id.name_hospital);
        NumberPhone = (TextView) view.findViewById(R.id.hospital_phone);
        Insurance = (TextView) view.findViewById(R.id.hospital_Insurance);
        mStorageRef = FirebaseStorage.getInstance().getReference("Photos");
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        reference = FirebaseDatabase.getInstance().getReference("Doctordb").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RegisterClass user = dataSnapshot.getValue(RegisterClass.class);
                ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {

                        String UserName = dataSnapshot1.child("cName").getValue(String.class);
                        Name.setText(UserName);
                        String img = dataSnapshot1.child("cUri").getValue(String.class);
                        Glide.with(getActivity()).load(img).into(Photo);
                        String cCity = dataSnapshot1.child("cCity").getValue(String.class);
                        City.setText(cCity);
                        String Num = dataSnapshot1.child("cPhone").getValue(String.class);
                        NumberPhone.setText(Num);
                        String cInsurance = dataSnapshot1.child("cInsurance").getValue(String.class);
                        Insurance.setText(cInsurance);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };
                reference.addValueEventListener(postListener1);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ProfileHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayImportImageDialog();
            }
        });
        EditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatData = "Enter Name";
                editDialog2(whatData);
            }
        });
        listCityItems = getResources().getStringArray(R.array.countries_array);
        EditCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("SELECT City");

                mBuilder.setSingleChoiceItems(listCityItems, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO Auto-generated method stub
                        databaseDoctor.child(doctorId).child("cCity").setValue(listCityItems[i]);
                        databaseChat.child(doctorId).child("cCity").setValue(listCityItems[i]);
                        City.setText(listCityItems[i]);

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
        EditInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInsurancesDialog();
            }
        });
        EditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatData = "Phone Number";
                editDialog(whatData);
            }
        });
        return view;
    }
    private void displayInsurancesDialog() {
        final String[] insuranceList = getResources().getStringArray(R.array.insurance_array);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        String insur = Insurance.getText().toString();
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
                    Insurance.setText("Nothing");
                } else {
                    Insurance.setText(insuranceItems);
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
    private void editDialog2(final String whatdata) {


        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_data_dialig);
        // dialog.setTitle("Edit your data");
        dialog.setCanceledOnTouchOutside(false);

        final EditText editField = (EditText) dialog.findViewById(R.id.edit_data_tv_e);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_e);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_e);
        editField.setHint(whatdata);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editfield1 = editField.getText().toString().trim();

                if (editfield1.isEmpty()) {
                    editField.setError("Please fill the field");
                    editField.requestFocus();
                    return;
                }
                databaseDoctor.child(doctorId).child("cName").setValue(editfield1, whatdata);
                databaseChat.child(doctorId).child("cname").setValue(editfield1, whatdata);
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
    private void editDialog(final String whatdata) {


        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_data_dialig);
        // dialog.setTitle("Edit your data");
        dialog.setCanceledOnTouchOutside(false);

        final EditText editField = (EditText) dialog.findViewById(R.id.edit_data_tv_e);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv_e);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv_e);
        editField.setHint(whatdata);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editfield1 = editField.getText().toString().trim();

                if (editfield1.isEmpty()) {
                    editField.setError("Please fill the field");
                    editField.requestFocus();
                    return;
                }
                databaseDoctor.child(doctorId).child("cPhone").setValue(editfield1, whatdata);
                databaseChat.child(doctorId).child("ccity").setValue(editfield1, whatdata);
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
    private void openGalleryAction() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }
    private void openCameraAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

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
                        ProfileHospital.setImageBitmap(compressedBitmap);
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
                ProfileHospital.setImageBitmap(compressedBitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byteImageData = baos.toByteArray();
                uploadImage();
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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ProfileHospital.getLayoutParams();
        params.width = width;
        params.height = height;
        ProfileHospital.setLayoutParams(params);

        return scaledBitmap;

    }
    // convert dp to pixel
    private int dpToPx(int dp) {
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
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
                                databaseDoctor.child(FUser.getUid()).child("cUri").setValue(mTrampPhotoUrl);
                                databaseChat.child(FUser.getUid()).child("curi").setValue(mTrampPhotoUrl);
                                databaseChat.child(FUser.getUid()).child("cUri").setValue(mTrampPhotoUrl);

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
}


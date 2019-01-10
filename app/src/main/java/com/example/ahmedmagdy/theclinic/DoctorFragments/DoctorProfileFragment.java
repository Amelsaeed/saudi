package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.ahmedmagdy.theclinic.activities.BookingListActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.example.ahmedmagdy.theclinic.map.DoctorMapFrag;
import com.example.ahmedmagdy.theclinic.map.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static com.example.ahmedmagdy.theclinic.map.Constants.ERROR_DIALOG_REQUEST;
import static com.example.ahmedmagdy.theclinic.map.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.ahmedmagdy.theclinic.map.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

//import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;

public class DoctorProfileFragment extends Fragment implements OnRequestPermissionsResultCallback {
    ImageView ppicuri, editName, editCity, editPhone, editDegree, editSpeciality, editPrice, insuranceEdit;
    TextView pname, pcity, pspeciality, pdegree, pphone, pprice, ptime, drEmail, insuranceView;
    EditText peditbox;
    private ProgressBar progressBarImage;
    Button Doc;
    private Uri imagePath;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    String address, idm;
    String mTrampPhotoUrl = "";
    double latitude;
    double longitude;
    String doctorId;
    String DoctorName, insuranceItems = "";
    byte[] byteImageData;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor, databaseChat, databaseMap;
    private FirebaseUser fUser;
    private ValueEventListener doctorEventListener;

    String picuri;
    final int theRequestCodeForLocation = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Boolean isPermissionGranted;
    String[] listCityItems;
    String[] listSpecialityItems;
    String[] listDegreeItems;
    //gps
    //create user location to save all doctor locations
    private static final String TAG = "DoctorProfileFragment";
    private UserLocation mUserLocaiotn;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;


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
        Doc = rootView.findViewById(R.id.doc);
        peditbox = rootView.findViewById(R.id.peditbox);
        ppicuri = rootView.findViewById(R.id.edit_photo);


        editName.setVisibility(View.VISIBLE);
        editPhone.setVisibility(View.VISIBLE);
        editDegree.setVisibility(View.VISIBLE);
        editSpeciality.setVisibility(View.VISIBLE);
        editCity.setVisibility(View.VISIBLE);
        editPrice.setVisibility(View.VISIBLE);
        insuranceEdit.setVisibility(View.VISIBLE);
        drEmail.setVisibility(View.VISIBLE);


        Button workingHours = rootView.findViewById(R.id.working_hours_btn);
        Button confirmLocation = rootView.findViewById(R.id.confim_loc_btn);

        drEmail.setText(fUser.getEmail());


        getallData();
        workingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookingListActivity.class);
                intent.putExtra("DoctorID", doctorId);
                intent.putExtra("DoctorName", DoctorName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });
        // open gps fragment
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMap();
                /*Intent intent = new Intent(getActivity(), BookingListActivity.class);
                intent.putExtra("DoctorID", doctorId);
                intent.putExtra("DoctorName", DoctorName);
                startActivity(intent);*/

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

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            } else if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {

                if (mLocationPermissionGranted) {
                    // getChatrooms();
                    getLastKnownLocation();
                    System.out.println(TAG + "permission granted on activity result true");
                } else {
                    getLocationPermission();
                }

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

    public void onStart() {
        super.onStart();

    }

    private void getallData() {

        //**************************************************//
        doctorEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

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

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new RoundedCorners(16));
                if (DoctorPic != null) {
                    Glide.with(DoctorProfileFragment.this)
                            .load(DoctorPic)
                            .apply(requestOptions)
                            .into(ppicuri);
                } else {
                    Glide.with(DoctorProfileFragment.this)
                            .load("https://firebasestorage.googleapis.com/v0/b/the-clinic-66fa1.appspot.com/o/doctor_logo_m.jpg?alt=media&token=d3108b95-4e16-4549-99b6-f0fa466e0d11")
                            .apply(requestOptions)
                            .into(ppicuri);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseDoctor.addValueEventListener(doctorEventListener);
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
    //-----------------------------add Gps----------------------------------

    /**
     * Request the Location Permission
     */
    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        } else {
            isPermissionGranted = true;
        }

    }

    private void displayInsurancesDialog() {
        final String[] insuranceList = getResources().getStringArray(R.array.insurance_array);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        final ArrayList<Integer> mInsurItems = new ArrayList<>();
        String insur = insuranceView.getText().toString();
        String[] insurances = insur.split(",");
        final ArrayList<Boolean> checkedList = new ArrayList<>();
        boolean checked;
        for (int x = 0; x < insuranceList.length; x++) {
            checked = false;
            for (int y = 0; y < insurances.length; y++) {
                if (insuranceList[x].equals(insurances[y])) {
                    checked = true;
                }
            }

            checkedList.add(x, checked);
        }

        final boolean[] checkedItems = new boolean[insuranceList.length];
        for (int i = 0; i < insuranceList.length; i++) {
            checkedItems[i] = checkedList.get(i);
        }

        mBuilder.setTitle("SELECT Insurance");

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


    /**
     * this method is called by android system after we request a permission
     * and the system pass the result of our request to this method so we can check if we got
     * the permission or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case theRequestCodeForLocation:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                } else {
                    Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                    isPermissionGranted = false;
                }
                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (doctorEventListener != null) {
            databaseDoctor.removeEventListener(doctorEventListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (doctorEventListener != null) {
            databaseDoctor.removeEventListener(doctorEventListener);
        }
    }


    /*gps start*/
    /*gps start*/
    //save doctor user location step 3
    private void saveUserLocaion() {

        if (mUserLocaiotn != null) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            database.child("DoctorMap").child(mAuth.getCurrentUser().getUid()).
                    setValue(mUserLocaiotn).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        System.out.println(TAG + "Doc Profile Activity>> " + mUserLocaiotn);
                        Toast.makeText(getContext(), "Location Saved Successfully.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        //end if

    }

    //get map location permission
    private void getLastKnownLocation() {
        System.out.println(TAG + "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    final Location location = task.getResult();
                    //get user first
                    if (mUserLocaiotn == null) {
                        mUserLocaiotn = new UserLocation();

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        System.out.println("UIDGET :" + uid);

                        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = mFirebaseDatabaseReference.child("Doctordb").child(uid);
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DoctorFirebaseClass dF = dataSnapshot.getValue(DoctorFirebaseClass.class);
                                System.out.println(TAG + " qerrrrrrrrrrry " + dF.getcName());

                                mUserLocaiotn.setLat(location.getLatitude());
                                mUserLocaiotn.setLng(location.getLongitude());
                                mUserLocaiotn.setcName(dF.getcName());
                                mUserLocaiotn.setcCity(dF.getcCity());
                                mUserLocaiotn.setcType(dF.getcType());
                                mUserLocaiotn.setcEmail(dF.getcEmail());
                                mUserLocaiotn.setcUid(dF.getcId());
                                mUserLocaiotn.setcSpec(dF.getcSpecialty());
                                mUserLocaiotn.setcURI(dF.getcUri());


                                saveUserLocaion();
                                System.out.println(TAG + " getLastKNown>> USERafterAdDate: " + mUserLocaiotn);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        query.addValueEventListener(valueEventListener);
                    }
                }
            }
        });

    }

    /*check map permissions */
    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            System.out.println(TAG + " get location permission fine");
            //getChatrooms();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                //getChatrooms();
                goToMap();
                Toast.makeText(getContext(), "Permission granted Successfully",
                        Toast.LENGTH_LONG).show();
            } else {
                getLocationPermission();
            }
        }
    }

    private void goToMap() {
        System.out.println(TAG + " goToMap>>");
        hideSoftKeyboard();


        DoctorMapFrag fragment = DoctorMapFrag.newInstance();
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList(getString(R.string.intent_user_list), mUserList);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack("User List");
        transaction.commit();

        getLastKnownLocation();
    }

    private void hideSoftKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    /*check map permissions */

    /*gps end*/
    /*gps end*/
}
package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.WorkingHoursActivity;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

//import com.example.ahmedmagdy.theclinic.Adapters.DoctorAdapter;

public class DoctorProfileFragment extends Fragment implements OnRequestPermissionsResultCallback {
    ImageView ppicuri, editName, editCity, editPhone, editDegree, editSpeciality, editPrice;
    TextView pname, pcity, pspeciality, pdegree, pphone, pprice, ptime, paddbook, drEmail;
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
    String doctorId, arrange;

    byte[] byteImageData;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseDoctor, databaseChat, databaseMap;
    private DatabaseReference databaseUserReg;
    private DatabaseReference databasetimeBooking;
    private FirebaseUser fUser;
    private ValueEventListener doctorEventListener;

    String  mDate, picuri;
    final int theRequestCodeForLocation = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Boolean isPermissionGranted;
    APIService apiService;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = getLayoutInflater().inflate(R.layout.activity_doctor_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        if (fUser != null){
            doctorId = fUser.getUid();
        }




        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseUserReg = FirebaseDatabase.getInstance().getReference("user_data");
        databasetimeBooking = FirebaseDatabase.getInstance().getReference("bookingtimes");
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
        paddbook = rootView.findViewById(R.id.add);



        Button workingHours = rootView.findViewById(R.id.working_hours_btn);
        workingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WorkingHoursActivity.class);
                intent.putExtra("doctorId",doctorId);
                startActivity(intent);
            }
        });

        drEmail.setText(fUser.getEmail());



        getallData();


        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatdata = "Name";
                editDialog(whatdata);
            }
        });

        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatdata = "State/ City/ Region";
                editDialog(whatdata);
            }
        });

        editPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatdata = "Detection price";
                editDialog(whatdata);
            }
        });

        editSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatdata = "Specialty";
                editDialog(whatdata);
            }
        });

        editDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatdata = "Degree";
                editDialog(whatdata);
            }
        });

        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whatdata = "Phone Number";
                editDialog(whatdata);
            }
        });


        //--------Gps---------------------

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        requestPermission();

        paddbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--------Gps---------------------
                if (isPermissionGranted) {

                    getLocation();
                } else {
                    requestPermission();
                    if (isPermissionGranted) {
                        //We have it, Get the location.
                        getLocation();
                    } else {
                        Toast.makeText(getContext(), "Please Give us permission so you can use the app", Toast.LENGTH_SHORT).show();
                    }
                }
                //--------------------------------------
                //String whatdata = "Ex:from Sat to Mon in address at 15:00 clock";

            }
        });

        ////////////////////////////////
        //--------Gps---------------------


        ppicuri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses image

                displayImportImageDialog();
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

        } else if (whatdata.equals("State/ City/ Region")) {
            databaseDoctor.child(doctorId).child("cCity").setValue(editfield1);
            databaseChat.child(doctorId).child("cCity").setValue(editfield1);
            pcity.setText(editfield1);

        } else if (whatdata.equals("Specialty")) {
            databaseDoctor.child(doctorId).child("cSpecialty").setValue(editfield1);
            databaseChat.child(doctorId).child("cSpecialty").setValue(editfield1);
            databaseMap.child(idm).child("cmdoctorspecialty").setValue(editfield1);
            pspeciality.setText(editfield1);

        } else if (whatdata.equals("Degree")) {
            databaseDoctor.child(doctorId).child("cDegree").setValue(editfield1);
            databaseChat.child(doctorId).child("cDegree").setValue(editfield1);
            pdegree.setText(editfield1);

        } else if (whatdata.equals("Phone Number")) {
            databaseDoctor.child(doctorId).child("cPhone").setValue(editfield1);
            databaseChat.child(doctorId).child("cPhone").setValue(editfield1);
            pphone.setText(editfield1);

        } else if (whatdata.equals("Detection price")) {
            databaseDoctor.child(doctorId).child("cPrice").setValue(editfield1);
            pprice.setText(editfield1);
        } else if (whatdata.equals("Average detection time in min")) {
            databaseDoctor.child(doctorId).child("cTime").setValue(editfield1);
        }

        //**************************************************//

        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String DoctorName = dataSnapshot1.child(doctorId).child("cName").getValue(String.class);
                String DoctorCity = dataSnapshot1.child(doctorId).child("cCity").getValue(String.class);
                String DoctorSpecialty = dataSnapshot1.child(doctorId).child("cSpecialty").getValue(String.class);
                String DoctorDegree = dataSnapshot1.child(doctorId).child("cDegree").getValue(String.class);
                String DoctorPhone = dataSnapshot1.child(doctorId).child("cPhone").getValue(String.class);
                String DoctorPrice = dataSnapshot1.child(doctorId).child("cPrice").getValue(String.class);
                String DoctorTime = dataSnapshot1.child(doctorId).child("cTime").getValue(String.class);
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
        // private void getallData();
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
                if (DoctorAbout != null) {
                    peditbox.setText(DoctorAbout);
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

    /**
     * this method gets the location of the user then
     * Calls the showAddress Method and pass to it the Latitude
     * and the Longitude
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, theRequestCodeForLocation);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    showAddress(latitude, longitude);
                } else {
                    Toast.makeText(getActivity(), "Error we didn't get the Location\n Please try again after Few seconds", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * this method shows the User's address to the screen, it calls the getAddress which returns a string
     * contains the address then changes the TextView text to it.
     *
     * @param latitude  is the latitude of the location
     * @param longitude is the longitude of the location
     */
    private void showAddress(double latitude, double longitude) {
        String msg = "";

        try {
            msg = getAddress(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pcity.setText(msg);
//
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

    /**
     * this method takes the longitude and latitude of the location then convert them into real address
     * and return it as string
     *
     * @param latitude  the Latitude
     * @param longitude the Longitude
     * @return the address as String
     * @throws IOException
     */
    private String getAddress(double latitude, double longitude) throws IOException {

        //Geocoder class helps us to convert longitude and latitude into Address
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        address = addresses.get(0).getAddressLine(0);
        String city = "City: " + addresses.get(0).getLocality();
        String state = "State:" + addresses.get(0).getAdminArea();
        String country = "Country: " + addresses.get(0).getCountryName();
        // String wholeAddress = address + "\n" + city + "\n" + state + "\n" + country;
        String wholeAddress = address;
        // pcity.setText(address);

        return wholeAddress;

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
}


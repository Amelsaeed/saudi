package com.example.ahmedmagdy.theclinic.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import static com.example.ahmedmagdy.theclinic.map.Constants.ERROR_DIALOG_REQUEST;
import static com.example.ahmedmagdy.theclinic.map.Constants.MAPVIEW_BUNDLE_KEY;
import static com.example.ahmedmagdy.theclinic.map.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.ahmedmagdy.theclinic.map.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class DoctorMapFrag extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "UserListFragment";
    public static int cunterRefresh = 0;

    //widgets
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    //private double lat = 24.774265, lng = 46.738586;
    private double lat = 24.774265, lng = 46.738586;
    private double myLat = 0.0, myLng = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted = false;
    private UserLocation mUserLocaiotn;
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocs = new ArrayList<>();


    public static DoctorMapFrag newInstance() {
        return new DoctorMapFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (getArguments() != null) {
            mUserLocs = getArguments().getParcelableArrayList("intent_user_locs");
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_map, container, false);

        mMapView = view.findViewById(R.id.user_list_map);

        checkMapServices();

        initGoogleMap(savedInstanceState);



        return view;
    }

    private void goToMap() {
        System.out.println(TAG + " goToMap>>");
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //set camera view for all docs
    private void setCameraView() {

        //overall map view will 0.4 = 0.2 , 0.2
        double mBottomBoundry = lat - 2.2;
        double mLeftBoundry = lng - 2.2;
        double mTopBoundry = lat + 2.2;
        double mRightBoundry = lng + 2.2;

        mMapBoundary = new LatLngBounds(
                new LatLng(mBottomBoundry, mLeftBoundry),
                new LatLng(mTopBoundry, mRightBoundry)
        );

        System.out.println(TAG + " mMapBoundary " + mMapBoundary);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));

    }

    //add map markers
    private void addMapMarkers() {

        if (mGoogleMap != null && mUserLocs != null) {

            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for (UserLocation userLocation : mUserLocs) {

                System.out.println(TAG + "addMapMarkers: location: " + userLocation.getLat() + "," + userLocation.getLng());
                try {
                    String snippet = "";
                    try {
                        snippet = userLocation.getcSpec();

                    } catch (NumberFormatException e) {
                        System.out.println(TAG + "addMapMarkers: no Spec for " + userLocation.getcName() +
                                ", setting default.");
                    }

                    int avatar = R.drawable.cartman_cop; // set the default avatar
                    String avatarUrl = null; // set the default avatar
                    try {
                        avatarUrl = userLocation.getcURI();

                    } catch (NumberFormatException e) {
                        System.out.println(TAG + "addMapMarkers: no avatar for " + userLocation.getcName() +
                                ", setting default.");
                    }
                    ClusterMarker newClusterMarker;
                    if (avatarUrl == null) {
                        newClusterMarker = new ClusterMarker(
                                new LatLng(userLocation.getLat(), userLocation.getLng()),
                                userLocation.getcName(),
                                snippet,
                                avatar
                        );
                    } else {
                        newClusterMarker = new ClusterMarker(
                                new LatLng(userLocation.getLat(), userLocation.getLng()),
                                userLocation.getcName(),
                                snippet,
                                avatarUrl
                        );
                    }
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                } catch (NullPointerException e) {
                    System.out.println(TAG + "addMapMarkers: NullPointerException: " + e.getMessage());
                }

            }
            mClusterManager.cluster();

            setCameraView();


        } else {
            //for doctor profile
            setMyPosCamera();
        }

    }

    //get my position with set camera close 0.1 for doctor profile
    private void setMyPosCamera() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    final Location location = task.getResult();
                    myLat =  location.getLatitude();
                    myLng =  location.getLongitude();
                    //overall map view will 0.4 = 0.2 , 0.2
                    double mBottomBoundry = myLat - 0.1;
                    double mLeftBoundry = myLng - 0.1;
                    double mTopBoundry = myLat + 0.1;
                    double mRightBoundry = myLng + 0.1;

                    mMapBoundary = new LatLngBounds(
                            new LatLng(mBottomBoundry, mLeftBoundry),
                            new LatLng(mTopBoundry, mRightBoundry)
                    );

                    System.out.println(TAG + " mMapBoundary " + mMapBoundary);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
                }
            }
        });


    }


    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getLocationPermission();
                Toast.makeText(getContext(), "Permission granted Successfully",
                        Toast.LENGTH_LONG).show();
            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        /*map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("ksa"));
        map.addMarker(new MarkerOptions().position(new LatLng(lat - 5, lng - 4)).title("-5,-4"));
        map.addMarker(new MarkerOptions().position(new LatLng(lat + 5, lng + 6)).title("+5,+3"));*/
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        mGoogleMap = map;
        addMapMarkers();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /*test check permission*/
    /*check map permissions */
    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                goToMap();
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
        final LocationManager manager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            System.out.println(TAG + " get location permission fine");
            //getChatrooms();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

    /*check map permissions */
    /*test check permission*/

    /*refresh*/
    /*refresh*/

}

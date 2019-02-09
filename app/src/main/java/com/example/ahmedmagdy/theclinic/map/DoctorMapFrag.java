package com.example.ahmedmagdy.theclinic.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.AddDoctorActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import android.location.LocationListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.ahmedmagdy.theclinic.map.Constants.ERROR_DIALOG_REQUEST;
import static com.example.ahmedmagdy.theclinic.map.Constants.LOCATION_UPDATE_MIN_DISTANCE;
import static com.example.ahmedmagdy.theclinic.map.Constants.LOCATION_UPDATE_MIN_TIME;
import static com.example.ahmedmagdy.theclinic.map.Constants.MAPVIEW_BUNDLE_KEY;
import static com.example.ahmedmagdy.theclinic.map.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.ahmedmagdy.theclinic.map.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class DoctorMapFrag extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, View.OnClickListener{

    private static final String TAG = "DoctorMapFragment";
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
    private GeoApiContext mGeoApiContext = null;
    /*direction*/
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                myLat = location.getLatitude();
                myLng= location.getLongitude();
                System.out.println(TAG + " listener my" + myLat + myLng);
                //drawMarker(location);
                addMapMarkers();
                mLocationManager.removeUpdates(mLocationListener);
            } else {
                System.out.println(TAG + " listener " + "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private LocationManager mLocationManager;
    /*direction*/


    public static DoctorMapFrag newInstance() {
        return new DoctorMapFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (getArguments() != null) {
            mUserLocs = getArguments().getParcelableArrayList("intent_user_locs");
            /*for(UserLocation u : mUserLocs){
                System.out.println(TAG+": "+u);
            }*/
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_map, container, false);

        mMapView = view.findViewById(R.id.user_list_map);
        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);

        checkMapServices();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        initGoogleMap(savedInstanceState);

        return view;
    }

    private void goToMap() {
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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 20));

    }

    //add map markers
    private void addMapMarkers() {

        if (mGoogleMap != null && mUserLocs != null) {

            resetMap();

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

                if (userLocation.getCmdoctorid() != null &&
                        userLocation.getCmname() != null &&
                        userLocation.getCmdoctorspecialty() != null &&
                        userLocation.getCmDoctorGander() != null
                        ) {
                    /*System.out.println(TAG + "addMapMarkers: location: " +
                            Double.parseDouble(userLocation.getCmlatitude()) +
                            "," + Double.parseDouble(userLocation.getCmlongitude()) +
                            "," + userLocation.getCmdoctorid() +
                            ",gender: " + userLocation.getCmDoctorGander() +
                            ",speci: " + userLocation.getCmdoctorspecialty() +
                            "," + userLocation.getCmname()
                    );*/
                    try {
                        int avatar = 0; // set the default avatar
                        if (userLocation.getCmDoctorGander().equals("Male")) {
                            avatar = R.drawable.doc_male_icon;
                            System.out.println("avatarMale: " + avatar);
                        } else {
                            avatar = R.drawable.doc_female_icon;
                            System.out.println("avatarFemale: " + avatar);
                        }
                        String avatarUrl = null; // set the default avatar
                        try {
                            avatarUrl = userLocation.getCmdoctorpic();

                        } catch (NumberFormatException e) {
                            System.out.println(TAG + "addMapMarkers: no avatar for " + userLocation.getCmname() +
                                    ", setting default.");
                        }
                        ClusterMarker newClusterMarker = new ClusterMarker();

                        newClusterMarker.setPosition(new LatLng(Double.parseDouble(userLocation.getCmlatitude()),
                                Double.parseDouble(userLocation.getCmlongitude())));
                        newClusterMarker.setTitle(userLocation.getCmname());
                        newClusterMarker.setGender(userLocation.getCmDoctorGander());
                        newClusterMarker.setSnippet(userLocation.getCmdoctorspecialty());
                        if (avatarUrl == null) {
                            newClusterMarker.setIconPic(avatar);
                        } else {
                            newClusterMarker.setIconPicUrl(avatarUrl);
                        }

                        mClusterManager.addItem(newClusterMarker);
                        mClusterMarkers.add(newClusterMarker);

                    } catch (NullPointerException e) {
                        System.out.println(TAG + "addMapMarkers: NullPointerException: " + e.getMessage());
                    }
                }
            }
            mClusterManager.cluster();

            setCameraView();

            //add my marker
            getCurrentLocation();
            LatLng gps = new LatLng(myLat, myLng);
            System.out.println(TAG+" gps marker "+gps);
            if( gps != null){
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(gps)
                        .title(getString(R.string.mylocation)));
            }

        } else {
            //for doctor profile
            setMyPosCamera();
        }

    }

    private void resetMap() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();

            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

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
                    myLat = location.getLatitude();
                    myLng = location.getLongitude();
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

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key)).build();
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
                //getCurrentLocation();
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
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        mLocationManager.removeUpdates(mLocationListener);
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

    @Override
    public void onInfoWindowClick(final Marker marker) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to get direction to " + marker.getTitle() + " ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //calculateDirections(marker);
                        /*test goog dir*/
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Open Google Maps?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            String latitude = String.valueOf(marker.getPosition().latitude);
                                            String longitude = String.valueOf(marker.getPosition().longitude);
                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            try{
                                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                    startActivity(mapIntent);
                                                }
                                            }catch (NullPointerException e){
                                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                                Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            dialog.cancel();
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.show();

                        /*test goog dir*/
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    /*private void calculateDirections(Marker marker) {
        System.out.println(TAG + "calculateDirections: calculating directions." +
                " .. myLocLatLng >>> " + myLat + "," + myLng);

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        myLat,
                        myLng
                )
        );
        System.out.println(TAG + "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Toast.makeText(getContext(), "onResult: routes: " + result.routes[0].toString()
                        , Toast.LENGTH_SHORT).show();
                //System.out.println(TAG + "onResult: routes: " + result.routes[0].toString());
                //System.out.println(TAG + "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                System.out.println(TAG + "onFailure: " + e.getMessage());

            }
        });
    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reset_map:{
                addMapMarkers();
                break;
            }

        }
    }

    /*check map permissions */
    /*test check permission*/

    /*refresh*/
    /*refresh*/

    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Snackbar.make(mMapView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
        else {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            System.out.println(TAG+"getCurrentLocation"+ location.getLatitude()+
                    location.getLongitude());
            //drawMarker(location);
            //addMapMarkers();
        }
    }

    private void drawMarker(Location location) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
        }

    }
}

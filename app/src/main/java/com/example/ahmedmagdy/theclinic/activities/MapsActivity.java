package com.example.ahmedmagdy.theclinic.activities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.Mapinfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference databaseMap;
    Marker marker;
    List<Mapinfo> venueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(false);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
        databaseMap = FirebaseDatabase.getInstance().getReference("mapdb");
        venueList = new ArrayList<>();
        databaseMap.push().setValue(marker);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        //   mMap.getUiSettings().setZoomGesturesEnabled(true);
        //   mMap.getUiSettings().setRotateGesturesEnabled(true);
        databaseMap.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    //   Mapinfo user =s.getValue(Mapinfo.class);

                    String DoctorName = s.child("cmname").getValue(String.class);
                    String Doctorspecialty = s.child("cmdoctorspecialty").getValue(String.class);
                    //  String Doctordata=DoctorName+" ("+ Doctorspecialty+")";
                    Double latitude = Double.parseDouble(s.child("cmlatitude").getValue(String.class));
                    Double longitude = Double.parseDouble(s.child("cmlongitude").getValue(String.class));
                    String Doctorpic = s.child("cmdoctorpic").getValue(String.class);

                    Mapinfo user = new Mapinfo(DoctorName, latitude, longitude);
                    venueList.add(user);
                    for (int i = 0; i < venueList.size(); i++) {
                        LatLng location = new LatLng(user.latitude, user.longitude);
                        if (mMap != null) {
                            marker = mMap.addMarker(new MarkerOptions().position(location).title(user.name).snippet(Doctorspecialty));
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.map_options, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Change the map type based on the user's selection.
            switch (item.getItemId()) {
                case R.id.normal_map:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                case R.id.hybrid_map:
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                case R.id.satellite_map:
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                case R.id.terrain_map:
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    **/
    private void addCustomMarkerFromURL() {


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
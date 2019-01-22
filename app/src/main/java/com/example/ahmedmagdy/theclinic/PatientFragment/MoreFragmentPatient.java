package com.example.ahmedmagdy.theclinic.PatientFragment;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.Adapters.MoreAdapter;
import com.example.ahmedmagdy.theclinic.DoctorFragments.DoctorProfileFragment;
import com.example.ahmedmagdy.theclinic.DoctorHome;
import com.example.ahmedmagdy.theclinic.HospitalFragment.HospitalProfileFragment;

import com.example.ahmedmagdy.theclinic.HospitalHome;
import com.example.ahmedmagdy.theclinic.PatientHome;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.AllHospitalActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;

import com.example.ahmedmagdy.theclinic.activities.SplashActivity;
import com.example.ahmedmagdy.theclinic.activities.StartCahtRoomFragment;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.example.ahmedmagdy.theclinic.map.DoctorMapFrag;
import com.example.ahmedmagdy.theclinic.map.UserLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MoreFragmentPatient extends Fragment {
    private FirebaseAuth mAuth;
    DatabaseReference databaseChat;
    String usertype;
    //gps
    public static ArrayList<UserLocation> mUserLocations = new ArrayList<>();


    public MoreFragmentPatient() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        mAuth = FirebaseAuth.getInstance();
        //run get data for map
        getAllDoctorsMap();
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getString(R.string.chat_room));
        titles.add(getString(R.string.doctor_map));
        titles.add(getString(R.string.Profile));

        titles.add(getString(R.string.hospitals));
        titles.add(getString(R.string.contact_us));
        titles.add(getString(R.string.rate_us));

        titles.add(getString(R.string.share));
        titles.add(getString(R.string.about));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null) {
            titles.add(getString(R.string.sign_in));
        } else {
            titles.add(getString(R.string.sign_out));
        }
        //words.add("Sign out");
        ArrayList<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.chat_room);
        icons.add(R.drawable.map_all);
        icons.add(R.drawable.ic_person);
        icons.add(R.drawable.ic_hospital_12);
        icons.add(R.drawable.ic_email);
        icons.add(R.drawable.rating);
        icons.add(R.drawable.ic_share_black_24dp);
        icons.add(R.drawable.ic_help);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat.keepSynced(true);
        if (user == null) {
            icons.add(R.drawable.icn_sign_in);
        } else {
            icons.add(R.drawable.icn_sign_out);
        }
        // icons.add(R.drawable.icn_sign_out);


        ListView listview = (ListView) rootView.findViewById(R.id.listView1);
        MoreAdapter adapter = new MoreAdapter(getActivity(), titles, icons);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        ChatRoomClicked();

                        break;
                    case 1:
                        inflateDocMapFragment();
                        break;
                    case 2:
                        profileClicked();
                        break;
                    case 3:
                        HospitalsClicked();
                        break;
                    case 4:
                        emailClicked();

                        break;
                    case 5:
                        rateClicked();
                        break;
                    case 6:
                        shareClicked();

                        break;
                    case 7:
                        about();
                        break;
                    case 8:
                        signOutClicked();
                        break;
                }
            }
        });


        return rootView;
    }


    private void HospitalsClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "2");
            startActivity(it);

        } else {
            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {

                    usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
                    if (usertype.equals("User")) {

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new AllHospitalfragment()).addToBackStack(null).commit();
                    } else if (usertype.equals("Doctor")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new AllHospitalfragment()).addToBackStack(null).commit();
                    } else if (usertype.equals("Hospital")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hospital,
                                new AllHospitalfragment()).addToBackStack(null).commit();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            databaseChat.addValueEventListener(postListener1);



        }

    }

    private void about() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.about_dialog);

        TextView cancel = dialog.findViewById(R.id.about_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void ChatRoomClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "2");
            startActivity(it);
        } else
        {  final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {

                    usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
                    if (usertype.equals("User")) {

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new StartCahtRoomFragment()).addToBackStack(null).commit();
                    } else if (usertype.equals("Doctor")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new StartCahtRoomFragment()).addToBackStack(null).commit();
                    } else if (usertype.equals("Hospital")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hospital,
                                new StartCahtRoomFragment()).addToBackStack(null).commit();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
        databaseChat.addValueEventListener(postListener1);

    }}

    private void profileClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "2");
            startActivity(it);
        } else {
            final ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {

                    usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);
                    if (usertype.equals("User")) {

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new UserProfileFragment()).addToBackStack(null).commit();
                    } else if (usertype.equals("Doctor")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new DoctorProfileFragment()).addToBackStack(null).commit();
                    } else if (usertype.equals("Hospital")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hospital,
                                new HospitalProfileFragment()).addToBackStack(null).commit();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                }
            };
            databaseChat.addValueEventListener(postListener1);


        }

    }


    private void rateClicked() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }  // handle Rate us

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getActivity().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    } // rate us helper method

    private void shareClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = getString(R.string.share_body_link);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, getString(R.string.share_title_chooser)));
    }  // handle share item

    private void emailClicked() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:Al-Souq@gmail.com"));  // only email apps should handle this
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    } // handle Mail item

    private void signOutClicked() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("comefrom", "2");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
        /**
         ////////////////delay for starting avtivity
         final Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
        @Override public void run() {
        // Do something after 5s = 5000ms
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("comefrom", "2");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
        }
        }, 100);

         }  // handle sign out item

         **/
    }

    //get all doc data
    private void getAllDoctorsMap() {
        if (UtilClass.isNetworkConnected(getContext())) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child("mapdb");
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("qerrrrrrrrrrry Count " + "" + dataSnapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserLocation post = postSnapshot.getValue(UserLocation.class);
                        mUserLocations.add(post);
                        System.out.println("qerrrrrrrrrrry Get Data" + post.getCmname());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            query.addValueEventListener(valueEventListener);

        } else {
            Toast.makeText(getContext(), getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
    }

    private void inflateDocMapFragment() {
        DoctorMapFrag fragment = DoctorMapFrag.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("intent_user_locs", mUserLocations);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container_doc_menu, fragment, "User List");
        transaction.addToBackStack("User List");
        transaction.commit();

    }


}

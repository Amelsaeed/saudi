package com.example.ahmedmagdy.theclinic.PatientFragment;


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

import com.example.ahmedmagdy.theclinic.Adapters.MoreAdapter;
import com.example.ahmedmagdy.theclinic.PatientFragment.AllDoctorfragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.AllHospitalfragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.UserBookingFragment;
import com.example.ahmedmagdy.theclinic.PatientFragment.UserProfileFragment;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.AllHospitalActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.MapsActivity;
import com.example.ahmedmagdy.theclinic.activities.SplashActivity;
import com.example.ahmedmagdy.theclinic.activities.StartCahtRoom;
import com.example.ahmedmagdy.theclinic.map.DoctorMapFrag;
import com.example.ahmedmagdy.theclinic.map.UserLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MoreFragmentPatient extends Fragment {
    public static ArrayList<UserLocation> mUserLocations = new ArrayList<>();


    public MoreFragmentPatient() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        ArrayList<String> words = new ArrayList<String>();
        words.add("Map");

        words.add("Chat");
        words.add("Profile");
        words.add("Rate us");
        words.add("Share This App");
        words.add("Email us");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        words.add("Hospitals");
        if (user == null) {
            words.add("Log in");
        } else {
            words.add("Sign out");
        }
        //words.add("Sign out");
        ArrayList<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.map_all);

        icons.add(R.drawable.chat_room);
        icons.add(R.drawable.ic_person);
        icons.add(R.drawable.rating);
        icons.add(R.drawable.ic_share_black_24dp);
        icons.add(R.drawable.ic_email);

        icons.add(R.drawable.ic_hospital_12);
        if (user == null) {
            icons.add(R.drawable.icn_sign_in);
        } else {
            icons.add(R.drawable.icn_sign_out);
        }
        // icons.add(R.drawable.icn_sign_out);


        ListView listview = (ListView) rootView.findViewById(R.id.listView1);
        MoreAdapter adapter = new MoreAdapter(getActivity(), words, icons);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mapClicked();
                        break;
                    case 1:
                        ChatRoomClicked();
                        break;
                    case 2:
                        profileClicked();
                        break;
                    case 3:
                        rateClicked();
                        break;
                    case 4:
                        shareClicked();
                        break;
                    case 5:
                        emailClicked();
                        break;
                    case 6:
                        HospitalsClicked();
                        break;
                    case 7:
                        signOutClicked();
                        break;
                }
            }
        });


        return rootView;
    }

    private void mapClicked() {
        DoctorMapFrag fragment = DoctorMapFrag.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("intent_user_locs",mUserLocations);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                fragment, "User List").addToBackStack("User List").commit();

    }

    private void HospitalsClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "2");
            startActivity(it);

        } else {
            Intent intent = new Intent(getActivity(), AllHospitalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        }

    }

    private void ChatRoomClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "2");
            startActivity(it);
        } else
            startActivity(new Intent(getActivity(), StartCahtRoom.class));
    }

    private void profileClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "2");
            startActivity(it);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    new UserProfileFragment()).addToBackStack(null).commit();
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
}

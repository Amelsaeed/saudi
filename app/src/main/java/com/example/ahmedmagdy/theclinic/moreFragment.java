package com.example.ahmedmagdy.theclinic;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ahmedmagdy.theclinic.Adapters.MoreAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class moreFragment extends Fragment {


    public moreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        ArrayList<String> words=new ArrayList<String>();
        words.add("Map");
        words.add("Chat");
        words.add("Profile");
        words.add("Rate us");
        words.add("Share This App");
        words.add("Email us");
        words.add("Sign out");
        ArrayList<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.ic_place_black_24dp);
        icons.add(R.drawable.ic_person);
        icons.add(R.drawable.ic_chat_black_24dp);
        icons.add(R.drawable.ic_star_black_24dp);
        icons.add(R.drawable.ic_share_black_24dp);
        icons.add(R.drawable.ic_email);
        icons.add(R.drawable.ic_supervisor_account_black_24dp);



        ListView listview =(ListView)rootView.findViewById(R.id.listView1);
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
                        profileClicked();
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
                        signOutClicked();
                        break;
                }
            }
        });


        return rootView;
    }
    private void mapClicked() {
    }

    private void profileClicked(){}

    private void rateClicked(){
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }  // handle Rate us

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url,getActivity().getPackageName())));
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

    private void shareClicked(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = getString(R.string.share_body_link);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, getString(R.string.share_title_chooser)));
    }  // handle share item

    private void emailClicked(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:Al-Souq@gmail.com"));  // only email apps should handle this
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    } // handle Mail item

    private void signOutClicked(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }  // handle sign out item


}

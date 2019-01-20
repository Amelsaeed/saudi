package com.example.ahmedmagdy.theclinic.DoctorFragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.Adapters.MoreAdapter;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.AllHospitalActivity;
import com.example.ahmedmagdy.theclinic.activities.LoginActivity;
import com.example.ahmedmagdy.theclinic.activities.MapsActivity;
import com.example.ahmedmagdy.theclinic.activities.StartCahtRoom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MoreFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = getLayoutInflater().inflate(R.layout.fragment_more,container,false);

        ListView listView  = rootView.findViewById(R.id.listView1);

        ArrayList<String> titles = new ArrayList<>();
        titles.add(getString(R.string.chat_room));
        titles.add(getString(R.string.doctor_sites));
        titles.add(getString(R.string.hospitals));
        titles.add(getString(R.string.rate_us));
        titles.add(getString(R.string.contact_us));
        titles.add(getString(R.string.share));
        titles.add(getString(R.string.about));
        titles.add(getString(R.string.sign_out));

        ArrayList<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.chat_room);
        icons.add(R.drawable.map_all);
        icons.add(R.drawable.ic_hospital);
        icons.add(R.drawable.rating);
        icons.add(R.drawable.ic_email);
        icons.add(R.drawable.ic_share_black_24dp);

        icons.add(R.drawable.ic_help);
        icons.add(R.drawable.icn_sign_out);

        MoreAdapter adapter = new MoreAdapter(getActivity(), titles, icons);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
             switch (position){
                 case 0: openChatRoom();
                         break;
                 case 1: getActivity().getSupportFragmentManager().beginTransaction()
                         .replace(R.id.fragment_container,
                         new MapsActivity()).addToBackStack(null).commit();
                         break;
                 case 2: hospitals();
                         break;
                 case 3:rateUs();
                     break;
                 case 4:contactUs();
                     break;
                 case 5: share();
                         break;
                 case 6: about();
                         break;
                 case 7: signOut();
                     break;

             }
            }
        });

        return rootView;
    }

    private void hospitals() {
        Intent intent = new Intent(getActivity(), AllHospitalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    private void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:Al-Souq@gmail.com"));  // only email apps should handle this
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }

    }


    private void rateUs() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private void openChatRoom() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
            it.putExtra("comefrom", "1");
            startActivity(it);}
        else{
            startActivity(new Intent(getActivity(), StartCahtRoom.class));
        }



    }

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
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = getString(R.string.share_body_link);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, getString(R.string.share_title_chooser)));
    }

    private void about(){
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



    private void signOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("comefrom", "1");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }
}

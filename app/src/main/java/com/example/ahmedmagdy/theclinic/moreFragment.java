package com.example.ahmedmagdy.theclinic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ahmedmagdy.theclinic.Adapters.MoreAdapter;

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


        ListView listview =(ListView)rootView.findViewById(R.id.listView1);
        MoreAdapter adapter = new MoreAdapter(getActivity(), words);

        listview.setAdapter(adapter);


        return rootView;
    }

}

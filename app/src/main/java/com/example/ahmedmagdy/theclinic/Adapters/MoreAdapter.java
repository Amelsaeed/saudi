package com.example.ahmedmagdy.theclinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.R;

import java.util.ArrayList;

public class MoreAdapter extends ArrayAdapter<String> {
    private final Context context;
    ArrayList<String> messages;
    int font;
    String g_name;


    public MoreAdapter(Context context, ArrayList<String> words) {
        super(context, 0, words);
        this.context = context;
        messages=words;
    }

    TextView message;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.more_list, parent, false);
        }


        message = (TextView) listItemView.findViewById(R.id.textView1);

        message.setText(messages.get(position));



        return listItemView;
    }
}

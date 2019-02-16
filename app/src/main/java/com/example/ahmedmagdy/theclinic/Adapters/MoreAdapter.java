package com.example.ahmedmagdy.theclinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.R;

import java.util.ArrayList;

public class MoreAdapter extends ArrayAdapter<String> {
    private final Context context;
    ArrayList<String> messages;
    ArrayList<Integer> icons;
    int font;
    String g_name;


    public MoreAdapter(Context context, ArrayList<String> words,ArrayList<Integer> icons) {
        super(context, 0, words);
        this.context = context;
        messages=words;
        this.icons = icons;
    }

    TextView message;
    ImageView imageIcon;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.more_list, parent, false);
        }


        message = (TextView) listItemView.findViewById(R.id.textView1);

        message.setText(messages.get(position));

        imageIcon = listItemView.findViewById(R.id.imageView_icon);

        imageIcon.setImageResource(icons.get(position));

        Animation animation = AnimationUtils.loadAnimation(context,R.anim.scale);
        listItemView.startAnimation(animation);

        return listItemView;
    }
}

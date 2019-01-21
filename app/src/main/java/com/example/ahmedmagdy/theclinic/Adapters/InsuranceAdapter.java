package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.OneWordClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AHMED MAGDY on 10/23/2018.
 */

public class InsuranceAdapter extends ArrayAdapter<OneWordClass> implements Filterable{


    private Activity context;
    private List<OneWordClass> timingList1;
    private List<OneWordClass> mSearchList;
    //private List<String> positioncolorList;
    //private List<BookingTimesClass> positioncolorList;


    public InsuranceAdapter(Activity context, List<OneWordClass> timingList1) {
        super((Context) context, R.layout.time_grid_item, timingList1);

        this.context = context;
        this.timingList1 = timingList1;

    }

    @NonNull
    @Override
    public View getView( int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
         View listViewItem = inflater.inflate(R.layout.time_grid_item, parent, false);

         TextView atime = listViewItem.findViewById(R.id.tv);
         CardView cardview=  listViewItem.findViewById(R.id.cv);



            OneWordClass onewordclass = timingList1.get(position);
            atime.setText(onewordclass.getWord());



        return listViewItem;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults resultsFilter = new FilterResults();
                final List<OneWordClass> resultsList = new ArrayList<>();
                if (mSearchList == null)
                    mSearchList = timingList1;
                if (charSequence != null) {
                    if (mSearchList != null && mSearchList.size() > 0) {
                        for (final OneWordClass word : mSearchList) {
                            if (word.getWord().toLowerCase().contains(charSequence.toString())
                                    ){
                                resultsList.add(word);
                            }

                        }
                    }
                    resultsFilter.values = resultsList;
                }
                return resultsFilter;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
       timingList1 = (ArrayList<OneWordClass>) filterResults.values;
       notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getCount() {
        return timingList1.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

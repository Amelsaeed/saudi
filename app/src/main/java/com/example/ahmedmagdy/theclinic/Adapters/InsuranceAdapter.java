package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.Notifications.Client;
import com.example.ahmedmagdy.theclinic.Notifications.Data;
import com.example.ahmedmagdy.theclinic.Notifications.MyResponse;
import com.example.ahmedmagdy.theclinic.Notifications.Sender;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.OneWordClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AHMED MAGDY on 10/23/2018.
 */

public class InsuranceAdapter extends ArrayAdapter<OneWordClass> {


    private Activity context;
    List<OneWordClass> timingList1;
    //private List<String> positioncolorList;
    //private List<BookingTimesClass> positioncolorList;


    public InsuranceAdapter(Activity context, List<OneWordClass> timingList1) {
        super((Context) context, R.layout.time_grid_item, timingList1);

        this.context = context;
        this.timingList1 = timingList1;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.time_grid_item, null, true);

        final TextView atime = (TextView) listViewItem.findViewById(R.id.tv);
        final CardView cardview= (CardView) listViewItem.findViewById(R.id.cv);





        final OneWordClass onewordclass = timingList1.get(position);
        atime.setText(onewordclass.getWord());






        return listViewItem;
    }


}

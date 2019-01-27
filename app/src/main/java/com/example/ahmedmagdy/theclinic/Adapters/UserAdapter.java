package com.example.ahmedmagdy.theclinic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ahmedmagdy.theclinic.Model.Chat;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.MessageActivity;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<RegisterClass> mUsers;
    private boolean ischat;
    String theLastMessage;
    DatabaseReference databaseChat;


    public UserAdapter(Context mContext, List<RegisterClass> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final RegisterClass user = mUsers.get(position);
        holder.username.setText(user.getCname());
        if (user.getCUri() != null) {
            Glide.with(mContext).load(user.getCUri()).into(holder.profile_image);
        } else {
            holder.profile_image.setImageResource(R.mipmap.ic_person);
        }
        /*if (ischat) {
         */
        lastMessage(user.getcId(), holder.last_msg);
       /* } else {
            holder.last_msg.setVisibility(View.GONE);
        }*/

      /*if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }*/
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseChat.keepSynced(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {

                        String DType = dataSnapshot1.child(user.getcId()).child("cType").getValue(String.class);
                        String DChatstart = dataSnapshot1.child(user.getcId()).child("cChatstart").getValue(String.class);
                        String DChatend = dataSnapshot1.child(user.getcId()).child("cChatend").getValue(String.class);
                        ////////////////*************************
                        if ((DChatstart != null)&&(DChatend != null)){
                            ///////////////current time chat////cal 3**********************
                            Calendar caldef = Calendar.getInstance();
                            SimpleDateFormat formatterdef = new SimpleDateFormat("yyyy_MM_dd");
                            String currentdatedef = formatterdef.format(caldef.getTime());

                            Calendar cal2 = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH:mm");
                            String currentdate = sdf.format(cal2.getTime());

//****************************start time chat////cal 1*************************
                            //  String a="17:00";  String b="18:00";
                            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy_MM_dd HH:mm");

                            Date date5 = null;
                            try {
                                date5 = formatter1.parse(currentdatedef + " " + DChatstart);

                                // date5 = formatter1.parse(a);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(date5);
                            ///////////////*ending time chat////cal 3**********************

                            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy_MM_dd HH:mm");
                            Date date6 = null;
                            try {
                                date6 = formatter2.parse(currentdatedef + " " + DChatend);
                                //   date6 = formatter2.parse(b);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal3 = Calendar.getInstance();
                            cal3.setTime(date6);


                            int timecomp1 = cal2.compareTo(cal1);
                            int timecomp2 = cal2.compareTo(cal3);

                            if ((timecomp1 >= 0) && (timecomp2 <= 0)) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("userid", user.getcId());
                                mContext.startActivity(intent);

                            } else {
                                Toast.makeText(mContext, "Doctor is available from " + DChatstart + " To " + DChatend, Toast.LENGTH_LONG).show();
                            }

///****************************past date*************************
                        }else{
                            Intent intent = new Intent(mContext, MessageActivity.class);
                            intent.putExtra("userid", user.getcId());
                            mContext.startActivity(intent);

                        }

                        ////////////******************************


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {    }
                };
                databaseChat.addValueEventListener(postListener1);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        TextView last_msg;


        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);


        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg) {

        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen() && chat.getSender().equals(userid)) {
                            theLastMessage = chat.getMessage();

                        }
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

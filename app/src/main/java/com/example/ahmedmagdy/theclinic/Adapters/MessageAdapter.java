package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ahmedmagdy.theclinic.Model.Chat;
import com.example.ahmedmagdy.theclinic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl, msgurl;


    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

    }



    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Chat chat = mChat.get(position);
        msgurl = chat.getImgURL();
        holder.img_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Log.d(TAG, "onClick: clicked on: ");
                Intent intent = new  Intent(mContext, OpenImage.class);
                    intent.putExtra("image_url", msgurl);
                    mContext.startActivity(intent);*/
                Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                loadPhoto(holder.img_show,width,height);
            }
        });
        holder.show_message.setText(chat.getMessage());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_person);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
        /////////////////////////////
        if (msgurl != null) {
            holder.img_show.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
            Glide.with(holder.img_show.getContext())
                    .load(msgurl)
                    .into(holder.img_show);
            //holder.txt_seen.setVisibility(View.GONE);
//            System.out.println("\n 2---MesAdap: Image url >> " + chat.getImgURL());
        } else {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.img_show.setVisibility(View.GONE);
            holder.show_message.setText(chat.getMessage());
        }

    }
    /////////// show photo profile ////////////////////////
    private void loadPhoto(ImageView imageView, int width, int height) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_dialoge_view_chat_image,
                (ViewGroup) dialog.findViewById(R.id.layout_root_view));
        ImageView image = (ImageView) v.findViewById(R.id.fullimageview);
        PhotoViewAttacher PV = new PhotoViewAttacher(image);
        //mixmumZoom
        PV.setMaximumScale(5);
        PV.update();
        image.setImageDrawable(imageView.getDrawable());
        image.getLayoutParams().height = height;
        image.getLayoutParams().width = width;
        image.requestLayout();
        dialog.setContentView(v);
        dialog.show();
    }
    /////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public ImageView img_show;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            img_show = (ImageView) itemView.findViewById(R.id.img_show);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
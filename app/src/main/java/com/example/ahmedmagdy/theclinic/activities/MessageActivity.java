package com.example.ahmedmagdy.theclinic.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ahmedmagdy.theclinic.Adapters.MessageAdapter;
import com.example.ahmedmagdy.theclinic.ChatRoomFragments.APIService;
import com.example.ahmedmagdy.theclinic.Model.Chat;
import com.example.ahmedmagdy.theclinic.Notifications.Client;
import com.example.ahmedmagdy.theclinic.Notifications.Data;
import com.example.ahmedmagdy.theclinic.Notifications.MyResponse;
import com.example.ahmedmagdy.theclinic.Notifications.Sender;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference, chatRef ,Activrefrance;
    CircleImageView StatusChat;
    TextView Active;
    ImageButton btn_send;
    EditText text_send;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoPickerRef;
    public static final int RC_PHOTO_PICKER = 2;

    MessageAdapter messageAdapter;
    List<Chat> mchat;
    private RecyclerView.Adapter mMediaAdapter;
    private RecyclerView.LayoutManager mMediaLayoutManager;
    RecyclerView recyclerView, mMedia;

    Intent intent;

    ValueEventListener seenListener;
    private ProgressDialog progressDialog;
    String userid;
    APIService apiService;
    FirebaseAuth mAuth;
    DatabaseReference databaseChat, databaseDoctor;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        Activrefrance = FirebaseDatabase.getInstance().getReference("Doctordb");


        Activrefrance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DoctorFirebaseClass doctorFirebaseClass = dataSnapshot.child(userid).getValue(DoctorFirebaseClass.class);

                if (doctorFirebaseClass.getstatus()){
                    StatusChat.setVisibility(View.VISIBLE);
                    Active.setText("Active Now");
                }else {
                    StatusChat.setVisibility(View.GONE);
                    Active.setText("Ofline Now");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        StatusChat = (CircleImageView) findViewById(R.id.status_doctor_chat);
        Active = (TextView) findViewById(R.id.user_active);
        Toolbar toolbar = findViewById(R.id.toolbarchat);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                loadPhoto(profile_image, width, height);
            }
        });
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        Button mAddMedia = findViewById(R.id.addMedia);
        /*Select image from gallery*/
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        /*Firebase Storage*/
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotoPickerRef = mFirebaseStorage.getReference().child("chat_photos");
        /*Firebase Storage*/
//----------------------------------------------------------------
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (getSupportActionBar() != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(false);
            ab.setDisplayShowTitleEnabled(false);

        }
//=============================================================

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = mAuth.getInstance().getCurrentUser();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(userid);
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RegisterClass user = dataSnapshot.getValue(RegisterClass.class);

                ///////////////////////////////////////////// to get Name Users //////////////////////////////////////////////

                ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {

                        String UsreName = dataSnapshot1.child(userid).child("cname").getValue(String.class);
                        username.setText(UsreName);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };
                databaseChat.addValueEventListener(postListener1);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (user.getCUri() != null) {
                    Glide.with(getApplicationContext()).load(user.getCUri()).into(profile_image);
                } else {
                    profile_image.setImageResource(R.mipmap.ic_person);
                }
                readMesagges(fuser.getUid(), userid, user.getCname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    /////////// show photo profile ////////////////////////
    private void loadPhoto(ImageView imageView, int width, int height) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_fullimage_dialoge,
                (ViewGroup) findViewById(R.id.layout_root));
        CircleImageView image = (CircleImageView) layout.findViewById(R.id.fullimage);
        image.setImageDrawable(imageView.getDrawable());
        image.getLayoutParams().height = height;
        image.getLayoutParams().width = width;
        image.requestLayout();
        dialog.setContentView(layout);
        dialog.show();
    }

    /////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * @Override public boolean onCreateOptionsMenu(Menu menu) {
     * MenuInflater inflater = getMenuInflater();
     * inflater.inflate(R.menu.back, menu);
     * return true;
     * }
     * @Override public boolean onOptionsItemSelected(MenuItem item) {
     * switch (item.getItemId()) {
     * case R.id.back:
     * <p>
     * startActivity(new Intent(MessageActivity.this,AllDoctorActivity.class));
     * return true;
     * default:
     * return super.onOptionsItemSelected(item);
     * }
     * }
     **/
/*

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            progressDialog.setMessage("Sending photo........");
            progressDialog.show();
            notify = true;
            Uri selectedImageUri = data.getData();
            final StorageReference imageRef = mPhotoPickerRef.child(selectedImageUri.getLastPathSegment());
            if (UtilClass.isNetworkConnected(MessageActivity.this)) {
                imageRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("Photo sent.........");
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final Uri downloadUrl = uri;
                                String url = downloadUrl.toString();
                                sendMessage(fuser.getUid(), userid, null, url);
                                progressDialog.dismiss();
                            }

                        });
                    }
                });
            } else {
                Toast.makeText(MessageActivity.this, getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }


        }
    }

    /*Start Send photo message*/
    private void sendMessage(String sender, final String receiver, String message, String imgurl) {

        if (UtilClass.isNetworkConnected(MessageActivity.this)) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", null);
            hashMap.put("imgURL", imgurl);
            hashMap.put("isseen", false);


            reference.child("Chats").push().setValue(hashMap);
            if (reference.child("isseen").equals(false)) {
                chatRef.setValue(userid);
            }

            // add user to chat fragment
            chatRef = FirebaseDatabase.getInstance().getReference("Chatlist");

            chatRef.child(fuser.getUid())
                    .child(userid).child("id").setValue(userid);
            chatRef.child(userid)
                    .child(fuser.getUid()).child("id").setValue(fuser.getUid());

            final String msg = "New Photo Message";

            reference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    RegisterClass user = dataSnapshot.getValue(RegisterClass.class);
                    if (notify) {
                        sendNotifiaction(receiver, user.getCname(), msg);
                        System.out.println("2-- send notifi :" + msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(this, getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
    }
    /*End Send photo message*/


    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}
                    , 1);
        }
    }

    private void seenMessage(final String userid) {

        if (UtilClass.isNetworkConnected(MessageActivity.this)) {
            reference = FirebaseDatabase.getInstance().getReference("Chats");
            seenListener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, getString(R.string.network_connection_msg), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String sender, final String receiver, String message) {

        if (UtilClass.isNetworkConnected(MessageActivity.this)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("isseen", false);

            reference.child("Chats").push().setValue(hashMap);
            if (reference.child("isseen").equals(false)) {
                chatRef.setValue(userid);
            }

            // add user to chat fragment
            chatRef = FirebaseDatabase.getInstance().getReference("Chatlist");

            chatRef.child(fuser.getUid())
                    .child(userid).child("id").setValue(userid);
            chatRef.child(userid)
                    .child(fuser.getUid()).child("id").setValue(fuser.getUid());


            final String msg = message;

            reference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    RegisterClass user = dataSnapshot.getValue(RegisterClass.class);
                    if (notify) {
                        sendNotifiaction(receiver, user.getCname(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {


        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_person,
                            username + ": " + message, "New Message", userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                            mchat.add(chat);
                        }
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*   private void currentUser(String userid) {
           SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
           editor.putString("currentuser", userid);
           editor.apply();
       }

       private void status(String status){
           reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

           HashMap<String, Object> hashMap = new HashMap<>();
           hashMap.put("status", status);

           reference.updateChildren(hashMap);
       }

       @Override
       protected void onResume() {
           super.onResume();
           status("online");
           currentUser(userid);
       }

       @Override
       protected void onPause() {
           super.onPause();
           reference.removeEventListener(seenListener);
           status("offline");
           currentUser("none");
       }*/
    @Override
    public void onResume() {
        super.onResume();
        databaseDoctor.child(fuser.getUid()).child("status").setValue(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        databaseDoctor.child(fuser.getUid()).child("status").setValue(false);

    }
}


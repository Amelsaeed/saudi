package com.example.ahmedmagdy.theclinic.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ahmedmagdy.theclinic.ChatRoomFragments.ChatsFragment;
import com.example.ahmedmagdy.theclinic.Model.Chat;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.RegisterClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartCahtRoomFragment extends Fragment {
    CircleImageView profile_image;
    ImageView StatusProfile;
    TextView username;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    View rootView;
    DatabaseReference reference, databaseChat, databaseDoctor;
    Context thiscontext;

    public StartCahtRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_start_caht_room, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        StatusProfile = (ImageView) rootView.findViewById(R.id.status_profile);
        profile_image = rootView.findViewById(R.id.profile_image);
        username = rootView.findViewById(R.id.username);
        thiscontext = container.getContext();

        mAuth = FirebaseAuth.getInstance();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("Doctordb");
        databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        reference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RegisterClass user = dataSnapshot.getValue(RegisterClass.class);
                //   final String usertype = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);

                if (user.getCUri() != null) {
                    Glide.with(getActivity().getApplicationContext()).load(user.getCUri()).into(profile_image);
                } else {
                    profile_image.setImageResource(R.mipmap.ic_person);

                }
                profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Display display = getActivity().getWindowManager().getDefaultDisplay();
                        int width = display.getWidth();
                        int height = display.getHeight();
                        loadPhoto(profile_image, width, height);
                    }
                });


                ///////////////////////////////////////////// to get Name Users //////////////////////////////////////////////

                ValueEventListener postListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {

                        String UsreName = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("cname").getValue(String.class);
                        username.setText(UsreName);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };
                databaseChat.addValueEventListener(postListener1);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
        final ViewPager viewPager = rootView.findViewById(R.id.view_pager);


        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(mAuth.getCurrentUser().getUid()) && !chat.isIsseen()) {
                        unread++;
                    }
                }

                if (unread == 0) {
                    viewPagerAdapter.addFragment(new ChatsFragment(),  getString(R.string.chat_patient));
                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ")"+  getString(R.string.chat_patient));
                }

                /*viewPagerAdapter.addFragment(new UsersFragment(), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
*/
                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
        return rootView;
    }

    /////////// show photo profile ////////////////////////
    private void loadPhoto(ImageView imageView, int width, int height) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) thiscontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_fullimage_dialoge,
                (ViewGroup) rootView.findViewById(R.id.layout_root));
        CircleImageView image = (CircleImageView) layout.findViewById(R.id.fullimage);
        image.setImageDrawable(imageView.getDrawable());
        image.getLayoutParams().height = height;
        image.getLayoutParams().width = width;
        image.requestLayout();
        dialog.setContentView(layout);
        dialog.show();
    }
/////////////////////////////////////////////////////////////////////////////

    private boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                // change this code beacuse your app will crash
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }

        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    /*  private void status(String status) {
          reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

          HashMap<String, Object> hashMap = new HashMap<>();
          hashMap.put("status", status);

          reference.updateChildren(hashMap);
      }

      @Override
      protected void onResume() {
          super.onResume();
          status("online");
      }

      @Override
      protected void onPause() {
          super.onPause();
          status("offline");
      }*/

}

package com.example.ahmedmagdy.theclinic.PatientFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.HospitalHome;
import com.example.ahmedmagdy.theclinic.Notifications.Token;
import com.example.ahmedmagdy.theclinic.PatientHome;
import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.RegestrationPathActivity;
import com.example.ahmedmagdy.theclinic.activities.SplashActivity;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginAFragment extends Fragment {
    private TextView singin, create, forget;
    private EditText editTextemail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseUser fuser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        editTextemail = view.findViewById(R.id.edit_email);
        editTextPassword =  view.findViewById(R.id.edit_password);
        singin =  view.findViewById(R.id.getstarted);
        create =  view.findViewById(R.id.create);
        forget =  view.findViewById(R.id.forget);
        progressBar =  view.findViewById(R.id.progressbar);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), RegestrationPathActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             getActivity().finish();
                startActivity(it);

            }
        });

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // to recover your password
                // forgetPassword();
                resetPasswordRequest();
            }
        });

        // to keep user logged in
        //  initAuthStateListener();
        return view;
    }
    private void userLogin() {
        String mEmail = editTextemail.getText().toString().trim();
        String mPassword = editTextPassword.getText().toString().trim();

        if (mEmail.isEmpty()) {
            editTextemail.setError(getString(R.string.email_is_required));
            editTextemail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            editTextemail.setError(getString(R.string.please_enter_a_valid_email));
            editTextemail.requestFocus();
            return;
        }

        if (mPassword.length() < 6) {
            editTextPassword.setError(getString(R.string.minimum_length_of_password_should_be_6));
            editTextPassword.requestFocus();
            return;
        }

        if (mPassword.isEmpty()) {
            editTextPassword.setError(getString(R.string.password_is_required));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
// to login using email and password
        if (UtilClass.isNetworkConnected(getContext())) {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        //Log.d(TAG, "SIGNIN SUCCESS");
                        Toast.makeText(getActivity(), R.string.signin_sccess, Toast.LENGTH_SHORT).show();
                        /** Intent intend= new Intent(LoginActivity.this, AllDoctorActivity.class);
                         intend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         finish();
                         startActivity(intend);**/
                        /*  updateToken(FirebaseInstanceId.getInstance().getToken());*/

                        Intent iii = new Intent(getActivity(), SplashActivity.class);
                        iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().finish();
                   startActivity(iii);
                        getActivity().finish();

                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        // Log.e(TAG, task.getException().getMessage());
                        //Log.e(TAG, "SIGNIN ERROR");
                        // Toast.makeText(LoginActivity.this, "SIGNIN ERROR", Toast.LENGTH_SHORT).show();

                    }
                }

            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_connection_msg), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

  /*  private void getallData() {
        DatabaseReference databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");

        // private void getallData();
        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);


                if (usertype.equals("User")) {
                    Intent iii = new Intent(LoginActivity.this, SplashActivity.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(iii);
                    finish();
                } else if (usertype.equals("Doctor")) {
                    Intent iii = new Intent(LoginActivity.this, SplashActivity.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(iii);
                    finish();
                } else {
                    Intent iii = new Intent(LoginActivity.this, SplashActivity.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(iii);
                    finish();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat.addValueEventListener(postListener1);
    }*/

    // to keep user logged when you leave app
    private void initAuthStateListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            getallData12();
                /*    Intent iii = new Intent(LoginActivity.this, SplashActivity.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(iii);*/
            // Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {

            // User is signed out
            // Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }


    private void getallData12() {

        DatabaseReference databaseChat = FirebaseDatabase.getInstance().getReference("ChatRoom");
        //**************************************************//
        // private void getallData();
        final ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                String usertype = dataSnapshot1.child(mAuth.getCurrentUser().getUid()).child("ctype").getValue(String.class);


                if(usertype .equals("User") ) {
                    Intent iii= new Intent(getActivity(),PatientHome.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().onBackPressed();
                            startActivity(iii);
                    getActivity().onBackPressed();
                }else if(usertype .equals("Doctor")){
                    Intent iii= new Intent(getActivity(),PatientHome.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().onBackPressed();
                    startActivity(iii);
                    getActivity().onBackPressed();
                }else if(usertype .equals("Hospital")){
                    Intent iii= new Intent(getActivity(),HospitalHome.class);
                    iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().onBackPressed();
                    startActivity(iii);
                    getActivity().onBackPressed();
                }else{

                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseChat .addValueEventListener(postListener1);
    }

/*
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
*/


    private void displayResetPasswordDialog(String email) {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.reset_password_dialog_title));
        alertDialog.setMessage(getString(R.string.sending_email));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void resetPasswordRequest() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog);
        final EditText editTextemail = (EditText) dialog.findViewById(R.id.edit_email_tv);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_tv);
        TextView submit = (TextView) dialog.findViewById(R.id.submit_tv);
        final TextView errorMessage = (TextView) dialog.findViewById(R.id.error_message_tv);

        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mEmail = editTextemail.getText().toString().trim();

                if (mEmail.isEmpty()) {
                    editTextemail.setError(getString(R.string.email_is_required));
                    editTextemail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    editTextemail.setError(getString(R.string.please_enter_a_valid_email));
                    editTextemail.requestFocus();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
// to login using email and password
                if (UtilClass.isNetworkConnected(getContext())) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        displayResetPasswordDialog(mEmail);
                                        dialog.dismiss();
                                    } else {
                                        errorMessage.setText(getString(R.string.reset_password_dialog_title));
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), R.string.please_check_the_network_connection, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    }
package com.example.ahmedmagdy.theclinic.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ahmedmagdy.theclinic.R;

public class RegestrationPathActivity extends AppCompatActivity {
    ImageView gotouser,gotoudoctor,gotohospital;
    String selector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration_path);

        gotouser = (ImageView) findViewById(R.id.Pataint_logo_r);
        gotoudoctor = (ImageView) findViewById(R.id.doctor_logo_r);
        gotohospital = (ImageView) findViewById(R.id.hospital_logo_r);



        gotouser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uIntent = new Intent(RegestrationPathActivity.this, RegisterPatientActivity.class);
                uIntent.putExtra("selector", "User");
                uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uIntent);
                //finish();
            }
        });
        gotoudoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uIntent = new Intent(RegestrationPathActivity.this, RegisterDoctorActivity.class);
                uIntent.putExtra("selector", "Doctor");
                uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uIntent);
                //finish();
            }
        });
        gotohospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uIntent = new Intent(RegestrationPathActivity.this, RegisterHospitalActivity.class);
                uIntent.putExtra("selector", "Hospital");
                uIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uIntent);
                //finish();
            }
        });
    }
}

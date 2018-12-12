package com.example.ahmedmagdy.theclinic.classes;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class My_self extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        registerActivityLifecycleCallbacks(new FirebaseDatabaseConnectionHandler());
    }
}


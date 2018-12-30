package com.example.ahmedmagdy.theclinic.classes;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class saudi extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        registerActivityLifecycleCallbacks(new FirebaseDatabaseConnectionHandler());
    }
}


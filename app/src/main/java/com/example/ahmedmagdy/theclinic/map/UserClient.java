package com.example.ahmedmagdy.theclinic.map;

import android.app.Application;

import com.example.ahmedmagdy.theclinic.classes.DoctorFirebaseClass;

public class UserClient extends Application {

    private DoctorFirebaseClass user = null;

    public DoctorFirebaseClass getUser() {
        return user;
    }

    public void setUser(DoctorFirebaseClass user) {
        this.user = user;
    }
}

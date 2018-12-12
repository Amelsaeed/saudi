package com.example.ahmedmagdy.theclinic.classes;

import java.util.Objects;

public class NoteClass {
    private String cId;
    private String cText;
    private String cUserId;
    private String cDoctorId;

    private String cDate;

    public NoteClass(){}

    public NoteClass(String cId, String cText, String cUserId, String cDoctorId, String cDate) {
        this.cId = cId;
        this.cText = cText;
        this.cUserId = cUserId;
        this.cDoctorId = cDoctorId;
        this.cDate = cDate;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcText() {
        return cText;
    }

    public void setcText(String cText) {
        this.cText = cText;
    }

    public String getcUserId() {
        return cUserId;
    }

    public void setcUserId(String cUserId) {
        this.cUserId = cUserId;
    }

    public String getcDoctorId() {
        return cDoctorId;
    }

    public void setcDoctorId(String cDoctorId) {
        this.cDoctorId = cDoctorId;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public Boolean canView(String id) {
        return getcUserId().equals(id) || getcDoctorId().equals(id);
    }

    public Boolean isOwner(String id) {
        return getcDoctorId().equals(id);
    }
}

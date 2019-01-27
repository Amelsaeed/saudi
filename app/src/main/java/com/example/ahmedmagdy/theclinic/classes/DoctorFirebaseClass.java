package com.example.ahmedmagdy.theclinic.classes;

import java.io.Serializable;

/**
 * Created by AHMED MAGDY on 9/10/2018.
 */

public class DoctorFirebaseClass implements Serializable {
    private  String cType;
    private  String cEmail;

    public String cId;
    private String cName;
    private String cSpecialty;
    private String cCity;
    private String cUri;
    private String cUriID;
    private String cUriWP;

    private String cDegree;
    private String cPhone;
    private String cPrice;
    private String cDiscount;
    private String cTime;
    private String cAbout;
    private String cInsurance;
    public Boolean checked;

    private String cHospitalName;
    private String cHospitalID;

    private String cGandr;
    private Boolean cbookingtypestate;
    private Boolean status;

    public DoctorFirebaseClass(){}

    public DoctorFirebaseClass(String cId, String cName, String cInsurance, String cCity, String cSpecialty,
                               String cEmail, String cType,String cPhone,String cUri,String cUriID,String cUriWP,String cHospitalName,String cHospitalID,String cGandr,Boolean cbookingtypestate ,Boolean status) {
        this.cId = cId;
        this.cName = cName;
        this.cInsurance = cInsurance;
        this.cCity = cCity;
        this.cSpecialty = cSpecialty;
        this.cEmail = cEmail;
        this.cType = cType;
        this.cPhone = cPhone;
        this.cUri = cUri;
        this.cUriID = cUriID;
        this.cUriWP = cUriWP;
        this.cHospitalName = cHospitalName;
        this.cHospitalID = cHospitalID;
        this.cGandr = cGandr;
        this.cbookingtypestate = cbookingtypestate;
        this.status = status;
    }

    public DoctorFirebaseClass(String cId, String cName, String cInsurance, String cCity, String cSpecialty,
                               String cEmail, String cType,String cPhone,String cUri,String cUriID,String cUriWP) {
        this.cId = cId;
        this.cName = cName;
        this.cInsurance = cInsurance;
        this.cCity = cCity;
        this.cSpecialty = cSpecialty;
        this.cEmail = cEmail;
        this.cType = cType;
        this.cPhone = cPhone;
        this.cUri = cUri;
        this.cUriID = cUriID;
        this.cUriWP = cUriWP;
    }


    public DoctorFirebaseClass(String cId, String cName, String cSpecialty, String cCity, String cUri,
                               String cDegree,String cPhone,String cPrice,String cTime,String cAbout , Boolean checked) {
        this.cId = cId;
        this.cName = cName;
        this.cSpecialty = cSpecialty;
        this.cCity = cCity;
        this.cUri = cUri;
        this.cDegree = cDegree;
        this.cPhone = cPhone;
        this.cPrice = cPrice;
        this.cTime = cTime;
        this.cAbout = cAbout;
        this.checked = checked;

    }


    public DoctorFirebaseClass(String cId, String cName,String cPhone,  String cCity, String cSpecialty,String cEmail,String cType) {
        this.cId = cId;
        this.cName = cName;
        this.cPhone = cPhone;
        this.cCity = cCity;
        this.cSpecialty = cSpecialty;
        this.cEmail = cEmail;
        this.cType = cType;

        this.cUri = cUri;
        this.cDegree = cDegree;
        this.cPrice = cPrice;
        this.cTime = cTime;
        this.cAbout = cAbout;
        this.checked = checked;

    }
  //                                 DID, DName, DSpecialty,                DCity,          DUri,            DInsurance,   DDegree        ,DPrice,checked,HospitalID,DType,DType,bookingtype

    public DoctorFirebaseClass(String cId, String cName, String cSpecialty, String cCity, String cUri,String cInsurance,String cDegree,String cPrice,Boolean checked,String cHospitalID,String cType,String cAbout) {
        this.cId = cId;
        this.cName = cName;
        this.cSpecialty = cSpecialty;
        this.cCity = cCity;
        this.cUri = cUri;
        this.cDegree = cDegree;
        this.cInsurance = cInsurance;
        this.cPrice = cPrice;
        this.cTime = cTime;

        this.checked = checked;
        this.cHospitalID = cHospitalID;
        this.cType = cType;
        this.cAbout = cAbout;
    }
    public DoctorFirebaseClass(String cId, String cName, String cSpecialty, String cCity, String cUri,String cInsurance,String cDegree,String cPrice,Boolean checked,String cHospitalID,String cType,String cAbout,Boolean cbookingtypestate , Boolean status) {
        this.cId = cId;
        this.cName = cName;
        this.cSpecialty = cSpecialty;
        this.cCity = cCity;
        this.cUri = cUri;
        this.cDegree = cDegree;
        this.cInsurance = cInsurance;
        this.cPrice = cPrice;
        this.cTime = cTime;

        this.checked = checked;
        this.cHospitalID = cHospitalID;
        this.cType = cType;
        this.cAbout = cAbout;
        this.cbookingtypestate = cbookingtypestate;
        this.status = status ;

    }



    public String getcHospitalName() {
        return cHospitalName;
    }

    public void setcHospitalName(String cHospitalName) {
        this.cHospitalName = cHospitalName;
    }

    public String getcGandr() {
        return cGandr;
    }

    public void setcGandr(String cGandr) {
        this.cGandr = cGandr;
    }

    public String getcHospitalID() {
        return cHospitalID;
    }

    public void setcHospitalID(String cHospitalID) {
        this.cHospitalID = cHospitalID;
    }

    public String getcUriID() {
        return cUriID;
    }

    public void setcUriID(String cUriID) {
        this.cUriID = cUriID;
    }

    public String getcUriWP() {
        return cUriWP;
    }

    public void setcUriWP(String cUriWP) {
        this.cUriWP = cUriWP;
    }

    public String getcInsurance() {
        return cInsurance;
    }

    public void setcInsurance(String cInsurance) {
        this.cInsurance = cInsurance;
    }

    public String getcType() {
        return cType;
    }

    public void setcType(String cType) {
        this.cType = cType;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public void setcSpecialty(String cSpecialty) {
        this.cSpecialty = cSpecialty;
    }

    public void setcCity(String cCity) {
        this.cCity = cCity;
    }

    public void setcUri(String cUri) {
        this.cUri = cUri;
    }

    public String getcName() {
        return cName;
    }

    public String getcSpecialty() {
        return cSpecialty;
    }

    public String getcCity() {
        return cCity;
    }

    public String getcUri() {
        return cUri;
    }


    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcDegree() {
        return cDegree;
    }

    public void setcDegree(String cDegree) {
        this.cDegree = cDegree;
    }

    public String getcPhone() {
        return cPhone;
    }

    public void setcPhone(String cPhone) {
        this.cPhone = cPhone;
    }

    public String getcPrice() {
        return cPrice;
    }

    public void setcPrice(String cPrice) {
        this.cPrice = cPrice;
    }

    public void setcDiscount(String cDiscount) {
        this.cDiscount = cDiscount;
    }
    public String getcDiscount() {
        if (this.cDiscount == null){
         cDiscount = "0";
        }

        return cDiscount;


    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    public String getcAbout() {
        return cAbout;
    }

    public void setcAbout(String cAbout) {
        this.cAbout = cAbout;
    }

    public String getcId() {
        return cId;
    }

    public Boolean getChecked() {
        if (checked == null)
            return false;
        else
            return checked;
    }


    public void setChecked(Boolean checked) {
        this.checked = checked;
    }




    public Boolean getCbookingtypestate() {
        if (cbookingtypestate == null)
            return false;
        else
            return cbookingtypestate;
    }

    public void setCbookingtypestate(Boolean cbookingtypestate) {
        this.cbookingtypestate = cbookingtypestate;
    }





    public Boolean getstatus() {
        if (status == null)
            return false;
        else
            return status;
    }

    public void setstatus(Boolean status) {
        this.status = status;
    }


}


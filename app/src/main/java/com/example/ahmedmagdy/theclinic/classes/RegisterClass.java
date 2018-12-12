package com.example.ahmedmagdy.theclinic.classes;

/**
 * Created by AHMED MAGDY on 9/23/2018.
 */

public class RegisterClass {
    private String cId;
    private String cname;
    private String cphone;
    private String ccity;
    private String cbirthday;
    private String cemail;
    private String ctype;
    private String cUri;
    private String cInsurance;
    private String cspecialty;




  //  Id,mName, mInsurance, mCity, mSpecialty, mEmail, mtype,mPhone
    public RegisterClass() {}

    public RegisterClass(String cId, String cname, String cInsurance, String ccity, String cbirthday, String cemail, String ctype,String cUri) {
        this.cId = cId;
        this.cname = cname;
        this.cInsurance = cInsurance;
        this.ccity = ccity;
        this.cbirthday = cbirthday;
        this.cemail = cemail;
        this.ctype = ctype;
        this.cUri = cUri;
    }

    public RegisterClass(String cId, String cname, String cInsurance, String ccity, String cbirthday, String cemail, String ctype) {
        this.cId = cId;
        this.cname = cname;
        this.cInsurance = cInsurance;
        this.ccity = ccity;
        this.cbirthday = cbirthday;
        this.cemail = cemail;
        this.ctype = ctype;
        this.cUri = cUri;
    }
    public RegisterClass(String cId, String cname, String cInsurance, String ccity, String cemail, String ctype) {
        this.cId = cId;
        this.cname = cname;
        this.cInsurance = cInsurance;
        this.ccity = ccity;
        this.cemail = cemail;
        this.ctype = ctype;
    }

    public String getcInsurance() {
        return cInsurance;
    }

    public String getCspecialty() {
        return cspecialty;
    }

    public void setCspecialty(String cspecialty) {
        this.cspecialty = cspecialty;
    }

    public void setcInsurance(String cInsurance) {
        this.cInsurance = cInsurance;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getCcity() {
        return ccity;
    }

    public void setCcity(String ccity) {
        this.ccity = ccity;
    }

    public String getCbirthday() {
        return cbirthday;
    }

    public void setCbirthday(String cbirthday) {
        this.cbirthday = cbirthday;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getCUri() {
        return cUri;
    }

    public void setCUri(String cUri) {
        this.cUri = cUri;
    }
}

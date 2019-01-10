package com.example.ahmedmagdy.theclinic.classes;

/**
 * Created by AHMED MAGDY on 10/24/2018.
 */
//timesid, patientname, patientage, mDate, picuri
public class BookingTimesClass {
    private String ctid;
    private String ctname;
    private String ctage;
    private String ctdate;
    private String ctAddress;
    private String ctStartTime;
    private String ctEndTime;



    private String ctPeriod;
    private String ctpicuri;
    private Boolean checked;

    private String cttimeid;
    private String ctbookingdate;
    private String ctArrangement;
    private String ctphone;
    private String ctSpc;

    private int ctposition;



    public  BookingTimesClass(){}

    ///////////////////////////////userid, patientName, patientBirthday, mDate, DoctorAddress,onewordclass.getWord() , picuri,timeID,datedmy

    public BookingTimesClass(String ctid, String ctname, String ctage, String ctdate,String ctAddress, String ctStartTime, String ctEndTime, String ctPeriod, String ctpicuri,String cttimeid,String ctbookingdate,int ctposition) {
        this.ctid = ctid;
        this.ctname = ctname;
        this.ctage = ctage;
        this.ctdate = ctdate;
        this.ctAddress = ctAddress;
        this.ctStartTime = ctStartTime;
        this.ctEndTime = ctEndTime;
        this.ctPeriod = ctPeriod;
        this.ctpicuri = ctpicuri;
        this.cttimeid = cttimeid;
        this.ctbookingdate = ctbookingdate;
        this.ctposition = ctposition;
    }
    public BookingTimesClass(String ctid, String ctname, String ctage, String ctdate,String ctAddress, String ctPeriod, String ctpicuri, Boolean checked,String cttimeid,String ctbookingdate) {
        this.ctid = ctid;
        this.ctname = ctname;
        this.ctage = ctage;
        this.ctdate = ctdate;
        this.ctAddress = ctAddress;
        this.ctPeriod = ctPeriod;
        this.ctpicuri = ctpicuri;
        this.checked = checked;
        this.cttimeid = cttimeid;
        this.ctbookingdate = ctbookingdate;
        this.ctArrangement = ctArrangement;
    }

    public BookingTimesClass(String ctid, String ctname,  String ctdate, String ctphone, String ctpicuri) {
        this.ctid = ctid;
        this.ctname = ctname;
        this.ctage = ctage;
        this.ctdate = ctdate;
        this.ctAddress = ctAddress;
        this.ctphone = ctphone;
        this.ctpicuri = ctpicuri;
        this.checked = checked;
        this.cttimeid = cttimeid;
        this.ctbookingdate = ctbookingdate;
        this.ctArrangement = ctArrangement;
    }
public BookingTimesClass(String ctid, String ctdate,String ctAddress, String ctPeriod, String ctbookingdate,String ctArrangement) {
    this.ctid = ctid;
    this.ctdate = ctdate;
    this.ctAddress = ctAddress;
    this.ctPeriod = ctPeriod;
    this.ctbookingdate = ctbookingdate;
    this.ctArrangement = ctArrangement;
}
    public BookingTimesClass(String ctid,String ctname, String ctdate,String ctAddress, String ctPeriod, String ctpicuri,String ctbookingdate, String ctArrangement,String ctSpc) {
        this.ctid = ctid;
        this.ctname = ctname;
        this.ctage = ctage;
        this.ctdate = ctdate;
        this.ctAddress = ctAddress;
        this.ctPeriod = ctPeriod;
        this.ctpicuri = ctpicuri;
        this.cttimeid = cttimeid;
        this.ctbookingdate = ctbookingdate;
        this.ctArrangement = ctArrangement;
        this.ctSpc = ctSpc;

    }

    public int getCtposition() {
        return ctposition;
    }

    public void setCtposition(int ctposition) {
        this.ctposition = ctposition;
    }

    public String getCtid() {
        return ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid;
    }

    public String getCtname() {
        return ctname;
    }

    public void setCtname(String ctname) {
        this.ctname = ctname;
    }

    public String getCtage() {
        return ctage;
    }

    public void setCtage(String ctage) {
        this.ctage = ctage;
    }

    public String getCtdate() {
        return ctdate;
    }

    public void setCtdate(String ctdate) {
        this.ctdate = ctdate;
    }

    public String getCtAddress() {
        return ctAddress;
    }

    public String getCtPeriod() {
        return ctPeriod;
    }

    public String getCtpicuri() {
        return ctpicuri;
    }

    public void setCtpicuri(String ctpicuri) {
        this.ctpicuri = ctpicuri;
    }

    public String getCttimeid() {
        return cttimeid;
    }

    public void setCttimeid(String cttimeid) {
        this.cttimeid = cttimeid;
    }

    public String getCtbookingdate() {
        return ctbookingdate;
    }

    public void setCtbookingdate(String ctbookingdate) {
        this.ctbookingdate = ctbookingdate;
    }

    public String getCtArrangement() {
        return ctArrangement;
    }

    public void setCtArrangement(String ctArrangement) {
        this.ctArrangement = ctArrangement;
    }

    public String getCtphone() {
        return ctphone;
    }

    public void setCtphone(String ctphone) {
        this.ctphone = ctphone;
    }

    public String getCtSpc() {
        return ctSpc;
    }

    public void setCtSpc(String ctSpc) {
        this.ctSpc = ctSpc;
    }


    public String getCtStartTime() {
        return ctStartTime;
    }

    public String getCtEndTime() {
        return ctEndTime;
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
}

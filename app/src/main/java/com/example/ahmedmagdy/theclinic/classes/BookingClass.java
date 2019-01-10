package com.example.ahmedmagdy.theclinic.classes;

/**
 * Created by AHMED MAGDY on 10/23/2018.
 */

public class BookingClass {
    private String cbid;
    private String cbtime;
    private String cbtimestart;
    private String cbtimeend;
    private String cbaddress;
    private String cbdoctorid;
    private String cblatitude;
    private String cblongitude;
    private String steptime;
    private Boolean satchecked;
    private Boolean sunchecked;
    private Boolean monchecked;
    private Boolean tuschecked;
    private Boolean wedchecked;
    private Boolean thuchecked;
    private Boolean frichecked;

    public  BookingClass(){}
    public BookingClass(String cbid, String cbtimestart,String cbtimeend, String cbaddress, String cbdoctorid, String cblatitude, String cblongitude,String steptime, Boolean satchecked, Boolean sunchecked, Boolean monchecked, Boolean tuschecked, Boolean wedchecked, Boolean thuchecked, Boolean frichecked) {
        this.cbid = cbid;
        this.cbtimestart = cbtimestart;
        this.cbtimeend = cbtimeend;
        this.cbaddress = cbaddress;
        this.cbdoctorid = cbdoctorid;
        this.cblatitude = cblatitude;
        this.cblongitude = cblongitude;
        this.steptime = steptime;
       this.satchecked = satchecked;
         this.sunchecked = sunchecked;
         this.monchecked = monchecked;
         this.tuschecked = tuschecked;
         this.wedchecked = wedchecked;
         this.thuchecked = thuchecked;
         this.frichecked = frichecked;
    }

    public BookingClass(String cbid, String cbtime, String cbaddress, String cbdoctorid, String cblatitude, String cblongitude) {
        this.cbid = cbid;
        this.cbtime = cbtime;
        this.cbaddress = cbaddress;
        this.cbdoctorid = cbdoctorid;
        this.cblatitude = cblatitude;
        this.cblongitude = cblongitude;
       /** this.satchecked = satchecked;
        this.sunchecked = sunchecked;
        this.monchecked = monchecked;
        this.tuschecked = tuschecked;
        this.wedchecked = wedchecked;
        this.thuchecked = thuchecked;
        this.frichecked = frichecked;**/
    }

    public String getSteptime() {
        return steptime;
    }

    public void setSteptime(String steptime) {
        this.steptime = steptime;
    }

    public String getCbtimestart() {
        return cbtimestart;
    }

    public void setCbtimestart(String cbtimestart) {
        this.cbtimestart = cbtimestart;
    }

    public String getCbtimeend() {
        return cbtimeend;
    }

    public void setCbtimeend(String cbtimeend) {
        this.cbtimeend = cbtimeend;
    }

    public String getCbid() {
        return cbid;
    }

    public void setCbid(String cbid) {
        this.cbid = cbid;
    }

    public String getCbtime() {
        return cbtime;
    }

    public void setCbtime(String cbtime) {
        this.cbtime = cbtime;
    }

    public String getCbaddress() {
        return cbaddress;
    }

    public void setCbaddress(String cbaddress) {
        this.cbaddress = cbaddress;
    }

    public String getCbdoctorid() {
        return cbdoctorid;
    }

    public void setCbdoctorid(String cbdoctorid) {
        this.cbdoctorid = cbdoctorid;
    }

    public String getCblatitude() {
        return cblatitude;
    }

    public void setCblatitude(String cblatitude) {
        this.cblatitude = cblatitude;
    }

    public String getCblongitude() {
        return cblongitude;
    }

    public void setCblongitude(String cblongitude) {
        this.cblongitude = cblongitude;
    }
///////////////////////////////////////////
public Boolean getSatchecked() {
        if (satchecked == null)
        return false;
    else
        return satchecked;
}
   public void setSatchecked(Boolean satchecked) {
        this.satchecked = satchecked;
    }

    ///////////////////////////////////////////

    public Boolean getSunchecked() {
        if (sunchecked == null)
            return false;
        else
            return sunchecked;
    }
    public void setSunchecked(Boolean sunchecked) {
        this.sunchecked = sunchecked;
    }
    ///////////////////////////////////////////
    public Boolean getMonchecked() {
        if (monchecked == null)
            return false;
        else
            return monchecked;
    }

    public void setMonchecked(Boolean monchecked) {
        this.monchecked = monchecked;
    }
    ///////////////////////////////////////////
    public Boolean getTuschecked() {
        if (tuschecked == null)
            return false;
        else
            return tuschecked;
    }

    public void setTuschecked(Boolean tuschecked) {
        this.tuschecked = tuschecked;
    }
    ///////////////////////////////////////////
    public Boolean getWedchecked() {
        if (wedchecked == null)
            return false;
        else
            return wedchecked;
    }

    public void setWedchecked(Boolean wedchecked) {
        this.wedchecked = wedchecked;
    }
    ///////////////////////////////////////////
    public Boolean getThuchecked() {
        if (thuchecked == null)
            return false;
        else
            return thuchecked;
    }

    public void setThuchecked(Boolean thuchecked) {
        this.thuchecked = thuchecked;
    }
    ///////////////////////////////////////////
    public Boolean getFrichecked() {
        if (frichecked == null)
            return false;
        else
            return frichecked;
    }

    public void setFrichecked(Boolean frichecked) {
        this.frichecked = frichecked;
    }
    ///////////////////////////////////////////

}

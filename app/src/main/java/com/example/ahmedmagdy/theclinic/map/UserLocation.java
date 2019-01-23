package com.example.ahmedmagdy.theclinic.map;

import android.os.Parcel;
import android.os.Parcelable;

public class UserLocation implements Parcelable {

    private String cmlatitude;
    private String cmlongitude;
    private String cmname;
    private String cmdoctorpic;
    private String cmDoctorType;
    private String cmdoctorspecialty;
    private String cmdoctorid;
    private String cmDoctorGander;
    private String cmHC;

    public UserLocation(String cmDoctorGander ,String cmDoctorType,String cmHC,
                        String cmdoctorid ,String cmdoctorpic, String cmdoctorspecialty ,
                        String cmlatitude, String cmlongitude,
                        String cmname
                          ) {

        this.cmlatitude = cmlatitude;
        this.cmlongitude = cmlongitude;
        this.cmname = cmname;
        this.cmdoctorpic = cmdoctorpic;
        this.cmDoctorType = cmDoctorType;
        this.cmdoctorspecialty = cmdoctorspecialty;
        this.cmdoctorid = cmdoctorid;
        this.cmDoctorGander = cmDoctorGander;
        this.cmHC = cmHC;
    }

    public UserLocation() {
    }

    public UserLocation(Parcel in) {
        cmDoctorGander = in.readString();
        cmDoctorType = in.readString();
        cmHC = in.readString();
        cmdoctorid = in.readString();
        cmdoctorpic = in.readString();
        cmdoctorspecialty = in.readString();
        cmlatitude = in.readString();
        cmlongitude = in.readString();
        cmname = in.readString();
    }


    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    public String getCmDoctorGander() {
        return cmDoctorGander;
    }

    public void setCmDoctorGander(String cmDoctorGander) {
        this.cmDoctorGander = cmDoctorGander;
    }

    public String getCmHC() {
        return cmHC;
    }

    public void setCmHC(String cmHC) {
        this.cmHC = cmHC;
    }

    public String getCmlatitude() {
        return cmlatitude;
    }

    public String getCmdoctorid() {
        return cmdoctorid;
    }

    public void setCmdoctorid(String cmdoctorid) {
        this.cmdoctorid = cmdoctorid;
    }

    public void setCmlatitude(String cmlatitude) {
        this.cmlatitude = cmlatitude;
    }

    public String getCmlongitude() {
        return cmlongitude;
    }

    public void setCmlongitude(String cmlongitude) {
        this.cmlongitude = cmlongitude;
    }

    public String getCmname() {
        return cmname;
    }

    public void setCmname(String cmname) {
        this.cmname = cmname;
    }

    public String getCmdoctorpic() {
        return cmdoctorpic;
    }

    public void setCmdoctorpic(String cmdoctorpic) {
        this.cmdoctorpic = cmdoctorpic;
    }

    public String getCmDoctorType() {
        return cmDoctorType;
    }

    public void setCmDoctorType(String cmDoctorType) {
        this.cmDoctorType = cmDoctorType;
    }

    public String getCmdoctorspecialty() {
        return cmdoctorspecialty;
    }

    public void setCmdoctorspecialty(String cmdoctorspecialty) {
        this.cmdoctorspecialty = cmdoctorspecialty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(cmlatitude);
        dest.writeString(cmlongitude);
        dest.writeString(cmname);
        dest.writeString(cmdoctorpic);
        dest.writeString(cmDoctorType);
        dest.writeString(cmdoctorspecialty);
        dest.writeString(cmdoctorid);
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "cmlatitude=" + cmlatitude +
                ", cmlongitude=" + cmlongitude +
                ", cmname='" + cmname + '\'' +
                ", cmdoctorpic='" + cmdoctorpic + '\'' +
                ", cmDoctorType='" + cmDoctorType + '\'' +
                ", cmdoctorspecialty='" + cmdoctorspecialty + '\'' +
                ", cmdoctorid='" + cmdoctorid + '\'' +
                '}';
    }



}

package com.example.ahmedmagdy.theclinic.map;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserLocation implements Parcelable {

    private double lat;
    private double lng;
    private Date time_stamp;
    private String cName;
    private String cURI;
    private String cCity;
    private String cType;
    private String cSpec;
    private String cEmail;
    private String cUid;

    public UserLocation(double lat, double lng, Date time_stamp,
                        String cName, String cURI, String cCity,
                        String cType, String cSpec , String cEmail , String cUid) {
        this.lat = lat;
        this.lng = lng;
        this.time_stamp = time_stamp;
        this.cName = cName;
        this.cURI = cURI;
        this.cCity = cCity;
        this.cType = cType;
        this.cSpec = cSpec;
        this.cEmail = cEmail;
        this.cUid = cUid;
    }

    public UserLocation() {
    }

    public UserLocation(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        cName = in.readString();
        cURI = in.readString();
        cCity = in.readString();
        cType = in.readString();
        cSpec = in.readString();
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

    public double getLat() {
        return lat;
    }

    public String getcUid() {
        return cUid;
    }

    public void setcUid(String cUid) {
        this.cUid = cUid;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcURI() {
        return cURI;
    }

    public void setcURI(String cURI) {
        this.cURI = cURI;
    }

    public String getcCity() {
        return cCity;
    }

    public void setcCity(String cCity) {
        this.cCity = cCity;
    }

    public String getcType() {
        return cType;
    }

    public void setcType(String cType) {
        this.cType = cType;
    }

    public String getcSpec() {
        return cSpec;
    }

    public void setcSpec(String cSpec) {
        this.cSpec = cSpec;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(cName);
        dest.writeString(cURI);
        dest.writeString(cCity);
        dest.writeString(cType);
        dest.writeString(cSpec);
        dest.writeString(cEmail);
        dest.writeString(cUid);
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", time_stamp=" + time_stamp +
                ", cName='" + cName + '\'' +
                ", cURI='" + cURI + '\'' +
                ", cCity='" + cCity + '\'' +
                ", cType='" + cType + '\'' +
                ", cSpec='" + cSpec + '\'' +
                ", cEmail='" + cEmail + '\'' +
                ", cUid='" + cUid + '\'' +
                '}';
    }


}

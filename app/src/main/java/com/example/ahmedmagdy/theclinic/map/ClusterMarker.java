package com.example.ahmedmagdy.theclinic.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String uid;
    private String spec;
    private String city;
    private int iconPic;
    private String iconPicUrl;
    private String snippet;
    private String gender;


    public ClusterMarker(LatLng position, String title,int iconPic ,  String snippet,  String gender) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPic = iconPic;
        this.gender = gender;
    }

    public ClusterMarker(LatLng position, String title, String iconPicUrl ,  String snippet,  String gender) {
        this.position = position;
        this.title = title;
        this.iconPicUrl = iconPicUrl;
        this.snippet = snippet;
        this.gender = gender;
    }

    public ClusterMarker() {
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIconPic() {
        return iconPic;
    }

    public void setIconPic(int iconPic) {
        this.iconPic = iconPic;
    }

    public String getIconPicUrl() {
        return iconPicUrl;
    }

    public void setIconPicUrl(String iconPicUrl) {
        this.iconPicUrl = iconPicUrl;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ClusterMarker{" +
                "position=" + position +
                ", title='" + title + '\'' +
                ", uid='" + uid + '\'' +
                ", spec='" + spec + '\'' +
                ", city='" + city + '\'' +
                ", iconPic=" + iconPic +
                ", iconPicUrl='" + iconPicUrl + '\'' +
                ", snippet='" + snippet + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}

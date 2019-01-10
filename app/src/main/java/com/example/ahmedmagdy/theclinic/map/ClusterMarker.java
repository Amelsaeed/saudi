package com.example.ahmedmagdy.theclinic.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private String uid;
    private String spec;
    private String city;
    private int iconPic;
    private String iconPicUrl;

    public ClusterMarker(LatLng position, String title, String snippet, String uid, String spec, String city, int iconPic) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.uid = uid;
        this.spec = spec;
        this.city = city;
        this.iconPic = iconPic;
    }

    public ClusterMarker(LatLng position, String title,  String spec, int iconPic ) {
        this.position = position;
        this.title = title;
        this.spec = spec;
        this.iconPic = iconPic;
    }

    public ClusterMarker(LatLng position, String title,  String spec,  String iconPicUrl) {
        this.position = position;
        this.title = title;
        this.spec = spec;
        this.iconPicUrl = iconPicUrl;
    }

    public ClusterMarker() {
    }

    public String getIconPicUrl() {
        return iconPicUrl;
    }

    public void setIconPicUrl(String iconPicUrl) {
        this.iconPicUrl = iconPicUrl;
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

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
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
}

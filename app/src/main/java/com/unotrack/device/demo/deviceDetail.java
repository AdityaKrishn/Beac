package com.unotrack.device.demo;

/**
 * Created by aditya on 15/4/17.
 */

public class deviceDetail {

    public String deviceID;
    public String email;
    public String latitude;
    public String longitude;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public deviceDetail() {
    }

    public deviceDetail(String email, String name,String lat,String longi) {
        this.deviceID = name;
        this.email = email;
        this.latitude=lat;
        this.longitude=longi;
    }
}

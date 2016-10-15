package com.sct.trafficcontrol.item;

/**
 * Traffic light item.
 */

public class TrafficLightInfo {
    private int mId;
    private double mLatitude;
    private double mLongitude;

    private int mTotalRedTime = 0;
    private int mTotalGreenTime = 0;

    public TrafficLightInfo(int id, double latitude, double longitude, int totalRedTime, int totalGreenTime) {
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;

        mTotalRedTime = totalRedTime;
        mTotalGreenTime = totalGreenTime;
    }

    public int getId() {
        return mId;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public int getTotalRedTime() {
        return mTotalRedTime;
    }

    public int getTotalGreenTime() {
        return mTotalGreenTime;
    }
}

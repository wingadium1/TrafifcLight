package com.sct.trafficcontrol.item;

import com.google.android.gms.maps.model.Marker;

/**
 * Traffic light item.
 */

public class MarkerInfo {
    private int mId;
    private Marker mMarker;

    public MarkerInfo(int id, Marker marker) {
        mId = id;
        mMarker = marker;
    }

    public int getId() {
        return mId;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker mMarker) {
        this.mMarker = mMarker;
    }
}

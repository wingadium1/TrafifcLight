package com.sct.trafficcontrol;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sct.trafficcontrol.controller.GetDataFromServerAsyncTask;
import com.sct.trafficcontrol.controller.ViewToBitmapAsyncTask;
import com.sct.trafficcontrol.item.MarkerInfo;
import com.sct.trafficcontrol.item.TrafficLightInfo;
import com.sct.trafficcontrol.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        ViewToBitmapAsyncTask.DrawBitmapListener, GetDataFromServerAsyncTask.LoadDataListener {

    private static final String SERVER_URL = "";

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;

    private ViewToBitmapAsyncTask mViewToBitmapAsyncTask;
    private GetDataFromServerAsyncTask mGetDataFromServerAsyncTask;

    private Location mCurrentLocation;

    private List<TrafficLightInfo> mTrafficLightInfoList;
    private List<MarkerInfo> mMarkerInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMarkerInfoList = new ArrayList<>();

        getDataFromServer();
    }

    private void getDataFromServer() {
        if (mGetDataFromServerAsyncTask != null && !mGetDataFromServerAsyncTask.isCancelled()) {
            mGetDataFromServerAsyncTask.cancel(true);
        }
        mGetDataFromServerAsyncTask = new GetDataFromServerAsyncTask();
        mGetDataFromServerAsyncTask.setLoadDataListener(this);
        mGetDataFromServerAsyncTask.execute(SERVER_URL);
    }

    @Override
    protected void onDestroy() {
        if (mMap != null) {
            mMap.clear();
            mMap = null;
        }

        if (mViewToBitmapAsyncTask != null) {
            if (!mViewToBitmapAsyncTask.isCancelled()) {
                mViewToBitmapAsyncTask.cancel(true);
            }
            mViewToBitmapAsyncTask = null;
        }
        if (mCurrentLocation != null) {
            mCurrentLocation = null;
        }
        if (mMarkerInfoList != null) {
            mMarkerInfoList = null;
        }
        super.onDestroy();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enabling MyLocation Layer of Google Map.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
        }

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);
            Toast.makeText(this, "Provider: " + provider, Toast.LENGTH_SHORT).show();

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mTrafficLightInfoList = new ArrayList<>();
        mTrafficLightInfoList.add(new TrafficLightInfo(0, location.getLatitude(), location.getLongitude(), 20, 30));
        mTrafficLightInfoList.add(new TrafficLightInfo(1, location.getLatitude()+ 0.1, location.getLongitude()+ 0.1, 20, 30));
        mTrafficLightInfoList.add(new TrafficLightInfo(2, location.getLatitude()+ 0.2, location.getLongitude()+ 0.2, 10, 20));
        mTrafficLightInfoList.add(new TrafficLightInfo(3, location.getLatitude()+ 0.3, location.getLongitude() + 0.3, 40, 30));

        Toast.makeText(getApplicationContext(), "New Location:"+location.getLatitude() + " "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        updateTrafficLight();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

        addLine();
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    @Override
    public void onBitmapCreated(Bitmap bitmap, TrafficLightInfo trafficLightInfo) {
        if (bitmap != null) {
            addMakerToMap(bitmap, trafficLightInfo);
        }
    }

    @Override
    public void onBitmapCreateFailed() {}

    @Override
    public void onLoadDataSucceed(String data) {
        //TODO: updateTrafficLight();
    }

    @Override
    public void onLoadDataFailed() {}

    private void updateTrafficLight() {
        Log.v(TAG, "updateTrafficLight");
        for (TrafficLightInfo trafficLightItem : mTrafficLightInfoList) {
            mViewToBitmapAsyncTask = new ViewToBitmapAsyncTask(this, trafficLightItem);
            mViewToBitmapAsyncTask.setDrawBitmapListener(this);
            mViewToBitmapAsyncTask.execute();
        }
    }

    private void addMakerToMap(Bitmap bitmap, TrafficLightInfo trafficLightInfo) {
        Log.v(TAG, "addMakerToMap");
        Iterator<MarkerInfo> markerInfoListIterator = mMarkerInfoList.iterator();
        while (markerInfoListIterator.hasNext()) {
            MarkerInfo item = markerInfoListIterator.next();
            if (trafficLightInfo.getId() == item.getId()) {
                // Remove marker need update is added.
                item.getMarker().remove();
                markerInfoListIterator.remove();
            }
        }

        LatLng latLng = new LatLng(trafficLightInfo.getLatitude(), trafficLightInfo.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                .fromBitmap(bitmap)).title(Constants.EMPTY_STRING);

        Marker marker = mMap.addMarker(markerOptions);
        mMarkerInfoList.add(new MarkerInfo(trafficLightInfo.getId(), marker));
    }

    public void addLine() {
        PolylineOptions line=
                new PolylineOptions().add(new LatLng(40.70686417491799,
                                -74.01572942733765),
                        new LatLng(40.76866299974387,
                                -73.98268461227417),
                        new LatLng(40.765136435316755,
                                -73.97989511489868),
                        new LatLng(40.748963847316034,
                                -73.96807193756104))
                        .width(5).color(Color.RED);

        mMap.addPolyline(line);
    }
}

package com.sct.trafficcontrol.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sct.trafficcontrol.R;
import com.sct.trafficcontrol.item.TrafficLightInfo;
import com.sct.trafficcontrol.util.Constants;

public class ViewToBitmapAsyncTask extends AsyncTask<Void, Void, Bitmap> {
    public interface DrawBitmapListener {
        void onBitmapCreated(Bitmap bitmap, TrafficLightInfo trafficLightInfo);
        void onBitmapCreateFailed();
    }

    private static final String TAG = "ViewToBitmapAsyncTask";

    private Context mContext;

    private DrawBitmapListener mDrawBitmapListener;

    private TrafficLightInfo mTrafficLightInfo;

    private View mTrafficLightView;
    private TextView mTime;
    private ImageView mTrafficLight;


    public ViewToBitmapAsyncTask(Context context, TrafficLightInfo trafficLightInfo) {
        if (context == null || trafficLightInfo == null) {
            throw new IllegalArgumentException("params are not to be null.");
        }
        mContext = context;

        mTrafficLightInfo = trafficLightInfo;

        // Init view of Marker.
        mTrafficLightView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.traffic_lights_layout, null);
        mTime = (TextView) mTrafficLightView.findViewById(R.id.num_txt);
        mTrafficLight =  (ImageView) mTrafficLightView.findViewById(R.id.iv_traffic_light);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        int trafficLightStatus = mTrafficLightInfo.getStatus();
        Log.v(TAG, "onPreExecute trafficLightStatus="+trafficLightStatus +" time:"+mTrafficLightInfo.getCurrentTime());

        if (trafficLightStatus == TrafficLightInfo.RED_STATUS) {
            mTime.setText(String.valueOf(mTrafficLightInfo.getCurrentTime()));
            mTime.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            mTrafficLight.setImageResource(R.drawable.ic_red_traffic_light);
        } else if (trafficLightStatus == TrafficLightInfo.YELLOW_STATUS) {
            mTime.setText(Constants.EMPTY_STRING);
            mTrafficLight.setImageResource(R.drawable.ic_yellow_traffic_light);
        } else if (trafficLightStatus == TrafficLightInfo.GREEN_STATUS) {
            mTime.setText(String.valueOf(mTrafficLightInfo.getCurrentTime()));
            mTime.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
            mTrafficLight.setImageResource(R.drawable.ic_green_traffic_light);
        }
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {

        return createDrawableFromView();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mDrawBitmapListener != null && bitmap != null) {
            mDrawBitmapListener.onBitmapCreated(bitmap, mTrafficLightInfo);
        } else {
            mDrawBitmapListener.onBitmapCreateFailed();
        }
    }

    /**
     * Create drawable from view.
     * @return
     */
    private Bitmap createDrawableFromView() {
        Log.v(TAG, "createDrawableFromView");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mTrafficLightView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        mTrafficLightView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mTrafficLightView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        mTrafficLightView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(mTrafficLightView.getMeasuredWidth(), mTrafficLightView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        mTrafficLightView.draw(canvas);
        return bitmap;
    }

    public void setDrawBitmapListener(DrawBitmapListener drawBitmapListener) {
        mDrawBitmapListener = drawBitmapListener;
    }
}
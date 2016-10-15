package com.sct.trafficcontrol.controller;

import android.os.AsyncTask;

public class GetDataFromServerAsyncTask extends AsyncTask<String, Void, String> {

    public interface LoadDataListener {
        void onLoadDataSucceed(String data);
        void onLoadDataFailed();
    }

    private LoadDataListener mLoadDataListener;

    private HttpURLConnectionManager mHttpURLConnectionManager;


    public GetDataFromServerAsyncTask() {
        mHttpURLConnectionManager = new HttpURLConnectionManager();
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        if (url == null)
            return null;

        return mHttpURLConnectionManager.getDataFromUrl(url);
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        if (mLoadDataListener != null && data != null) {
            mLoadDataListener.onLoadDataSucceed(data);
        } else {
            mLoadDataListener.onLoadDataFailed();
        }
    }

    public void setLoadDataListener(LoadDataListener loadDataListener) {
        mLoadDataListener = loadDataListener;
    }
}
/**
 *
 */
package com.sct.trafficcontrol.controller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author trunghv2
 *
 */
public class HttpURLConnectionManager {
    private final String TAG = HttpURLConnectionManager.this.getClass().getSimpleName();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36";
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 15000;

    private static final String UTF_8 = "UTF-8";

    private static final String REQUEST_METHOD = "GET";
    private static final String REQUEST_AGENT_PROPERTY = "User-Agent";
    private static final String REQUEST_CONTENT_LENGTH_PROPERTY = "User-Agent";

    private static final String CONTENT_LENGTH_VALUE = "0";

    /**
     * Get JSON from Server.
     * @param url
     * @return
     */
    public String getDataFromUrl(String url) {
        Log.v(TAG, "getDataFromUrl");
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = openConnection(url);
            int status = httpURLConnection.getResponseCode();

            if (status == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                return sb.toString();
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        return null;
    }

    /** Open connection from HttpURLConnection.
     * @param url
     */
    private HttpURLConnection openConnection(String url) throws IOException {
        Log.v(TAG, "openConnection");
        URL u = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) u.openConnection();

        httpURLConnection.setRequestMethod(REQUEST_METHOD);
        httpURLConnection.setRequestProperty(REQUEST_AGENT_PROPERTY, USER_AGENT);
        httpURLConnection.setRequestProperty(REQUEST_CONTENT_LENGTH_PROPERTY, CONTENT_LENGTH_VALUE);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setAllowUserInteraction(false);
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(SOCKET_TIMEOUT);
        httpURLConnection.connect();
        return httpURLConnection;
    }
}

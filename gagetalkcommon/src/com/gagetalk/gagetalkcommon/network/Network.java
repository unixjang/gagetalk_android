package com.gagetalk.gagetalkcommon.network;

/**
 * Created by hyochan on 3/28/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcommon.R;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class Network {

    private static final String TAG = "Network";

    private static Network network;
    private SharedPreferences pref;
    private Context context;

    private Network(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static Network getInstance(Context context){
        if(network == null) network = new Network(context);
        return network;
    }

    //check if the network is available
    public boolean isNetworkAvailable(){
        boolean available = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()) available = true;
        return available;
    }

    // response json
    public void reqGet(Activity activity, String url, JsonHttpResponseHandler jsonHttpResponseHandler){
        url = NetworkPreference.getInstance(context).getServerUrl() + ":"+
                NetworkPreference.getInstance(context).getServerPort() + url;
        if(Network.getInstance(context).isNetworkAvailable()) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(context, url, null, jsonHttpResponseHandler);
            PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
            client.setCookieStore(myCookieStore);
            // 마음대로 쿠키를 조작할 수 있다.
            BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
            newCookie.setVersion(1);
            newCookie.setDomain("hyochan.org");
            newCookie.setPath("/");
            myCookieStore.addCookie(newCookie);
        }else{
            if(activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        CustomToast.getInstance(context).createToast(context.getResources().getString(R.string.network_error));
                    }
                });
            }
        }
    }

    // response json
    public void reqPost(Activity activity, String url, RequestParams requestParams, JsonHttpResponseHandler jsonHttpResponseHandler){
        url = NetworkPreference.getInstance(context).getServerUrl() + ":"+
                        NetworkPreference.getInstance(context).getServerPort() + url;
        if(Network.getInstance(context).isNetworkAvailable()) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, url, requestParams, jsonHttpResponseHandler);
            PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
            client.setCookieStore(myCookieStore);
            // 마음대로 쿠키를 조작할 수 있다.
            BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
            newCookie.setVersion(1);
            newCookie.setDomain("www.gagetalk.com");
            newCookie.setPath("/");
            myCookieStore.addCookie(newCookie);
        }else{
            if(activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        CustomToast.getInstance(context).createToast(context.getResources().getString(R.string.network_error));
                    }
                });
            }
        }
    }

    public void toastErrorMsg(final Activity activity){
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    CustomToast.getInstance(context).createToast(
                            activity.getResources().getString(R.string.server_error));
                }
            });
        }
    }

    //get url address and show return the content of the web page
    public String downloadUrl(String strUrl) throws IOException {
        String s = null;
        byte[] buffer = new byte[1000];
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //connect to url
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            iStream.read(buffer);
            s = new String(buffer);
        } catch (Exception e){
            MyLog.d(TAG, e.toString());
        } finally {
            iStream.close();
        }
        return s;
    }
}


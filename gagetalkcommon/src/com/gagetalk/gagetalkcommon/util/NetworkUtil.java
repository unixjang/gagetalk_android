package com.gagetalk.gagetalkcommon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcommon.constant.ConstValue;

/**
 * Created by hyochan on 6/23/15.
 */
public class NetworkUtil {

    private static NetworkUtil networkUtil;
    private SharedPreferences pref;
    private Context context;

    private NetworkUtil(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static NetworkUtil getInstance(Context context){
        if(networkUtil == null) networkUtil = new NetworkUtil(context);
        return networkUtil;
    }

    public int getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return ConstValue.TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return ConstValue.TYPE_MOBILE;
        }
        return ConstValue.TYPE_NOT_CONNECTED;
    }

    public String getConnectivityStatusString() {
        int conn = getConnectivityStatus();
        String status = null;
        if (conn == ConstValue.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == ConstValue.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == ConstValue.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
}
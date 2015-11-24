package com.gagetalk.gagetalkcommon.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hyochan on 3/28/15.
 */
public class NetworkPreference {
    // commit test
    private static NetworkPreference networkPreference;
    private SharedPreferences pref;
    private Context context;
    private final String serverUrl = "http://hyochan.org";
    // private final String serverUrl = "http://172.20.10.4";
    // private final String serverUrl = "http://192.168.43.5";
    // private final String serverUrl = "http://192.168.0.5";
    /*private final String serverUrl = "http://192.168.123.182";*/
    private final String serverPort = "3000";

    private NetworkPreference(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static NetworkPreference getInstance(Context context){
        if(networkPreference == null) networkPreference = new NetworkPreference(context);
        return networkPreference;

    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getServerPort() {
        return serverPort;
    }

}
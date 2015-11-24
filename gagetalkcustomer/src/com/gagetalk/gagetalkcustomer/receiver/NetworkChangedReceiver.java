package com.gagetalk.gagetalkcustomer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcommon.util.NetworkUtil;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;

/**
 * Created by hyochan on 6/23/15.
 */
public class NetworkChangedReceiver extends BroadcastReceiver {

    private final static String TAG = "NetworkChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        String strStatus = NetworkUtil.getInstance(context).getConnectivityStatusString();
        MyLog.i(TAG, "NetworkChangedReceiver - " + strStatus);

        int status = NetworkUtil.getInstance(context).getConnectivityStatus();
        switch (status){
            case ConstValue.TYPE_NOT_CONNECTED:
                CustomerNetwork.getInstance(context).disconnectSocket();
                break;
            case ConstValue.TYPE_MOBILE:
            case ConstValue.TYPE_WIFI:
                CustomerNetwork.getInstance(context).connectSocket();
                break;
        }
    }
}

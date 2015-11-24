package com.gagetalk.gagetalkcustomer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.util.AlarmUtil;

/**
 * Created by hyochan on 6/23/15.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private final static String TAG = "BootCompletedReceiver";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.d(TAG, "connect to socket - CHAT SERVICE");
        this.context = context;
        if(Network.getInstance(context).isNetworkAvailable()) {
            AlarmUtil.getInstance(context).unregisterRestartChatServiceAlarm();
            CustomerNetwork.getInstance(context).connectSocket();
        }else {
            AlarmUtil.getInstance(context).registerRestartChatServiceAlarm(10);
        }
    }}

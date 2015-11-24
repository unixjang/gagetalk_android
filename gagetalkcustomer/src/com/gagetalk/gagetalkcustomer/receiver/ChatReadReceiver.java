package com.gagetalk.gagetalkcustomer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.database.AccessDB;

/**
 * Created by hyochan on 7/12/15.
 */
public class ChatReadReceiver extends BroadcastReceiver {

    private static final String TAG = "ChatReadReceiver";
    private String marId;

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.d(TAG, "ChatReadReceiver : " + intent.getAction());
        if(intent.getAction().equals(ConstValue.CHAT_READ_RECEIVER)){
            marId = intent.getStringExtra("mar_id");
            // update local chatroom db
            AccessDB.getInstance(context).updateChatRoomReadFlag(marId);
        }

    }
}

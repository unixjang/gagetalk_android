package com.gagetalk.gagetalkcustomer.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcustomer.service.ChatService;

/**
 * Created by hyochan on 6/23/15.
 */
public class AlarmUtil {

    private static final String TAG = "AlarmUtil";

    private static AlarmUtil alarmUtil;
    private SharedPreferences pref;
    private Context context;

    private AlarmUtil(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static AlarmUtil getInstance(Context context){
        if(alarmUtil == null) alarmUtil = new AlarmUtil(context);
        return alarmUtil;
    }


    public void registerRestartChatServiceAlarm(int sec) {
        Intent intent = new Intent(context, ChatService.class );
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += sec*1000; // sec 초 후 이벤트 발생
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10*1000, sender);
    }

    public void unregisterRestartChatServiceAlarm() {
        Intent intent = new Intent(context, ChatService.class);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0 );
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }
}

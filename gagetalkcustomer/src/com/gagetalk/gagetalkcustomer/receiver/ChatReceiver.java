package com.gagetalk.gagetalkcustomer.receiver;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;

import com.gagetalk.gagetalkcustomer.activities_dialog.ChatDialogActivity;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities.ChatActivity;
import com.gagetalk.gagetalkcustomer.activities_dialog.ChatLockScreenActivity;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.preference.AppPref;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;


import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 6/21/15.
 */
public class ChatReceiver extends BroadcastReceiver {

    private static final String TAG = "ChatReceiver";
    private static final int CHAT_NOTIFICATION_ID = 5897;
    private NotificationManager notificationManager;
    private Notification chatNotification;

    @Override
    public void onReceive(final Context context, Intent intent) {

        MyLog.d(TAG, "ChatReceiver Broadcast : " + intent.getAction());

        // received my send message
        if(intent.getAction().equals(ConstValue.CHAT_MY_RECEIVER)){

        }
        // received peer message
        if(intent.getAction().equals(ConstValue.CHAT_PEERS_RECEIVER)){

            final String marId = intent.getStringExtra("id_peer");
            final String marName = intent.getStringExtra("name_peer");
            final String message = intent.getStringExtra("msg_peer");
            final int type = intent.getIntExtra("type", 0);
            final String datePeer = intent.getStringExtra("date_peer");

            MyLog.d(TAG, "marId : " + marId);
            MyLog.d(TAG, "marName : " + marName);
            MyLog.d(TAG, "message : " + message);
            MyLog.d(TAG, "type : " + type);
            MyLog.d(TAG, "datePeer : " + datePeer);
            MyLog.d(TAG, "chatroom : " + CustomerFunction.getInstance(context).getChatRoom());

            // show notification if current chatroom is not what user is watching
            if(
                CustomerFunction.getInstance(context).getChatRoom() == null ||
                (CustomerFunction.getInstance(context).getChatRoom() != null && !CustomerFunction.getInstance(context).getChatRoom().equals(marId))){
                MyLog.i(TAG, "chatroom : " + CustomerFunction.getInstance(context).getChatRoom() + ", mar_id : " + marId);


                // show notification & message activity if app is not set
                // set notification manager if it is not mine

                CustomerFunction.getInstance(context).setNotiRoom(marId);

                Intent dialogIntent;

                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if( myKM.inKeyguardRestrictedInputMode()) {
                    //it is locked
                    dialogIntent = new Intent(context, ChatLockScreenActivity.class);
                } else {
                    //it is not locked
                    dialogIntent = new Intent(context, ChatDialogActivity.class);
                }
                dialogIntent.putExtra("mar_id", marId);
                dialogIntent.putExtra("mar_name", marName);
                dialogIntent.putExtra("message", message);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialogIntent);


                // NOTIFICATION
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                // intent.putExtra(ConstValue.ACTIVITY_STARTED_FROM_CHAT_NOTI, true);
                intent.putExtra("mar_id", marId);
                intent.putExtra("mar_name", marName);
                intent.putExtra(ConstValue.ACTIVITY_STARTED_FROM_CHAT_NOTI, true);

                final PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(NetworkPreference.getInstance(context).getServerUrl() + ":" +
                                NetworkPreference.getInstance(context).getServerPort() + "/images/" + marId.replaceAll("\\s", "") + ".png",
                        new FileAsyncHttpResponseHandler(context) {
                            @Override
                            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                // do something
                                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                chatNotification = new Notification.Builder(context)
                                        .setAutoCancel(true) // 노티 클릭하면 사라지게
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setTicker(marName + " : " + message)
                                        .setContentTitle(marName)
                                        .setContentText(message)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentIntent(pIntent)
                                        .build();
                                notificationManager.notify(ConstValue.CHAT_NOTIFICATION_ID, chatNotification);
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, File file) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                myBitmap = myBitmap.createScaledBitmap(
                                        myBitmap,
                                        Function.getInstance(context).dpToPx(48),
                                        Function.getInstance(context).dpToPx(48),
                                        false);
                                chatNotification = new Notification.Builder(context)
                                        .setAutoCancel(true) // 노티 클릭하면 사라지게
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setTicker(marName + " : " + message)
                                        .setContentTitle(marName)
                                        .setContentText(message)
                                        .setWhen(System.currentTimeMillis())
                                        .setLargeIcon(myBitmap)
                                        .setContentIntent(pIntent)
                                        .build();

                                notificationManager.notify(ConstValue.CHAT_NOTIFICATION_ID, chatNotification);
                            }
                        });


                // for setting custom noti
                // RemoteViews contentiew = new RemoteViews(getPackageName(), R.layout.noti_chat);
                // contentiew.setOnClickPendingIntent(R.id.button, pIntent);
                // noti.contentView = contentview;
            }
        }

    }
}

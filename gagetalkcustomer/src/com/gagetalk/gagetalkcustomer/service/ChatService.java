package com.gagetalk.gagetalkcustomer.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.socket.IOAcknowledge;
import com.gagetalk.gagetalkcommon.socket.IOCallback;
import com.gagetalk.gagetalkcommon.socket.SocketIO;
import com.gagetalk.gagetalkcommon.socket.SocketIOException;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.preference.AppPref;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatService extends Service {

    private static final String TAG = "ChatService";
    private static final int CHAT_NOTIFICATION_ID = 5897;
    private int startId;
    private NotificationManager notificationManager;
    private Notification chatNotification;
    private Context context;
    private String room;

    public ChatService() {
    }

    @Override
    public void onDestroy() {
        registerRestartAlarm();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getApplicationContext();

        this.startId = startId;
        // when not logged in chatservice don't have to restart
        unregisterRestartAlarm();
        MyLog.i(TAG, "startId : " + startId);

        if(CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
            if(CustomerNetwork.getInstance(context).getSocket() == null) {
                MyLog.i(TAG, "socket is null so set it");
                try {
                    CustomerNetwork.getInstance(context).setSocket(
                            new SocketIO((NetworkPreference.getInstance(context).getServerUrl())
                                    + ":" + NetworkPreference.getInstance(context).getServerPort())
                    );
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                // sendbroadcast to refresh the HomeFragment and MsgFragment if the network was lost
                sendBroadcast(new Intent().setAction(ConstValue.SERVER_ALIVE_RECEIVER));
                socketClient(CustomerNetwork.getInstance(context).getSocket());
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void socketClient(final SocketIO socket){
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JSONObject json, IOAcknowledge ack) {
                try {
                    MyLog.i("Server said", "Server said" + json.toString(2));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                MyLog.i("Server said", "Server said" + data);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                MyLog.i("Server Error", "Error occured");
/*
                if(NetworkPreference.getInstance(context).getSocket() == null) {
                    try {
                        NetworkPreference.getInstance(context).setSocket(
                                new SocketIO((NetworkPreference.getInstance(context).getServerUrl())
                                        + ":"+ NetworkPreference.getInstance(context).getSocketPort()));
                    } catch (Exception e) {
                        Log.d("Exception while getting url", e.toString());
                    }
                }
*/
                // socketClient(NetworkPreference.getInstance(context).getSocket());
                socketIOException.printStackTrace();
                CustomerNetwork.getInstance(context).setSocket(null);
                registerRestartAlarm();
            }

            @Override
            public void onDisconnect() {
                /*
                try {
                    JSONObject json = new JSONObject();
                    json.putOpt("market_name", marketName);
                    json.putOpt("customer_email", customerEmail);
                    socket.emit("disconnect", json);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                */
                CustomerNetwork.getInstance(context).setSocket(null);
                registerRestartAlarm();
                MyLog.i("Server Disconnect", "Connection disconnected");
            }

            @Override
            public void onConnect() {
                try {
                    // emit socket server login
                    JSONObject jsonUser = new JSONObject();
                    try{
                        jsonUser.put("id", CustomerFunction.getInstance(context).getCusID());
                        jsonUser.put("name", CustomerFunction.getInstance(context).getCusName());
                        jsonUser.put("login", "customer");

                        CustomerNetwork.getInstance(context).getSocket().emit("login", jsonUser);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (NullPointerException e){
                    Log.d(TAG, "NullPointerException : " + e.toString());
                }
                MyLog.i(TAG, "Server Connection connected - id : " + CustomerFunction.getInstance(context).getCusID());
            }

            @Override
            public void on(String event, IOAcknowledge ack, Object... data) {
                MyLog.i("Server triggered event", "Server triggered event '" + event + "'");

                // change the last chat num
                // AppPref.getInstance(context).put("CHAT_NUM", 1);
                if (event.equals("message_my")) {
                    MyLog.i(TAG, data[0].toString() + "\n" + data[1].toString());
                    Intent intent = new Intent();
                    try{
                        JSONObject myJSON = new JSONObject(data[0].toString());
                        JSONObject chatJSON = new JSONObject(data[1].toString());
                        intent.putExtra("room", myJSON.getString("room"));
                        intent.putExtra("id_my", myJSON.getString("cus_id"));
                        intent.putExtra("name_my", myJSON.getString("cus_name"));
                        intent.putExtra("msg_my", chatJSON.getString("message"));
                        intent.putExtra("type", chatJSON.getInt("type"));
                        intent.putExtra("path", chatJSON.getString("path"));
                        intent.putExtra("date_my", chatJSON.getString("send_date"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    intent.setAction(ConstValue.CHAT_MY_RECEIVER);
                    sendBroadcast(intent);
                }
                else if(event.equals("message_peers")){
                    MyLog.i(TAG, data[0].toString() + "\n" + data[1].toString());
                    Intent intent = new Intent();
                    try{
                        JSONObject ownerJSON = new JSONObject(data[0].toString());
                        JSONObject chatJSON = new JSONObject(data[1].toString());
                        intent.putExtra("id_peer", ownerJSON.getString("mar_id"));
                        intent.putExtra("name_peer", ownerJSON.getString("mar_name"));
                        intent.putExtra("msg_peer", chatJSON.getString("message"));
                        intent.putExtra("type", chatJSON.getInt("type"));
                        intent.putExtra("path", chatJSON.getString("path"));
                        intent.putExtra("date_peer", chatJSON.getString("send_date"));

                        // UPDATE CHAT_NUM
                        MyLog.d(TAG, "CHAT_NUM : " + chatJSON.getInt("num"));
                        AppPref.getInstance(context).put("CHAT_NUM", chatJSON.getInt("num"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    intent.setAction(ConstValue.CHAT_PEERS_RECEIVER);
                    sendBroadcast(intent);
                }
                else if(event.equals("my_chat_num")){
                    MyLog.d(TAG, "chat_num : " + data[0]);
                    int CHAT_NUM;
                    if(data[0].toString().equals("null")) CHAT_NUM = 0;
                    else CHAT_NUM = Integer.parseInt(data[0].toString());

                    // DO NOT UPDATE CHAT_NUM AT FIRST
                    if (AppPref.getInstance(context).getValue("CHAT_NUM", 0) != 0 &&
                            AppPref.getInstance(context).getValue("CHAT_NUM", 0) < CHAT_NUM) {
                        // get new_msg and send notification
                        Intent intent = new Intent();
                        intent.putExtra("chat_num", AppPref.getInstance(context).getValue("CHAT_NUM", 0));
                        intent.setAction(ConstValue.CHAT_UNREAD_RECEIVER);
                        sendBroadcast(intent);
                    }
                    // update chat_num
                    AppPref.getInstance(context).put("CHAT_NUM", CHAT_NUM);
                }
                else if(event.equals("owner_read_msg")){
                    MyLog.d(TAG, "owner_read_msg - mar_id : " + data[0]);
                    if(data[0] != null){
                        Intent intent = new Intent();
                        intent.putExtra("mar_id", data[0].toString());
                        intent.setAction(ConstValue.CHAT_READ_RECEIVER);
                        sendBroadcast(intent);
                    }
                }

                /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            output.setText(out_buf);
                        }
                    });
                */
            }
        });
        // This line is cached until the connection is established.
        // socket.send("Hello Server!");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        unregisterRestartAlarm();
        registerRestartAlarm();
        super.onTaskRemoved(rootIntent);
    }

    // restart service when sevice is dead
    private void registerRestartAlarm(){
        Intent intent = new Intent(ChatService.this, ChatService.class);
        PendingIntent sender = PendingIntent.getService(ChatService.this, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1*1000; // 0.1sec
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10*1000, sender);
    }

    // unregister alarm
    private void unregisterRestartAlarm(){
        Intent intent = new Intent(ChatService.this, ChatService.class);
        PendingIntent sender = PendingIntent.getService(ChatService.this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
}

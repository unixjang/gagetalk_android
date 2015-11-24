package com.gagetalk.gagetalkcustomer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.data.ChatData;
import com.gagetalk.gagetalkcustomer.data.DayData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 7/11/15.
 * 소켓이 중간에 끊어지고 다시 connect 되었을 때 못읽은 메시지 받아오기
 */
public class ChatUnreadReceiver extends BroadcastReceiver {

    private static final String TAG = "ChatUnreadReceiver";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        if(intent.getAction().equals(ConstValue.CHAT_UNREAD_RECEIVER)){
            // reqServer unread message
            int chatNum = intent.getIntExtra("chat_num", 0);
            MyLog.d(TAG, "chatNum : " + chatNum);
            reqServerCustomerUnreadChat(chatNum);
            // show new messages notifications
        }

    }

    private void reqServerCustomerUnreadChat(int chatNum){
        String url = ReqUrl.CustomerChatTask + "/unread";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("chat_num", String.valueOf(chatNum));
        RequestParams requestParams = new RequestParams(hashMap);
        Network.getInstance(context).reqPost(null, url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try{
                    int resultCode = response.getInt("resultCode");
                    MyLog.d(TAG, "resultCode : " + resultCode);
                    switch (resultCode){
                        case ConstValue.RESPONSE_NOT_LOGGED_IN:
                            break;
                        case ConstValue.RESPONSE_WRONG_PARAMETER:
                            break;
                        case ConstValue.RESPONSE_SUCCESS:
                            JSONArray chatJSONArray = response.getJSONArray("chat");
                            for(int i = 0; i<chatJSONArray.length(); i++) {
                                JSONObject chatJSONObject = chatJSONArray.getJSONObject(i);
                                String dateStr = chatJSONObject.getString("send_date");
                                DayData dayData = new DayData(
                                        Integer.parseInt(dateStr.substring(0, 4)),
                                        Integer.parseInt(dateStr.substring(5, 7)),
                                        Integer.parseInt(dateStr.substring(8, 10)),
                                        Integer.parseInt(dateStr.substring(11, 13)),
                                        Integer.parseInt(dateStr.substring(14, 16)),
                                        Integer.parseInt(dateStr.substring(17, 19)));

                                ChatData chatData = new ChatData(
                                        chatJSONObject.getString("mar_id"),
                                        chatJSONObject.getString("mar_name"),
                                        chatJSONObject.getString("cus_id"),
                                        chatJSONObject.getString("cus_name"),
                                        chatJSONObject.getString("message"),
                                        chatJSONObject.getInt("type"),
                                        chatJSONObject.getString("path"),
                                        dayData.getChatDate(),
                                        chatJSONObject.getInt("read_msg"),
                                        chatJSONObject.getString("sender")
                                );
                                chatData.setNum(chatJSONObject.getInt("num"));
                                // 작업중 : show notification for unread msg
                                if(!chatData.getSender().equals(CustomerFunction.getInstance(context).getCusID())){
                                    Intent intent = new Intent();
                                    intent.putExtra("id_peer", chatData.getMarId());
                                    intent.putExtra("name_peer", chatData.getMarName());
                                    intent.putExtra("msg_peer", chatData.getMessage());
                                    intent.putExtra("type", chatData.getType());
                                    intent.putExtra("path", chatData.getPath());
                                    intent.putExtra("date_peer", dateStr);
                                    intent.setAction(ConstValue.CHAT_PEERS_RECEIVER);
                                    context.sendBroadcast(intent);
                                }
                            }
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    MyLog.d(TAG, "error parsing json data");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                MyLog.d(TAG, "Failed getting unread message");
            }
        });
    }
}

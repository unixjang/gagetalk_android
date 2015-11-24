package com.gagetalk.gagetalkcustomer.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities.ChatActivity;
import com.gagetalk.gagetalkcustomer.adapter.ChatRoomAdapter;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.data.ChatRoomData;
import com.gagetalk.gagetalkcustomer.data.DayData;
import com.gagetalk.gagetalkcustomer.database.AccessDB;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/28/15.
 */
public class MsgFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "MsgFragment";

    private Activity activity;
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ChatRoomData> arrayChatRoom;
    private ListView listChatOn;
    private ChatRoomAdapter chatRoomAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.msg_fragment, container, false);

        activity = getActivity();
        context = getActivity().getApplicationContext();
        // register receiver for view more click in ProductListAdapter

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstValue.LOGIN_FILTER);
        intentFilter.addAction(ConstValue.SERVER_ALIVE_RECEIVER);
        intentFilter.addAction(ConstValue.CHAT_MY_RECEIVER);
        intentFilter.addAction(ConstValue.CHAT_PEERS_RECEIVER);
        activity.registerReceiver(msgFragReceiver, intentFilter);

        listChatOn = (ListView) view.findViewById(R.id.list_chat_on);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // send broadcast to MainFragment to change the fragment if not logged in
        if(!CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
            Intent intent = new Intent().setAction(ConstValue.MOVE_TO_MSG_FRAG_FILTER);
            intent.putExtra("fragment", ConstValue.HOME_FRAGMENT);
            context.sendBroadcast(intent);

        }
        // selectChatRoom();
        reqServerCustomerChatRoom();

    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(msgFragReceiver);
        super.onDestroyView();
    }

    private void selectChatRoom(){
        MyLog.i(TAG, "selectChatRoom onResume");
        // 1. setup data from local database first time
        arrayChatRoom = AccessDB.getInstance(context).selectChatRoom();
        // 2. setup adapter
        chatRoomAdapter = new ChatRoomAdapter(context, R.id.txt_name_peer, arrayChatRoom);
        // 3. bind adapter
        listChatOn.setAdapter(chatRoomAdapter);
        listChatOn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set chatroom msg and chat msg read
                Intent intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("mar_id", chatRoomAdapter.getItem(position).getMarId());
                intent.putExtra("mar_name", chatRoomAdapter.getItem(position).getMarName());
                startActivity(intent);
                // activity.finish();
                activity.overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
            }
        });
    }

    @Override
    public void onRefresh() {
        reqServerCustomerChatRoom();
    }

    private BroadcastReceiver msgFragReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "action : " + action);
            if (action.equals(ConstValue.LOGIN_FILTER)) {
                reqServerCustomerChatRoom();
                /*selectChatRoom();*/

                /* when logged in =>
                   1. select the data in server
                   2. insert the data existing in server
                 */
            }
            else if(action.equals(ConstValue.SERVER_ALIVE_RECEIVER)){
                MyLog.d(TAG, "SERVER ALIVE RECEIVER !!!!");
                reqServerCustomerChatRoom();
            }
            else if(action.equals(ConstValue.CHAT_PEERS_RECEIVER)){
                MyLog.d(TAG, "ChatBR PEER MSG RECEIVED!!!!!!!!!!!!!!!!!!");
                String idPeer = intent.getStringExtra("id_peer");
                String namePeer = intent.getStringExtra("name_peer");
                String msgPeer = intent.getStringExtra("msg_peer");
                int type = intent.getIntExtra("type", 0);
                String path = intent.getStringExtra("path");
                String datePeer = intent.getStringExtra("date_peer");

                DayData dayData = new DayData(
                        Integer.parseInt(datePeer.substring(0,4)),
                        Integer.parseInt(datePeer.substring(5,7)),
                        Integer.parseInt(datePeer.substring(8,10)),
                        Integer.parseInt(datePeer.substring(11,13)),
                        Integer.parseInt(datePeer.substring(14,16)),
                        Integer.parseInt(datePeer.substring(17,19)));

                ChatRoomData chatRoomData = new ChatRoomData(idPeer, namePeer,
                        CustomerFunction.getInstance(context).getCusID(), null, msgPeer,
                        type, path, dayData.getChatDate(), 0, idPeer);
                AccessDB.getInstance(context).updateChatRoom(chatRoomData);
                selectChatRoom();
            }
            else if(action.equals(ConstValue.CHAT_MY_RECEIVER)){
                MyLog.d(TAG, "ChatBR MY MSG RECEIVED!!!!!!!!!!!!!!!!!!");
                String room = intent.getStringExtra("room");
                String idMy = intent.getStringExtra("id_my");
                String nameMy = intent.getStringExtra("name_my");
                String msgMy = intent.getStringExtra("msg_my");
                int type = intent.getIntExtra("type", 0);
                String path = intent.getStringExtra("path");
                String dateMy = intent.getStringExtra("date_my");

                MyLog.d(TAG, "dateMy : " + dateMy);
                DayData dayData = new DayData(
                        Integer.parseInt(dateMy.substring(0,4)),
                        Integer.parseInt(dateMy.substring(5,7)),
                        Integer.parseInt(dateMy.substring(8,10)),
                        Integer.parseInt(dateMy.substring(11,13)),
                        Integer.parseInt(dateMy.substring(14,16)),
                        Integer.parseInt(dateMy.substring(17,19)));

                MyLog.d(TAG, "year : " + dayData.getYear() + ", month : " + dayData.getMonth() + ", day : " + dayData.getDay() + ", " +
                        "hour : " + dayData.getHour() + ", min : " + dayData.getMin() + ", sec : " + dayData.getSecond());

                ChatRoomData chatRoomData = new ChatRoomData(room, null,
                        idMy, nameMy, msgMy,
                        type, path, dayData.getChatDate(), 0, idMy);
                AccessDB.getInstance(context).updateChatRoom(chatRoomData);
                selectChatRoom();
            }
        }
    };

    private void reqServerCustomerChatRoom(){
        String url = ReqUrl.CustomerRoomTask + ReqUrl.SELECT;
        Network.getInstance(context).reqPost(activity, url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int resultCode = response.getInt("resultCode");
                    switch (resultCode){
                        case ConstValue.RESPONSE_NOT_LOGGED_IN:
                            // this will not happen because it is done after login (received from the broadcast login filter)
                            break;
                        case ConstValue.RESPONSE_NO_DATA:
                            AccessDB.getInstance(context).deleteAllChatRoom();
                            break;
                        case ConstValue.RESPONSE_SUCCESS:
                            JSONArray chatJSONArray = response.getJSONArray("chatroom");
                            /*MyLog.i(TAG, "chatJSONArray : " + chatJSONArray.toString());*/
                            for(int i = 0; i<chatJSONArray.length(); i++){
                                JSONObject chatJSONObject = chatJSONArray.getJSONObject(i);

                                String dateStr = chatJSONObject.getString("send_date");
                                MyLog.d(TAG, "dateStr : " + dateStr);
                                DayData dayData = new DayData(
                                        Integer.parseInt(dateStr.substring(0, 4)),
                                        Integer.parseInt(dateStr.substring(5, 7)),
                                        Integer.parseInt(dateStr.substring(8, 10)),
                                        Integer.parseInt(dateStr.substring(11, 13)),
                                        Integer.parseInt(dateStr.substring(14, 16)),
                                        Integer.parseInt(dateStr.substring(17, 19)));
                                MyLog.d(TAG, "dateData.getChatDate() : " + dayData.getChatDate());

                                ChatRoomData chatRoomData = new ChatRoomData(
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

                            /*MyLog.d(TAG, "ChatRoomData - "
                                    + "\n mar_id : " + chatRoomData.getMarId()
                                    + "\n mar_name : " + chatRoomData.getMarName()
                                            + "\n cus_id : " + chatRoomData.getCusId()
                                    + "\n cus_name : " + chatRoomData.getCusName()
                                    + "\n message : " + chatRoomData.getMessage()
                                            + "\n type : " + chatRoomData.getType()
                                            + "\n path : " + chatRoomData.getPath()
                                    + "\n send_date : " + chatRoomData.getSendDate()
                                    + "\n read_msg : " + chatRoomData.getReadMsg()
                                    + "\n sender : " + chatRoomData.getSender()
                            );*/
                                AccessDB.getInstance(context).insertChatRoom(chatRoomData);
                            }
                            break;
                    }
                    selectChatRoom();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Function.getInstance(context).logErrorParsingJson(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Network.getInstance(context).toastErrorMsg(activity);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
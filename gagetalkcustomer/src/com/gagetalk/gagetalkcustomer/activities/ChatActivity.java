package com.gagetalk.gagetalkcustomer.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.adapter.ChatAdapter;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.data.ChatData;
import com.gagetalk.gagetalkcustomer.data.DayData;
import com.gagetalk.gagetalkcustomer.database.AccessDB;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 4/5/15.
 */
public class ChatActivity extends Activity implements View.OnClickListener{
    /**
     * Called when the activity is first created.
     */

    private static String TAG = "ChatActivity";

    // message type
    private final static int TYPE_MESSAGE = 0;
    private final static int TYPE_PIC = 1;
    private final static int TYPE_FILE = 2;
    private final static int TYPE_MOV = 3;

    private Activity activity;
    private Context context;

    private ProgressBar progressBar;

    // layout
    private LinearLayout linBack;
    private TextView txtTitle;
    private LinearLayout linSearch; // search icon
    private RelativeLayout relSearch; // search layout that will be shown/hidden below
    private EditText editSearch;
    private ImageView imgErase;
    private ListView listChat;
    private LinearLayout linUpload;
    private ImageView imgPic;
    private ImageView imgCam;
    private ImageView imgFile;
    private ImageView imgMov;
    private ImageView imgPlus;
    private EditText editChat;
    private Button btnChat;

    private ArrayList<ChatData> arrayChat;
    private ChatAdapter chatAdapter;
    private String marId;
    private String marName;

    private Intent intent;


    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                if(listChat != null && arrayChat.size() != 0)
                    listChat.setSelection(arrayChat.size() - 1);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if(listChat != null && arrayChat.size() != 0)
                    listChat.setSelection(arrayChat.size() - 1);
                break;
        }
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.chat_activity);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_chat);

        activity = this;
        context = this;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstValue.CHAT_MY_RECEIVER);
        intentFilter.addAction(ConstValue.CHAT_PEERS_RECEIVER);
        // change the read status when the owner read the message
        intentFilter.addAction(ConstValue.CHAT_READ_RECEIVER);
        registerReceiver(chatBR, intentFilter);

        // editChat 포커스 되었을 때 레이아웃 위로 밀기
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        linBack = (LinearLayout) findViewById(R.id.lin_back);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        linSearch = (LinearLayout) findViewById(R.id.lin_search);
        relSearch = (RelativeLayout) findViewById(R.id.rel_search);
        editSearch = (EditText) findViewById(R.id.edit_search);
        imgErase = (ImageView) findViewById(R.id.img_erase);
        listChat = (ListView) findViewById(R.id.list_chat);
        listChat.addFooterView(Function.getInstance(context).getEmptyViewForListView());
        linUpload = (LinearLayout) findViewById(R.id.lin_upload);
        imgPic = (ImageView) findViewById(R.id.img_pic);
        imgCam = (ImageView) findViewById(R.id.img_cam);
        imgFile = (ImageView) findViewById(R.id.img_file);
        imgMov = (ImageView) findViewById(R.id.img_mov);
        imgPlus = (ImageView) findViewById(R.id.img_plus);
        editChat = (EditText) findViewById(R.id.edit_chat);
        btnChat = (Button) findViewById(R.id.btn_chat);

        marId = getIntent().getStringExtra("mar_id");
        CustomerFunction.getInstance(context).setChatRoom(marId);
        marName = getIntent().getStringExtra("mar_name");

        // check if the ChatActivity is started from notification
        if(getIntent().getBooleanExtra(ConstValue.ACTIVITY_STARTED_FROM_CHAT_NOTI, false)){
            // reset noti room
            CustomerFunction.getInstance(context).setNotiRoom(null);
        }else{
            if(CustomerFunction.getInstance(context).getNotiRoom() != null && CustomerFunction.getInstance(context).getNotiRoom().equals(marId)){
                CustomerFunction.getInstance(context).setNotiRoom(null);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(ConstValue.CHAT_NOTIFICATION_ID);

            }
        }

        MyLog.d(TAG, "mar_id : " + marName + ", mar_name : " + marName);

        // emit socket to change the read_msg flag
        if(CustomerNetwork.getInstance(context).getSocket() != null){
            MyLog.d(TAG, "emit evt_customer_read_msg - mar_id : " + marId);
            CustomerNetwork.getInstance(context).getSocket().emit("evt_customer_read_msg", marId);
        }

        txtTitle.setText(marName);
        editChat.setOnClickListener(this);
        linBack.setOnClickListener(this);
        linSearch.setOnClickListener(this);
        imgErase.setOnClickListener(this);
        imgPic.setOnClickListener(this);
        imgCam.setOnClickListener(this);
        imgFile.setOnClickListener(this);
        imgMov.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        btnChat.setOnClickListener(this);

        relSearch.setVisibility(View.GONE);
        linUpload.setVisibility(View.GONE);


        arrayChat = AccessDB.getInstance(context).selectChat(marId);
        chatAdapter = new ChatAdapter(context, R.id.txt_msg_my, arrayChat);
        listChat.setAdapter(chatAdapter);
        listChat.setSelection(arrayChat.size() - 1);
        // 아.. 이걸로 개고생 했는데 ㅠㅠ 이렇게하면 키보드가 올라오면 자동으로 listview 아래로 스크롤해준다..
        listChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        reqServerCustomerChat();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // set progressbar in listview
                if(progressBar == null) {
                    progressBar = new ProgressBar(context);
                    progressBar.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    AbsListView.LayoutParams.WRAP_CONTENT,
                                    AbsListView.LayoutParams.WRAP_CONTENT));
                    progressBar.setIndeterminate(true);     // animation is turned on

                    // to set progressbar in the middle, overlap with linearlayout
                    LinearLayout progressBarContainer = new LinearLayout(context);
                    progressBarContainer.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    AbsListView.LayoutParams.MATCH_PARENT,
                                    AbsListView.LayoutParams.MATCH_PARENT));
                    progressBarContainer.setGravity(Gravity.CENTER);
                    progressBarContainer.addView(progressBar);

                    // set linearlayout as an empty view and add it to root view
                    listChat.setEmptyView(progressBarContainer);
                    ViewGroup root = (ViewGroup) findViewById(R.id.rel_main);
                    root.addView(progressBarContainer);
                }
                progressBar.setVisibility(View.VISIBLE);
                if(chatAdapter != null)
                    chatAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(linSearch.isSelected()){
            relSearch.setVisibility(View.GONE);
            linSearch.setSelected(false);
            return;
        }
        if(imgPlus.isSelected()){
            linUpload.setVisibility(View.GONE);
            imgPlus.setSelected(false);
            return;
        }
        if(!MainActivity.IS_ALIVE){
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(ConstValue.ACTIVITY_STARTED_FROM_CHAT_ACTIVITY, true);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        CustomerFunction.getInstance(context).setChatRoom(null);
        if(chatBR != null){
            unregisterReceiver(chatBR);
            chatBR = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        /*
            lin_back
            lin_search
            img_erase
            img_pic
            img_cam
            img_file
            img_mov
            img_plus
            btn_chat
         */
        switch (v.getId()){
            case R.id.lin_back:
                onBackPressed();
                break;
            case R.id.lin_search:
                if(relSearch.getVisibility() == View.VISIBLE){
                    linSearch.setSelected(false);
                    relSearch.setVisibility(View.GONE);
                } else{
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editChat.getWindowToken(), 0);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linSearch.setSelected(true);
                            relSearch.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                }
                break;
            case R.id.img_erase:
                editSearch.setText("");
                break;
            case R.id.img_pic:
                break;
            case R.id.img_cam:
                break;
            case R.id.img_file:
                break;
            case R.id.img_mov:
                break;
            case R.id.edit_chat:
                linUpload.setVisibility(View.GONE);
                imgPlus.setSelected(false);
                break;
            case R.id.img_plus:
                if(linUpload.getVisibility() == View.GONE){
                    linUpload.setVisibility(View.VISIBLE);
                    imgPlus.setSelected(true);
                } else{
                    linUpload.setVisibility(View.GONE);
                    imgPlus.setSelected(false);
                }
                break;
            case R.id.btn_chat:
                try {
                    JSONObject ownerJSON = new JSONObject();
                    ownerJSON.putOpt("mar_id", marId);
                    ownerJSON.putOpt("mar_name", marName);
                    JSONObject chatJSON = new JSONObject();
                    chatJSON.putOpt("message", editChat.getText().toString());
                    chatJSON.putOpt("type", TYPE_MESSAGE);
                    chatJSON.putOpt("path", "");
                    chatJSON.putOpt("send_date", "");
                    if(CustomerNetwork.getInstance(context).getSocket() != null)
                        CustomerNetwork.getInstance(context).getSocket().emit("evt_msg_owner", ownerJSON, chatJSON);
                    else
                        CustomToast.getInstance(context).createToast("서버와 연결이 원할하지 않습니다. 잠시 후 다시 이용해주세요.");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                } finally {
                    editChat.setText("");
                }
                //socket.send("good!!");
                break;
        }
    }

    private void reqServerCustomerChat(){
        MyLog.d(TAG, "reqServerCustomerChat");
        String url = ReqUrl.CustomerChatTask + ReqUrl.SELECT;
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mar_id", marId);
        RequestParams requestParams = new RequestParams(hashMap);
        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int resultCode = response.getInt("resultCode");
                    switch (resultCode){
                        case ConstValue.RESPONSE_NOT_LOGGED_IN:
                            MyLog.d(TAG, "RESPONSE_NOT_LOGGED_IN");
                            CustomerNetwork.getInstance(context).logout();
                            onBackPressed();
                            // this will not happen because it is done after login (received from the broadcast login filter)
                            break;
                        case ConstValue.RESPONSE_NO_DATA:
                            MyLog.d(TAG, "RESPONSE_NO_DATA");
                            // hmm.... ???
                            break;
                        case ConstValue.RESPONSE_SUCCESS:
                            MyLog.d(TAG, "RESPONSE_SUCCESS");
                            JSONArray chatJSONArray = response.getJSONArray("chat");
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
                                chatAdapter.add(chatData);
                                // AccessDB.getInstance(context).insertChatRoom(chatRoomData);
                            }
                            chatAdapter.notifyDataSetChanged();
                            listChat.setSelection(chatAdapter.getCount()-1);

                            break;
                    }
                    // selectChatRoom();
                    // swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    Function.getInstance(context).logErrorParsingJson(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Network.getInstance(context).toastErrorMsg(activity);
                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private BroadcastReceiver chatBR = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // if owner read the message
            if(action.equals(ConstValue.CHAT_READ_RECEIVER)){
                String tmpMarId = intent.getStringExtra("mar_id");
                if(tmpMarId.equals(marId)){
                    for(int i=0; i<chatAdapter.getCount(); i++){
                        if(chatAdapter.getItem(i).getReadMsg() == ConstValue.READ_MSG_UNREAD)
                        chatAdapter.getItem(i).setReadMsg(ConstValue.READ_MSG_READ);
                    }
                    chatAdapter.notifyDataSetChanged();
                }
            }
            else if(action.equals(ConstValue.CHAT_PEERS_RECEIVER)){
                String idPeer = intent.getStringExtra("id_peer");
                String namePeer = intent.getStringExtra("name_peer");
                String msgPeer = intent.getStringExtra("msg_peer");
                int type = intent.getIntExtra("type", 0);
                String path = intent.getStringExtra("path");
                String datePeer = intent.getStringExtra("date_peer");
                MyLog.d(TAG, "ChatBR PEER MSG RECEIVED!!!!!!!!!!!!!!!!!!\n" +
                        "id_peer : " + idPeer +",\n" +
                        "name_peer : " + namePeer +",\n" +
                        "msg_peer : " + msgPeer +",\n" +
                        "type : " + type +",\n" +
                        "path : " + path +",\n" +
                        "datePeer : " + datePeer +",\n"
                );

                DayData dayData = new DayData(
                        Integer.parseInt(datePeer.substring(0,4)),
                        Integer.parseInt(datePeer.substring(5,7)),
                        Integer.parseInt(datePeer.substring(8,10)),
                        Integer.parseInt(datePeer.substring(11,13)),
                        Integer.parseInt(datePeer.substring(14,16)),
                        Integer.parseInt(datePeer.substring(17,19)));

                if(idPeer.equals(marId)){
                    // append ChatActivity peers message
                    ChatData chatData = new ChatData(idPeer, namePeer, null, null,
                            msgPeer, type, path, dayData.getChatDate(), ConstValue.READ_MSG_READ, idPeer);
                    chatAdapter.add(chatData);
                    chatAdapter.notifyDataSetChanged();
                    // user is watching current chatacitivity
                    CustomerNetwork.getInstance(context).getSocket().emit("evt_customer_read_msg", marId);
                }
            }else if(action.equals(ConstValue.CHAT_MY_RECEIVER)){
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

                MyLog.d(TAG, "year : " + dayData.getYear() + ", month : " + dayData.getMonth() + ", day : " + dayData.getDay() + ", hour : " + dayData.getHour() + ", min : " + dayData.getMin() + ", sec : " + dayData.getSecond());

                if(room.equals(marId)){
                    // append ChatActivity my message
                    ChatData chatData = new ChatData(null, null, idMy, nameMy, msgMy, type, path,
                            dayData.getChatDate(), ConstValue.READ_MSG_UNREAD, idMy);
                    chatAdapter.add(chatData);
                    chatAdapter.notifyDataSetChanged();
                }
            }
            listChat.setSelection(chatAdapter.getCount()-1);
        }
    };
}
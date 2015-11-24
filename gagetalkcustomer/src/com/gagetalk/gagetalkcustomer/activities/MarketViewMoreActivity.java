package com.gagetalk.gagetalkcustomer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities_dialog.LoginDialogActivity;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.data.ChatRoomData;
import com.gagetalk.gagetalkcustomer.data.MarketData;
import com.gagetalk.gagetalkcustomer.database.AccessDB;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 4/4/15.
 */
public class MarketViewMoreActivity extends Activity
    implements View.OnClickListener{


    private final static String TAG = "MarketViewMoreActivitiy";
    private Activity activity;
    private Context context;
    private ImageView imgMarket;

    // linearlayout
    private LinearLayout linTel;
    private LinearLayout linPhone;
    private LinearLayout linEmail;
    private LinearLayout linAddress;
    private LinearLayout linHomepage;

    // textview
    private TextView txtCategory;
    private TextView txtName;
    private TextView txtTel;
    private TextView txtPhone;
    private TextView txtEmail;
    private TextView txtAddress;
    private TextView txtHomepage;
    private TextView txtDescription;
    private Button btnCancel;
    private Button btnChat;
    private MarketData marketData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_view_more_activity);

        activity = this;
        context = this;

        Bundle bundle = getIntent().getExtras();
        marketData = bundle.getParcelable("marketVal");

        imgMarket = (ImageView) findViewById(R.id.img_market);

        // lin
        linTel = (LinearLayout) findViewById(R.id.lin_tel);
        linPhone = (LinearLayout) findViewById(R.id.lin_phone);
        linEmail = (LinearLayout) findViewById(R.id.lin_email);
        linAddress = (LinearLayout) findViewById(R.id.lin_address);
        linHomepage = (LinearLayout) findViewById(R.id.lin_homepage);
        linTel.setOnClickListener(this);
        linPhone.setOnClickListener(this);
        linEmail.setOnClickListener(this);
        linAddress.setOnClickListener(this);
        linHomepage.setOnClickListener(this);

        // textview
        txtCategory = (TextView) findViewById(R.id.txt_category);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtTel = (TextView) findViewById(R.id.txt_tel);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtEmail = (TextView) findViewById(R.id.txt_id);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtHomepage = (TextView) findViewById(R.id.txt_homepage);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnChat = (Button) findViewById(R.id.btn_chat);

        ImageDownloader.getInstance(context).getImage(marketData.getImg(), imgMarket);
        txtCategory.setText(marketData.getCategory());
        txtName.setText(marketData.getMarName());
        txtTel.setText(marketData.getTel());
        txtPhone.setText(marketData.getPhone());
        txtEmail.setText(marketData.getEmail());
        txtAddress.setText(marketData.getAddress());
        txtHomepage.setText(marketData.getHomepage());
        txtDescription.setText(marketData.getDescription());
        btnCancel.setOnClickListener(this);
        btnChat.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.fade_out);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_tel:
                CustomToast.getInstance(context).createToast("CallActivity");
                break;
            case R.id.lin_phone:
                CustomToast.getInstance(context).createToast("CallActivity");
                break;
            case R.id.lin_email:
                CustomToast.getInstance(context).createToast("EmailActivity");
                break;
            case R.id.lin_address:
                CustomToast.getInstance(context).createToast("AddressActivity");
                break;
            case R.id.lin_homepage:
                CustomToast.getInstance(context).createToast("HomepageActivity");
                break;
            case R.id.btn_cancel:
                btnCancel.setSelected(true);
                onBackPressed();
                break;
            case R.id.btn_chat:
                // ask login if not logged in
                if(!CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
                    Intent intent = new Intent(context, LoginDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_top, 0);
                } else{
                    btnChat.setSelected(true);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    ChatRoomData chatRoomData = new ChatRoomData(
                            marketData.getMarId(),
                            marketData.getMarName(),
                            CustomerFunction.getInstance(context).getCusID(),
                            CustomerFunction.getInstance(context).getCusName(),
                            "",
                            ConstValue.MSG_TEXT,
                            "",
                            dateFormat.format(date),
                            ConstValue.MSG_READ,
                            CustomerFunction.getInstance(context).getCusID()
                    );
                    // insert into database
                    AccessDB.getInstance(context).insertChatRoom(chatRoomData);
                    // send to server to insert into database in server
                    reqServerAddCustomerChatRoom(chatRoomData);
                }
                break;
        }
    }

    private void reqServerAddCustomerChatRoom(ChatRoomData chatRoomData){
        Log.i(TAG, "reqServerAccountUpdate");
        String url = ReqUrl.CustomerRoomTask + ReqUrl.INSERT;
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("mar_id", chatRoomData.getMarId());
        paramMap.put("mar_name", chatRoomData.getMarName());
/*
        paramMap.put("message", chatRoomData.getMessage());
        paramMap.put("type", String.valueOf(chatRoomData.getType()));
        paramMap.put("path", chatRoomData.getPath());
        paramMap.put("send_date", chatRoomData.getSendDate());
        paramMap.put("read_msg", String.valueOf(chatRoomData.getReadMsg()));
        paramMap.put("sender", String.valueOf(chatRoomData.getSender()));
*/
        RequestParams requestParams = new RequestParams(paramMap);

        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // check resultCode
                int resultCode = 0;
                try {
                    resultCode = response.getInt("resultCode");
                }catch (Exception e){
                    Function.getInstance(context).logErrorParsingJson(e);
                }

                int checkResponse = CustomerNetwork.getInstance(context).checkResponse(resultCode, true);

                if (checkResponse == ConstValue.RESPONSE_SUCCESS) {
                    try {
                        // send broadcast to mainfragment
                        Intent intent = new Intent().setAction(ConstValue.MOVE_TO_MSG_FRAG_FILTER);
                        intent.putExtra("fragment", ConstValue.MSG_FRAGMENT);
                        intent.putExtra("mar_id", marketData.getMarId());
                        intent.putExtra("mar_name", marketData.getMarName());
                        context.sendBroadcast(intent);
                        onBackPressed();
                    } catch (Exception e) {
                        Function.getInstance(context).logErrorParsingJson(e);
                    }
                } else if(checkResponse == ConstValue.RESPONSE_NOT_LOGGED_IN){
                    btnChat.setSelected(false);
                    Intent intent = new Intent(context, LoginDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_top, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Network.getInstance(context).toastErrorMsg(activity);
            }
        });
    }
}
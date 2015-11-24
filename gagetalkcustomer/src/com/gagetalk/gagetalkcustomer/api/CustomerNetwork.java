package com.gagetalk.gagetalkcustomer.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstPrefValue;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.socket.SocketIO;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.service.ChatService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 6/21/15.
 */
public class CustomerNetwork {

    private static final String TAG = "CustomerNetwork";

    private static CustomerNetwork function;
    private SharedPreferences sharedPreferences;
    private Context context;
    private SocketIO socket;


    private CustomerNetwork(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static CustomerNetwork getInstance(Context context) {
        if (function == null) function = new CustomerNetwork(context);
        return function;
    }

    public void setSocket(SocketIO socket) {
        this.socket = socket;
    }

    public SocketIO getSocket() {
        return socket;
    }

    public void connectSocket(){
        if(getSocket() == null || !getSocket().isConnected()) {
            // StrictMode.enableDefaults();
            //* start : unix.jang => the way of code to ignore the policy violoation *//*
            StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                    .permitDiskWrites()
                    .build());
            //doCorrectStuffThatWritesToDisk();
            StrictMode.setThreadPolicy(old);
            //* end : unix.jang => ignoring policy violation *//*
            Intent serviceIntent = new Intent(context, ChatService.class);
            context.startService(serviceIntent);
            MyLog.i(TAG, "########## SET SOCKET BECAUSE IT IS NULL or not connected!!!!!!!!! ###########");
            // Logged int
        }
    }

    public void disconnectSocket(){
        if(getSocket() != null){
            Intent serviceIntent = new Intent(context, ChatService.class);
            context.stopService(serviceIntent);
            getSocket().disconnect();
            setSocket(null);
        }
    }


    public void logout() {
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ConstPrefValue.ID, null);
        editor.putString(ConstPrefValue.NAME, null);
        editor.putString(ConstPrefValue.PASSWORD, null);
        editor.commit();
        CustomToast.getInstance(context).createToast(context.getResources().getString(com.gagetalk.gagetalkcommon.R.string.logout_success));
        // disconnect socket
        disconnectSocket();
        context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
    }

    public boolean forceLogin(final Activity activity) {
        MyLog.i(TAG, "########## forceLogin ##########");
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);

        // check login from local database
        if (pref.getString(ConstPrefValue.ID, null) != null) {
            // MyLog.i(TAG, "logged in id : " + pref.getString(ConstPrefValue.ID, null));
            MyLog.i(TAG, "logged in id : " + CustomerFunction.getInstance(context).getCusID());
            // set session if not logged in

            // if locally logged in request to server login
            String url = ReqUrl.CustomerLoginTask + "/forcelogin";
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("cus_id",CustomerFunction.getInstance(context).getCusID());
            hashMap.put("password", CustomerFunction.getInstance(context).getCusPW());
            RequestParams requestParams = new RequestParams(hashMap);
            Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int resultCode = response.getInt("resultCode");
                        if(resultCode == 1){
                            // success logged in
                            CustomerFunction.getInstance(context).cusLogin(
                                    CustomerFunction.getInstance(context).getCusID(),
                                    CustomerFunction.getInstance(context).getCusName(),
                                    CustomerFunction.getInstance(context).getCusPW()
                            );
                        }
                        else{
                            // not successed so logout
                            logout();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Function.getInstance(context).logErrorParsingJson(e);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Network.getInstance(context).toastErrorMsg(activity);
                }
            });

            return true;
        }
        else return false;
    }

    // checking the server response from the mainActivity
    public int checkResponse(int resultCode, boolean isChildActivity){
        if(resultCode == ConstValue.RESPONSE_NOT_LOGGED_IN){
            logout();
            // sendbroadcast only if MainActivity is front activity
            if(!isChildActivity){
                Intent intent = new Intent().setAction(ConstValue.LOGIN_FILTER);
                context.sendBroadcast(intent);
            }
            CustomToast.getInstance(context).createToast(context.getResources().getString(com.gagetalk.gagetalkcommon.R.string.login_request));
            return ConstValue.RESPONSE_NOT_LOGGED_IN;
        }
        else if(resultCode == ConstValue.RESPONSE_NO_DATA){
            CustomToast.getInstance(context).createToast(context.getResources().getString(com.gagetalk.gagetalkcommon.R.string.response_no_data));
            return ConstValue.RESPONSE_NO_DATA;
        }
        return ConstValue.RESPONSE_SUCCESS;
    }
}

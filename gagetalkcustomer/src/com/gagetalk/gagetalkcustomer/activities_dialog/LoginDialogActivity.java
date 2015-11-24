package com.gagetalk.gagetalkcustomer.activities_dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities.SignupActivity;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


/**
 * Created by hyochan on 3/28/15.
 */
public class LoginDialogActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "LoginDialogActivity";

    private Activity activity;
    private Context context;
    private ImageView imgClose;
    // ui for body
    private EditText editCusId;
    private EditText editPw;
    private Button btnLogin;
    private Button btnSignup;

    // usage intent onclick listener
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_dialog_activity);

        activity = this;
        context = this;
        activity.setFinishOnTouchOutside(false);

        imgClose = (ImageView) findViewById(R.id.img_close);
        imgClose.setOnClickListener(this);
        // unix.jang => init ui for body
        editCusId = (EditText) findViewById(R.id.edit_id);
        editPw = (EditText) findViewById(R.id.edit_pw);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_close:
                Function.getInstance(context).hideKeyboard(activity);
                finish();
                overridePendingTransition(R.anim.fade_out, 0);
                break;
            case R.id.btn_login:
                if(!isEditTextEmpty()) reqServerCustomerLogin();
                break;
            case R.id.btn_signup:
                intent = new Intent(LoginDialogActivity.this, SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
    }

    private boolean isEditTextEmpty(){
        if(editCusId.getText().toString().equals("") || editPw.getText().toString().equals("")) {
            CustomToast.getInstance(context).
                    createToast(getResources().getString(R.string.plz_type_id_password));
            return true;
        }
        else { return false; }
    }

    private void reqServerCustomerLogin(){
        String url = ReqUrl.CustomerLoginTask;
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("cus_id", editCusId.getText().toString());
        paramMap.put("password", editPw.getText().toString());
        RequestParams requestParams = new RequestParams(paramMap);
        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                try {
                    resultCode = response.getInt("resultCode");
                    switch (resultCode) {
                        case ConstValue.RESPONSE_NOT_LOGGED_IN:
                            CustomToast.getInstance(context).createToast(getResources().getString(R.string.login_failed));
                            break;
                        case ConstValue.RESPONSE_SUCCESS:
                            // set login pref
                            CustomerFunction.getInstance(context).cusLogin(
                                    response.getString("cus_id"),
                                    response.getString("name"),
                                    editPw.getText().toString()
                            );
                            CustomToast.getInstance(context).createToast(getResources().getString(R.string.hello) + " " +
                                    CustomerFunction.getInstance(context).getCusName() + getResources().getString(R.string.nim));
                            context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
                            CustomerNetwork.getInstance(context).connectSocket();
                            finish();

                            overridePendingTransition(R.anim.fade_out, 0);
                            break;
                        case ConstValue.RESPONSE_NO_REQ_PARAM:
                            CustomToast.getInstance(context).createToast(getResources().getString(R.string.missing_req_param));
                            break;
                    }
                }catch (Exception e) {
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
    }
}
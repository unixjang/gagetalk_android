package com.gagetalk.gagetalkowner.activities_dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkowner.R;
import com.gagetalk.gagetalkowner.activities.SignupActivity;
import com.gagetalk.gagetalkowner.api.OwnerFunction;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/29/15.
 */
public class LoginDialogActivity extends Activity implements View.OnClickListener{

    private Activity activity;
    private Context context;
    private ImageView imgClose;
    // ui for body
    private EditText editID;
    private EditText editPW;
    private Button btnLogin;
    private Button btnSignup;

    // usage of intent on click listener
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_activity);

        activity = this;
        context = this;
        activity.setFinishOnTouchOutside(false);

        imgClose = (ImageView) findViewById(R.id.img_close);
        imgClose.setOnClickListener(this);
        // unix.jang => init ui for body
        editID = (EditText) findViewById(R.id.edit_id);
        editPW = (EditText) findViewById(R.id.edit_pw);
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
                if(!isEditTextEmpty()) reqServerOwnerLogin();
                break;
            case R.id.btn_signup:
                intent = new Intent(LoginDialogActivity.this, SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
    }

    private boolean isEditTextEmpty(){
        if(editID.getText().toString().equals("") || editPW.getText().toString().equals("")) {
            CustomToast.getInstance(context).
                    createToast(getResources().getString(R.string.plz_type_id_password));
            return true;
        }
        else { return false; }
    }

    private void reqServerOwnerLogin(){
        String url = ReqUrl.OwnerLoginTask;
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("mar_id", editID.getText().toString());
        paramMap.put("password", editPW.getText().toString());
        RequestParams requestParams = new RequestParams(paramMap);
        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                try {
                    resultCode = response.getInt("resultCode");
                } catch (Exception e) {
                    Function.getInstance(context).logErrorParsingJson(e);
                }
                if (resultCode == ConstValue.RESULT_FAILED) {
                    CustomToast.getInstance(context).
                            createToast(getResources().getString(R.string.login_failed));
                } else {
                    try {
                        OwnerFunction.getInstance(context).marLogin(
                                response.getString("mar_id"),
                                response.getString("mar_name"),
                                editPW.getText().toString()
                        );
                    }catch (Exception e) {
                        Function.getInstance(context).logErrorParsingJson(e);
                        e.printStackTrace();
                    }
                    CustomToast.getInstance(context).createToast(getResources().getString(R.string.hello) + " " +
                            OwnerFunction.getInstance(context).getMarID() + getResources().getString(R.string.nim));
                    finish();
                    context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
                    overridePendingTransition(R.anim.fade_out, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Network.getInstance(context).toastErrorMsg(activity);
            }
        });
    }
}
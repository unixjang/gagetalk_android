package com.gagetalk.gagetalkcustomer.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/28/15.
 */
public class SignupActivity extends Activity implements View.OnClickListener {

    private final String TAG = "SignupActivity";
    private Activity activity;
    private Context context;

    private ImageView imgClose;
    private EditText editCusId;
    private EditText editPw;
    private EditText editPwOk;
    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private Button btnSignup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        activity = this;
        context = this;

        imgClose = (ImageView) findViewById(R.id.img_close);
        editCusId = (EditText) findViewById(R.id.edit_cus_id);
        editPw = (EditText) findViewById(R.id.edit_pw);
        editPwOk = (EditText) findViewById(R.id.edit_pw_ok);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editName = (EditText) findViewById(R.id.edit_name);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        imgClose.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_close :
                finish();
                break;
            case R.id.btn_signup :
                if(checkEmptyEditText()){
                    reqServerCustomerSignup();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onBackPressed();
    }

    private boolean checkEmptyEditText() {
        if (editCusId.getText().toString().equals("")) {
            return false;
        }
        if (editEmail.getText().toString().equals("")) {
            return false;
        }
        if (editPw.getText().toString().equals("")) {
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.plz_write_password));
            return false;
        }
        if (editPwOk.getText().toString().equals("")) {
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.plz_check_password));
            return false;
        }
        if (editName.getText().toString().equals("") || editPhone.getText().toString().equals("")) {
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.plz_write_customer_info));
            return false;
        }
        return true;
    }

    private boolean validateEditText(){
        if(!Function.getInstance(context).checkId(editCusId.getText().toString())){
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.id_isnt_alphanumeric));
            return false;
        }
        if(!Function.getInstance(context).checkEmail(editEmail.getText().toString())){
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.is_not_validate_email));
            return false;
        }
        if(!Function.getInstance(context).checkPw(editPw.getText().toString(), editPwOk.getText().toString())){
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.pw_pwok_not_same));
            return false;
        }
        if(!Function.getInstance(context).checkName(editName.getText().toString())){
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.write_name_in_hangul));
            return false;
        }
        if(!Function.getInstance(context).checkPhone(editPhone.getText().toString())){
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.phone_number_is_not_correct));
            return false;
        }
        return true;
    }

    private void reqServerCustomerSignup(){
        String url = ReqUrl.CustomerSignupTask;
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("cus_id", editCusId.getText().toString());
        paramMap.put("password",  editPw.getText().toString());
        paramMap.put("email", editEmail.getText().toString());
        paramMap.put("name", editName.getText().toString());
        paramMap.put("phone", editPhone.getText().toString());
        RequestParams requestParams = new RequestParams(paramMap);
        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                try{
                    resultCode = response.getInt("resultCode");
                }catch (Exception e){
                    MyLog.e(TAG, "Error parsing json : " + e.toString());
                }

                switch (resultCode){
                    case ConstValue.RESPONSE_ALREADY_INSERTED:
                        CustomToast.getInstance(context).createToast(getResources().getString(R.string.signup_id_exist));
                        break;
                    case ConstValue.RESPONSE_SUCCESS:
                        CustomToast.getInstance(context).createToast(getResources().getString(R.string.signup_success_plz_login));
                        finish();
                        break;
                    case ConstValue.RESPONSE_NO_REQ_PARAM:
                        CustomToast.getInstance(context).createToast(getResources().getString(R.string.missing_req_param));
                        break;
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
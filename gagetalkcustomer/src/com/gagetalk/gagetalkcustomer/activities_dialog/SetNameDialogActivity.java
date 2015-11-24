package com.gagetalk.gagetalkcustomer.activities_dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.preference.CustomerPref;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 4/5/15.
 */
public class SetNameDialogActivity extends Activity
    implements View.OnClickListener{

    private static final String TAG = "SetNameDialogActivity";

    private String originalName;

    private Activity activity;
    private Context context;

    private ImageView imgCLose;
    private ImageView imgErase;
    private EditText editName;
    private Button btnUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_name_dialog_activity);

        activity = this;
        context = this;

        imgCLose = (ImageView) findViewById(R.id.img_close);
        imgErase = (ImageView) findViewById(R.id.img_erase);
        editName = (EditText) findViewById(R.id.edit_name);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnUpdate.setEnabled(false);

        Intent intent = getIntent();
        originalName = intent.getStringExtra(("name"));
        editName.setText(originalName);

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.i(TAG, "s : " + s);
                MyLog.i(TAG, "original name : " + originalName);
                // disable btn if name is same
                if(s.toString().equals(originalName)){
                    btnUpdate.setEnabled(false);
                    imgErase.setVisibility(View.VISIBLE);
                }
                // hide erase btn if text is empty
                else if(s.toString().equals("")){
                    btnUpdate.setEnabled(false);
                    imgErase.setVisibility(View.GONE);
                }
                else{
                    btnUpdate.setEnabled(true);
                    imgErase.setVisibility(View.VISIBLE);
                }
            }
        });

        imgCLose.setOnClickListener(this);
        imgErase.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

    }

    private void reqServerUpdateCustomerName(){
        MyLog.i(TAG, "reqServerUpdateCustomerName");
        String url = ReqUrl.CustomerAccountTask + ReqUrl.UPDATE;
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("request_param", String.valueOf(ConstValue.UPDATE_NAME));
        paramMap.put("request_value", editName.getText().toString());
        RequestParams requestParams = new RequestParams(paramMap);

        Network.getInstance(context).reqPost(activity, url,requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                int resultCode = 0;
                try {
                    resultCode = response.getInt("resultCode");
                }catch (Exception e){
                    Function.getInstance(context).logErrorParsingJson(e);
                }

                int checkResponse = CustomerNetwork.getInstance(context).checkResponse(resultCode, true);
                if(checkResponse == ConstValue.RESPONSE_NOT_LOGGED_IN){
                    onBackPressed();
                }
                else if (checkResponse == ConstValue.RESPONSE_SUCCESS) {
                    try {
                        switch (resultCode) {
                            case ConstValue.RESPONSE_NOT_LOGGED_IN:
                                CustomToast.getInstance(context).createToast(getResources().getString(R.string.failed_getting_account_info));
                                context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
                                break;
                            case 1:
                                JSONArray jsonArr1 = new JSONArray("[" + response.getString("res_data") + "]");
                                JSONObject jsonObj1 = jsonArr1.getJSONObject(0);

                                CustomerFunction.getInstance(context).setCusName(jsonObj1.getString("name"));
                                CustomerPref.getInstance(context).setName(jsonObj1.getString("name"));
                                setResult(Activity.RESULT_OK);
                                onBackPressed();
                                break;
                        }

                    } catch (Exception e) {
                        Function.getInstance(context).logErrorParsingJson(e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Network.getInstance(context).toastErrorMsg(activity);
            }
        });
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
            case R.id.img_close:
                onBackPressed();
                break;
            case R.id.img_erase:
                editName.setText("");
                break;
            case R.id.btn_update:
                reqServerUpdateCustomerName();
                break;
        }
    }
}
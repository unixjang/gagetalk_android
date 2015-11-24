package com.gagetalk.gagetalkcustomer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.preference.CustomerPref;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 4/11/15.
 */
public class AccountUpdateActivity extends Activity
    implements
        View.OnClickListener{

    private final static String TAG = "AccountUpdateActivity";
    private LinearLayout linBack;
    private TextView txtTitle;
    private TextView txtLabel;
    private ImageView imgErase;
    private EditText editVal;
    private Button btnUpdate;

    private Activity activity;
    private Context context;
    private int position;
    private String originalVal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_update_activity);

        activity = this;
        context = this;

        linBack = (LinearLayout) findViewById(R.id.lin_back);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtLabel = (TextView) findViewById(R.id.txt_label);
        imgErase = (ImageView) findViewById(R.id.img_erase);
        editVal = (EditText) findViewById(R.id.edit_val);
        btnUpdate = (Button) findViewById(R.id.btn_update);

        btnUpdate.setEnabled(false);
        txtTitle.setText(getIntent().getStringExtra("title"));
        txtLabel.setText(getIntent().getStringExtra("label"));
        originalVal = getIntent().getStringExtra("value");
        position = getIntent().getIntExtra("position", 0);
        editVal.setText(originalVal);
        linBack.setOnClickListener(this);
        imgErase.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        if(position == ConstValue.ACCOUNT_PASSWORD_POS){
            editVal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        // change the edittext size
        if(position == ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS){
            imgErase.setVisibility(View.GONE);
            editVal.getLayoutParams().height = Function.getInstance(context).dpToPx(200);
            editVal.setSingleLine(false);
        }
        else{
            imgErase.setVisibility(View.VISIBLE);
            editVal.getLayoutParams().height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
            editVal.setSingleLine(true);
        }

        editVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.i(TAG, "s : " + s);
                MyLog.i(TAG, "originalVal : " + originalVal);
                // disable btn if name is same
                if(s.toString().equals(originalVal)){
                    btnUpdate.setEnabled(false);
                    if(position != ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS)
                        imgErase.setVisibility(View.VISIBLE);
                }
                // hide erase btn if text is empty
                else if(s.toString().equals("")){
                    btnUpdate.setEnabled(false);
                    if(position != ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS)
                        imgErase.setVisibility(View.GONE);
                }
                else{
                    btnUpdate.setEnabled(true);
                    if(position != ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS)
                        imgErase.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
                int reqParam = 0;
                switch (position){
                    case ConstValue.ACCOUNT_PASSWORD_POS:
                        reqParam = ConstValue.UPDATE_PASSWORD;
                        break;
                    case ConstValue.ACCOUNT_NAME_POS:
                        reqParam = ConstValue.UPDATE_NAME;
                        break;
                    case ConstValue.ACCOUNT_PHONE_POS:
                        reqParam = ConstValue.UPDATE_PHONE;
                        break;
                    case ConstValue.ACCOUNT_PROFILE_EMAIL_POS:
                        reqParam = ConstValue.UPDATE_EMAIL;
                        break;
                    case ConstValue.ACCOUNT_PROFILE_IMG_POS:
                        reqParam = ConstValue.UPDATE_IMG;
                        break;
                    case ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS:
                        reqParam = ConstValue.UPDATE_DESCRIPTION;
                        break;
                }
                reqServerAccountUpdate(reqParam, editVal.getText().toString());
                break;
            case R.id.img_erase:
                editVal.setText("");
                break;
            case R.id.lin_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out);
        super.onBackPressed();
    }

    private void reqServerAccountUpdate(final int reqParam, String reqValue){
        Log.i(TAG, "reqServerAccountUpdate");
        String url = ReqUrl.CustomerAccountTask + ReqUrl.UPDATE;
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("request_param", String.valueOf(reqParam));
        paramMap.put("request_value", reqValue);

        RequestParams requestParams = new RequestParams(paramMap);
        Network.getInstance(context).reqPost(activity, url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                // getting resultCode
                try {
                    resultCode = response.getInt("resultCode");
                }catch (Exception e){
                    Function.getInstance(context).logErrorParsingJson(e);
                }
                // getting res_data

                try{
                    JSONObject resObject = response.getJSONObject("res_data");
                    if (CustomerNetwork.getInstance(context).checkResponse(resultCode, true) == ConstValue.RESPONSE_SUCCESS
                            &&  resObject != null) {
                        String resData;
                        switch (reqParam) {
                            case ConstValue.UPDATE_NAME:
                                resData = resObject.getString("name");
                                CustomerFunction.getInstance(context).setCusName(resData);
                                CustomerPref.getInstance(context).setName(resData);
                                break;
                            case ConstValue.UPDATE_PASSWORD:
                                resData = resObject.getString("password");
                                CustomerPref.getInstance(context).setPassword(resData);
                                break;
                            case ConstValue.UPDATE_PHONE:
                                resData = resObject.getString("phone");
                                CustomerPref.getInstance(context).setPhone(resData);
                                break;
                            case ConstValue.UPDATE_IMG:
                                // img url is the same
                                break;
                            case ConstValue.UPDATE_EMAIL:
                                resData = resObject.getString("email");
                                CustomerPref.getInstance(context).setEmail(resData);
                                break;
                            case ConstValue.UPDATE_DESCRIPTION:
                                resData = resObject.getString("description");
                                CustomerPref.getInstance(context).setDescription(resData);
                                break;
                        }
                        if (resultCode == ConstValue.RESPONSE_SUCCESS) {
                            Intent intent = new Intent();
                            intent.putExtra("position", position);
                            intent.putExtra("value", editVal.getText().toString());
                            setResult(Activity.RESULT_OK, intent);
                            onBackPressed();
                        }
                    }
                }catch (Exception e){
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
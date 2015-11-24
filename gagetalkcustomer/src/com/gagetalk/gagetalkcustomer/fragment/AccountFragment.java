package com.gagetalk.gagetalkcustomer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities.AccountUpdateActivity;
import com.gagetalk.gagetalkcustomer.adapter.MenuAdapter;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.data.DayData;
import com.gagetalk.gagetalkcustomer.data.MenuData;
import com.gagetalk.gagetalkcustomer.preference.CustomerPref;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/28/15.
 */
public class AccountFragment extends Fragment
    implements
        View.OnClickListener,
        AdapterView.OnItemClickListener{

    private static final String TAG = "AccountFragment";

    private Activity activity;
    private Context context;
    private Intent intent;

    private ImageView imgHamburger;
    private TextView txtTitle;
    private ListView listAccount;

    private MenuAdapter menuAdapter;

    public interface HamburgerBtnClick {
        void onHamburgerBtnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        activity = getActivity();
        context = getActivity().getApplicationContext();

        imgHamburger = (ImageView) view.findViewById(R.id.img_hamburger);
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setText(context.getResources().getString(R.string.my_account));

        listAccount = (ListView) view.findViewById(R.id.list_account);

        // 1. setup data
        String[] arrayHelp = getResources().getStringArray(R.array.array_account);
        ArrayList<MenuData> arrayCustom = new ArrayList<>();


/*
        for(int i=0; i<arrayHelp.length; i++){
            arrayCustom.add(new MenuData(
                    arrayHelp[i].toString(), ""
            ));
        }
*/
        for(String str : arrayHelp){
            arrayCustom.add(new MenuData(
                    str, ""
            ));
        }
        // 2. setup adapter
        menuAdapter = new MenuAdapter(context, R.layout.custom_adapter, arrayCustom);
        // 3. bind to listview
        listAccount.setAdapter(menuAdapter);
        listAccount.setOnItemClickListener(this);

        if(listAccount.getHeaderViewsCount() == 0) {
            listAccount.addHeaderView(Function.getInstance(context).getEmptyViewForListView());
        }
        if(listAccount.getFooterViewsCount() == 0){
            listAccount.addFooterView(Function.getInstance(context).getEmptyViewForListView());
        }

        imgHamburger.setOnClickListener(this);

        getAccountInfo();

        return view;
    }

    private void setAccountInfo(){
        // show account data
        menuAdapter.getItem(ConstValue.ACCOUNT_MAIN_POS).setTxtMore(
                CustomerPref.getInstance(context).getCusId()
        );
        // password
        menuAdapter.getItem(ConstValue.ACCOUNT_PASSWORD_POS).setTxtMore(
                CustomerPref.getInstance(context).getPassword()
        );
        // name
        menuAdapter.getItem(ConstValue.ACCOUNT_NAME_POS).setTxtMore(
                CustomerPref.getInstance(context).getName()
        );
        // phone
        menuAdapter.getItem(ConstValue.ACCOUNT_PHONE_POS).setTxtMore(
                CustomerPref.getInstance(context).getPhone()
        );
        // signup date
        menuAdapter.getItem(ConstValue.ACCOUNT_SIGNUP_DATE_POS).setTxtMore(
                CustomerPref.getInstance(context).getDateSign()
        );
        // login date
        menuAdapter.getItem(ConstValue.ACCOUNT_LOGIN_DATE_POS).setTxtMore(
                CustomerPref.getInstance(context).getDateLogin().getMonth()
                        + getResources().getString(R.string.month) + " " +
                CustomerPref.getInstance(context).getDateLogin().getDay()
                        + getResources().getString(R.string.day) + " " +
                CustomerPref.getInstance(context).getDateLogin().getHour() + ":" +
                CustomerPref.getInstance(context).getDateLogin().getMin() + ":" +
                CustomerPref.getInstance(context).getDateLogin().getSecond()
        );
        // email
        menuAdapter.getItem(ConstValue.ACCOUNT_PROFILE_EMAIL_POS).setTxtMore(
                CustomerPref.getInstance(context).getEmail()
        );
        // description
        menuAdapter.getItem(ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS).setTxtMore(
                CustomerPref.getInstance(context).getDescription()
        );
        menuAdapter.notifyDataSetChanged();
    }

    private void getAccountInfo(){
        if(!CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
            context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
            CustomToast.getInstance(context).createToast(getResources().getString(R.string.login_request));
        }
        else if(CustomerPref.getInstance(context).getCusId() == null){
            reqServerAccountInfo();
        } else{
            // reqServerAccountInfo();
            setAccountInfo();
        }
    }

    private void reqServerAccountInfo(){
        Log.i(TAG, "reqServerAccountInfo");
        String url = ReqUrl.CustomerAccountTask + ReqUrl.SELECT;
        // HashMap<String, String> paramMap = new HashMap<>();
        // RequestParams requestParams = new RequestParams(paramMap);
        Network.getInstance(context).reqPost(activity, url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                // try get resultCode
                try {
                    resultCode = response.getInt("resultCode");
                }catch (Exception e){
                    Function.getInstance(context).logErrorParsingJson(e);
                }
                int checkResponse = CustomerNetwork.getInstance(context).checkResponse(resultCode, false);

                // try get customer JSONObject
                try{
                    JSONObject jsonCustomer = response.getJSONObject("customer");
                    if (checkResponse == ConstValue.RESPONSE_SUCCESS && jsonCustomer != null) {
                        // set CustomerData
                        String dateLogin = null;
                        try {
                            CustomerPref.getInstance(context).setCusId(jsonCustomer.getString("cus_id"));
                            CustomerPref.getInstance(context).setPassword(jsonCustomer.getString("password"));
                            CustomerPref.getInstance(context).setEmail(jsonCustomer.getString("email"));
                            CustomerPref.getInstance(context).setName(jsonCustomer.getString("name"));
                            CustomerPref.getInstance(context).setPhone(jsonCustomer.getString("phone"));
                            CustomerPref.getInstance(context).setImg(jsonCustomer.getString("img"));
                            CustomerPref.getInstance(context).setDescription(jsonCustomer.getString("description"));
                            CustomerPref.getInstance(context).setDateSign(
                                    jsonCustomer.getString("date_sign").substring(0, 10)
                            );
                            dateLogin = jsonCustomer.getString("date_login");
                        }catch (Exception e){
                            Function.getInstance(context).logErrorParsingJson(e);
                        }
                        Log.i(TAG, "dateLogin : " + dateLogin);

                        DayData dayData = new DayData(
                                Integer.parseInt(dateLogin.substring(0,4)),
                                Integer.parseInt(dateLogin.substring(5,7)),
                                Integer.parseInt(dateLogin.substring(8,10)),
                                Integer.parseInt(dateLogin.substring(11,13)),
                                Integer.parseInt(dateLogin.substring(14,16)),
                                Integer.parseInt(dateLogin.substring(17,19)));
                        CustomerPref.getInstance(context).setDateLogin(dayData);
                        setAccountInfo();
                    } else{
                        CustomToast.getInstance(context).
                                createToast(getResources().getString(R.string.failed_getting_account_info));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Network.getInstance(context).toastErrorMsg(activity);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_hamburger:
                ((HamburgerBtnClick) activity).onHamburgerBtnClick();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // do this because of headerview
        position--;
        switch (parent.getId()){
            case R.id.list_account:
                switch (position){
                    case ConstValue.ACCOUNT_MAIN_POS:
                    case ConstValue.ACCOUNT_SIGNUP_DATE_POS:
                    case ConstValue.ACCOUNT_LOGIN_DATE_POS:
                        CustomToast.getInstance(context).createToast(
                                menuAdapter.getItem(position).getTxt()+ " : "
                                        + menuAdapter.getItem(position).getTxtMore());
                        break;
                    case ConstValue.ACCOUNT_PASSWORD_POS:
                        intent = new Intent(context, AccountUpdateActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("position", position);
                        intent.putExtra("title", menuAdapter.getItem(position).getTxt() + " "
                            + getResources().getString(R.string.update));
                        intent.putExtra("label", menuAdapter.getItem(position).getTxt());
                        intent.putExtra("value", CustomerPref.getInstance(context).getPassword());
                        startActivityForResult(intent, ConstValue.UPDATE_PASSWORD);
                        activity.overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
                        break;
                    case ConstValue.ACCOUNT_NAME_POS:
                        intent = new Intent(context, AccountUpdateActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("position", position);
                        intent.putExtra("title", menuAdapter.getItem(position).getTxt() + " "
                                + getResources().getString(R.string.update));
                        intent.putExtra("label", menuAdapter.getItem(position).getTxt());
                        intent.putExtra("value", CustomerPref.getInstance(context).getName());
                        startActivityForResult(intent, ConstValue.UPDATE_NAME);
                        activity.overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
                        break;
                    case ConstValue.ACCOUNT_PHONE_POS:
                        intent = new Intent(context, AccountUpdateActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("position", position);
                        intent.putExtra("title", menuAdapter.getItem(position).getTxt() + " "
                                + getResources().getString(R.string.update));
                        intent.putExtra("label", menuAdapter.getItem(position).getTxt());
                        intent.putExtra("value", CustomerPref.getInstance(context).getPhone());
                        startActivityForResult(intent, ConstValue.UPDATE_PHONE);
                        activity.overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
                        break;
                    case ConstValue.ACCOUNT_PROFILE_IMG_POS:
/*
                        intent.putExtra("title", customAdapter.getItem(position).getTxt().toString() + " "
                                + getResources().getString(R.string.update));
                        intent.putExtra("position", position);
*/
                        // intent.putExtra("label", customAdapter.getItem(position).getTxt());
                        // intent.putExtra("value", CustomerPref.getInstance(context).getCusName());
                        // startActivityForResult(intent, ConstValue.UPDATE_IMG);
                        CustomToast.getInstance(context).createToast(getResources().getString(R.string.plz_change_pic_in_main));
                        break;
                    case ConstValue.ACCOUNT_PROFILE_EMAIL_POS:
                        intent = new Intent(context, AccountUpdateActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("position", position);
                        intent.putExtra("title", menuAdapter.getItem(position).getTxt() + " " + getResources().getString(R.string.update));
                        intent.putExtra("label", menuAdapter.getItem(position).getTxt());
                        intent.putExtra("value", CustomerPref.getInstance(context).getEmail());
                        startActivityForResult(intent, ConstValue.UPDATE_EMAIL);
                        activity.overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
                        break;
                    case ConstValue.ACCOUNT_PROFILE_DESCRIPTION_POS:
                        intent = new Intent(context, AccountUpdateActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("position", position);
                        intent.putExtra("title", menuAdapter.getItem(position).getTxt() + " "
                                + getResources().getString(R.string.update));
                        intent.putExtra("label", menuAdapter.getItem(position).getTxt());
                        intent.putExtra("value", CustomerPref.getInstance(context).getDescription());
                        startActivityForResult(intent, ConstValue.UPDATE_DESCRIPTION);
                        activity.overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
                        break;
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case ConstValue.UPDATE_PASSWORD:
                case ConstValue.UPDATE_NAME:
                case ConstValue.UPDATE_PHONE:
                case ConstValue.UPDATE_IMG:
                case ConstValue.UPDATE_DESCRIPTION:
                case ConstValue.UPDATE_EMAIL:
                    String value = data.getStringExtra("value");
                    MyLog.i(TAG, "value : " + value);
                    menuAdapter.getItem(data.getIntExtra("position", 0)).setTxtMore(value);
                    menuAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
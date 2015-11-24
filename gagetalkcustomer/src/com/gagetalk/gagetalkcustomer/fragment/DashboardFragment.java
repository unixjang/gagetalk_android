package com.gagetalk.gagetalkcustomer.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.constant.ReqUrl;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities_dialog.LoginDialogActivity;
import com.gagetalk.gagetalkcustomer.adapter.DashboardMenuAdapter;
import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.data.DashboardMenuData;
import com.gagetalk.gagetalkcommon.dialog_fragment.DialogBtn2;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 3/28/15.
 */
public class DashboardFragment extends Fragment
        implements
            View.OnClickListener{

    private static final String TAG = "DashboardFragment";
    private Activity activity;
    private Context context;

    private RelativeLayout relDash;
    private RelativeLayout relFooter;
    private ListView listMenu;
    private TextView txtLogin;
    private Button btnLogin;

    private ArrayList<DashboardMenuData> arrayMenu;
    private DashboardMenuAdapter dashboardMenuAdapter;

    // usage of intent onlcick listener
    private Intent intent;

    // dashboard menu position
    private int dashMenuPos = 0;

    public interface DashBoardMenuClick {
        void onMenuClick(final int resultCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        activity = getActivity();
        context = getActivity().getApplicationContext();

        // register receiver for chaning login ui
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstValue.LOGIN_FILTER);
        getActivity().registerReceiver(dashFragReceiver, intentFilter);


        relDash = (RelativeLayout) view.findViewById(R.id.rel_dash);
        relFooter = (RelativeLayout) view.findViewById(R.id.rel_footer);
        listMenu = (ListView) view.findViewById(R.id.list_menu);
        txtLogin = (TextView) view.findViewById(R.id.txt_login);
        btnLogin = (Button) view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);

        // 1. setup data
        arrayMenu = new ArrayList<>();
        arrayMenu.add(new DashboardMenuData(R.drawable.sel_ic_home_menu,
                context.getResources().getString(R.string.home)));
        arrayMenu.add(new DashboardMenuData(R.drawable.sel_ic_account_menu,
                context.getResources().getString(R.string.my_account)));
        arrayMenu.add(new DashboardMenuData(R.drawable.sel_ic_help_menu,
                context.getResources().getString(R.string.help)));
        arrayMenu.add(new DashboardMenuData(R.drawable.sel_ic_setting_menu,
                context.getResources().getString(R.string.setting)));

        // 2. setup adapter
        dashboardMenuAdapter = new DashboardMenuAdapter(context, arrayMenu);

        // 3. bind to listview
        listMenu.setAdapter(dashboardMenuAdapter);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // CustomToast.getInstance(context).createToast("clicked : " + arrayMenu.get(position).getCusName());
            ((DashBoardMenuClick) activity).onMenuClick(position);
            }
        });


        // to hide keyboard after login
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoginUI();
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(dashFragReceiver);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if(CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
                    Bundle args = new Bundle();
                    args.putString("content", getResources().getString(R.string.logout_ask));
                    final DialogBtn2 dialogBtn2 = new DialogBtn2();
                    dialogBtn2.setArguments(args);
                    dialogBtn2.show(getChildFragmentManager(), "fragment_logout");
                    dialogBtn2.setDialogBtn2(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBtn2.dismiss();
                                }
                            },
                            new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    CustomerNetwork.getInstance(context).logout();
                                    // disconnect socket
                                    CustomerNetwork.getInstance(context).disconnectSocket();
                                    setLoginUI();
                                    context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
                                    // reqGet for logout : destroy session in server
                                    reqServerCustomerLogout();
                                    dialogBtn2.dismiss();
                                }
                            });
                } else{
                    intent = new Intent(context, LoginDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_top, 0);
                }
                break;

        }
    }

    // reqServerCustomerLogout
    private void reqServerCustomerLogout(){
        String url = ReqUrl.CustomerLoginTask + "/logout";
        Network.getInstance(context).reqGet(activity, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                int resultCode = 0;
                try {
                    resultCode = response.getInt("resultCode");
                    MyLog.i(TAG, "logout : " + resultCode);
                } catch (Exception e) {
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


    private BroadcastReceiver dashFragReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "action : " + action);
            if (action.equals(ConstValue.LOGIN_FILTER)) {
                setLoginUI();
            }
        }
    };

    private void setLoginUI(){
        if(CustomerFunction.getInstance(context).getCusID() == null){
            txtLogin.setText(context.getResources().getString(R.string.login_request));
            btnLogin.setText(context.getResources().getString(R.string.login));
            relFooter.setAlpha(0.8f);
        }
        else {
            txtLogin.setText(context.getResources().getString(R.string.login_announce));
            btnLogin.setText(context.getResources().getString(R.string.logout));
            relFooter.setAlpha(1f);
        }
    }

}
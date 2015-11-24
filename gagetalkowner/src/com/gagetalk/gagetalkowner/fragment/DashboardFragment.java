package com.gagetalk.gagetalkowner.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.dialog_fragment.DialogBtn2;
import com.gagetalk.gagetalkowner.R;
import com.gagetalk.gagetalkowner.activities_dialog.LoginDialogActivity;
import com.gagetalk.gagetalkowner.adapter.DashboardMenuAdapter;
import com.gagetalk.gagetalkowner.api.OwnerFunction;
import com.gagetalk.gagetalkowner.api.OwnerNetwork;
import com.gagetalk.gagetalkowner.data.DashboardMenuData;

import java.util.ArrayList;

/**
 * Created by hyochan on 3/29/15.
 */
public class DashboardFragment extends Fragment
        implements
        View.OnClickListener{

    private static final String TAG = "DashboardFragment";
    private Activity activity;
    private Context context;

    private RelativeLayout relFooter;
    private ListView listMenu;
    private TextView txtLogin;
    private Button btnLogin;

    private ArrayList<DashboardMenuData> arrayMenu;
    private DashboardMenuAdapter dashboardMenuAdapter;

    // usage of intent on click listener
    private Intent intent;

    public interface DashBoardMenuClick {
        void onMenuClick(final int resultCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        activity = getActivity();
        context = getActivity().getApplicationContext();

        relFooter = (RelativeLayout) view.findViewById(R.id.rel_footer);
        listMenu = (ListView) view.findViewById(R.id.list_menu);
        txtLogin = (TextView) view.findViewById(R.id.txt_login);
        btnLogin = (Button) view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);

        // 1. setup data
        arrayMenu = new ArrayList<DashboardMenuData>();
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


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoginUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:

                if(OwnerFunction.getInstance(context).getMarID() != null){
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
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    OwnerNetwork.getInstance(context).logout();
                                    setLoginUI();
                                    context.sendBroadcast(new Intent().setAction(ConstValue.LOGIN_FILTER));
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

    // logout dialogBtn2Click

    private void setLoginUI(){
        if(OwnerFunction.getInstance(context).getMarID() == null) {
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
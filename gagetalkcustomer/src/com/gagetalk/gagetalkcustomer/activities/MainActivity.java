package com.gagetalk.gagetalkcustomer.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.network.Network;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities_dialog.LoginDialogActivity;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.api.CustomerNetwork;
import com.gagetalk.gagetalkcustomer.fragment.AccountFragment;
import com.gagetalk.gagetalkcustomer.fragment.DashboardFragment;
import com.gagetalk.gagetalkcustomer.fragment.HelpFragment;
import com.gagetalk.gagetalkcustomer.fragment.MainFragment;
import com.gagetalk.gagetalkcustomer.fragment.SettingFragment;
import com.gagetalk.gagetalkcustomer.preference.AppPref;

public class MainActivity extends FragmentActivity
        implements
            DashboardFragment.DashBoardMenuClick,
            MainFragment.HamburgerBtnClick,
            AccountFragment.HamburgerBtnClick,
            HelpFragment.HamburgerBtnClick,
            SettingFragment.HamburgerBtnClick
    {

    private final static String TAG = "MainActivity";

    private Activity activity;
    private Context context;
    private RelativeLayout relMain;
    private RelativeLayout relDashboard;
    private DrawerLayout drawerLayout;
    private Fragment fragment;
    private float lastTranslate = 0.0f;

    // dashboard menu position
    private int dashMenuPos = 0;

    // isAlive Flag to check MainActivity is alive
    public static boolean IS_ALIVE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        activity = this;
        context = this;
        IS_ALIVE = true;

        // test code : should be deleted
        CustomToast.getInstance(context).createToast("CHAT_NUM : " + AppPref.getInstance(context).getValue("CHAT_NUM", 0));

        if (Network.getInstance(context).isNetworkAvailable()) {
            if(CustomerNetwork.getInstance(context).forceLogin(activity)) {
                // set socket
                CustomerNetwork.getInstance(context).connectSocket();
            }else{
                // disconnect socket
                CustomerNetwork.getInstance(context).disconnectSocket();
            }
        }

        // register receiver for changing login ui
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstValue.LOGIN_FILTER);
        this.registerReceiver(mainActivityReceiver, intentFilter);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        relMain = (RelativeLayout) findViewById(R.id.rel_main);
        relDashboard = (RelativeLayout) findViewById(R.id.rel_dashboard);

        drawerLayout.setDrawerListener(drawerListener);
        drawerLayout.closeDrawer(relDashboard);

        selectItem(0);
        dashMenuPos = 0;

        if (savedInstanceState == null) {
            selectItem(ConstValue.MAIN_FRAGMENT);
            dashMenuPos = 0;
        }
    }


    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mainActivityReceiver);
        CustomerNetwork.getInstance(context).disconnectSocket();
        IS_ALIVE = false;
        super.onDestroy();
    }


    @Override
    public void onHamburgerBtnClick() {
        if(drawerLayout.isDrawerOpen(relDashboard))
            drawerLayout.closeDrawer(relDashboard);
        else
            drawerLayout.openDrawer(relDashboard);
    }


    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View view, float v) {
            // Log.i(TAG, "onDrawerSlide : " + String.format("%.2f", v));
            float moveFactor = (relDashboard.getWidth() * v);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                relMain.setTranslationX(moveFactor);
            }
            else
            {
                TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                anim.setDuration(0);
                anim.setFillAfter(true);
                relMain.startAnimation(anim);
                lastTranslate = moveFactor;
            }
        }

        @Override
        public void onDrawerOpened(View view) {
            // Check if no view has focus: hiding keyboard
            Function.getInstance(context).hideKeyboard(activity);
        }

        @Override
        public void onDrawerClosed(View view) {
        }
        @Override
        public void onDrawerStateChanged(int i) {

        }
    };

    @Override
    public void onMenuClick(int dashMenuPos) {
        // check login for AccountFragment
        if(!CustomerFunction.getInstance(context).isCusLocallyLoggedIn()
                && dashMenuPos == ConstValue.ACCOUNT_FRAGMENT){
            Intent intent = new Intent(context, LoginDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_from_top, 0);
        } else{
            // only change the fragment if the position is not the same
            if(this.dashMenuPos != dashMenuPos){
                selectItem(dashMenuPos);
                this.dashMenuPos = dashMenuPos;
            }
            //close the drawer
            drawerLayout.closeDrawer(relDashboard);
        }
    }

    // set main Fragment
    private void selectItem(int position) {
        // update the main content by replacing fragments
        switch (position){
            case ConstValue.MAIN_FRAGMENT:
                fragment = new MainFragment();
                break;
            case ConstValue.ACCOUNT_FRAGMENT:
                fragment = new AccountFragment();
                break;
            case ConstValue.HELP_FRAGMENT:
                fragment = new HelpFragment();
                break;
            case ConstValue.SETTING_FRAGMENT:
                fragment = new SettingFragment();
                break;
            default:
                fragment = new MainFragment();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rel_main, fragment);
        // ft.commit();
        ft.commitAllowingStateLoss();
    }

    private BroadcastReceiver mainActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MyLog.i(TAG, "action : " + action);
        if (action.equals(ConstValue.LOGIN_FILTER)) {
            if(!CustomerNetwork.getInstance(context).forceLogin(activity)){
                selectItem(ConstValue.MAIN_FRAGMENT);
            }
        }
        }
    };

}

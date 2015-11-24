package com.gagetalk.gagetalkowner.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkowner.R;
import com.gagetalk.gagetalkowner.fragment.AccountFragment;
import com.gagetalk.gagetalkowner.fragment.DashboardFragment;
import com.gagetalk.gagetalkowner.fragment.HomeFragment;
import com.gagetalk.gagetalkowner.fragment.SettingFragment;


public class MainActivity extends FragmentActivity
    implements
        DashboardFragment.DashBoardMenuClick,
        HomeFragment.HamburgerBtnClick,
        AccountFragment.HamburgerBtnClick,
        HelpFragment.HamburgerBtnClick,
        SettingFragment.HamburgerBtnClick{

    private final static String TAG = "MainActivity";

    private Activity activity;
    private Context context;
    private RelativeLayout relMain;
    private RelativeLayout relDashboard;
    private DrawerLayout drawerLayout;
    Fragment fragment;

    private float lastTranslate = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        MyLog.i(TAG, "owner mainactivity");

        activity = this;
        context = this;
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        relMain = (RelativeLayout) findViewById(R.id.rel_main);
        relDashboard = (RelativeLayout) findViewById(R.id.rel_dashboard);

        drawerLayout.setDrawerListener(drawerListener);
        drawerLayout.closeDrawer(relDashboard);
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }


    @Override
    public void onHamburgerBtnClick() {
        if(drawerLayout.isDrawerOpen(relDashboard))
            drawerLayout.closeDrawer(relDashboard);
        else
            drawerLayout.openDrawer(relDashboard);
    }

    // hiding keyboard
    private void hideKeyboard(){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
            hideKeyboard();
        }

        @Override
        public void onDrawerClosed(View view) {
        }
        @Override
        public void onDrawerStateChanged(int i) {

        }
    };

    @Override
    public void onMenuClick(int resultCode) {
        selectItem(resultCode);
    }

    // set main Fragment
    private void selectItem(int position) {
        // update the main content by replacing fragments

        switch (position){
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new AccountFragment();
                break;
            case 2:
                fragment = new HelpFragment();
                break;
            case 3:
                fragment = new SettingFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.rel_main, fragment);
        ft.commit();

        //close the drawer
        drawerLayout.closeDrawer(relDashboard);
    }

}

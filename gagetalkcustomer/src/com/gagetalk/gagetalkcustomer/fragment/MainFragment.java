package com.gagetalk.gagetalkcustomer.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.network.NetworkPreference;
import com.gagetalk.gagetalkcommon.util.CustomToast;
import com.gagetalk.gagetalkcommon.util.ImageDownloader;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcommon.util.MyViewPager;
import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.activities.ChatActivity;
import com.gagetalk.gagetalkcustomer.activities_dialog.LoginDialogActivity;
import com.gagetalk.gagetalkcustomer.activities_dialog.ProfileImgDialogActivity;
import com.gagetalk.gagetalkcustomer.activities_dialog.SetNameDialogActivity;
import com.gagetalk.gagetalkcustomer.adapter.PagerAdapter;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.preference.CustomerPref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyochan on 3/28/15.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "MainFragment";
    private static final int REQUEST_NAME_ACTIVITY = 1;
    private static final int REQUEST_PHOTO_ACTIVITY = 2;

    private static final int HOME_FRAG_POS = 0;
    private static final int MSG_FRAG_POS = 1;

    private Activity activity;
    private Context context;

    private Intent intent;
    private ImageView imgHamburger;
    private ImageView imgProfile;
    private LinearLayout linProfile;
    private TextView txtName;
    private TextView txtId;
    private LinearLayout linHome;
    private LinearLayout linMsg;
    private MyViewPager viewPager;
    private PagerAdapter pagerAdapter;

    public interface HamburgerBtnClick {
        void onHamburgerBtnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        activity = getActivity();
        context = getActivity().getApplicationContext();

        // register receiver for view more click in ProductListAdapter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstValue.LOGIN_FILTER);
        intentFilter.addAction(ConstValue.MOVE_TO_MSG_FRAG_FILTER);
        getActivity().registerReceiver(mainFragReceiver, intentFilter);

        imgHamburger = (ImageView) view.findViewById(R.id.img_hamburger);
        imgProfile = (ImageView) view.findViewById(R.id.img_profile);
        linProfile = (LinearLayout) view.findViewById(R.id.lin_profile);
        txtName = (TextView) view.findViewById(R.id.txt_name);
        txtId = (TextView) view.findViewById(R.id.txt_id);
        linHome = (LinearLayout) view.findViewById(R.id.lin_home);
        linMsg = (LinearLayout) view.findViewById(R.id.lin_msg);
        viewPager = (MyViewPager) view.findViewById(R.id.view_pager);

        linProfile.setOnClickListener(this);
        imgHamburger.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        linHome.setOnClickListener(this);
        linMsg.setOnClickListener(this);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MsgFragment());

        pagerAdapter = new PagerAdapter(getChildFragmentManager(), context, fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }
            @Override
            public void onPageSelected(int i) {
                if(i==MSG_FRAG_POS){
                    // ask login if not logged in
                    if(!CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
                        // selectFragment(ConstValue.HOME_FRAGMENT);
                        viewPager.setCurrentItem(ConstValue.HOME_FRAGMENT, true);
                        Intent intent = new Intent(context, LoginDialogActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_from_top, 0);
                    }
                    else{
                        selectFragment(i);
                    }
                }
                else{
                    selectFragment(i);
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                Function.getInstance(context).hideKeyboard(activity);
            }
        });

        if(viewPager.getCurrentItem() == ConstValue.HOME_FRAGMENT)
            selectFragment(ConstValue.HOME_FRAGMENT);
        else
            selectFragment(ConstValue.MSG_FRAGMENT);

        // if activity was started from ChatActivity
        if(activity.getIntent().getBooleanExtra(ConstValue.ACTIVITY_STARTED_FROM_CHAT_ACTIVITY, false)){
            selectFragment(MSG_FRAG_POS);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoginUI();
    }

    private void setLoginUI(){
        if(CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
            txtId.setText(CustomerFunction.getInstance(context).getCusID());
            txtName.setText(CustomerFunction.getInstance(context).getCusName());
            txtId.setEnabled(true);
            txtName.setEnabled(true);
            ImageDownloader.getInstance(context).getImage(
                    NetworkPreference.getInstance(context).getServerUrl() + ":" +
                            NetworkPreference.getInstance(context).getServerPort() + "/images/customer/" +
                            CustomerFunction.getInstance(context).getCusID() + ".png", imgProfile
            );
        } else{
            txtId.setText(getResources().getText(R.string.no_email));
            txtName.setText(getResources().getText(R.string.no_name));
            txtId.setEnabled(false);
            txtName.setEnabled(false);
            imgProfile.setImageResource(R.mipmap.ic_err_img);
        }
    }
    private void selectFragment(int position){
        switch (position){
            case ConstValue.HOME_FRAGMENT:
                linHome.setSelected(true);
                linMsg.setSelected(false);
                break;
            case ConstValue.MSG_FRAGMENT:
                linMsg.setSelected(true);
                linHome.setSelected(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_home:
                selectFragment(ConstValue.HOME_FRAGMENT);
                viewPager.setCurrentItem(ConstValue.HOME_FRAGMENT, true);
                break;
            case R.id.lin_msg:
                // ask login if not logged in
                if(!CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
                    // selectFragment(ConstValue.HOME_FRAGMENT);
                    intent = new Intent(context, LoginDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_top, 0);
                }
                else{
                    selectFragment(ConstValue.MSG_FRAGMENT);
                    viewPager.setCurrentItem(ConstValue.MSG_FRAGMENT, true);
                }
                break;
            case R.id.img_hamburger:
                ((HamburgerBtnClick) activity).onHamburgerBtnClick();
                break;
            case R.id.img_profile:
                if(CustomerFunction.getInstance(context).getCusID() != null){
/*
                    imgProfile.buildDrawingCache();
                    Bitmap bitmap = imgProfile.getDrawingCache();
*/
                    imgProfile.setDrawingCacheEnabled(true);
                    imgProfile.buildDrawingCache(true);
                    Bitmap bitmap = Bitmap.createBitmap(imgProfile.getDrawingCache());
                    imgProfile.setDrawingCacheEnabled(false);

                    intent = new Intent(context, ProfileImgDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", bitmap);
                    intent.putExtras(extras);
                    startActivityForResult(intent, REQUEST_PHOTO_ACTIVITY);
                } else{
                    CustomToast.getInstance(context).createToast(getResources().getString(R.string.login_request));
                }
                break;
            case R.id.lin_profile:
                if(CustomerFunction.getInstance(context).isCusLocallyLoggedIn()){
                    intent = new Intent(context, SetNameDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("name", txtName.getText().toString());
                    startActivityForResult(intent, REQUEST_NAME_ACTIVITY);
                }else{
                    CustomToast.getInstance(context).createToast(getResources().getString(R.string.login_request));
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mainFragReceiver);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.i(TAG, "reqCode : " + requestCode + ", resultCode : " + resultCode);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NAME_ACTIVITY:
                    MyLog.i(TAG, "get name : " + CustomerPref.getInstance(context).getName());
                    txtName.setText(CustomerPref.getInstance(context).getName());
                    break;
                case REQUEST_PHOTO_ACTIVITY:
                    MyLog.i(TAG, "REQUEST_PHOTO_ACTIVITY");
                    byte[] byteArray = data.getByteArrayExtra("imgArray");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imgProfile.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    private BroadcastReceiver mainFragReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "mainFragReceiver action : " + action);
            if(action.equals(ConstValue.LOGIN_FILTER)){
                setLoginUI();
            }

            else if(action.equals(ConstValue.MOVE_TO_MSG_FRAG_FILTER)){
                int fragment = intent.getIntExtra("fragment", 0);
                if(fragment == ConstValue.HOME_FRAGMENT){
                    selectFragment(ConstValue.HOME_FRAGMENT);
                    viewPager.setCurrentItem(ConstValue.HOME_FRAGMENT, true);
                } else if(fragment == ConstValue.MSG_FRAGMENT){
                    selectFragment(ConstValue.MSG_FRAGMENT);
                    viewPager.setCurrentItem(ConstValue.MSG_FRAGMENT, true);
                    Intent newIntent = new Intent(context, ChatActivity.class);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    newIntent.putExtra("mar_id", intent.getStringExtra("mar_id"));
                    newIntent.putExtra("mar_name", intent.getStringExtra("mar_name"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(newIntent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }
    };
}
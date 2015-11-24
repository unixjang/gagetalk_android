package com.gagetalk.gagetalkcustomer.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gagetalk.gagetalkcustomer.R;
import com.gagetalk.gagetalkcustomer.adapter.MenuAdapter;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcustomer.data.MenuData;
import com.gagetalk.gagetalkcommon.util.CustomToast;

import java.util.ArrayList;

/**
 * Created by hyochan on 3/28/15.
 */
public class SettingFragment extends Fragment
    implements
        View.OnClickListener,
        AdapterView.OnItemClickListener{

    private static final String TAG = "SettingFragment";

    private Activity activity;
    private Context context;

    private ImageView imgHamburger;
    private TextView txtTitle;
    private ListView listSetting;

    public interface HamburgerBtnClick {
        void onHamburgerBtnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);

        activity = getActivity();
        context = getActivity().getApplicationContext();

        imgHamburger = (ImageView) view.findViewById(R.id.img_hamburger);
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setText(context.getResources().getString(R.string.setting));
        listSetting = (ListView) view.findViewById(R.id.list_setting);

        // 1. setup data
        String[] arraySetting = getResources().getStringArray(R.array.array_setting);
        String[] arraySettingMore = getResources().getStringArray(R.array.array_setting_more);
        ArrayList<MenuData> arrayCustom = new ArrayList<>();

        for(int i=0; i<arraySetting.length; i++){
            arrayCustom.add(new MenuData(
                    arraySetting[i], arraySettingMore[i]
            ));
        }
        // 2. setup adapter

        MenuAdapter menuAdapter = new MenuAdapter(context, R.layout.custom_adapter, arrayCustom);
        // 3. bind to listview
        listSetting.setAdapter(menuAdapter);
        listSetting.setOnItemClickListener(this);


        imgHamburger.setOnClickListener(this);

        return view;
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
        switch (parent.getId()){
            case R.id.list_setting:
                switch (position){
                    case ConstValue.SETTING_MANAGE_MSG_POS:
                        CustomToast.getInstance(context).createToast("메시지 관리");
                        break;
                    case ConstValue.SETTING_MANAGE_ALERT_POS:
                        CustomToast.getInstance(context).createToast("알림 설정");
                        break;
                    case ConstValue.SETTING_VERSION_INFO_POS:
                        CustomToast.getInstance(context).createToast(
                                getResources().getString(R.string.version_label) +
                                        " : " + getResources().getString(R.string.version));
                        break;
                }
                break;
        }
    }
}
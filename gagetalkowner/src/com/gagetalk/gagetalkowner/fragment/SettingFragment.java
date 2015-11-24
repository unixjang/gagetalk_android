package com.gagetalk.gagetalkowner.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gagetalk.gagetalkowner.R;

/**
 * Created by hyochan on 3/29/15.
 */
public class SettingFragment extends Fragment
        implements View.OnClickListener{
    private Activity activity;
    private Context context;
    private ImageView imgHamburger;
    private TextView txtTitle;

    public interface HamburgerBtnClick {
        void onHamburgerBtnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();
        context = activity.getApplicationContext();

        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        imgHamburger = (ImageView) view.findViewById(R.id.img_hamburger);
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setText(context.getResources().getString(R.string.setting));
        context.getResources().getString(R.string.setting);
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

}
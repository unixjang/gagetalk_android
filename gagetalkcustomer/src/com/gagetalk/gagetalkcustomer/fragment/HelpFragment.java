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
public class HelpFragment extends Fragment
    implements
        View.OnClickListener,
        AdapterView.OnItemClickListener{

    private static final String TAG = "HelpFragment";

    private Activity activity;
    private Context context;

    private ImageView imgHamburger;
    private TextView txtTitle;
    private ListView listHelp;

    public interface HamburgerBtnClick {
        void onHamburgerBtnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_fragment, container, false);

        activity = getActivity();
        context = getActivity().getApplicationContext();

        imgHamburger = (ImageView) view.findViewById(R.id.img_hamburger);
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setText(context.getResources().getString(R.string.help));
        listHelp = (ListView) view.findViewById(R.id.list_help);

        // 1. setup data
        String[] arrayHelp = getResources().getStringArray(R.array.array_help);
        String[] arrayHelpMore = getResources().getStringArray(R.array.array_help_more);
        ArrayList<MenuData> arrayCustom = new ArrayList<>();

        for(int i=0; i<arrayHelp.length; i++){
            arrayCustom.add(new MenuData(
                    arrayHelp[i], arrayHelpMore[i]
            ));
        }
        // 2. setup adapter
        MenuAdapter menuAdapter = new MenuAdapter(context, R.layout.custom_adapter, arrayCustom);
        // 3. bind to listview
        listHelp.setAdapter(menuAdapter);
        listHelp.setOnItemClickListener(this);

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
            case R.id.list_help:
                switch (position){
                    case ConstValue.HELP_GUIDE_POS:
                        CustomToast.getInstance(context).createToast("이용 안내");
                        break;
                    case ConstValue.HELP_MOST_QUESTION_POS:
                        CustomToast.getInstance(context).createToast("자주 묻는 질문");
                        break;
                    case ConstValue.HELP_CONTACT_POS:
                        CustomToast.getInstance(context).createToast("연락하기");
                        break;
                    case ConstValue.HELP_ABOUT_POS:
                        CustomToast.getInstance(context).createToast("ABOUT");
                        break;
                }
                break;
        }
    }
}
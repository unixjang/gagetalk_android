package com.gagetalk.gagetalkcommon.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gagetalk.gagetalkcommon.R;

/**
 * Created by hyochan on 3/29/15.
 */
public class DialogBtn1 extends DialogFragment{

    // components for view
    private TextView txtContent;
    private Button btn1;

    private View.OnClickListener mLeftClickListener;

    public DialogBtn1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogBtnAnimation;

        View view = inflater.inflate(R.layout.dialog_alert_2_btn, container);
        txtContent = (TextView) view.findViewById(R.id.txt_content);
        txtContent.setText(getArguments().getString("content"));
        btn1 = (Button) view.findViewById(R.id.btn_1);

        return view;
    }
    public void setDialogBtn1(View.OnClickListener btn1) {
        mLeftClickListener = btn1;
    }
}


package com.gagetalk.gagetalkcommon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by hyochan on 3/28/15.
 */
public class CustomToast {

    private static final String TAG = "CustomToast";
    private static final Boolean DEBUG = true;

    private SharedPreferences sharedPreferences;
    private final Context context;
    private static CustomToast customToast;

    private CustomToast(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static CustomToast getInstance(Context context){
        if(customToast == null) customToast = new CustomToast(context);
        return customToast;
    }
    public void createToast(String text){
        if(DEBUG == true){
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 800);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }
}

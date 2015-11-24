package com.gagetalk.gagetalkcommon.util;

import android.util.Log;

/**
 * Created by hyochan on 4/11/15.
 */
public class MyLog {

    private final static Boolean DEBUG = true;

    public static void i(String tag, String str){
        if(DEBUG)
            Log.i(tag, str);
    }

    public static void d(String tag, String str){
        if(DEBUG)
            Log.d(tag, str);
    }
    public static void e(String tag, String str){
        if(DEBUG)
            Log.e(tag, str);
    }
    public static void v(String tag, String str){
        if(DEBUG)
            Log.v(tag, str);
    }
}

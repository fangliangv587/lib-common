package com.cenco.lib.common;

import android.util.Log;

/**
 * Created by Administrator on 2018/2/2.
 */

public class LogUtil {

    private static final String tag = "xzlibCommon";
    public static boolean printLog = true;

    public static void i(String mes){
        if (printLog){
            Log.i(tag,mes);
        }
    }

    public static void e(String mes){
        if (printLog){
            Log.e(tag,mes);
        }
    }
    public static void w(String mes){
        if (printLog){
            Log.w(tag,mes);
        }
    }
}

package com.cenco.lib.common.log;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.cenco.lib.common.FileUtils;
import com.cenco.lib.common.log.CrashHandler;
import com.cenco.lib.common.log.DiskLogStrategy;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;


/**
 * Created by Administrator on 2018/3/5.
 */

public class LogUtils {

    private  static  String commontag = "libCommon";
    private static boolean isInit = false;
    public static boolean debug = true;

    public static void init(String tag,boolean isFormat, String logPath){
        if (isInit){
            return;
        }

        if (tag==null){
            tag = commontag;
        }

        //输出到控制台
        FormatStrategy strategy = null;
        if (isFormat){
            strategy= PrettyFormatStrategy.newBuilder()
                    .tag(tag)
                    .build();
        }else {
            strategy = SimpleFormatStrategy.newBuilder()
                    .tag(tag)
                    .build();
        }

        Logger.addLogAdapter(new AndroidLogAdapter(strategy));

        //保存到sd卡
        FormatStrategy formatStrategy = TxtFormatStrategy.newBuilder()
                .tag(tag)
                .logPath(logPath)
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));

        //异常捕获
        CrashHandler.getInstance().init();

        isInit = true;
    }

    public static void init(){
        init(null);
    }

    public static void init(String generalTag){
        init(generalTag,false);
    }
    public static void init(String generalTag,boolean isFormat){
        init(generalTag,isFormat,FileUtils.getDefaultLogFilePath());
    }


    private static boolean printLog(){
        return  isInit && debug;
    }

    public static void d(String tag,String mes){
        if (!printLog()){
            return;
        }
        if (!TextUtils.isEmpty(tag)){
            Logger.t(tag);
        }
        Logger.d(mes);
    }
    public static void i(String tag,String mes){
        if (!printLog()){
            return;
        }
        if (!TextUtils.isEmpty(tag)){
            Logger.t(tag);
        }
        Logger.i(mes);
    }
    public static void w(String tag,String mes){
        if (!printLog()){
            return;
        }
        if (!TextUtils.isEmpty(tag)){
            Logger.t(tag);
        }
        Logger.w(mes);
    }
    public static void e(String tag,String mes){
        if (!printLog()){
            return;
        }
        if (!TextUtils.isEmpty(tag)){
            Logger.t(tag);
        }
        Logger.e(mes);
    }


    public static void d(String mes){
       d(null,mes);
    }
    public static void i(String mes){
        i(null,mes);
    }
    public static void w(String mes){
        w(null,mes);
    }
    public static void e(String mes){
        e(null,mes);
    }

}

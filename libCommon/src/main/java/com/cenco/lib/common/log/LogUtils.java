package com.cenco.lib.common.log;


import android.text.TextUtils;
import android.util.Log;

import com.cenco.lib.common.FileUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;


/**
 * Created by Administrator on 2018/3/5.
 *
 * the default action is that don't save the log to the sdcard ,you can alter the param of save to save all logs{@link #save },
 * and also save the log what you want by call the method {@link #i(String, String, boolean)} which contains the params of save.
 * the default save path is define at   {@link FileUtils#getDefaultLogFilePath()}
 * the default global tag is {@link #commontag}
 */

public class LogUtils {

    private  static  String commontag = "libCommon";
    private static boolean isInit = false;
    public static boolean debug = true;
    /*是否全局保存*/
    public static boolean save = false;

    public static void init(String tag,boolean isFormat, String logPath){
        if (isInit){
            return;
        }

        if (tag==null){
            tag = commontag;
        }

        if (tag !=null){
            commontag = tag;
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


        if (logPath == null){
            logPath = FileUtils.getDefaultLogFilePath();
        }
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

    private static void log(int level,String tag,String mes,boolean save){
        if (!printLog()){
            return;
        }
        if (!save){
            String tag1 = formatTag(tag);
            log(level,tag1,mes);
            return;
        }
        if (!TextUtils.isEmpty(tag)){
            Logger.t(tag);
        }
        logger(level,tag,mes);
    }

    private static void log(int level,String tag,String mes){
        switch (level){
            case Level.VERBOSE:
                Log.v(tag,mes);
                break;
            case Level.DEBUG:
                Log.d(tag,mes);
                break;
            case Level.INFO:
                Log.i(tag,mes);
                break;
            case Level.WARN:
                Log.w(tag,mes);
                break;
            case Level.ERROR:
                Log.e(tag,mes);
                break;
            default:
                Log.d(tag,mes);
                break;
        }
    }


    private static void logger(int level,String tag,String mes){
        switch (level){
            case Level.VERBOSE:
                Logger.v(mes);
                break;
            case Level.DEBUG:
                Logger.d(mes);
                break;
            case Level.INFO:
                Logger.i(mes);
                break;
            case Level.WARN:
                Logger.w(mes);
                break;
            case Level.ERROR:
                Logger.e(mes);
                break;
            default:
                Logger.d(mes);
                break;
        }
    }

    public static void v(String tag,String mes,boolean save){
        log(Level.VERBOSE,tag,mes,save);
    }
    public static void d(String tag,String mes,boolean save){
        log(Level.DEBUG,tag,mes,save);

    }
    public static void i(String tag,String mes,boolean save){
        log(Level.INFO,tag,mes,save);
    }
    public static void w(String tag,String mes,boolean save){
        log(Level.WARN,tag,mes,save);
    }
    public static void e(String tag,String mes,boolean save){
        log(Level.ERROR,tag,mes,save);
    }


    public static void v(String tag,String mes){
        v(tag,mes,save);
    }
    public static void d(String tag,String mes){
        d(tag,mes,save);
    }
    public static void i(String tag,String mes){
        i(tag,mes,save);
    }
    public static void w(String tag,String mes){
        w(tag,mes,save);
    }
    public static void e(String tag,String mes){
        e(tag,mes,save);
    }

    public static void v(String mes){
       v(null,mes);
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

    private static String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(commontag, tag)) {
            return commontag + "-" + tag;
        }
        return commontag;
    }
}

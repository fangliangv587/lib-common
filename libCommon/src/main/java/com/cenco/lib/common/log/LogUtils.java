package com.cenco.lib.common.log;


import android.text.TextUtils;
import android.util.Log;

import com.cenco.lib.common.FileUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Created by Administrator on 2018/3/5.
 *
 * the default action is that don't save the log to the sdcard ,you can alter the param of save to save all logs{@link # },
 * and also save the log what you want by call the method {@link #i(String, String, boolean)} which contains the params of save.
 * the default save path is define at   {@link FileUtils#getDefaultLogFilePath()}
 * the default global tag is {@link #commontag}
 */

public class LogUtils {

    private  static  String commontag = "commonlib";
    private static boolean isInit = false;
    public static boolean debug = true;
    /*是否全局保存*/

    public static int saveLevel = Level.ERROR;

    public static void init(String tag,int level, String logPath){
        if (isInit){
            return;
        }

        saveLevel = level;

        if (tag==null){
            tag = commontag;
        }

        if (tag !=null){
            commontag = tag;
        }


        //输出到控制台
        FormatStrategy strategy = SimpleFormatStrategy.newBuilder()
                    .tag(tag)
                    .build();
        Logger.addLogAdapter(new AndroidLogAdapter(strategy));


        //保存到sd卡
        if (logPath == null){
            logPath = FileUtils.getDefaultLogFilePath();
        }
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
        init(generalTag,Level.ERROR);
    }
    public static void init(String generalTag,int level){
        init(generalTag,level,FileUtils.getDefaultLogFilePath());
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
        v(tag,mes,Level.VERBOSE>=saveLevel);
    }
    public static void d(String tag,String mes){
        d(tag,mes,Level.DEBUG>=saveLevel);
    }
    public static void i(String tag,String mes){
        i(tag,mes,Level.INFO>=saveLevel);
    }
    public static void w(String tag,String mes){
        w(tag,mes,Level.WARN>=saveLevel);
    }
    public static void e(String tag,String mes){
        e(tag,mes,Level.ERROR>=saveLevel);
    }
    public static void e(String tag,Throwable throwable){
        String mes = getExceptionLog(throwable);
        e(tag,mes,Level.ERROR>=saveLevel);
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

    public static void e(Throwable throwable){
        String log = getExceptionLog(throwable);
        e(log);
    }

    public static String getExceptionLog(Throwable throwable){
        if (throwable==null){
            return "throwable is null obj !";
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        return result;
    }

    private static String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(commontag, tag)) {
            return commontag + "-" + tag;
        }
        return commontag;
    }
}

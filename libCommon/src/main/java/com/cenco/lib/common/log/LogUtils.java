package com.cenco.lib.common.log;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

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

    private  static final String tag = "xzlibCommon";
    private static boolean isInit = false;
    public static boolean debug = true;

    public static void init(Context context,String logPath){
        if (isInit){
            return;
        }

        //输出到控制台
        FormatStrategy consoleFormat = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
//                .methodCount(0)         // (Optional) How many method line to show. Default 2
//                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(tag)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(consoleFormat));

        //保存到本地
        HandlerThread ht = new HandlerThread("AndroidFileLogger" );
        ht.start();
        Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(),logPath);
        LogStrategy logStrategy = new DiskLogStrategy(handler);

        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .logStrategy(logStrategy)
                .tag(tag)
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));

        //异常捕获
        CrashHandler.getInstance().init();

        isInit = true;
    }

    public static void init(Context context){
        init(context,FileUtils.getDefaultLogFilePath());
    }

    private static boolean printLog(){
        return  isInit && debug;
    }

    public static void d(String mes){
        if (!printLog()){
            return;
        }
        Logger.d(mes);
    }
    public static void i(String mes){
        if (!printLog()){
            return;
        }
        Logger.i(mes);
    }
    public static void w(String mes){
        if (!printLog()){
            return;
        }
        Logger.w(mes);
    }
    public static void e(String mes){
        if (!printLog()){
            return;
        }
        Logger.e(mes);
    }
}

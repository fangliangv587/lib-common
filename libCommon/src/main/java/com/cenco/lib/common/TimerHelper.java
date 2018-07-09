package com.cenco.lib.common;

import android.os.Handler;
import android.util.Log;

import com.cenco.lib.common.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class TimerHelper {

    private static final String TAG = SystemUtil.class.getSimpleName();

    //倒计时的最大值
    private int totalSecond;
    //时间间隔 单位秒
    private int interval = 1;


    private boolean isRunning = false;

    //计算消耗的总时间,单位s
    private int timer;

    private boolean showLog = true;

    //计时器名称
    private String name = "计时器";

    private List<TimerListener> listeners = new ArrayList<>();



    public TimerHelper(int totalSecond, TimerListener listener) {
        super();
        this.totalSecond = totalSecond;
        listeners.add(listener);

    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public TimerHelper(TimerListener listener) {
        this(0, listener);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param interval 秒
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setTotalSecond(int totalSecond) {
        this.totalSecond = totalSecond;
    }


    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 启动计时器
     */
    public void start() {

        isRunning = true;
        timer = 0;

        ThreadManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {

                while (isRunning){

                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!isRunning){
                        return;
                    }

                    timer = timer + interval;
                    //****************无限计时器****************
                    if (totalSecond <= 0) {
                        if (showLog){
                            LogUtils.v(TAG,name + "(" + TimerHelper.this.hashCode() + ")进行中:" + timer + "/" + totalSecond + "(MAX),thread:" + Thread.currentThread().getName());
                        }
                        if (listeners .size() != 0) {
                            for (TimerListener listener :listeners ){
                                listener.onTimerRunning(timer, totalSecond,false);
                            }
                        }
                    }else{
                        //****************倒计时计时器****************
                        boolean isOver = false;
                        if (timer >= totalSecond) {
                            isOver = true;
                            if (showLog){
                                LogUtils.v(TAG,name + "(" + TimerHelper.this.hashCode() + "):结束" + timer + "/" + totalSecond + ",thread:" + Thread.currentThread().getName());
                            }
                        } else {
                            if (showLog){
                                LogUtils.v(TAG,name + "(" + TimerHelper.this.hashCode() + ")进行中:" + timer + "/" + totalSecond + ",thread:" + Thread.currentThread().getName());
                            }
                        }

                        if (listeners .size() != 0) {
                            for (TimerListener listener :listeners ){
                                listener.onTimerRunning(timer, totalSecond,isOver);
                            }
                        }
                    }



                }
            }
        });

    }


    /**
     * 停止
     */
    public void stop() {

        isRunning = false;
        LogUtils.v(TAG,name + "(" + this.hashCode() + "):被中止 " + timer + "/" + totalSecond + ",thread:" + Thread.currentThread().getName());

    }


    public void addListener(TimerListener listener){
        listeners.add(listener);
    }

    public void removeListener(TimerListener listener){
        listeners.remove(listener);
    }

    public void removeAllListener(){
        listeners.clear();
    }


    public interface TimerListener {

        /**
         * 计数中
         */
        void onTimerRunning(int current, int total,boolean isOver);


    }
}

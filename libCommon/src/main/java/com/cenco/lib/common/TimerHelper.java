package com.cenco.lib.common;

import android.os.Handler;
import android.util.Log;

import com.cenco.lib.common.log.LogUtils;

public class TimerHelper {

    //倒计时的最大值
    private int totalSecond;
    //时间间隔 单位秒
    private int interval = 1;

    private static final String tag = TimerHelper.class.getName();
    //监听
    private TimerListener listener;


    //计算消耗的总时间,单位s
    private int timer;

    //计时器名称
    private String name = "计时器";


    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub

            timer = timer + interval;
            //****************无限计时器****************
            if (totalSecond <= 0) {
                handler.postDelayed(this, interval * 1000);
                LogUtils.d(name + "(" + TimerHelper.this.hashCode() + ")进行中:" + timer + "/" + totalSecond + "(MAX),thread:" + Thread.currentThread().getName());
                if (listener != null) {
                    listener.onTimerRunning(timer, totalSecond,false);
                }
                return;
            }


            //****************倒计时计时器****************
            boolean isOver = false;
            if (timer >= totalSecond) {
                isOver = true;
                handler.removeCallbacks(runnable);
                LogUtils.d(name + "(" + TimerHelper.this.hashCode() + "):结束" + timer + "/" + totalSecond + ",thread:" + Thread.currentThread().getName());
            } else {
                handler.postDelayed(this, interval * 1000);
                LogUtils.d(name + "(" + TimerHelper.this.hashCode() + ")进行中:" + timer + "/" + totalSecond + ",thread:" + Thread.currentThread().getName());

            }

            if (listener != null) {
                listener.onTimerRunning(timer, totalSecond,isOver);
            }


        }


    };


    public TimerHelper(int totalSecond, TimerListener listener) {
        super();
        this.totalSecond = totalSecond;
        this.listener = listener;

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


    /**
     * 启动计时器
     */
    public void start() {

        stop();

        timer = 0;
        handler.postDelayed(runnable, interval * 1000);
    }


    /**
     * 停止
     */
    public void stop() {

        handler.removeCallbacks(runnable);
        Log.i(tag, name + "(" + TimerHelper.this.hashCode() + "):被中止 " + timer + "/" + totalSecond + ",thread:" + Thread.currentThread().getName());

    }


    public interface TimerListener {

        /**
         * 计数中
         */
        void onTimerRunning(int current, int total,boolean isOver);


    }
}

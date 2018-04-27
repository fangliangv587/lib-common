package com.cenco.lib.common;

import android.os.Handler;
import android.util.Log;

public class TimerHelper {

	//倒计时的最大值
	private   int totalSecond;
	//倒计时临时变了
	private int tempSecond;
	//时间间隔
	private int interval = 1000;

	private static final String tag = TimerHelper.class.getName();
	//监听
	private TimerListener listener;


	//计算消耗的总时间
	private int timer;

	//计时器名称
	private String name = "计时器";


	private  Handler handler  = new Handler();

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			timer++;

			//****************无限计时器****************
			if (totalSecond<=0) {

				handler.postDelayed(this, interval);

				if (listener != null) {
					listener.onTimerRunning(timer,totalSecond);
				}
				return;
			}

			//****************倒计时计时器****************
			tempSecond--;

			if (tempSecond > 0) {
				Log.d(tag, name+ "计时器  进行中("+TimerHelper.this.hashCode()+"):"+timer + "/"+ totalSecond +",thread:"+Thread.currentThread().getName());
				handler.postDelayed(this, interval);

				if (listener != null) {
					listener.onTimerRunning(tempSecond,totalSecond);
				}
			}else {
				Log.d(tag, name+ "计时器  自然结束("+TimerHelper.this.hashCode()+"):"+timer + "/"+ totalSecond +",thread:"+Thread.currentThread().getName());
				stop();
			}

		}
	};



	public TimerHelper(int totalSecond, TimerListener listener) {
		super();
		this.totalSecond = totalSecond;
		this.listener = listener;

	}

	public TimerHelper(TimerListener listener) {
		this(0,listener);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setTotalSecond(int totalSecond){
		this.totalSecond = totalSecond;
	}


	/**
	 * 启动计时器
	 */
	public void start(){

		stop();

		timer = 0;
		tempSecond = totalSecond;
		handler.postDelayed(runnable, interval);
	}
	
	/**
	 * 外部调用有非主线程的风险
	 */
	public void stop(){
		
		handler.removeCallbacks(runnable);
		Log.i(tag, name+"计时器  强制结束("+TimerHelper.this.hashCode()+"):"+timer + "/"+ totalSecond+",thread:"+Thread.currentThread().getName());
		
		if (listener != null) {
			listener.onTimerOver();
		}
		
	}

	public interface TimerListener{
		
		/**
		 * 计数中
		 */
		void onTimerRunning(int current, int total);
		
		/**
		 * 计数结束
		 */
		void onTimerOver();
	}
}

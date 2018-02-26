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
	
	//是否正在计数
	private boolean isTimer;
	
	//计算消耗的总时间
	private int timer;
	
	//计时器名称
	private String name = "";
	
	/**
	 * 是否无限，计时器分两种，一种有上限，即倒计时；另一种无上限，单纯的计数，如时间的校验
	 */
	private boolean isInfinite = false;
	
	private  Handler handler;
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			//****************无限计时器****************
			if (isInfinite) {
				
				handler.postDelayed(this, interval);
				
				if (listener != null) {
					listener.onTimerRunning(tempSecond,totalSecond);
				}
				return;
			}
			
			//****************倒计时计时器****************
			tempSecond--;
			timer++;
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
	
	
	/**
	 * 构造
	 * @param handler  为空则自创，节省创建开销
	 * @param name
	 * @param totalSecond
	 * @param interval
	 * @param listener
	 */
	public TimerHelper( Handler handler,String name,int totalSecond, int interval,TimerListener listener) {
		super();
		this.totalSecond = totalSecond;
		this.interval = interval;
		this.listener = listener;
		this.name = name;
		
		if (handler == null) {
			handler  = new Handler();
		}
		
		this.handler = handler;
		isTimer = false;
	}
	
	public TimerHelper(Handler handler,String name,int totalSecond,TimerListener listener) {
		this(handler,name,totalSecond,1000,listener);
	}
	
	public TimerHelper(String name,int totalSecond,TimerListener listener) {
		this(null,name,totalSecond,1000,listener);
	}

	public TimerHelper(int totalSecond,TimerListener listener) {
		this(null,"",totalSecond,1000,listener);
		
	}
	public TimerHelper(int totalSecond) {
		this(totalSecond,null);
		
	}

	public TimerHelper(TimerListener listener) {
		this(0,listener);

	}

	public void setTotalSecond(int totalSecond){
		this.totalSecond = totalSecond;
	}
	
	/**
	 * 无限计时器
	 * @param handler
	 * @param name
	 * @param listener
	 */
	public TimerHelper(Handler handler,String name,TimerListener listener) {
		
		if (handler == null) {
			handler  = new Handler();
		}
		
		this.handler = handler;
		this.listener = listener;
		this.name = name;
		this.totalSecond = 0;
		this.isInfinite =true;
		isTimer = false;
	}
	
	/**
	 * 启动计时器
	 */
	public void start(){
		if (isTimer) {
			return;
		}
		timer = 0;
		isTimer = true;
		tempSecond = totalSecond;
		handler.postDelayed(runnable, interval);
	}
	
	/**
	 * 外部调用有非主线程的风险
	 */
	public void stop(){
		
		isTimer = false;
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

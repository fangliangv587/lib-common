package com.cenco.lib.common;

import android.content.Context;
import android.view.WindowManager;

public class SizeUtil {

	private static int screenWidth = 0;
	private static int screenHeight = 0;

	@SuppressWarnings("deprecation")
	private static void getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		screenWidth = width;
		screenHeight = height;
	}
	
	/**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context){
		if (screenWidth == 0) {
			getScreenSize(context);
			return screenWidth;
		}
		return screenWidth;
	}
	
	/**
	 * 获取屏幕高度
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context){
		if (screenHeight == 0) {
			getScreenSize(context);
			return screenHeight;
		}
		return screenHeight;
	}
	
	/**
	 * 输出等比例的宽高最小值
	 * @param context
	 * @param rate 0-1
	 * @return
	 */
	public static int getEqualSize(Context context, float rate){
		if (screenHeight == 0 || screenWidth == 0) {
			getScreenSize(context);
		}
		
		int w = (int) (screenWidth * rate);
		int h = (int) (screenHeight * rate);
		
		return w < h ? w : h;
		
	}
	
	
}

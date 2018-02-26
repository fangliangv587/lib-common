package com.cenco.lib.common;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void show(Context context,String text,boolean isLong){
		int duration = isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(context, text, duration).show();
	}
	
	public static void showLong(Context context,String text){
		show(context,text,true);
	}
	
	public static void showLong(Context context,int res){
		show(context,context.getString(res),true);
	}
	
	public static void showShort(Context context,String text){
		show(context,text,false);
	}
	
	public static void showShort(Context context,int res){
		show(context,context.getString(res),false);
	}
	
	public static void show(Context context,String text){
		show(context,text,true);
	}
	
	public static void show(Context context,int res){
		show(context,context.getString(res),true);
	}
	
	
	
}

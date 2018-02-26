package com.cenco.lib.common;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class SystemUtil {

	/**
	 * handler 发送消息
	 * @param handler
	 * @param what
	 * @param obj
	 */
	public static void sendMessage(Handler handler, int what, Object obj){
		Message message = Message.obtain();
		message.what = what;
		message.obj = obj;
		handler.sendMessage(message);
	}
	public static void sendMessage(Handler handler, int what){
		sendMessage(handler,what,null);
	}

    /**
     * 调用系统相机拍照
     * @param context
     * @param filePath
     * @param requestCode
     */
	public static void takePhotoBySystemCamera(Activity context, String filePath, int requestCode){
		if ((ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)||
				(ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
			//如果没有授权，则请求授权
			ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}else{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File file = new File(filePath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri photoURI = FileProvider.getUriForFile(context,
						BuildConfig.APPLICATION_ID + ".fileProvider",
						file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                LogUtil.i("take photo from  above android 7");
				context.startActivityForResult(intent, requestCode);
				return;
			}

			LogUtil.i(" takephoto");
			//实例化intent,指向摄像头
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//根据路径实例化图片文件
			File photoFile = new File(filePath);
			//设置拍照后图片保存到文件中
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			//启动拍照activity并获取返回数据
			context.startActivityForResult(intent, requestCode);
		}
	}


	/**
	 * 复制文本到剪贴板
	 *
	 * @param text 文本
	 */
	public static void copyText(Context context, CharSequence text) {
		ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
		if (clipboard != null) {
			clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
		}
	}

	/**
	 * 获取剪贴板的文本
	 *
	 * @return 剪贴板的文本
	 */
	public static CharSequence getText(Context context) {
		ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
		if (clipboard == null) return null;
		ClipData clip = clipboard.getPrimaryClip();
		if (clip != null && clip.getItemCount() > 0) {
			return clip.getItemAt(0).coerceToText(context.getApplicationContext());
		}
		return null;
	}
}

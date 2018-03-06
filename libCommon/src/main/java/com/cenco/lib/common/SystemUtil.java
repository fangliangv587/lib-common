package com.cenco.lib.common;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import android.text.TextUtils;
import android.util.Log;

import com.cenco.lib.common.log.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SystemUtil {

	/**
	 * 获取本地软件版本号
	 */
	public static int getVersionCode(Context ctx) {
		int localVersion = 0;
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}

	/**
	 * 获取本地软件版本号名称
	 */
	public static String getVersionName(Context ctx) {
		String localVersion = "";
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}

	/**
	 * 判断某一Service是否正在运行
	 *
	 * @param context     上下文
	 * @param serviceName Service的全路径： 包名 + service的类名
	 * @return true 表示正在运行，false 表示没有运行
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(Integer.MAX_VALUE);
		if (runningServiceInfos.size() <= 0) {
			return false;
		}
		for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
			if (serviceInfo.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMainProcess(Context context){
		// 获取当前包名
		String packageName = context.getPackageName();
		// 获取当前进程名
		String processName = getProcessName(android.os.Process.myPid());

		return processName != null && processName.equals(packageName);
	}

	public static boolean isMainProcess2(Context context){
		// 获取当前包名
		String packageName = context.getPackageName();
		// 获取当前进程名
		String processName = getCurrentProcessName(context);

		return processName != null && processName.equals(packageName);
	}

	/**
	 * 获取当前进程名
	 */
	private static String getCurrentProcessName(Context context) {
		int pid = android.os.Process.myPid();
		String processName = "";
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
			if (process.pid == pid) {
				processName = process.processName;
			}
		}
		return processName;
	}

	/**
	 * 获取进程号对应的进程名
	 *
	 * @param pid 进程号
	 * @return 进程名
	 */
	private static String getProcessName(int pid) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
			String processName = reader.readLine();
			if (!TextUtils.isEmpty(processName)) {
				processName = processName.trim();
			}
			return processName;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return null;
	}

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
                LogUtils.i("take photo from  above android 7");
				context.startActivityForResult(intent, requestCode);
				return;
			}

			LogUtils.i(" takephoto");
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

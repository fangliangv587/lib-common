package com.cenco.lib.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.cenco.lib.common.log.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class SystemUtil {

	private static final String TAG = SystemUtil.class.getSimpleName();

	/**
	 * 获取mac
	 * @param context
	 * @return
	 */
	public static String getMac(Context context){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String str = info.getMacAddress();

		String fixMac = "02:00:00:00:00:00";
		if (str == null || fixMac.equals(str)){
			str = getExeMac();
		}
		return  str;
	}


	private static String getMacAddress() {
		String address = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface netWork = interfaces.nextElement();
				if (netWork != null && netWork.getName().equals("wlan0")) {
					byte[] by = netWork.getHardwareAddress();
					if (by != null && by.length > 0) {
						StringBuilder builder = new StringBuilder();
						for (byte b : by) {
							builder.append(String.format("%02X:", b));
						}
						if (builder.length() > 0) {
							builder.deleteCharAt(builder.length() - 1);
							address = builder.toString();
						}
					}
				}
			}
		} catch (Exception e) {
		}

		return address;
	}


	/**
	 * 这是使用adb shell命令来获取mac地址的方式
	 * @return
	 */
	private static String getExeMac() {
		String macSerial = null;
		String str = "";

		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str; ) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}
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
	public static void sendMessage(Handler handler, int what, Object obj,int arg1){
		Message message = Message.obtain();
		message.what = what;
		message.obj = obj;
		message.arg1 = arg1;
		handler.sendMessage(message);
	}
	public static void sendMessage(Handler handler, int what, Object obj){
		sendMessage(handler,what,null,-1);
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
                LogUtils.v("take photo from  above android 7");
				context.startActivityForResult(intent, requestCode);
				return;
			}

			LogUtils.v(" takephoto");
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

	/**
	 * 检查网络是否可用
	 *
	 * @param context
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}


	/**
	 * 判断WIFI网络是否可用
	 * @param context
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 * @param context
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}


	/**
	 * ping命令判断网络
	 * @param ipAddress
	 * @return
	 */
	private static boolean pingIpAddress(String ipAddress) {
		try {
			Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 " + ipAddress);
			int status = process.waitFor();
			if (status == 0) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

}

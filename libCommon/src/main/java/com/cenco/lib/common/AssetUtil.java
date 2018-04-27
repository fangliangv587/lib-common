package com.cenco.lib.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cenco.lib.common.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/23.
 */

public class AssetUtil {


    /**
     * 判断assets文件夹下的文件是否存在
     *
     * @return false 不存在    true 存在
     */
    public static boolean isFileExists(Context context,String path) {
        boolean result = false;
        if (path == null)
            return false;
        String newPath = "";
        String filename = path;
        if (path.contains(File.separator)){
            int i = path.lastIndexOf(File.separator);
            newPath = path.substring(0, i);
            filename = path.substring(i + 1);
        }
        String[] list = null;
        try {
            list = context.getAssets().list(newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list == null) return false;
        for (String name : list){
            if (TextUtils.equals(name,filename)){
                result = true;
                break;
            }
        }
        LogUtils.d("util","dest folder:"+newPath+",file name:"+filename+"----->result:"+result);
        return result;
    }

    /**
     * copy 拷贝
     * @param context
     * @param outPath
     * @return
     */
    public static boolean copyAssetsFilesToData(Context context, String outPath) {
        String inPath = "";
        long begin = System.currentTimeMillis();
        boolean ret = copyFiles(context, inPath, outPath);
        long end = System.currentTimeMillis();
        LogUtils.i("util", "copyAssetsFilesToData() elapsedTime:" + (end - begin));
        return ret;
    }

    /**
     * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝
     *
     * @param context 上下文
     * @param inPath  文件目录，要拷贝的目录
     * @param outPath 目标文件夹位置如：/sdcrad/mydir
     */
    public static boolean copyFiles(Context context, String inPath, String outPath) {
        LogUtils.i("util", "copyFiles() inPath:" + inPath + ", outPath:" + outPath);
        String[] fileNames = null;
        try {// 获得Assets一共有几多文件
            fileNames = context.getAssets().list(inPath);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
        if (fileNames.length > 0) {//如果是目录
            File fileOutDir = new File(outPath);
            if (fileOutDir.isFile()) {
                boolean ret = fileOutDir.delete();
                if (!ret) {
                    LogUtils.e("util", "delete() FAIL:" + fileOutDir.getAbsolutePath());
                }
            }
            if (!fileOutDir.exists()) { // 如果文件路径不存在
                if (!fileOutDir.mkdirs()) { // 创建文件夹
                    LogUtils.e("util", "mkdirs() FAIL:" + fileOutDir.getAbsolutePath());
                    return false;
                }
            }
            for (String fileName : fileNames) { //递归调用复制文件夹
                String inDir = inPath;
                String outDir = outPath + File.separator;
                if (!inPath.equals("")) { //空目录特殊处理下
                    inDir = inDir + File.separator;
                }
                copyFiles(context, inDir + fileName, outDir + fileName);
            }
            return true;
        } else {//如果是文件
            try {
                File fileOut = new File(outPath);
                if (fileOut.exists()) {
                    boolean ret = fileOut.delete();
                    if (!ret) {
                        LogUtils.e("util", "delete() FAIL:" + fileOut.getAbsolutePath());
                    }
                }
                boolean ret = fileOut.createNewFile();
                if (!ret) {
                    LogUtils.e("util", "createNewFile() FAIL:" + fileOut.getAbsolutePath());
                }
                FileOutputStream fos = new FileOutputStream(fileOut);
                InputStream is = context.getAssets().open(inPath);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }


    /**
     * 获取assets中某文件的内容
     * @param context
     * @param path
     * @return
     * @throws IOException
     */
    public static String getAssetsFileText(Context context, String path) throws IOException {
        InputStream is = context.getResources().getAssets().open(path);
        String s = IOUtils.readStream2String(is);
        return s;
    }

}

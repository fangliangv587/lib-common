package com.cenco.lib.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/5.
 * SharePreference工具类
 */

public class SPUtil {

    private static final String TAG = SPUtil.class.getSimpleName();

    public static final String FILE_NAME = "share_data";

    public static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {

            editor.putString(key, (String) object);

        } else if (object instanceof Integer) {

            editor.putInt(key, (Integer) object);

        } else if (object instanceof Boolean) {

            editor.putBoolean(key, (Boolean) object);

        } else if (object instanceof Float) {

            editor.putFloat(key, (Float) object);

        } else if (object instanceof Long) {

            editor.putLong(key, (Long) object);

        } else {

            editor.putString(key, object.toString());

        }
        editor.apply();
    }

    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {

            return sp.getString(key, (String) defaultObject);

        } else if (defaultObject instanceof Integer) {

            return sp.getInt(key, (Integer) defaultObject);

        } else if (defaultObject instanceof Boolean) {

            return sp.getBoolean(key, (Boolean) defaultObject);

        } else if (defaultObject instanceof Float) {

            return sp.getFloat(key, (Float) defaultObject);

        } else if (defaultObject instanceof Long) {

            return sp.getLong(key, (Long) defaultObject);

        }
        return null;
    }
    /**
     * 移除某个key值已经对应的值
     */
    public static boolean remove(Context context, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        }catch (Exception e){
            return  false;
        }
        return true;
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }




}

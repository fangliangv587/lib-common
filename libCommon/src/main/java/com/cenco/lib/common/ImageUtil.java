package com.cenco.lib.common;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Administrator on 2018/2/1.
 * 使用参考 https://blog.csdn.net/leihuiaa/article/details/52890858
 */

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();
    public static void loadImage(Context context,String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    /**
     * 缓存加载
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadImageByCacheSource(Context context,String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .diskCacheStrategy( DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    /**
     * 缓存加载
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadImageByCacheResult(Context context,String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .diskCacheStrategy( DiskCacheStrategy.RESULT)
                .into(imageView);
    }
}

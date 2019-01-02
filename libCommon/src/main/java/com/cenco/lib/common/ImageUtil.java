package com.cenco.lib.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Administrator on 2018/2/1.
 * 使用参考 https://blog.csdn.net/leihuiaa/article/details/52890858
 */

public class ImageUtil {

    private static final String TAG = ImageUtil.class.getSimpleName();

    public static void init(Application app){
        Glide glide = Glide.get(app);
        glide.setMemoryCategory(MemoryCategory.HIGH);

    }

    public static void loadImage(Context context,String path, ImageView imageView){
        if (context==null){
            return;
        }

        if (context instanceof Activity){
            Activity activity = (Activity) context;
            if (activity.isDestroyed()){
                return;
            }
        }
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
    public static void loadImageByCacheSource(Context context,String path, ImageView imageView,int errorRes){
        if (context==null){
            return;
        }

        if (context instanceof Activity){
            Activity activity = (Activity) context;
            if (activity.isDestroyed()){
                return;
            }
        }

        Glide.with(context)
                .load(path)
                .diskCacheStrategy( DiskCacheStrategy.SOURCE)

                .error(errorRes)
                .into(imageView);
    }

    /**
     * 缓存加载
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadImageByCacheResult(Context context,String path, ImageView imageView){
        if (context==null){
            return;
        }

        if (context instanceof Activity){
            Activity activity = (Activity) context;
            if (activity.isDestroyed()){
                return;
            }
        }
        Glide.with(context)
                .load(path)
                .diskCacheStrategy( DiskCacheStrategy.RESULT)
                .into(imageView);
    }
}

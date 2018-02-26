package com.cenco.lib.common;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Administrator on 2018/2/1.
 */

public class ImageUtil {

    public static void loadImage(Context context,String path, ImageView imageView){
        Glide.with(context)
                .load(path)
                .into(imageView);
    }
}

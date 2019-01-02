package com.cenco.lib.common;

import android.graphics.Color;

/**
 * Created by Administrator on 2018/9/18.
 */

public class ColorUtils {

    /**
     * 获取随机色
     * @return
     */
    public static int getRandomColor(){
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return Color.rgb(r,g,b);
    }
}

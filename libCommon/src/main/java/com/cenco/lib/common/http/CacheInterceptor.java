package com.cenco.lib.common.http;

import android.content.Context;
import android.util.Log;

import com.cenco.lib.common.SystemUtil;
import com.cenco.lib.common.log.LogUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {

    String TAG = "http";

    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //拦截Request对象
        Request request = chain.request();
        //判断有无网络连接
        boolean connected = SystemUtil.isNetworkAvailable(context);
        if (!connected) {
            //如果没有网络,从缓存获取数据
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
          LogUtils.d(TAG, "have no network(沒有网络)------------->from cache");
        }
        Response response = chain.proceed(request);

        if (connected) {
            //有网络，缓存时间短
            Log.e("zhanghe", "有网络");
            String cacheControl = request.cacheControl().toString();
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control","public, max-age=90")
                    .build();
        } else {
            //没有网络
            Log.e("zhanghe", "没有网络的缓存设置");
            int maxTime = 3600;
            return response.newBuilder()
                    //这里的设置的是我们的没有网络的缓存时间，想设置多少就是多少。
                    .header("Cache-Control", "public, max-age=" + maxTime)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}

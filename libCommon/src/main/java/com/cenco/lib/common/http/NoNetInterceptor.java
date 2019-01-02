package com.cenco.lib.common.http;

import android.content.Context;

import com.cenco.lib.common.SystemUtil;
import com.cenco.lib.common.log.LogUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: zh on 2017/4/13.
 * 在没有网络的情况下，取读缓存数据
 * https://www.jianshu.com/p/cf59500990c7
 */

public class NoNetInterceptor implements Interceptor {

    private Context context;

    public NoNetInterceptor(Context context) {
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String cacheControl = request.header("Cache-Control");
        if (cacheControl!=null && cacheControl.equals("no-cache")){
            return chain.proceed(request);
        }

        boolean connected = SystemUtil.isNetworkAvailable(context);
        //如果没有网络，则启用 FORCE_CACHE
        if (!connected) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            LogUtils.d("http", "无网络设置_common");

            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=3600")
                    .removeHeader("Pragma")
                    .build();
        }
        //有网络的时候，这个拦截器不做处理，直接返回
        return chain.proceed(request);
    }
}

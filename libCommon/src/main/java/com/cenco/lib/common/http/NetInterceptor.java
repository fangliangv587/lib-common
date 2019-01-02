package com.cenco.lib.common.http;

import android.content.Context;

import com.cenco.lib.common.SystemUtil;
import com.cenco.lib.common.log.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 在有网络的情况下，先去读缓存，设置的缓存时间到了，在去网络获取
 * https://www.jianshu.com/p/cf59500990c7
 */

public class NetInterceptor implements Interceptor {

    private Context context;

    public NetInterceptor(Context context) {
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean connected = SystemUtil.isNetworkAvailable(context);
        if(connected){
            //如果有网络，缓存90s
            LogUtils.d("http","有网络:"+request.url().toString());
            Response response = chain.proceed(request);
            int maxTime = 10;
            String cacheControl = request.header("Cache-Control");
            if (cacheControl!=null && cacheControl.equals("no-cache")){
                maxTime = 0;
            }
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + maxTime)
                    .build();
        }
        //如果没有网络，不做处理，直接返回
        return chain.proceed(request);     
    }
}

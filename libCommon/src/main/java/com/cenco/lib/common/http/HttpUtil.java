package com.cenco.lib.common.http;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/3/13.
 */

public class HttpUtil {

    private static boolean isInit = false;

    /**
     * 初始化
     * @param app
     */
    public static void init(Application app) {

        if (isInit){
            return;
        }

        isInit = true;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        //全局的读取超时时间
        int seconds = 60;
        builder.readTimeout(seconds, TimeUnit.SECONDS);
        //全局的写入超时时间
        builder.writeTimeout(seconds, TimeUnit.SECONDS);
        //全局的连接超时时间
        builder.connectTimeout(seconds, TimeUnit.SECONDS);

        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        //信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);

        OkGo.getInstance().init(app)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0);                              //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0

    }


    /**
     * 添加公共请求头
     * @param headers
     */
    public static void addCommonHeaders(HttpHeaders headers){
        checkInit();
        OkGo.getInstance().addCommonHeaders(headers);
    }

    /**
     * 添加公共参数
     * @param params
     */
    public static void addCommonParams(HttpParams params){
        checkInit();
        OkGo.getInstance().addCommonParams(params);
    }


    /**
     * 检查初始化
     */
    public static void checkInit(){
        if (!isInit)
            throw new IllegalArgumentException("请在application中调用初始化方法");
    }


    public static<T> void get(String url,SimpleCallback<T> callback){
        OkGo.<T>get(url).execute(callback);
    }

    public static<T> void post(String url,HttpParams params, SimpleCallback<T> callback){
        OkGo.<T>post(url).params(params).execute(callback);
    }


}

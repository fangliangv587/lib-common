package com.cenco.lib.common.http;

import android.app.Application;

import com.cenco.lib.common.ToastUtil;
import com.cenco.lib.common.json.GsonUtil;
import com.cenco.lib.common.log.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/13.
 */

public class HttpUtil {

    public static final String TAG = HttpUtil.class.getSimpleName();

    //初始化
    private static boolean isInit = false;
    //接口mock
    private static boolean ismock = false;

    private static String mockPath = "mock";


    private static ApiMock mockManger;

    /**
     * 初始化
     *
     * @param app
     */
    public static void init(Application app) {
        init(app, false);
    }

    /**
     * @param app
     * @param isMock if true, you should know the default mockManger path is {@link HttpUtil#mockPath},also you can call {@link HttpUtil#init(Application, boolean, String)}
     */
    public static void init(Application app, boolean isMock) {
        init(app, isMock, mockPath);
    }

    /**
     * 初始化
     *
     * @param app
     * @param isMock 是否对接口进行mock
     */
    public static void init(Application app, boolean isMock, String path) {

        if (isInit) {
            return;
        }

        isInit = true;
        ismock = isMock;

        mockManger = new ApiMock(path, app.getApplicationContext());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        //全局的读取超时时间，超过30s的最终会显示30s
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
     *
     * @param headers
     */
    public static void addCommonHeaders(HttpHeaders headers) {
        checkInit();
        OkGo.getInstance().addCommonHeaders(headers);
    }

    /**
     * 添加公共参数
     *
     * @param params
     */
    public static void addCommonParams(HttpParams params) {
        checkInit();
        OkGo.getInstance().addCommonParams(params);
    }


    /**
     * 检查初始化
     */
    public static void checkInit() {
        if (!isInit)
            throw new IllegalArgumentException("请在application中调用初始化方法");
    }

    /**
     * get回调请求
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public static <T> void get(String url, HttpParams params, Callback<T> callback) {
        if (url == null) return;
        url = getParamsUrl(url, params);
        get(url, callback);
    }

    /**
     * get同步请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String getSync(String url, HttpParams params) {
        if (url == null) {
            return null;
        }
        url = getParamsUrl(url, params);
        return getSync(url);
    }

    /**
     * get回调请求
     *
     * @param url
     * @param callback
     * @param <T>
     */
    public static <T> void get(String url, Callback<T> callback) {
        checkInit();
        if (ismock) {
            mockManger.interruptWeb(url, callback);
            return;
        }
        OkGo.<T>get(url).execute(callback);
    }

    /**
     * get同步请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getSync(String url) {
        checkInit();
        if (ismock) {
            String result = mockManger.interruptWeb(url);

            return result;
        }
        String body = null;
        try {
            LogUtils.w(TAG,"get url="+url);
            Response response = OkGo.get(url).execute();
            body = response.body().string();
            LogUtils.d(TAG,"get sync response ===> "+body);
        } catch (IOException e) {
            LogUtils.e(TAG,e);
            e.printStackTrace();
        }

        return body;
    }

    /**
     * post回调请求
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public static <T> void post(String url, HttpParams params, Callback<T> callback) {
        checkInit();
        if (ismock) {
            mockManger.interruptWeb(url, callback);
            return;
        }
        OkGo.<T>post(url).params(params).execute(callback);
    }


    /**
     * post同步请求
     * @param url
     * @param params
     * @return
     */
    public static String postSync(String url, HttpParams params)  {
        checkInit();
        if (ismock) {
            String result = mockManger.interruptWeb(url);
            return result;
        }
        try {
            LogUtils.w(TAG,"post url="+url);
            LogUtils.i(TAG,getPostParams(params));
            Response response = OkGo.post(url).params(params).execute();
            String body = response.body().string();
            LogUtils.d(TAG,"post sync response ===> "+body);
            return body;
        } catch (IOException e) {
            LogUtils.e(TAG,e);
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 此方法未经测试
     *
     * @param url
     * @param jsonString
     * @param callback
     * @param <T>
     */
    public static <T> void postJson(String url, String jsonString, Callback<T> callback) {
        checkInit();
        if (ismock) {
            mockManger.interruptWeb(url, callback);
            return;
        }
        OkGo.<T>post(url).upJson(jsonString).execute(callback);
    }

    /**
     * 简单的文件下载
     *
     * @param url
     * @param callback 可指定下载目录，见https://github.com/jeasonlzy/okhttp-OkGo/wiki/OkGo#5%E5%9F%BA%E6%9C%AC%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD
     */
    public static void download(String url, FileCallback callback) {
        checkInit();
        if (ismock) {
            mockManger.interruptWeb(url, callback);
            return;
        }

        OkGo.<File>get(url).execute(callback);
    }


    /**
     * get请求参数拼接
     *
     * @param url
     * @param params
     * @return
     */
    private static String getParamsUrl(String url, HttpParams params) {
        if (params != null) {

            StringBuilder result = new StringBuilder();
            for (ConcurrentHashMap.Entry<String, List<String>> entry : params.urlParamsMap.entrySet()) {
                if (result.length() > 0) result.append("&");
                result.append(entry.getKey()).append("=").append(entry.getValue().get(0));
            }

            String param = result.toString();
            if (!url.contains("?")) {
                url += "?";
            }
            url += param;
        }
        return url;
    }


    /**
     * 获取post请求参数
     * @param params
     * @return
     */
    public static String getPostParams(HttpParams params){
        if (params != null) {

            StringBuilder result = new StringBuilder();
            for (ConcurrentHashMap.Entry<String, List<String>> entry : params.urlParamsMap.entrySet()) {
                result.append(entry.getKey()).append(":").append(entry.getValue().get(0)).append("\n");
            }

           return result.toString();
        }
        return "";
    }


}

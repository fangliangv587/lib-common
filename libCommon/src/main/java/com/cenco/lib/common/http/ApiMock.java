package com.cenco.lib.common.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.cenco.lib.common.AssetUtil;
import com.cenco.lib.common.SystemUtil;
import com.cenco.lib.common.ThreadManager;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.HttpMethod;

import java.io.File;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/3/14.
 */

public class ApiMock<T> {

    private String folder;
    private Context context;
    private int number;

    private SparseArray<Callback> sparseArray = new SparseArray<>();

    private static final int msg_success = 0x0001;
    private static final int msg_error = 0x0002;
    private static final int msg_start = 0x0003;
    private static final int msg_finish = 0x0004;

    //mock等待时间
    private static final int wait_time = 1*1000;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            switch (msg.what){
                case msg_success:
                    int tag1 = msg.arg1;
                    com.lzy.okgo.model.Response<T> response1 = (com.lzy.okgo.model.Response<T>) msg.obj;
                    com.lzy.okgo.callback.Callback<T> callback1 = findCallback(tag1);
                    callback1.onSuccess(response1);
                    break;
                case msg_error:
                    int tag2 = msg.arg1;
                    com.lzy.okgo.callback.Callback<T> callback2 = findCallback(tag2);
                    com.lzy.okgo.model.Response<T> response2 = (com.lzy.okgo.model.Response<T>) msg.obj;
                    callback2.onError(response2);
                    break;

                case msg_start:
                    int tag3 = msg.arg1;
                    com.lzy.okgo.callback.Callback<T> callback3 = findCallback(tag3);
                    callback3.onStart(null);
                    break;

                case msg_finish:
                    int tag4 = msg.arg1;
                    com.lzy.okgo.callback.Callback<T> callback4 = findCallback(tag4);
                    callback4.onFinish();
                    break;

            }
        }
    };

    public ApiMock(String folder, Context applicationContext) {
        this.folder = folder;
        this.context = applicationContext;
    }


    public <T> void interruptWeb(String url, Callback<T> callback) {

        if (url == null || callback == null) {
            return;
        }
        int tag = addCallback(callback);
        MockRunnable<T> mockRunnable = new MockRunnable<>(url,callback,tag);
        ThreadManager.getPoolProxy().execute(mockRunnable);

    }

    private <T> int addCallback(Callback<T> callback) {
        int tag = number++;
        sparseArray.put(tag,callback);
        return tag;
    }

    private Callback<T>  findCallback(int key){
        return sparseArray.get(key);
    }

    private Request getRawRequest(String url){
        Request.Builder rbuild = new Request.Builder();
        if (!url.startsWith("http")){
            url = "http://www.mock.com/"+url;
        }
        rbuild.url(url);
        Request request = rbuild.build();
        return request;
    }

    private Response getRawResponse(Request request,String text) {

        Response.Builder builder = new Response.Builder();
        builder.body(ResponseBody.create(null,text));
        builder.message("mock the web api");
        builder.code(200);
        builder.protocol(Protocol.HTTP_1_1);
        builder.request(request);
        return builder.build();
    }

    @NonNull
    private String getUrlPath(String url) {
        url = url.substring(url.lastIndexOf("/"));
        if (url.contains("?")){
            url =url.substring(1,url.indexOf("?"));
        }else {
            url = url.substring(1);
        }
        return url;
    }


    class MockRunnable<T> implements Runnable{

        private Callback<T> callback;
        private String url;
        private int tag;

        public MockRunnable(String url,Callback<T> callback,int tag) {
            this.url = url;
            this.callback = callback;
            this.tag = tag;
        }

        @Override
        public void run() {

            SystemUtil.sendMessage(handler,msg_start,null,this.tag);
            final Request request = getRawRequest(url);
            try {

                Thread.sleep(wait_time);

                String urlPath = getUrlPath(url);
                String filePath = folder + File.separator + urlPath;
                if (!AssetUtil.isFileExists(context,filePath)){
                    throw new IllegalArgumentException("the path is not exist:"+filePath);
                }
                String text = AssetUtil.getAssetsFileText(context, filePath);
                Response rawResponse = getRawResponse(request,text);
                T t = callback.convertResponse(rawResponse);
                com.lzy.okgo.model.Response<T> response = com.lzy.okgo.model.Response.success(false,t,null,rawResponse);

                SystemUtil.sendMessage(handler,msg_success,response,this.tag);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Response rawResponse = getRawResponse(request,throwable.getMessage());
                com.lzy.okgo.model.Response<T> response = com.lzy.okgo.model.Response.error(false,null,rawResponse,throwable);

                SystemUtil.sendMessage(handler,msg_error,response,this.tag);
            }

            SystemUtil.sendMessage(handler,msg_finish,null,this.tag);
        }
    }
}

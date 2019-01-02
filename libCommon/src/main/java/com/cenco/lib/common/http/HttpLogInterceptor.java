/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cenco.lib.common.http;

import android.text.TextUtils;

import com.cenco.lib.common.log.LogUtils;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;


public class HttpLogInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private volatile Level printLevel = Level.BODY;
    private boolean simple;

    private String TAG = HttpLogInterceptor.class.getSimpleName();

    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY        //所有数据全部打印
    }

    public HttpLogInterceptor(String tag,boolean simple) {
        TAG = TextUtils.isEmpty(tag) ? TAG : tag;
        this.simple = simple;
    }

    public HttpLogInterceptor(String tag, Level level) {
        TAG = TextUtils.isEmpty(tag) ? TAG : tag;
        printLevel = level == null ? Level.BODY : level;
    }


    private void log(String message,boolean isResponse) {
        String msg = message;
        try {
            msg =  URLDecoder.decode(message,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (isResponse){
            LogUtils.w(TAG, msg);
        }else {
            LogUtils.i(TAG, msg);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (printLevel == Level.NONE) {
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e,true);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }

    private void logForRequest(Request request, Connection connection) throws IOException {
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            log(requestStartMessage,false);

            if (logHeaders) {
                if (!simple){
                    if (hasRequestBody) {
                        // Request body headers are only present when installed as a network interceptor. Force
                        // them to be included (when available) so there values are known.
                        if (requestBody.contentType() != null) {
                            log("\tContent-Type: " + requestBody.contentType(),false);
                        }
                        if (requestBody.contentLength() != -1) {
                            log("\tContent-Length: " + requestBody.contentLength(),false);
                        }
                    }
                    Headers headers = request.headers();
                    for (int i = 0, count = headers.size(); i < count; i++) {
                        String name = headers.name(i);
                        // Skip headers from the request body as they are explicitly logged above.
                        if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                            log("\t" + name + ": " + headers.value(i),false);
                        }
                    }

                    log(" ",false);
                }

                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody.contentType())) {
                        bodyToString(request);
                    } else {
                        log("\tbody: maybe [binary body], omitted!",false);
                    }
                }
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            log("--> END " + request.method(),false);
        }
    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS) ;

        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）",true);
            if (logHeaders) {
                if (!simple){
                    Headers headers = clone.headers();
                    for (int i = 0, count = headers.size(); i < count; i++) {
                        log("\t" + headers.name(i) + ": " + headers.value(i),true);
                    }
                    log(" ",true);
                }

                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (responseBody == null) return response;

                    if (isPlaintext(responseBody.contentType())) {
                        byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
                        MediaType contentType = responseBody.contentType();
                        String body = new String(bytes, getCharset(contentType));
                        log("\tbody:" + body,true);
                        responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                        return response.newBuilder().body(responseBody).build();
                    } else {
                        log("\tbody: maybe [binary body], omitted!",true);
                    }
                }
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            log("<-- END HTTP",true);
        }
        return response;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            if (request.body() instanceof FormBody) {
                StringBuilder sb = new StringBuilder();
                FormBody fb = (FormBody) request.body();
                for (int i = 0; i < fb.size(); i++) {
                    String key = fb.encodedName(i);
                    String value = fb.encodedValue(i);
                    if (value.length()>70){
                        value = value.substring(0,70)+"......";
                    }
                    sb.append( key + "=" + value + ",");
                }
                log("\tbody(form):" + sb.toString(),false);
            }
            //全打印
//            Buffer buffer = new Buffer();
//            body.writeTo(buffer);
//            Charset charset = getCharset(body.contentType());
//            log("\tbody:" + buffer.readString(charset),false);
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
    }
}

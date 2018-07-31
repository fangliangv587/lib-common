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

import com.cenco.lib.common.json.GsonUtil;
import com.cenco.lib.common.log.LogUtils;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.convert.Converter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GsonConvert<T> implements Converter<T> {

    private Type type;
    private Class<T> clazz;

    private static final String TAG =HttpUtil.TAG;

    public GsonConvert() {
    }

    public GsonConvert(Type type) {
        this.type = type;
    }

    public GsonConvert(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback

        ResponseBody body = response.body();
        if (body == null){
            return  null;
        }

        String text = body.string();


        LogUtils.d(TAG,"onResponse = = = = = = = = >>>\r\n"+response.request().toString()+",\r\nResponse:"+text);

        if (type == null) {
            if (clazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                return parseClass(text, clazz);
            }
        }

        if (type instanceof ParameterizedType) {
            return parseParameterizedType(text, (ParameterizedType) type);
        } else if (type instanceof Class) {
            return parseClass(text, (Class<T>) type);
        } else {
            return parseType(text, type);
        }
    }

    private T parseClass(String text, Class<T> rawType) throws Exception {
        if (rawType == null) return null;

        if (rawType == String.class) {
            //noinspection unchecked
            return (T) text;
        } else if (rawType == JSONObject.class) {
            //noinspection unchecked
            return (T) new JSONObject(text);
        } else if (rawType == JSONArray.class) {
            //noinspection unchecked
            return (T) new JSONArray(text);
        } else {
            T t = GsonUtil.fromJson(text, rawType);
            return t;
        }
    }

    private T parseType(String text, Type type) throws Exception {
        if (type == null) return null;

        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        T t = GsonUtil.fromJson(text, type);
        return t;
    }

    private T parseParameterizedType(String text, ParameterizedType type) throws Exception {
        if (type == null) return null;

            T t = GsonUtil.fromJson(text, type);
            return t;

    }
}

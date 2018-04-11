package com.cenco.lib.common;

import android.text.TextUtils;

import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.http.SimpleCallback;

/**
 * Created by Administrator on 2018/3/30.
 */

public class UpdateHelper<T> {
    /**
     * 下载url
     */
    private String downUrl;

    private String checkUrl;

    public UpdateHelper(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    public void checkVersion(){
        if (TextUtils.isEmpty(checkUrl)){
            return;
        }
        HttpUtil.get(this.checkUrl, new SimpleCallback<T>() {
            @Override
            public void onSuccess(T t) {

            }

            @Override
            public void onError(String reason) {

            }
        });
    }
}

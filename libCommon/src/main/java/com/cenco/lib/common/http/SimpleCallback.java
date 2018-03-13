package com.cenco.lib.common.http;

import com.lzy.okgo.model.Response;

/**
 * Created by Administrator on 2018/3/13.
 */

public abstract class SimpleCallback<T> extends JsonCallback<T> {

    @Override
    public void onSuccess(Response<T> response) {
        onSuccess(response.body());
    }

    @Override
    public void onError(Response<T> response) {
        super.onError(response);
        onError(response.getException().getMessage());
    }

    @Override
    public void onCacheSuccess(Response<T> response) {
        super.onCacheSuccess(response);
        onSuccess(response);
    }

    public abstract void onSuccess(T t);

    public abstract void onError(String reason);

}

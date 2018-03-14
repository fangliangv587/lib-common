package com.cenco.lib.common.http;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;

import com.lzy.okgo.request.base.Request;

/**
 * Created by Administrator on 2018/3/13.
 * 提供默认的等待dialog显示消失
 */

public abstract class SimpleDialogCallback<T> extends SimpleCallback<T> {

    private Dialog dialog;
    private Context context;

    public SimpleDialogCallback(Context context,String msg) {
        this.dialog= createDefaultDialog(context, msg);
        this.context = context;
    }

    public SimpleDialogCallback(Context context,Dialog dialog) {
        this.dialog= dialog;
        this.context = context;
    }

    public SimpleDialogCallback(Context context){
        this(context,"请求网络中...");
    }

    /**
     * 创建默认的显示dialog
     * @param context
     * @param msg
     * @return
     */
    @NonNull
    private ProgressDialog createDefaultDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        return progressDialog;
    }


    /**
     * 显示dialog
     * @param request
     */
    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        if (dialog != null){
            dialog.show();
        }
    }

    /**
     * 隐藏dialog
     */
    @Override
    public void onFinish() {
        super.onFinish();
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}

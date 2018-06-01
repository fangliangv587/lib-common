package com.cenco.xz.eclipseuselib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cenco.lib.common.ToastUtil;
import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.http.SimpleDialogCallback;
import com.cenco.lib.common.log.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testLog();
    }

    private void testLog() {
        LogUtils.v("mmmmmm");
        LogUtils.i("helloworld");
        LogUtils.d("xz","hahaha",true);
        LogUtils.w("xin","风雨",true);
        LogUtils.e("zhong","填写");
        LogUtils.d("----显示时间:" );
    }

    public void httpRequestClick(View view) {
        String host = "http://192.168.0.110:3000";
        String url =host+"/api/getUserName?id=1";
        HttpUtil.get(url, new SimpleDialogCallback<String>(this) {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("api",s);
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
            }
        });
    }
}

package com.cenco.xz.eclipseuselib;

import android.app.Application;

import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.log.Level;
import com.cenco.lib.common.log.LogUtils;

/**
 * Created by Administrator on 2018/6/1.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init("testuse", Level.VERBOSE);
        HttpUtil.init(this);
    }
}

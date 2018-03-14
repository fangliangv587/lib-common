package cenco.xz.com.commonlib;

import android.app.Application;

import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.log.LogUtils;

/**
 * Created by Administrator on 2018/3/5.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init("libsample");
        HttpUtil.init(this,false,"mockapi");
    }
}

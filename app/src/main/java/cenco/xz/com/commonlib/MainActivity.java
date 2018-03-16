package cenco.xz.com.commonlib;

import android.os.Environment;
import android.os.Bundle;
import android.view.View;

import com.cenco.lib.common.AssetUtil;
import com.cenco.lib.common.SPUtil;
import com.cenco.lib.common.ToastUtil;
import com.cenco.lib.common.activity.BaseActivity;
import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.http.SimpleDialogCallback;
import com.cenco.lib.common.log.LogUtils;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.util.List;

import cenco.xz.com.commonlib.bean.Result;
import cenco.xz.com.commonlib.bean.Task;
import cenco.xz.com.commonlib.bean.User;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.d("xz","message log xz");
        LogUtils.i("helloworld");

    }


    public void getClick(View view) {
        String url ="http://172.26.96.1:3000/api/getUserName?id=1";
        HttpUtil.get(url, new SimpleDialogCallback<Result<User>>(this) {
            @Override
            public void onSuccess(Result<User> s) {
                LogUtils.d("api",s.getData().toString());
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
                ToastUtil.show(mContext,reason);
            }
        });
    }

    public void postClick(View view) {

        String url ="http://172.26.96.1:3000/api/setUserAddress";
        HttpParams params = new HttpParams();
        params.put("address","济南市");
        HttpUtil.post(url, params, new SimpleDialogCallback<Result<String>>(this) {
            @Override
            public void onSuccess(Result<String> s) {
                LogUtils.d("api",s.toString());
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
                ToastUtil.show(mContext,reason);
            }
        });
    }

    public void postFileClick(View view) {
        String url ="http://172.26.96.1:3000/api/upload";
        String path  = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"xz"+File.separator+"xz.jpg";
        File file = new File(path);
        if (!file.exists()){
            ToastUtil.show(this,"文件不存在:"+path);
            return;
        }
        HttpParams params = new HttpParams();
        params.put("imageFile",file);
        HttpUtil.post(url, params, new SimpleDialogCallback<Result<String>>(this) {
            @Override
            public void onSuccess(Result<String> s) {
                LogUtils.d("api",s.toString());
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
                ToastUtil.show(mContext,reason);
            }
        });
    }

    public void taskListClick(View view) {

        String url ="http://172.26.96.1:3000/api/getTaskList";
        HttpParams params = new HttpParams();
        params.put("address","济南市");
        HttpUtil.post(url, params, new SimpleDialogCallback<Result<List<Task>>>(this) {
            @Override
            public void onSuccess(Result<List<Task>> s) {
                LogUtils.d("api",s.toString());
                SPUtil.put(mContext,"task",s);
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
                ToastUtil.show(mContext,reason);
            }
        });
    }

    public void taskDetailClick(View view) {
    }

    public void taskPointPlanClick(View view) {
    }

    public void spClick(View view) {

    }
}


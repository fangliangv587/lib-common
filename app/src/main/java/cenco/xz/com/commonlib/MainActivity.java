package cenco.xz.com.commonlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import com.cenco.lib.common.AssetUtil;
import com.cenco.lib.common.BitmapUtil;
import com.cenco.lib.common.DateUtil;
import com.cenco.lib.common.FileUtils;
import com.cenco.lib.common.PermissionManager;
import com.cenco.lib.common.SPUtil;
import com.cenco.lib.common.ThreadManager;
import com.cenco.lib.common.TimerHelper;
import com.cenco.lib.common.ToastUtil;
import com.cenco.lib.common.UpdateHelper;
import com.cenco.lib.common.activity.BaseActivity;
import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.http.SimpleCallback;
import com.cenco.lib.common.http.SimpleDialogCallback;
import com.cenco.lib.common.json.GsonUtil;
import com.cenco.lib.common.log.LogUtils;
import com.cenco.lib.common.system.MacUtil;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import cenco.xz.com.commonlib.bean.Result;
import cenco.xz.com.commonlib.bean.Task;
import cenco.xz.com.commonlib.bean.User;

public class MainActivity extends BaseActivity implements TimerHelper.TimerListener {

    private ImageView watermarkImageView;
    private ImageView infoImageView;

    private static  final int REQUESTCODE = 0x0011;


    private String token;

    String path;

    TimerHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testLog();

        initView();

        permission();

        initAction();


        String mac = MacUtil.getMac(this);
        LogUtils.d(mac);

    }

    private void test(){
        Type genType = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) genType).getActualTypeArguments()[0];
    }

    private void testLog() {
        LogUtils.v("mmmmmm");
        LogUtils.i("helloworld");
        LogUtils.d("zhang","hahaha");
        LogUtils.w("xin","风雨");
        LogUtils.e("zhong","填写");
        LogUtils.d("----显示时间:" );




    }


    private void initAction() {

        path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"xz"+File.separator+"image";
        AssetUtil.copyFiles(this,"watermark",path);

    }


    private void permission() {
        PermissionManager pm = new PermissionManager(this,REQUESTCODE);
        pm.requestPermission(new PermissionManager.PermissionCallback() {
            @Override
            public void onGrant() {
                ToastUtil.show(mContext,"权限已获取");
            }

            @Override
            public void onDeny() {
                ToastUtil.show(mContext,"权限已被您拒绝");
            }
        },Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void initView() {
        watermarkImageView = findViewById(R.id.watermarkImageView);
        infoImageView = findViewById(R.id.infoImageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("onActivityResult");
        if (requestCode == REQUESTCODE){
            LogUtils.d("requestCode == REQUESTCODE");
            permission();
        }
    }

    /**************************************媒体助手***************************************************/
    public void taskListClickRecieved(View view) {
        String url ="http://devapi.kuaifa.tv/mediaAide/v1/task/getTaskListIng";
        HttpUtil.get(null,url, new SimpleDialogCallback<String>(this) {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("api",s.toString());
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
            }
        });
    }
    public void exceptionClick(View view) {
       int a=0;
       int b= 5/a;
    }

    /**************************************计时器***************************************************/
    public void timerClick(View view) {
        if (helper==null){
            helper = new TimerHelper(this);
        }

        if (helper.isRunning()){
            helper.stop();
        }else {
            helper.start();
        }

    }
    /**************************************打水印***************************************************/

    public void watermarkClick(View view) {
        String name="a.jpg";
        if (view.getId()==R.id.btnWaterA){
            name="a.jpg";
        }
        if (view.getId()==R.id.btnWaterB){
            name="b.jpg";
        }
        if (view.getId()==R.id.btnWaterC){
            name="c.jpg";
        }



        String fontassetpath = "fonts/akong.ttf";
        Typeface typeface = null;
        if (AssetUtil.isFileExists(this,fontassetpath)){
            typeface = Typeface.createFromAsset(getAssets(), fontassetpath);
        }


        String imagePath = path+File.separator+name;
        if(!FileUtils.isFileExists(imagePath)){
            ToastUtil.show(this,"文件不存在");
            return;
        }
        Bitmap bitmap = BitmapUtil.getBitmap(imagePath);

        String str  = "快发网络abc";
        String str1  = "快发网络abc,我们都是勇敢的人，我们都是自信的人，我们都是阳光的人";

        long time1 = System.currentTimeMillis();
        Bitmap bitmap1 = BitmapUtil.getWatermarkBitmap( bitmap, str,typeface,"时间", "地点","执行人","媒体位置","订单号","方案名称");
        long time2 = System.currentTimeMillis();
        LogUtils.d("mark","时间:"+(time2-time1));


        bitmap.recycle();
        watermarkImageView.setImageBitmap(bitmap1);
    }





    /**************************************网络请求部分***************************************************/

    String host = "http://192.168.0.110:3000";
    public void getClick(View view) {
        String url =host+"/api/getUserName?id=1";
        HttpUtil.get(null,url, new SimpleDialogCallback<Result<User>>(this) {
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

    public void getSyncClick(View view) {

        ThreadManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                String url =host+"/api/getUserName?id=1";
                String result = HttpUtil.getSync(url);
                LogUtils.d("api","sync:"+result);
            }
        });
    }

    public void postClick(View view) {

        String url =host+"/api/setUserAddress";
        HttpParams params = new HttpParams();
        params.put("address","济南市");
        HttpUtil.post(null,url, params, new SimpleDialogCallback<Result<String>>(this) {
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

    public void postSyncClick(View view) {
        ThreadManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                String url =host+"/api/setUserAddress";
                HttpParams params = new HttpParams();
                params.put("address","济南市");
                String result = HttpUtil.postSync(url, params);
                LogUtils.d("api","sync:"+result);
            }
        });
    }

    public void postFileClick(View view) {
        String url =host+"/api/upload";
        String path  = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"xz"+File.separator+"xz.jpg";
        File file = new File(path);
        if (!file.exists()){
            ToastUtil.show(this,"文件不存在:"+path);
            return;
        }
        HttpParams params = new HttpParams();
        params.put("imageFile",file);
        HttpUtil.post(null,url, params, new SimpleDialogCallback<Result<String>>(this) {
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

        String url =host+"/api/getTaskList";
        HttpParams params = new HttpParams();
        params.put("address","济南市");
        HttpUtil.post(null,url, params, new SimpleDialogCallback<Result<List<Task>>>(this) {
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


    public void jsonClick(View view) {
        String url ="/api/getTaskList";
        String str = "";
        HttpUtil.postJson(url, str, new SimpleDialogCallback<String>(this) {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("api",s.toString());
            }

            @Override
            public void onError(String reason) {
                LogUtils.e("api",reason);
                ToastUtil.show(mContext,reason);
            }
        });
    }


    public void getImageClick(View view) {
        String url = "http://www.ybol.vip/CheckCode?flag=3";
        HttpUtil.get(null,url, new BitmapCallback() {
            @Override
            public void onSuccess(Response<Bitmap> response) {
                Bitmap body = response.body();
                watermarkImageView.setImageBitmap(body);
            }
        });
    }

    public void downloadClick(View view) {
        LogUtils.w("downloadClick");
        String url = "http://kuaifa.tv/updateversion/adAssist.apk";
        HttpUtil.download(url, new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                LogUtils.v("download","onSuccess:"+response.body().getAbsolutePath());
            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                LogUtils.v("download",progress.fraction+"");
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                LogUtils.e("download",response.getException().getMessage());
            }
        });
    }


    @Override
    public void onTimerRunning(int current, int total, boolean isOver) {

    }

    @Override
    protected void onDestroy() {
        if (helper!=null){
            helper.stop();
        }

        super.onDestroy();

    }
}


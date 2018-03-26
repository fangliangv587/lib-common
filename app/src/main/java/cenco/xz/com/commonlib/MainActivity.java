package cenco.xz.com.commonlib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.cenco.lib.common.ToastUtil;
import com.cenco.lib.common.activity.BaseActivity;
import com.cenco.lib.common.http.HttpUtil;
import com.cenco.lib.common.http.SimpleDialogCallback;
import com.cenco.lib.common.log.LogUtils;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.util.Date;
import java.util.List;

import cenco.xz.com.commonlib.bean.Result;
import cenco.xz.com.commonlib.bean.Task;
import cenco.xz.com.commonlib.bean.User;

public class MainActivity extends BaseActivity {

    private ImageView watermarkImageView;
    private ImageView infoImageView;

    private static  final int REQUESTCODE = 0x0011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.i("helloworld");

        initView();

        permission();


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

    /**************************************打水印***************************************************/

    public void watermarkClick(View view) {
        String fontassetpath = "fonts/akong.ttf";
        Typeface typeface = null;
        if (AssetUtil.isFileExists(this,fontassetpath)){
            typeface = Typeface.createFromAsset(getAssets(), fontassetpath);
        }


        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"xz"+File.separator+"image1.jpg";
        if(!FileUtils.isFileExists(path)){
            ToastUtil.show(this,"文件不存在");
            return;
        }
        Bitmap bitmap = BitmapUtil.getBitmap(path);

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


}


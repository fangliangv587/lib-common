package cenco.xz.com.commonlib;

import android.Manifest;
import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.i("helloworld");

        initView();

        permission();


    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
    }

    private void initView() {
        watermarkImageView = findViewById(R.id.watermarkImageView);
        infoImageView = findViewById(R.id.infoImageView);
    }

    /**************************************打水印***************************************************/

    public void watermarkClick(View view) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/akong.ttf");
        long time1 = System.currentTimeMillis();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"xz"+File.separator+"image1.jpg";
        Bitmap bitmap = BitmapUtil.getBitmap(path);
//        Bitmap bitmap1 = BitmapUtil.getWatermarkBitmap(this, bitmap, "快发网络abc",null,"时间", "地点","执行人","媒体位置","订单号","方案名称");
        Bitmap bitmap1 = BitmapUtil.getWatermarkBitmap( bitmap, "快发网络abc",typeface,"时间", "地点","执行人","媒体位置","订单号","方案名称");

        bitmap.recycle();
        watermarkImageView.setImageBitmap(bitmap1);
        long time2 = System.currentTimeMillis();
        LogUtils.d("mark","时间:"+(time2-time1));
    }


    /**
     * 图片打水印 并将信息内容合成到图片中
     * @param bitmap
     * @param mark 水印
     * @param infos 信息内容
     * example:Bitmap bitmap1 = getWatermarkBitmap( bitmap, "快发网络","时间", "地点","执行人","媒体位置","订单号","方案名称");
     * @return
     */
    public Bitmap getWatermarkBitmap(Bitmap bitmap,String mark,String... infos){
        Bitmap newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(newBmp);
        canvas.drawBitmap(bitmap, 0, 0, null);  //绘制原始图片
        canvas.save();

        canvas.rotate(-45);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/akong.ttf");
        int textColor = Color.DKGRAY;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor); //白色半透明
        paint.setTextSize(80 );
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setTypeface(typeface);

        //计算文字宽高
        Rect rect = new Rect();
        paint.getTextBounds(mark, 0, mark.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        //旋转角度比例
        float ratio = 1.414f;


        /**
         * 计算起点位置
         * 超过有效长度则默认从有效起点开始，否则居中
         */
        int middleLine1 = (int) (bitmap.getWidth()*1f/ratio);
        int y1 = middleLine1+textHeight/2;
        int totalWidth = (int) (bitmap.getWidth()*ratio);
        int validWidth = totalWidth - textHeight;
        int x1 = 0;
        if (textWidth>validWidth){
            x1 = -validWidth/2;
        }else {
            x1 = -textWidth/2;
        }
        canvas.drawText(mark, x1, y1, paint);

        //第二个文字
        int middleLine2 = (int) (bitmap.getHeight()*1f/ratio);
        int y2 = middleLine2+textHeight/2;
        int x2 =0;
        int dis = totalWidth>middleLine2 ? totalWidth-middleLine2 : 0;
        if (textWidth>validWidth){
            x2 = -(totalWidth-dis-textHeight/2);
        }else {
            x2 = -(totalWidth-dis-(totalWidth/2-textWidth/2));
        }
        canvas.drawText(mark, x2, y2, paint);

        canvas.restore();

        Bitmap infoBitmap = getInfoBitmap(bitmap.getWidth(), bitmap.getHeight(), infos);
        if (infoBitmap == null){
            return newBmp;
        }

        int infoHeight = infoBitmap.getHeight();
        int top = bitmap.getHeight() - infoHeight;
        canvas.drawBitmap(infoBitmap,0,top,null);
        infoBitmap.recycle();
        return newBmp;
    }



    public Bitmap getInfoBitmap(int width,int height,String... infos){
        if (infos == null ){
            return null;
        }
        if (width<=0 || height<=0){
            return null;
        }

        int itemHeight = 40;
        int bottomPadding = 20;
        int textSize = itemHeight -10;//50
        int validHeight = infos.length * itemHeight + bottomPadding;
        if (validHeight>height){
            validHeight = height;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setDither(true);

        Bitmap newBmp = Bitmap.createBitmap(width, validHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBmp);
        canvas.drawColor(Color.argb(180,0,0,0));//color:dark gray

        for (int i = 0; i<infos.length; i++){
            String info = infos[i];
            int x = 20;//文字的左边距
            int y = itemHeight*(i+1);
            canvas.drawText(info, x, y, paint);
        }


        return newBmp;
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


package com.cenco.lib.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import com.cenco.lib.common.log.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getSimpleName();

    public static Bitmap getWatermarkBitmap(Context context,Bitmap bitmap,String mark){
        return getWatermarkBitmap(context,bitmap,mark,null);
    }

    public static Bitmap getWatermarkBitmap(Context context,Bitmap bitmap,String mark,String fontAssetPath){
        String info = null;
        return getWatermarkBitmap(context,bitmap,mark,fontAssetPath,info);
    }

    /**
     * 注意：加载字体极为耗时
     * @param context
     * @param bitmap
     * @param mark
     * @param fontAssetPath
     * @param infos
     * @return
     */
    public static Bitmap getWatermarkBitmap(Context context,Bitmap bitmap,String mark,String fontAssetPath,String... infos){
        if (fontAssetPath==null){
            fontAssetPath = "fonts/akong.ttf";
        }
        Typeface typeface = null;
        if (AssetUtil.isFileExists(context,fontAssetPath)){
            typeface = Typeface.createFromAsset(context.getAssets(), fontAssetPath);
        }
        return getWatermarkBitmap(bitmap,mark,typeface,infos);
    }
    /**
     * 图片打水印 并将信息内容合成到图片中
     * @param bitmap
     * @param mark 水印
     * @param typeface 字体 在某些型号下部分字体无法显示，导致文字无法绘制，建议更换字体
     * @param infos 信息内容
     * example:Bitmap bitmap1 = getWatermarkBitmap( bitmap, "快发网络","时间", "地点","执行人","媒体位置","订单号","方案名称");
     * @return
     */
    public static Bitmap getWatermarkBitmap(Bitmap bitmap,String mark,Typeface typeface,String... infos){
        Bitmap newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(newBmp);
        canvas.drawBitmap(bitmap, 0, 0, null);  //绘制原始图片
        canvas.save();

        int minSize = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        int textSize = minSize /10;

        canvas.rotate(-45);
        int textColor = Color.BLACK;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setStrokeWidth(10);
        if (typeface!=null){
            paint.setTypeface(typeface);
        }

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
        int x1 = 0;

        if (bitmap.getHeight()>bitmap.getWidth()){
            int totalWidth = (int) (bitmap.getWidth()*ratio);
            int validWidth = totalWidth - textHeight;
            if (textWidth>validWidth){
                x1 = -validWidth/2;
            }else {
                x1 = -textWidth/2;
            }
        }else {
            int totalWidth = (int) (bitmap.getHeight()*ratio);
            int validWidth = totalWidth - textHeight;
            if (textWidth>validWidth){
                x1 = (int) ((bitmap.getWidth()-2*bitmap.getHeight())/ratio+textHeight/2);
            }else {
                x1 = (int) ((bitmap.getWidth()-2*bitmap.getHeight())/ratio + (bitmap.getHeight()*ratio-textWidth)/2);
            }
        }
        canvas.drawText(mark, x1, y1, paint);
//        canvas.drawCircle(x1,y1,20,paint);

        //第二个文字
        int middleLine2 = (int) (bitmap.getHeight()*1f/ratio);
        int y2 = middleLine2+textHeight/2;
        int x2 =0;

        if (bitmap.getHeight()>bitmap.getWidth()){
            int totalWidth = (int) (bitmap.getWidth()*ratio);
            int validWidth = totalWidth - textHeight;
            int dis = totalWidth>middleLine2 ? totalWidth-middleLine2 : 0;
            if (textWidth>validWidth){
                x2 = -(totalWidth-dis-textHeight/2);
            }else {
                x2 = -(totalWidth-dis-(totalWidth/2-textWidth/2));
            }
        }else {
            int totalWidth = (int) (bitmap.getHeight() * ratio);
            int validWidth = totalWidth - textHeight;
            if (textWidth > validWidth) {
                x2 = -validWidth / 2;
            } else {
                x2 = -textWidth / 2;
            }
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


    /**
     * 合成信息bitmap
     * @param width
     * @param height
     * @param infos
     * @return
     */
    public static Bitmap getInfoBitmap(int width,int height,String... infos){
        if (infos == null ){
            return null;
        }
        if (width<=0 || height<=0){
            return null;
        }


        float scale = 0.5f;
        if (width>height){
            scale=0.8f;
        }else {
            scale=0.6f;
        }
        int itemHeight = (int) (height/2*scale/infos.length);

//        int itemHeight = 40;
        int bottomPadding = itemHeight/10;
        int textSize = itemHeight -20;//50
        if (textSize<20){
            textSize = 20;
        }
        int validHeight = infos.length * itemHeight + bottomPadding + bottomPadding;
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



    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param data byte array on YUV420 NV21 format.
     * @param width pixels width
     * @param height pixels height
     * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    public static int[] convertYUV420_NV21toRGB8888(byte [] data, int width, int height) {
        int size = width*height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = data[i  ]&0xff;
            y2 = data[i+1]&0xff;
            y3 = data[width+i  ]&0xff;
            y4 = data[width+i+1]&0xff;

            u = data[offset+k  ]&0xff;
            v = data[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            pixels[i  ] = convertYUVtoRGB(y1, u, v);
            pixels[i+1] = convertYUVtoRGB(y2, u, v);
            pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
            pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i+=width;
        }

        return pixels;
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)(1.402f*v);
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)(1.772f*u);
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }

    /**
     * 摄像头预览原数据byte[] 转 bitmap
     * @param data
     * @param size
     * @return
     */
    public static Bitmap cameraToBitmap(byte[] data, Camera.Size size){
        int width = size.width;
        int height = size.height;
        int[] pixels = convertYUV420_NV21toRGB8888(data, width, height);
        Bitmap bm = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        return bm;
    }



    /**
     * 摄像头预览原数据byte[] 转 bitmap
     * compressToJpeg 有内存溢出
     * 长时间调用不推荐此方法
     * @param data
     * @param size
     * @return
     */
    public static Bitmap cameraToBitmap(byte[] data, Camera.Size size, Rect rect) {
        Bitmap bmp = null;
        try {
            if (rect == null) {
                rect = new Rect(0, 0, size.width, size.height);
            }
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,
                    size.height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(rect, 80,
                    stream);
            bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0,
                    stream.size());
            stream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        }
        return bmp;
    }

    /**
     * 摄像头原数据byte[] 转 base64
     *
     * @param data
     * @param previewSize
     * @return
     */
    public static String cameraPreByteToBase64(byte[] data, Camera.Size previewSize) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21,
                previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width,
                previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        // 将rawImage转换成bitmap
        return Base64.encodeToString(rawImage, Base64.DEFAULT);
    }


    /**
     * 旋转bitmap
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }


    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 图片文件路径转base64
     *
     * @param imgPath
     * @return
     */
    public static String getBase64(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()){
            LogUtils.w("file [" + imgPath +"] not exist");
            return null;
        }

        Bitmap bitmap = getBitmap(imgPath);
        if (bitmap == null) {
            LogUtils.w("bitmap not found");
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
            byte[] imgBytes = out.toByteArray();
            bitmap.recycle();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.e(e.getMessage());
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                LogUtils.e(e.getMessage());
            }
        }
    }

    /**
     * 由路径读取bitmap
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getBitmap(String imgPath) {

        File file = new File(imgPath);
        if (!file.exists()) {
            LogUtils.w("file not exist");
            return null;
        }
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.e(e.getMessage());
            return null;
        }

    }

    /**
     * 根据路径获取正向的bitmap
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getNormalBitmap(String imgPath) {
        int degree = getBitmapDegree(imgPath);
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 获取正向的并且压缩过的bitmap
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getNormalCompressBitmap(String imgPath) {
        int degree = getBitmapDegree(imgPath);
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        Bitmap bitmap = getCompressBitmap(imgPath);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }


    public static boolean saveBitmap(Bitmap mybitmap, String path) {
        boolean result = false;

        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            mybitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            result = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        }

        return result;
    }

    /**
     * 从指定路径下读取图片，并获取其EXIF信息。
     *
     * @param path 这里传入需要获取图片的sd卡路径
     * @return
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        }
        return degree;
    }


    /**
     * @param path    图片路径
     * @param size    宽高的缩放比，大于1为缩小
     * @param quality 质量
     * @return
     */
    public static Bitmap getCompressBitmap(String path, int size, int quality) {

        BitmapFactory.Options options = null;
        if (size != -1 && size > 0) {
            options = new BitmapFactory.Options();
            //采样率压缩
            options.inSampleSize = size;
            //RGB_565,没有透明度要求
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        try {
            //质量压缩
            if (quality != -1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                baos.flush();
                baos.close();
                byte[] bytes = baos.toByteArray();
                bitmap.recycle();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("xzutil", "原路径：" + path + ",压缩后bitmap:" + (bitmap.getByteCount() / 1024) + "kb");
        return bitmap;
    }

    public static Bitmap getCompressBitmap(String path) {
        return getCompressBitmap(path, 10, 80);
    }

    /**
     * 获取指定大小的bitmap
     * 会拉伸
     * @param orgBitmap ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap getBitmapBySize1(Bitmap orgBitmap, double newWidth, double newHeight) {
        if (null == orgBitmap) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        // 获取图片的宽和高
        float width = orgBitmap.getWidth();
        float height = orgBitmap.getHeight();
        // 创建操作图片的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    public static Bitmap getBitmapBySize2(Bitmap orgBitmap, int newWidth, int newHeight) {
        if (null == orgBitmap) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        // 获取图片的宽和高
        int width = orgBitmap.getWidth();
        int height = orgBitmap.getHeight();

        int inSampleSize = 1;
        if (height > newHeight || width > newWidth) {
            if (width > height) {
                inSampleSize = Math.round(height / newHeight);
            } else {
                inSampleSize = Math.round(width / newWidth);
            }
        }

        // 创建操作图片的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scale = 1 / inSampleSize;
        // 缩放图片动作
        matrix.postScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }




    /**
     * 将Bitmap对象转换成Drawable对象
     *
     * @param context 应用程序上下文
     * @param bitmap  Bitmap对象
     * @return 返回转换后的Drawable对象
     */
    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        if (context == null || bitmap == null) {
            throw new IllegalArgumentException("参数不合法，请检查你的参数");
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * 将Drawable对象转换成Bitmap对象
     *
     * @param drawable Drawable对象
     * @return 返回转换后的Bitmap对象
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable为空，请检查你的参数");
        }
        Bitmap bitmap =
                Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] compress(byte[] data, int width, int height){
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap bitmap1 = getBitmapBySize2(bitmap, width, height);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG,80,stream);
        try {
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            bitmap.recycle();
            bitmap1.recycle();
        }

        return stream.toByteArray();
    }

}

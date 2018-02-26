package com.cenco.lib.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {




    /**
     * 摄像头预览原数据byte[] 转 bitmap
     *
     * @param data
     * @param size
     * @return
     */
    public static Bitmap cameraPreByteToBitmap(byte[] data, Camera.Size size, Rect rect) {
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
            LogUtil.e(e.getMessage());
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
            LogUtil.w("file [" + imgPath +"] not exist");
            return null;
        }

        Bitmap bitmap = getBitmap(imgPath);
        if (bitmap == null) {
            LogUtil.w("bitmap not found");
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
            LogUtil.e(e.getMessage());
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                LogUtil.e(e.getMessage());
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
            LogUtil.w("file not exist");
            return null;
        }
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.e(e.getMessage());
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
            LogUtil.e(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
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
            LogUtil.e(e.getMessage());
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

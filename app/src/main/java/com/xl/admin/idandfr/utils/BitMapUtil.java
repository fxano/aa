package com.xl.admin.idandfr.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Base64;

import com.zkteco.android.IDReader.IDPhotoHelper;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2018/9/9.
 */

public class BitMapUtil {

    public static String getByte(final byte[] arr){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.out.println("checkwltdecode image=" + arr);
            System.out.println("checkwltdecode wltstr=" + new String(arr));
            Bitmap bt = IDPhotoHelper.Bgr2Bitmap(arr);
            if (bt != null) {
                bt.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String phoneSb = new StringBuffer(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)).toString();
//        String phoneSb = new String(baos.toByteArray());
        return  phoneSb;

    }

    public static String getByte(final Bitmap bt){
        ByteArrayOutputStream  baos = new ByteArrayOutputStream();
        try {
            if (bt != null) {
                bt.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        String phoneSb = new StringBuffer(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)).toString();
//        String phoneSb = new String(baos.toByteArray());
        return  phoneSb;
    }


    public static Bitmap runInPreviewFrame(byte[] data, Camera camera) {
        camera.setOneShotPreviewCallback(null);
        //处理data
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        //下面对bitmap进行处理
        return bitmap;
    }


    public static Bitmap stringtoBitmap(String string){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[] bitmapArray;
            bitmapArray=Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    /**
     * @param bitmap
     * @param orientationDegree 0 - 360 范围
     * @return
     */
    Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {


        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bitmap.getHeight();
            targetY = 0;
        } else {
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }


        final float[] values = new float[9];
        matrix.getValues(values);


        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];


        matrix.postTranslate(targetX - x1, targetY - y1);


        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);


        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);


        return canvasBitmap;
    }

}

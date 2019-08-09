package com.xl.admin.idandfr.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xl.admin.idandfr.R;
import com.xl.admin.idandfr.utils.ZKLiveFaceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author gy.lin
 * @create 2018/8/13
 * @Describe
 */

public class VerifyFromCamera extends Activity implements SurfaceHolder.Callback {
    private SurfaceView mSurfaceView;
    private Button mTemplate1Button;
    private Button mTemplate2Button;
    private Button mVerifyButton;
    private TextView mStatusTextView;
    private ImageView iv_image;

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private final int CAMERA_WIDTH = 640;
    private final int CAMERA_HEIGH = 480;
    private final int cameraId = 0;

    private boolean FIRST_OPTION = false;
    private boolean SECOND_OPTION = false;

    private byte[] mTemplate1 = null;
    private byte[] mTemplate2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_from_camera);
        initView();
    }

    private void initView() {
        mSurfaceView = (SurfaceView)findViewById(R.id.verifySurfaceView);
        mTemplate1Button = (Button)findViewById(R.id.template1ButtonCamera);
        mTemplate2Button = (Button)findViewById(R.id.template2ButtonCamera);
        mVerifyButton = (Button)findViewById(R.id.verifyButtonCamera);
        mStatusTextView = (TextView)findViewById(R.id.statusTextCamera);
        iv_image = findViewById(R.id.iv_image);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        OpenCameraAndSetSurfaceviewSize(cameraId);

        mTemplate1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FIRST_OPTION = true;
            }
        });
        mTemplate2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SECOND_OPTION = true;
            }
        });
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTemplate1 == null){
                    mStatusTextView.setText(getString(R.string.pls_extract_template1));
                    return;
                }
                if(mTemplate2 == null){
                    mStatusTextView.setText(getString(R.string.pls_extract_template2));
                    return;
                }
                int score = ZKLiveFaceManager.getInstance().verify(mTemplate1,mTemplate2);
                if(score >= ZKLiveFaceManager.getInstance().DEFAULT_VERIFY_SCORE){
                    mStatusTextView.setText(getString(R.string.verify_success));
                }else{
                    mStatusTextView.setText(getString(R.string.verify_fail));
                }
            }
        });
    }

    private Void OpenCameraAndSetSurfaceviewSize(int cameraId) {

        if(mCamera == null){
            mCamera = Camera.open(cameraId);
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGH);
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        mCamera.setParameters(parameters);

        return null;
    }
    private Void SetAndStartPreview(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewCallback(new VerifyPreview());
            mCamera.startPreview();
            //mCamera.cancelAutoFocus();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    private Void kill_camera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return null;
    }

    class VerifyPreview implements Camera.PreviewCallback{
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if(FIRST_OPTION){
                FIRST_OPTION = false;
                mTemplate1 = ZKLiveFaceManager.getInstance().getTemplateFromNV21(data,CAMERA_WIDTH,CAMERA_HEIGH);
                if(mTemplate1 == null){
                    mStatusTextView.setText(getString(R.string.extract_template1_fail));
                }else{
                    mStatusTextView.setText(getString(R.string.extract_template1_success));
                    final Bitmap bitmap = runInPreviewFrame(data,camera);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(bitmap != null) {
                                iv_image.setImageBitmap(bitmap);
                            }else{
                                Toast.makeText(VerifyFromCamera.this, "bt为空", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            if(SECOND_OPTION){
                SECOND_OPTION = false;
                mTemplate2 = ZKLiveFaceManager.getInstance().getTemplateFromNV21(data,CAMERA_WIDTH,CAMERA_HEIGH);
                if(mTemplate2 == null){
                    mStatusTextView.setText(getString(R.string.extract_template2_fail));
                }else{
                    mStatusTextView.setText(getString(R.string.extract_template2_success));
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera!=null){
            SetAndStartPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void onDestroy(){
        super.onDestroy();
        mTemplate1 = null;
        mTemplate2 = null;
    }


    public Bitmap runInPreviewFrame(byte[] data, Camera camera) {
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
}

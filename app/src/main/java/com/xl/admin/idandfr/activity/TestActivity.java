package com.xl.admin.idandfr.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.xl.admin.idandfr.R;
import com.xl.admin.idandfr.utils.BitMapUtil;
import com.zkteco.android.IDReader.IDPhotoHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    /*private EditText count;
    private TextView rigestID;
    public static boolean isForeground = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        count = findViewById(R.id.et_alias);
        rigestID = findViewById(R.id.tv_registID);
        registerMessageReceiver();

    }


    public void setAlias(View view){
        String alias = count.getText().toString();
        JPushInterface.setAlias(this,1,alias);
        Toast.makeText(this, "设置别名成功", Toast.LENGTH_SHORT).show();
    }


    public void getregistID(View view){
        rigestID.setText(JPushInterface.getRegistrationID(this));
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.xl.admin.idandfr.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

    private void setCostomMsg(String s) {
        if (null != count) {
            count.setText(s);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    @Override
    public void onResume(){
        super.onResume();
        isForeground = true;

    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }*/

//-------------------------------------------------------------------------------
//    private HideControl hideControl;
//    private View obj_view;//要定时隐藏的View
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//        hideControl = new HideControl();
//        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideControl.startHideTimer();
//            }
//        });
//        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideControl.resetHideTimer();
//            }
//        });
//        obj_view = findViewById(R.id.obj_view);
//    }
//
//    public class HideControl {
//        public final static int MSG_HIDE = 0x01;
//
//        private HideHandler mHideHandler;
//
//        public HideControl() {
//            mHideHandler = new HideHandler();
//        }
//
//        public class HideHandler extends Handler {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case MSG_HIDE:
//                        obj_view.setVisibility(View.INVISIBLE);
//                        break;
//                }
//
//            }
//        }
//
//        private Runnable hideRunable = new Runnable() {
//
//            @Override
//            public void run() {
//                mHideHandler.obtainMessage(MSG_HIDE).sendToTarget();
//            }
//        };
//
//        public void startHideTimer() {//开始计时,三秒后执行runable
//            mHideHandler.removeCallbacks(hideRunable);
//            if(obj_view.getVisibility() == View.INVISIBLE){
//                obj_view.setVisibility(View.VISIBLE);
//            }
//            mHideHandler.postDelayed(hideRunable, 3000);
//        }
//
//        public void endHideTimer() {//移除runable,将不再计时
//            mHideHandler.removeCallbacks(hideRunable);
//        }
//
//        public void resetHideTimer() {//重置计时
//            mHideHandler.removeCallbacks(hideRunable);
//            mHideHandler.postDelayed(hideRunable, 3000);
//        }
//
//    }
    private ImageView iv_ico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        iv_ico = findViewById(R.id.iv_ico);
        String image = getIntent().getStringExtra("image");
        Bitmap bt = stringtoBitmap(image);
//        Bitmap bt = runInPreviewFrame()
        iv_ico.setImageBitmap(bt);
//        AlertDialog.Builder ab = new AlertDialog.Builder(TestActivity.this);
//        ab.setTitle("结果");
//        ab.setMessage("指纹录取成功");
//        ab.setIcon(R.mipmap.ic_info);
//        ab.setPositiveButton("好", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        ab.show();
//        //获得外部存储的根目录
//        File dir = Environment.getExternalStorageDirectory();
//        ArrayList<String> images = new ArrayList<String>();
//        //调用遍历所有文件的方法
//        recursionFile(dir,images);
//        //返回文件路径集合
//        for (String path: images) {
//            Logger.d(path);
//        }


        File f = new File("/storage/emulated/0/rename_cpuid_80_71_7a_eb_6c_97_readsense.lic");
        boolean exists = f.exists();
        boolean isdir = f.isDirectory();
        boolean isfile = f.isFile();
        Logger.d(isfile + "=========");


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

    public Bitmap stringtoBitmap(String string){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray=Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap stringtoBitmap(String string,int i){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            byte[] bytes = string.getBytes();
            bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static void  main(String[] arg){

    }


//    //遍历手机所有文件 并将路径名存入集合中 参数需要 路径和集合
//    public void recursionFile(File dir, List<String> images) {
//        //得到某个文件夹下所有的文件
//        File[] files = dir.listFiles();
//        //文件为空
//        if (files == null) {
//            return;
//        }
//        //遍历当前文件下的所有文件
//        for (File file : files) {
//            //如果是文件夹
//            if (file.isDirectory()) {
//                //则递归(方法自己调用自己)继续遍历该文件夹
//                recursionFile(file,images);
//            } else { //如果不是文件夹 则是文件
//                //如果文件名以 .mp3结尾则是mp3文件
//                if (file.getName().endsWith(".lic")) {
//                    //往图片集合中 添加图片的路径
//                    images.add(file.getAbsolutePath() + "----" +  file.getName());
//                }
//            }
//        }
//    }
}

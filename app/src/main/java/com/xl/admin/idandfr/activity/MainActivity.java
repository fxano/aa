package com.xl.admin.idandfr.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xl.admin.idandfr.R;
import com.xl.admin.idandfr.entity.FPREntity;
import com.xl.admin.idandfr.entity.JpushEntity;
import com.xl.admin.idandfr.entity.uploadForAndroid;
import com.xl.admin.idandfr.utils.BitMapUtil;
import com.xl.admin.idandfr.utils.ConnectUtil;
import com.xl.admin.idandfr.utils.DialogUtil;
import com.xl.admin.idandfr.utils.ExampleUtil;
import com.xl.admin.idandfr.utils.ZKLiveFaceManager;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.HHDeviceControl;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.nidfpsensor.NIDFPFactory;
import com.zkteco.android.biometric.nidfpsensor.NIDFPSensor;
import com.zkteco.android.biometric.nidfpsensor.exception.NIDFPException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushMessage;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private TextView name, address, idcard, sex, mingzu, birth, date, topCount;
    private String lastID = "";
    private ImageView ico, iv_test;

    private RelativeLayout bottom_view;

    private HideControl hideControl;

    private int score;

    private Bitmap m_bt;

    private byte[] zhiwen;
    private byte[] image;
    private IDCardReader idCardReader = null;

    public static boolean isForeground = false;


    final static int baudrate = 115200;


    final static int idPort = 13;   //ID510 串口13，ID500串口12

    final static String idPower = "rfid power";

    private boolean bopen = false;

    private boolean isStop = false;

    private IDCardInfo idCardInfo = null;

    private List<FPREntity> mList = new ArrayList<>();
//指纹的base64位String

    String phoneSb;
    //------------------------------------------------
    private static final int VID = 6997;    //zkteco device VID  6997
    private static int PID = 772;    //NIDFPSensor PID 根据实际设置

    private WorkThread mWorkThread = null;
    private boolean mbStop = true;
    private boolean mbStart = false;
    private NIDFPSensor mNIDFPSensor = null;
    private byte[] mBufImage;
    private CountDownLatch countdownLatch = null;

    private int shibaiCount = 0;

    private String bijiaoResult = "no";

    private String jpushMessge = "";

    private String user;

    private Context mContext = null;
    private UsbManager musbManager = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.idfprdemo.USB_PERMISSION";

    //-------------------------------------------人脸---------
    private SurfaceView mSurfaceView;

    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;

    private final int CAMERA_WIDTH = 640;
    private final int CAMERA_HEIGH = 480;

    private final int cameraId = 0;

    private boolean FIRST_OPTION = false;

    private byte[] mTemplate1 = null;
    private byte[] mTemplate2 = null;

    private String bijiaoPhone = "";
    private String bijiaoFpr = "";
    private String dateType = "0";

    private int bijiaoCount = 1;

    private byte[] buf = null;


    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openDevice();
                    } else {
                        Toast.makeText(mContext, "USB未授权", Toast.LENGTH_SHORT).show();
                        //mTxtReport.setText("USB未授权");
                    }
                }
            }
        }
    };


    private void OpenDeviceAndRequestDevice() {
        if (mbStart) {
            return;
        }
        musbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        for (UsbDevice device : musbManager.getDeviceList().values()) {
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                musbManager.requestPermission(device, pendingIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = getIntent().getStringExtra("user");
        initView();
        //实例化定时器的对象
        hideControl = new HideControl();
        //初始化读取身份证功能
        startIDCardReader();
        initReadIdFun();
        //TODO 初始化指纹识别功能
        startFp();
        //动态注册极光推送的广播
        registerMessageReceiver();
        //测试设置别名
//        JPushInterface.setAlias(this,1,"nihao");
    }

    private void initReadIdFun() {
        try {
            if (bopen) return;
            HHDeviceControl.HHDevicePowerOn(idPower);
            idCardReader.open(idPort);
            bopen = true;
            Toast.makeText(this, "读取设备连接成功", Toast.LENGTH_SHORT).show();
            /**
             * 设备链接成功后 开始读取身份证。
             */

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!isStop) {
                            //读取身份证
                            Thread.sleep(2000);
                            readerCard();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IDCardReaderException e) {
            Logger.d("关闭设备成功");
            LogHelper.d("连接设备失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }
    }

    private void


    startIDCardReader() {
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map idrparams = new HashMap();
        idrparams.put(ParameterHelper.PARAM_SERIAL_BAUDRATE, baudrate);  //设置波特率
        //HHSERIALPORT 为ID500/ID510串口协议
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.HHSERIALPORT, idrparams);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        HHDeviceControl.HHDevicePowerOff(idPower);
        IDCardReaderFactory.destroy(idCardReader);

        CloseDevice();  //尝试关闭设备
        // Destroy fingerprint sensor when it's not used
        NIDFPFactory.destroy(mNIDFPSensor);
    }


    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
        HHDeviceControl.HHDevicePowerOn(idPower);

    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    private void initView() {
        name = findViewById(R.id.tv_name);
        address = findViewById(R.id.tv_address);
        idcard = findViewById(R.id.tv_cardid);
        ico = findViewById(R.id.iv_image);
        sex = findViewById(R.id.tv_sex);
        mingzu = findViewById(R.id.tv_mingzu);
        birth = findViewById(R.id.tv_birth);
        date = findViewById(R.id.tv_date);
        topCount = findViewById(R.id.tv_topCount);
        bottom_view = findViewById(R.id.bottom_view);
        mContext = this.getApplicationContext();
        mSurfaceView = findViewById(R.id.verifySurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        iv_test = findViewById(R.id.iv_test);
        OpenCameraAndSetSurfaceviewSize(cameraId);
    }


    private boolean authenticate() {
        try {
            idCardReader.findCard(idPort);
            idCardReader.selectCard(idPort);
            return true;
        } catch (IDCardReaderException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void readerCard() {
        try {
            if (!bopen)
                Toast.makeText(mContext, "请先连接设备", Toast.LENGTH_SHORT).show();
            if (authenticate()) {

            }


            final IDCardInfo idCardInfo = new IDCardInfo();
            boolean ret = idCardReader.readCard(idPort, 1, idCardInfo);
            if (ret) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastID.equals(idCardInfo.getId())) {
                            return;
                        }
                        lastID = idCardInfo.getId();
                        name.setText(idCardInfo.getName());
                        address.setText(idCardInfo.getAddress());
                        idcard.setText(idCardInfo.getId());
                        sex.setText(idCardInfo.getSex());
                        mingzu.setText(idCardInfo.getNation());
                        birth.setText(idCardInfo.getBirth());
                        date.setText(idCardInfo.getValidityTime());

                        if (idCardInfo.getFpdata() != null) {
                            topCount.setText("请录制指纹！");
                            zhiwen = idCardInfo.getFpdata();
                        } else {
                            topCount.setText("请对准摄像头对比人脸！");
                            if (FIRST_OPTION == false) {
                                FIRST_OPTION = true;
                            }
                        }
                        image = idCardInfo.getPhoto();
                        if (idCardInfo.getPhotolength() > 0) {
                            buf = new byte[WLTService.imgLength];
                            if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                                Bitmap bt = IDPhotoHelper.Bgr2Bitmap(buf);
                                ico.setImageBitmap(bt);
                                hideControl.startHideTimer();
                            }
                        }
                    }
                });
                this.idCardInfo = idCardInfo;
            } else {
                lastID = "";
            }
        } catch (IDCardReaderException e) {
            lastID = "";
            Toast.makeText(mContext, "读卡失败", Toast.LENGTH_SHORT).show();
            LogHelper.d("读卡失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }
    }


    //TODO 关闭身份证读取  优化的时候可以吧其中的代码放到Activity销毁方法中。
    public void close(View view) {
        try {
            if (bopen) {
                idCardReader.close(idPort);
                //重要说明；ID500/ID510 关闭后需将串口切换到串口11.否则可能出现二代证无法使用
                HHDeviceControl.HHDeviceSwitchSerial(11);   //！！！重要！！！
                HHDeviceControl.HHDevicePowerOff(idPower);
                bopen = false;
            }
            Logger.d("关闭设备成功");
        } catch (IDCardReaderException e) {
            Logger.d("关闭设备失败");
            LogHelper.d("关闭设备失败, 错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n 内部错误码=" + e.getInternalErrorCode());
        }

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


    /**
     * 极光推送的动态注册广播
     */

    private JpushEntity je = null;

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    je = new JpushEntity();
                    HashMap<String, String> mMap = new HashMap<>();
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    jpushMessge = messge;

                    je.setMessge(messge);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    final StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    JSONObject jo = new JSONObject(extras);
                    Iterator it = jo.keys();
                    while (it.hasNext()) {
                        String key = String.valueOf(it.next());
                        mMap.put(key, jo.optString(key));
                        Logger.d(jo.optString(key) + "==============");
                    }
                    je.setmMap(mMap);
                    if (messge.equals("1")) {
                        DialogUtil.showDialog(MainActivity.this, "", "请开始人证合一", "好");
                    } else if (messge.equals("2")) {
                        DialogUtil.showDialog(MainActivity.this, "", "请开始录制指纹和人脸数据上传", "好");
                        // TODO 此处要删除
                        if(FIRST_OPTION == false){
                            FIRST_OPTION = true;
                        }
                    } else {
                        h.sendEmptyMessage(2);
                    }
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, showMsg.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    }

    //---------------------------------------------------------------------------------------------

    private UsbDevice findDevice() {
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            System.out.println("usbinfo:" + device.toString());
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                return device;
            }
        }

        return null;
    }

    private void startIDFPFingerSensor() {
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        mNIDFPSensor = NIDFPFactory.createNIDFPSensor(this, TransportType.USBSCSI, fingerprintParams);

    }

    @SuppressLint("HandlerLeak")
    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DialogUtil.showDialog(MainActivity.this, "", "指纹录制完成，请录制人脸", "好");
                    break;
                case 2:
                    Map<String, String> list = je.getmMap();
                    String liuShuiHao = list.get("orderNumber");
                    requstUserInfo(liuShuiHao);
                    
                    break;

                case 3:
                    if (!bijiaoFpr.equals("") || bijiaoFpr != null) {
                        if (fpMatch(Base64.decode(bijiaoFpr, Base64.NO_WRAP))) {
                            Toast.makeText(MainActivity.this, "验证指纹成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "验证指纹失败", Toast.LENGTH_SHORT).show();
                        }
                        if (FIRST_OPTION == false) {
                            FIRST_OPTION = true;
                        }
                    }
                    break;

                case 4:
                    Toast.makeText(MainActivity.this, "请开始录制指纹", Toast.LENGTH_SHORT).show();
//                    if (FIRST_OPTION == false) {
//                        FIRST_OPTION = true;
//                    }
                    break;

                case 5:
                    Toast.makeText(MainActivity.this, "比较上传失败", Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    Toast.makeText(MainActivity.this, "比较上传成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void requstUserInfo(String liuShuiHao) {
        OkHttpClient oc = new OkHttpClient();
        FormEncodingBuilder fb = new FormEncodingBuilder();
        fb.add("orderNumber", liuShuiHao);

        Request r = new Request.Builder().post(fb.build()).url(ConnectUtil.URL_NAME + ConnectUtil.REQUEST_INFO).build();
        oc.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Logger.d("请求失败=================" + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                Logger.d("请求成功=================");
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<uploadForAndroid>() {}.getType();
                uploadForAndroid ua = gson.fromJson(result, type);

                bijiaoFpr = ua.getResultFingerprint();
                dateType = ua.getDataType();
                bijiaoPhone = ua.getResultPicture();
                h.sendEmptyMessage(4);

            }
        });
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            mbStop = false;
            while (!mbStop) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    mNIDFPSensor.GetFPRawData(0, mBufImage);
                } catch (NIDFPException e) {
                    e.printStackTrace();
                    continue;
                }
                byte[] retQualityScore = new byte[1];
                int ret = mNIDFPSensor.getQualityScore(mBufImage, retQualityScore);
                final byte qualityScore = retQualityScore[0];

                //qualityScore:如果这个数值不大于45  说明指纹不匹配
                if (1 != ret || qualityScore < 45) {
                    continue;
                }
                //TODO 此代码是指纹转化为图片的代码   此数组数据可以传递给后台
                /*runOnUiThread(new Runnable() {
                    public void run() {
                        Bitmap bitmap = ToolUtils.renderCroppedGreyScaleBitmap(mBufImage, mNIDFPSensor.getFpImgWidth(), mNIDFPSensor.getFpImgHeight());
                        mImgView.setImageBitmap(bitmap);
                    }
                });*/
                final FPREntity fpr = new FPREntity();
                if (jpushMessge.equals("1")) {
                    if (idCardInfo == null) {
                        continue;
                    }

                    if (idCardInfo.getFpdata().length > 0) {
                        hideControl.resetHideTimer();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fpMatch(idCardInfo.getFpdata())) {
                                    topCount.setText("指纹录制成功,请对准摄像头对比人脸！");
                                    fpr.setOK(true);
                                    if (FIRST_OPTION == false) {
                                        FIRST_OPTION = true;
                                    }
                                } else {
                                    fpr.setOK(false);
                                    topCount.setText("指纹录制失败，请重新录制！");
                                    if (shibaiCount < 2) {
                                        hideControl.resetHideTimer();
                                        shibaiCount++;
                                    } else {
                                        Toast.makeText(mContext, "验证失败", Toast.LENGTH_SHORT).show();
                                        if (bottom_view.getVisibility() == View.VISIBLE) {
                                            bottom_view.setVisibility(View.INVISIBLE);
                                        }
                                        shibaiCount = 0;
                                        hideControl.endHideTimer();
                                    }
                                }
                                fpr.setFprInfo(mBufImage);
                                mList.add(fpr);
                            }
                        });
                    } else {
                        continue;
                    }
                } else if (jpushMessge.equals("2")) {
                    Logger.d("已获取到指纹======");
                    if (FIRST_OPTION == false) {
                        FIRST_OPTION = true;
                    }
                    h.sendEmptyMessage(1);
                } else if (jpushMessge.equals("3")) {
                    h.sendEmptyMessage(3);
                } else {
                    Logger.d("失败=======" + jpushMessge);
                }
            }
            countdownLatch.countDown();
            //Toast.makeText(mContext, "线程退出", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isTure = false;

    public boolean fpMatch(byte[] fpdate) {
        //拆开比两次 如果有一次成功则成功
        byte[] byte1 = SubArray(fpdate, 0, 512);
        byte[] byte2 = SubArray(fpdate, 512, 512);
        final float score = mNIDFPSensor.ImageMatch(0, mBufImage, byte1);
        final float score1 = mNIDFPSensor.ImageMatch(0, mBufImage, byte2);

        if (score >= 35) {
            isTure = true;
            System.out.println("====================第一个指纹");
        } else if (score1 > 35) {
            isTure = true;
            System.out.println("====================第二个指纹");
        } else {
            isTure = false;
        }

        return isTure;

    }

    private void openDevice() {
        startIDFPFingerSensor();
        if (mbStart) {
            Toast.makeText(mContext, "设备已连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //连接设备
            mNIDFPSensor.open(0);
            //分配读取指纹图像内存(width*height Bytes)
            mBufImage = new byte[mNIDFPSensor.getFpImgWidth() * mNIDFPSensor.getFpImgHeight()];
            countdownLatch = new CountDownLatch(1);
            mWorkThread = new WorkThread();
            mWorkThread.start();// 线程启动
            Toast.makeText(mContext, "设备连接成功，图像宽：" + mNIDFPSensor.getFpImgWidth() + ",图像高：" + mNIDFPSensor.getFpImgHeight(),
                    Toast.LENGTH_SHORT).show();
//            mNIDFPSensor.setParameter(0 , 0 ,mNIDFPSensor.getFpImgWidth()*mNIDFPSensor.getFpImgHeight());
            mbStart = true;

        } catch (NIDFPException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Open device fail.errorcode:" + e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void powerOn() {
        HHDeviceControl.HHDevicePowerOn("5V");
        HHDeviceControl.HHDeviceGpioLow(141);
    }

    private void powerDown() {
        HHDeviceControl.HHDeviceGpioHigh(141);
        HHDeviceControl.HHDevicePowerOff("5V");
    }


    public void startFp() {
        String strPid = "772";
        int tempPID = 0;
        if (strPid != null) {
            tempPID = Integer.parseInt(strPid);
            if (tempPID > 0 && tempPID <= 65535) {
                if (tempPID != PID) {
                    mNIDFPSensor = null;
                    PID = tempPID;
                }
            }
        }

        powerDown();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        powerOn();
        long lTickStart = System.currentTimeMillis();
        UsbDevice usbDevice = null;
        while (System.currentTimeMillis() - lTickStart < 5 * 1000) {
            if ((usbDevice = findDevice()) != null) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (null == usbDevice) {
            Toast.makeText(MainActivity.this, "未找到指纹模块", Toast.LENGTH_SHORT).show();
            return;
        }

        OpenDeviceAndRequestDevice();
    }

    //TODO 关闭录制指纹线程
    void CloseDevice() {
        if (mbStart) {
            mbStop = true;  //停止采集线程
            if (null != countdownLatch) {
                try {
                    //等待线程退出，10S
                    countdownLatch.await(10 * 1000, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                mNIDFPSensor.close(0);  //关闭设备
            } catch (NIDFPException e) {
                e.printStackTrace();
            }
        }
        mbStart = false;
    }


    //TODO 关闭按钮  布局中已被删除   在优化的时候 这个方法可以被删除
    public void OnBnClose(View view) {
        CloseDevice();
        Toast.makeText(mContext, "关闭成功", Toast.LENGTH_SHORT).show();
    }


    /**
     * 字节数组拆分
     *
     * @param paramArrayOfByte 原始数组
     * @param paramInt1        起始下标
     * @param paramInt2        要截取的长度
     * @return 处理后的数组
     */
    public static byte[] SubArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        byte[] arrayOfByte = new byte[paramInt2];
        int i = 0;
        while (true) {
            if (i >= paramInt2)
                return arrayOfByte;
            arrayOfByte[i] = paramArrayOfByte[(i + paramInt1)];
            i += 1;
        }
    }


    public class HideControl {
        public final static int MSG_HIDE = 0x01;

        private MainActivity.HideControl.HideHandler mHideHandler;

        public HideControl() {
            mHideHandler = new MainActivity.HideControl.HideHandler();
        }

        public class HideHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_HIDE:
                        bottom_view.setVisibility(View.INVISIBLE);
                        break;
                }

            }
        }

        private Runnable hideRunable = new Runnable() {

            @Override
            public void run() {
                mHideHandler.obtainMessage(MSG_HIDE).sendToTarget();
            }
        };

        public void startHideTimer() {//开始计时,三秒后执行runable
            mHideHandler.removeCallbacks(hideRunable);
            if (bottom_view.getVisibility() == View.INVISIBLE) {
                bottom_view.setVisibility(View.VISIBLE);
            }
            mHideHandler.postDelayed(hideRunable, 15 * 1000);
        }

        public void endHideTimer() {//移除runable,将不再计时
            mHideHandler.removeCallbacks(hideRunable);
        }

        public void resetHideTimer() {//重置计时
            mHideHandler.removeCallbacks(hideRunable);
            mHideHandler.postDelayed(hideRunable, 15 * 1000);
        }

    }


    //-------------------------人脸----------------------------
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            SetAndStartPreview(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    private Void OpenCameraAndSetSurfaceviewSize(int cameraId) {

        if (mCamera == null) {
            mCamera = Camera.open(cameraId);
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGH);
        mCamera.setDisplayOrientation(90);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
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

    class VerifyPreview implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if (FIRST_OPTION) {
                mTemplate1 = ZKLiveFaceManager.getInstance().getTemplateFromNV21(data, CAMERA_WIDTH, CAMERA_HEIGH);
                if (mTemplate1 == null) {
                    Toast.makeText(MainActivity.this, "提取模板失败", Toast.LENGTH_SHORT).show();
                    Logger.d("提取模板失败==================");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "提取模板成功", Toast.LENGTH_SHORT).show();
                    Logger.d(R.string.extract_template1_success);
                    if (m_bt != null) {
                        m_bt = null;
                    }

                    m_bt = BitMapUtil.runInPreviewFrame(data, camera);

                    if (jpushMessge.equals("2")) {
                        //TODO 上传数据
                        DialogUtil.showDialog(MainActivity.this, "", "人脸获取成功", "好");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                uploadPFRAndFp();
                            }
                        }).start();

                        return;
                    } else if (jpushMessge.equals("3")) {
                        if(dateType .equals("1")){
                            Bitmap bt = BitMapUtil.stringtoBitmap(bijiaoPhone);
                            byte[] result1 =  ZKLiveFaceManager.getInstance().getTemplateFromBitmap(bt);
                            int score = ZKLiveFaceManager.getInstance().verify(mTemplate1,result1);
                            Logger.d(score + "========================");
                            if (score >= ZKLiveFaceManager.getInstance().DEFAULT_VERIFY_SCORE) {
                                Toast.makeText(MainActivity.this, "比较成功", Toast.LENGTH_SHORT).show();
                                bijiaoResult = "yes";
                            } else {
                                Toast.makeText(MainActivity.this, "比较失败", Toast.LENGTH_SHORT).show();
                                bijiaoResult = "no";
                            }
                        }else if(dateType.equals("2")){
                            byte[] result = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(m_bt);
                            Bitmap bitmap = BitMapUtil.stringtoBitmap(bijiaoPhone);
                            byte[] result1 = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(bitmap);
                            int score = ZKLiveFaceManager.getInstance().verify(result1, result);
                            Logger.d(score + "==============");
                            if (score >= ZKLiveFaceManager.getInstance().DEFAULT_VERIFY_SCORE) {
                                Logger.d("比较成功==========================");
                                bijiaoResult = "yes";
                                Toast.makeText(MainActivity.this, "比较成功", Toast.LENGTH_SHORT).show();
                            } else {
                                bijiaoResult = "no";
                                Logger.d("比较失败==========================");
                                Toast.makeText(MainActivity.this, "比较失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                        uploadBijiaoResult();
                        return;
                    }
//                    System.out.println("checkwltdecode image=" + image);
//                    System.out.println("checkwltdecode wltstr=" + new String(image));

                    final Bitmap bt = IDPhotoHelper.Bgr2Bitmap(buf);
                    mTemplate2 = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(bt);

                    score = ZKLiveFaceManager.getInstance().verify(mTemplate1, mTemplate2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (score >= ZKLiveFaceManager.getInstance().DEFAULT_VERIFY_SCORE) {

                                Toast.makeText(MainActivity.this, "比较成功", Toast.LENGTH_SHORT).show();
                                FIRST_OPTION = false;
                                topCount.setText("比较成功！");
                                hideControl.endHideTimer();
                                bottom_view.setVisibility(View.INVISIBLE);
                                bijiaoResult = "yes";


                            } else {

                                topCount.setText("第" + (bijiaoCount + 1) + "比较失败！");
                                bijiaoResult = "no";
                                if (bijiaoCount < 3) {
                                    hideControl.resetHideTimer();
                                    bijiaoCount++;
                                } else {
                                    FIRST_OPTION = false;
                                    bijiaoCount = 0;
                                    hideControl.endHideTimer();
                                    bottom_view.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    });
                    if (je != null) {
                        uploadMessge();
                    } else {
                        mList.clear();
                    }

                }
            } else {

            }

        }
    }

    private void uploadBijiaoResult() {
        OkHttpClient oc = new OkHttpClient();
        FormEncodingBuilder fe = new FormEncodingBuilder();
        fe.add("resultType" , bijiaoResult);
        fe.add("serialNumber" , je.getmMap().get("serialNumber"));
        fe.add("serialUserName", user);
        fe.add("serialType" , je.getmMap().get("serialType"));
        fe.add("serialInpatient" , je.getmMap().get("serialInpatient"));
        fe.add("resultPicture" , bijiaoPhone);
        fe.add("serialPicture" , BitMapUtil.getByte(m_bt));
        fe.add("resultFingerprint" , bijiaoFpr);
        fe.add("serialFingerprint" , Base64.encodeToString(mBufImage , Base64.DEFAULT));
        if(je != null){
            je = null;
        }
        bijiaoResult = "";

        Request r = new Request.Builder().post(fe.build()).url(ConnectUtil.URL_NAME + ConnectUtil.UPLOAD_INFO).build();
        oc.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                h.sendEmptyMessage(5);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                h.sendEmptyMessage(6);

            }
        });

    }


    private void uploadPFRAndFp() {
        bijiaoResult = "yes";
        Map<String, String> map = je.getmMap();
        OkHttpClient oc = new OkHttpClient();
        FormEncodingBuilder fe = new FormEncodingBuilder();
        fe.add("serialNumber", map.get("serialNumber"));
        fe.add("serialUserName", user);
        fe.add("resultType", bijiaoResult);
        fe.add("serialType", map.get("serialType"));
        fe.add("serialInpatient", map.get("serialInpatient"));
        fe.add("dataType", "2");
        fe.add("resultPicture", BitMapUtil.getByte(m_bt));
//        String allFpr = FPRAll();
        Bitmap bitmap = ToolUtils.renderCroppedGreyScaleBitmap(mBufImage, mNIDFPSensor.getFpImgWidth(), mNIDFPSensor.getFpImgHeight());
        fe.add("resultFingerprint", BitMapUtil.getByte(bitmap));
        close();
        Request r = new Request.Builder().post(fe.build()).url(ConnectUtil.URL_NAME + ConnectUtil.UPLOAD_INFO).build();
        oc.newCall(r).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Logger.d("請求失敗" + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Logger.d("請求成功");
            }
        });
    }


    public void close() {
        if (!jpushMessge.equals("")) {
            jpushMessge = "";
        }
        if (je != null) {
            je = null;
        }
    }

    private String FPRAll() {
        String allFpr = "";
        for (FPREntity fe : mList) {
            allFpr += Base64.encodeToString(fe.getFprInfo(), Base64.DEFAULT);
        }
        return allFpr;
    }


    private void uploadMessge() {
        Map<String, String> map = je.getmMap();
        OkHttpClient oc = new OkHttpClient();
        FormEncodingBuilder fe = new FormEncodingBuilder();
        fe.add("serialNumber", map.get("serialNumber"));
        fe.add("serialUserName", user);
        fe.add("resultType", bijiaoResult);
        fe.add("serialType", map.get("serialType"));
        fe.add("serialInpatient", map.get("serialInpatient"));
        fe.add("resultName", idCardInfo.getName());
        fe.add("resultSex", idCardInfo.getSex());
        fe.add("resultBirthday", idCardInfo.getBirth());
        fe.add("resultNation", idCardInfo.getNation());
        fe.add("resultAddress", idCardInfo.getAddress());
        fe.add("resultValidityPeriod", idCardInfo.getValidityTime());
        fe.add("resultCardno", idCardInfo.getId());

        fe.add("resultPicture", BitMapUtil.getByte(buf));
        fe.add("serialPicture", BitMapUtil.getByte(m_bt));
        fe.add("dataType", "1");
        if (mList.size() > 0) {
            for (FPREntity fpr : mList) {
                if (fpr.isOK()) {
                    fe.add("serialFingerprint", Base64.encodeToString(fpr.getFprInfo(), Base64.DEFAULT));
//                    fe.add("serialFingerprint", new String(fpr.getFprInfo()));
                    break;
                }
            }
        }
        fe.add("resultFingerprint", Base64.encodeToString(idCardInfo.getFpdata(), Base64.DEFAULT));
        mList.clear();
        close();
        Request r = new Request.Builder().post(fe.build()).url(ConnectUtil.URL_NAME + ConnectUtil.UPLOAD_INFO).build();
        oc.newCall(r).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Logger.d("請求失敗" + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Logger.d("請求成功");
            }
        });
    }


}

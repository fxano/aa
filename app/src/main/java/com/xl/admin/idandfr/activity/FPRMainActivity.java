package com.xl.admin.idandfr.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xl.admin.idandfr.R;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.HHDeviceControl;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.nidfpsensor.NIDFPFactory;
import com.zkteco.android.biometric.nidfpsensor.NIDFPSensor;
import com.zkteco.android.biometric.nidfpsensor.exception.NIDFPException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FPRMainActivity extends AppCompatActivity {
    private static final int VID = 6997;    //zkteco device VID  6997
    private static int PID = 772;    //NIDFPSensor PID 根据实际设置
    private TextView mTxtReport = null;
    private TextView mTxtStatus = null;
    private ImageView mImgView = null;
    private EditText editTextPID = null;


    private WorkThread mWorkThread = null;
    private boolean mbStop = true;
    private boolean mbStart = false;
    private NIDFPSensor mNIDFPSensor = null;
    private byte[] mBufImage;
    private CountDownLatch countdownLatch = null;

    private Context mContext = null;
    private UsbManager musbManager = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.idfprdemo.USB_PERMISSION";

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        openDevice();
                    }
                    else
                    {
                        Toast.makeText(mContext, "USB未授权", Toast.LENGTH_SHORT).show();
                        //mTxtReport.setText("USB未授权");
                    }
                }
            }
        }
    };

    private void OpenDeviceAndRequestDevice()
    {
        if (mbStart)
        {
            mTxtReport.setText("设备已连接，请先断开连接");
            return;
        }
        musbManager = (UsbManager)this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        for (UsbDevice device : musbManager.getDeviceList().values())
        {
            if (device.getVendorId() == VID && device.getProductId() == PID)
            {
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                musbManager.requestPermission(device, pendingIntent);
            }
        }
    }

    private UsbDevice findDevice()
    {
        UsbManager usbManager = (UsbManager)this.getSystemService(Context.USB_SERVICE);
        for (UsbDevice device : usbManager.getDeviceList().values())
        {

            if (device.getVendorId() == VID && device.getProductId() == PID)
            {
                return device;
            }
        }

        return null;
    }
    private byte[] zhiwen , image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fpractivity_main);
        zhiwen = getIntent().getByteArrayExtra("zhiwen");
        image = getIntent().getByteArrayExtra("image");
        mTxtReport = (TextView)findViewById(R.id.txtReport);
        mTxtStatus = (TextView)findViewById(R.id.txtStatus);
        mImgView = (ImageView)findViewById(R.id.imageView);
        editTextPID = (EditText)findViewById(R.id.editPID);
        mContext = this.getApplicationContext();
        mTxtReport.setText("");
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


    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            mbStop = false;
            while (!mbStop) {
                try {
                    Thread.sleep(50);
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
                runOnUiThread(new Runnable() {
                    public void run() {
                        Bitmap bitmap = ToolUtils.renderCroppedGreyScaleBitmap(mBufImage, mNIDFPSensor.getFpImgWidth(), mNIDFPSensor.getFpImgHeight());
                        mImgView.setImageBitmap(bitmap);
                        mTxtStatus.setText("ImageQualityScore:" + qualityScore);
                    }
                });
                if (1 !=  ret || qualityScore < 45)
                {
                    continue;
                }
                byte[] byte1 = SubArray(zhiwen,0,512);
                byte[] byte2 = SubArray(zhiwen,512,512);
                final float score = mNIDFPSensor.ImageMatch(0, mBufImage, byte1);
                final float score1 = mNIDFPSensor.ImageMatch(0, mBufImage, byte2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(score >= 35){
                            mTxtReport.setText("score:" + score);
                            System.out.println("====================第一个指纹");
                        }else if (score < 35){
                            mTxtReport.setText("score:" + score1);
                            System.out.println("====================第二个指纹");
                        }else{
                            mTxtReport.setText("指纹不匹配");
                        }
                    }
                });
                System.out.println(mTxtReport.getText().toString() + "=================");
            }
            countdownLatch.countDown();
            //Toast.makeText(mContext, "线程退出", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDevice()
    {
        startIDFPFingerSensor();
        if (mbStart)
        {
            //mTxtReport.setText("设备已连接");
            Toast.makeText(mContext, "设备已连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //连接设备
            mNIDFPSensor.open(0);
            //分配读取指纹图像内存(width*height Bytes)
            mBufImage = new byte[mNIDFPSensor.getFpImgWidth()*mNIDFPSensor.getFpImgHeight()];
            countdownLatch = new CountDownLatch(1);
            mWorkThread = new WorkThread();
            mWorkThread.start();// 线程启动
            mTxtReport.setText("Open device succ");
            Toast.makeText(mContext, "设备连接成功，图像宽：" + mNIDFPSensor.getFpImgWidth() + ",图像高：" + mNIDFPSensor.getFpImgHeight(),
                    Toast.LENGTH_SHORT).show();
            mbStart = true;
        } catch (NIDFPException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Open device fail.errorcode:"+ e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode(), Toast.LENGTH_SHORT).show();
            //mTxtReport.setText("Open device fail.errorcode:"+ e.getErrorCode() + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode());
        }
    }

    private void powerOn(){
        HHDeviceControl.HHDevicePowerOn("5V");
        HHDeviceControl.HHDeviceGpioLow(141);
    }

    private void powerDown(){
        HHDeviceControl.HHDeviceGpioHigh(141);
        HHDeviceControl.HHDevicePowerOff("5V");
    }

    public void OnBnOpen(View view)
    {
        String strPid = editTextPID.getText().toString();
        int tempPID = 0;
        if (strPid != null)
        {
            tempPID = Integer.parseInt(strPid);
            if (tempPID > 0 && tempPID <= 65535)
            {
                if (tempPID != PID)
                {
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
        while (System.currentTimeMillis() - lTickStart < 5*1000)
        {
            if ((usbDevice = findDevice()) != null)
            {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (null == usbDevice)
        {
            mTxtReport.setText("未找到指纹模块");
            return;
        }

        OpenDeviceAndRequestDevice();
    }


    void CloseDevice()
    {
        if (mbStart)
        {
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

    public void OnBnClose(View view)
    {
        CloseDevice();
        mTxtReport.setText("Close device succ!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseDevice();  //尝试关闭设备
        // Destroy fingerprint sensor when it's not used
        NIDFPFactory.destroy(mNIDFPSensor);
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


}

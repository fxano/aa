package com.xl.admin.idandfr.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionsUtil {
    //读写权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_EXTERNAL_CAMERA = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};

    /**
     * 检查应用程序是否有权写入设备存储
     * 如果应用程序没有权限，则会提示用户授予权限
     *
     * @param activity 所在的Activity
     */
    public static void verifyStoragePermissions(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED|| ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE);
        }
    }
}
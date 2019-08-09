package com.xl.admin.idandfr.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xl.admin.idandfr.R;

/**
 * Created by Administrator on 2018/9/9.
 */

public class DialogUtil {

    public static void showDialog(Context context , String title , String content,String buttonText){
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setTitle(title);
        ab.setMessage(content);
        ab.setIcon(R.mipmap.ic_info);
        ab.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.show();
    }

}

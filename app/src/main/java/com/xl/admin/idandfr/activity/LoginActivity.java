package com.xl.admin.idandfr.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xl.admin.idandfr.R;
import com.xl.admin.idandfr.entity.MD5;
import com.xl.admin.idandfr.entity.resultAndroid;
import com.xl.admin.idandfr.utils.ConnectUtil;
import com.xl.admin.idandfr.utils.PermissionsUtil;
import com.xl.admin.idandfr.utils.ZKLiveFaceManager;

import java.io.File;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends AppCompatActivity {
    private EditText user,psw ,et_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        //TODO 权限申请 有问题需要解决
        PermissionsUtil.verifyStoragePermissions(this);
    }

    private void initView() {
        user = findViewById(R.id.et_user);
        psw = findViewById(R.id.et_psw);
        et_ip = findViewById(R.id.et_ip);
    }

    public void login(View view){
        ConnectUtil.URL_NAME = et_ip.getText().toString();
        OkHttpClient oc = new OkHttpClient();
        String macAddress = ConnectUtil.getLocalMacAddressFromIp();
        final String newMac = macAddress.replace(":","_");

        final String userCount = user.getText().toString();
        String pswCount = psw.getText().toString();
        if(!userCount.equals("") && !pswCount .equals("")){
            FormEncodingBuilder fe  = new FormEncodingBuilder();
            fe.add("userName" , userCount);
            fe.add("passWord" , new MD5().getMD5ofStr(pswCount));
            fe.add("macId" , newMac);
            Request r = new Request.Builder().post(fe.build()).url(ConnectUtil.URL_NAME + ConnectUtil.LOGIN).build();
            oc.newCall(r).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    System.out.println("请求失败" + e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    System.out.println("请求成功");
                    String stringTemp = response.body().string();
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<resultAndroid>() {}.getType();
                    final resultAndroid ra = gson.fromJson(stringTemp, type);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(ra.getResultType().equals("yes")){
                                //初始化算法
                                String filePath = initSuanfa();
                                if(ZKLiveFaceManager.getInstance().setParameterAndInit(filePath)){
                                    Toast.makeText(LoginActivity.this, "算法初始化成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, "算法初始化失败", Toast.LENGTH_SHORT).show();
                                }
                                JPushInterface.setAlias(LoginActivity.this,1,newMac);
                                Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class).putExtra("user",userCount));
//                                startActivity(new Intent(LoginActivity.this,VerifyFromFile.class));
                                finish();
                            }
                        }
                    });

                }
            });
        }

    }

    private String initSuanfa() {
        File f = new File("/storage/emulated/0/idFile/");
        File[] files = f.listFiles();
        for (File file: files) {
            if (file.isFile() && file.getName().endsWith(".lic")){
                return file.getPath();
            }
        }
        return null;
    }


    public void setting(View view){

    }
}

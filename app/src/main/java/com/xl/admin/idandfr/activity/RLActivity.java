package com.xl.admin.idandfr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xl.admin.idandfr.R;
import com.xl.admin.idandfr.utils.FileUtils;
import com.xl.admin.idandfr.utils.VerifyFromFile;
import com.xl.admin.idandfr.utils.ZKLiveFaceManager;

import java.io.File;

public class RLActivity extends Activity {
    private final String TAG = RLActivity.class.getSimpleName();

    private TextView mHardwareId;
    private Button mInitButtn;
    private TextView mInitTextView;
    private Button mVerifyButton;

    private String FILE_PATH ;
    private final int INIT_CODE = 1;
    private byte[] image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rlactivity_main);
        image = getIntent().getByteArrayExtra("image");
        initView();
        //初始化算法
        String filePath = initSuanfa();
        if(ZKLiveFaceManager.getInstance().setParameterAndInit(filePath)){
            Toast.makeText(this, "算法初始化成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "算法初始化失败", Toast.LENGTH_SHORT).show();
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

    private void initView(){
        mInitButtn = (Button)findViewById(R.id.initButton);
        mHardwareId = (TextView)findViewById(R.id.hardwardId);
        mInitButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, INIT_CODE);
            }
        });
        mInitTextView = (TextView)findViewById(R.id.initTextView);
        mHardwareId.setText(""+getString(R.string.hardware_id)+ ZKLiveFaceManager.getInstance().getHardwareId());

        mVerifyButton = (Button)findViewById(R.id.verifyButton);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ZKLiveFaceManager.getInstance().isInit()){
                    Toast.makeText(getApplicationContext(),getString(R.string.pls_init_algorithm), Toast.LENGTH_SHORT).show();
                    return;
                }

                //TODO 1：1比较方式  后期可以优化
//                startActivity(new Intent(RLActivity.this,VerifyFromCamera.class).putExtra("image" , image));


                AlertDialog.Builder builder = new AlertDialog.Builder(RLActivity.this);
                builder.setTitle(getString(R.string.choose_method));
                builder.setPositiveButton(getString(R.string.from_local_file), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RLActivity.this,VerifyFromFile.class));
                    }
                });
                builder.setNegativeButton(getString(R.string.from_camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RLActivity.this,VerifyFromCamera.class));
                    }
                });
                builder.setNeutralButton(getString(R.string.ignore), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if(INIT_CODE == requestCode){
                FILE_PATH = FileUtils.getPath(this,uri);

                Logger.d(FILE_PATH + "==========");
                if(!TextUtils.isEmpty(FILE_PATH)){
                    if(ZKLiveFaceManager.getInstance().setParameterAndInit(FILE_PATH)){
                        mInitTextView.setText(getString(R.string.init_algorithm_success));
                    }else{
                        mInitTextView.setText(getString(R.string.init_algorithm_fail));
                    }
                }
            }
        }
    }

}

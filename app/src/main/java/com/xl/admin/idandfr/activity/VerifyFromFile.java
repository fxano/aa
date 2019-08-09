package com.xl.admin.idandfr.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xl.admin.idandfr.R;
import com.xl.admin.idandfr.utils.FileUtils;
import com.xl.admin.idandfr.utils.ZKLiveFaceManager;

/**
 * @author gy.lin
 * @create 2018/8/13
 * @Describe
 */

public class VerifyFromFile extends Activity{
    private ImageView mFirstImage;
    private Button mFirstButton;
    private TextView mFirstTextView;
    private byte[] FirstTemplate;
    private Bitmap mFirstBitmap;

    private ImageView mSecondImage;
    private Button mSecondButton;
    private TextView mSecondTextView;
    private byte[] SecondTemplate;
    private Bitmap mSecondBitmap;

    private Button mVerifyButton;
    private TextView mVerifyTextView;

    private final int FIRST_CODE = 1;
    private final int SECOND_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiry_from_file);
        initView();
    }

    private void initView() {
        mFirstImage = (ImageView)findViewById(R.id.verifyFirstImage);
        mFirstButton = (Button)findViewById(R.id.verifyFirstButton);
        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, FIRST_CODE);
            }
        });
        mFirstTextView = (TextView)findViewById(R.id.verifyFirstTextView);

        mSecondImage = (ImageView)findViewById(R.id.verifySecondImage);
        mSecondButton = (Button)findViewById(R.id.verifySecondButton);
        mSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, SECOND_CODE);
            }
        });
        mSecondTextView = (TextView)findViewById(R.id.verifySecondTextView);

        mVerifyButton = (Button)findViewById(R.id.verifyButton);
        mVerifyTextView = (TextView)findViewById(R.id.verifyTextView);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirstTemplate == null || SecondTemplate == null){
                    mVerifyTextView.setText(getString(R.string.pls_choose_verify_file));
                    return;
                }
                int score = ZKLiveFaceManager.getInstance().verify(FirstTemplate,SecondTemplate);
                if(score >= ZKLiveFaceManager.getInstance().DEFAULT_VERIFY_SCORE){
                    mVerifyTextView.setText(getString(R.string.verify_success));
                }else{
                    mVerifyTextView.setText(getString(R.string.verify_fail));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = FileUtils.getPath(this,uri);
            if(FIRST_CODE == requestCode){
                if(!TextUtils.isEmpty(path)){
                    mFirstBitmap = BitmapFactory.decodeFile(path);
                    if(mFirstBitmap == null){
                        return;
                    }
                    mFirstImage.setImageBitmap(mFirstBitmap);
                    FirstTemplate = null;
                    FirstTemplate = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(mFirstBitmap);
                    if(FirstTemplate == null){
                        mFirstTextView.setText(getString(R.string.extract_template_fail));
                    }else{
                        mFirstTextView.setText(getString(R.string.extract_template_success));
                    }
                }
            }else if(SECOND_CODE == requestCode){
                if(!TextUtils.isEmpty(path)){
                    mSecondBitmap = BitmapFactory.decodeFile(path);
                    if(mSecondBitmap == null){
                        return;
                    }
                    mSecondImage.setImageBitmap(mSecondBitmap);
                    SecondTemplate = null;
                    SecondTemplate = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(mSecondBitmap);
                    if(SecondTemplate == null){
                        mSecondTextView.setText(getString(R.string.extract_template_fail));
                    }else{
                        mSecondTextView.setText(getString(R.string.extract_template_success));
                    }
                }
            }
        }
    }
    public void onDestroy(){
        super.onDestroy();
        FirstTemplate = null;
        SecondTemplate = null;
        if(mFirstBitmap != null){
            mFirstBitmap.recycle();
            mFirstBitmap = null;
        }
        if(mSecondBitmap != null){
            mSecondBitmap.recycle();
            mSecondBitmap = null;
        }
    }
}

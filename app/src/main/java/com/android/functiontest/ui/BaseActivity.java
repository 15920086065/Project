package com.android.functiontest.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.crlibrary.logs.Logger;
import com.caration.encryption.CREncryption;

import butterknife.ButterKnife;

/**
 * Created by yt on 2017/2/14.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CREncryption.isEncryption()){
            Logger.e("APP未授权使用");
            Toast.makeText(this,"APP未授权使用",Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(initLayout());
        ButterKnife.bind(butterKnife());
        initView();
    }

    /**
     * 初始化
     */
    public abstract void initView();

    /**
     * 初始化布局文件
     * @return
     */
    public abstract int initLayout();

    /**
     * 初始化butter
     * @return
     */
    public abstract Activity butterKnife();
}

package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.functiontest.R;

import butterknife.Bind;

/**
 * Created by yt on 2017/2/14.
 */
public class TPActivityTest extends BaseActivity {

    @Bind(R.id.content)
    LinearLayout content;
    @Bind(R.id.buttonNext)
    Button buttonNext;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, TPActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        content.addView(new TPView(this));
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiDisplayTest.openActivity(TPActivityTest.this);
                finish();
            }
        });

    }

    /**
     * 初始化布局文件
     *
     * @return
     */
    @Override
    public int initLayout() {
        return R.layout.activity_tp;
    }

    /**
     * 初始化butter
     *
     * @return
     */
    @Override
    public Activity butterKnife() {
        return this;
    }

}

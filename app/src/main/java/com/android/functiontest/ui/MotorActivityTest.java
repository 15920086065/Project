package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.Bind;

import com.android.functiontest.R;
import com.caration.robot.ctrl.RobotManager;

/**
 * 电机测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class MotorActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.buttonHead)
    Button buttonHead;
    @Bind(R.id.buttonWalk)
    Button buttonWalk;

    @Bind(R.id.content)
    LinearLayout content;


    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, MotorActivityTest.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        RobotManager.getInstance().init(this);
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings9));
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        buttonHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeadCtrlActivityTest.openActivity(MotorActivityTest.this);
            }
        });

        buttonWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalkCtrlActivityTest.openActivity(MotorActivityTest.this);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TFActivityTest.openActivity(MotorActivityTest.this);
                PsensorActivityTest.openActivity(MotorActivityTest.this);
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
        return R.layout.activity_motor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RobotManager.getInstance().init(this);
    }
}

package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.functiontest.R;
import com.caration.robot.ctrl.RobotManager;
import com.caration.robot.ctrl.utils.WriteUtils;

import butterknife.Bind;

/**
 * Created by yt on 2017/2/14.
 */
public class HeadCtrlActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.lookTop)
    Button lookTop;
    @Bind(R.id.lookLeft)
    Button lookLeft;
    @Bind(R.id.lookRight)
    Button lookRight;
    @Bind(R.id.lookBottom)
    Button lookBottom;
    @Bind(R.id.content)
    LinearLayout content;
    @Bind(R.id.lookCenter)
    Button lookCenter;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, HeadCtrlActivityTest.class);
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

        lookTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WriteUtils.setHeadAction(3)==-1){
                    Toast.makeText(HeadCtrlActivityTest.this,"该机器不支持头部运动",Toast.LENGTH_SHORT).show();
                };
            }
        });

        lookBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WriteUtils.setHeadAction(4)==-1){
                    Toast.makeText(HeadCtrlActivityTest.this,"该机器不支持头部运动",Toast.LENGTH_SHORT).show();
                }
            }
        });

        lookLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WriteUtils.setHeadAction(2)==-1){
                    Toast.makeText(HeadCtrlActivityTest.this,"该机器不支持头部运动",Toast.LENGTH_SHORT).show();
                }
            }
        });

        lookRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WriteUtils.setHeadAction(1)==-1){
                    Toast.makeText(HeadCtrlActivityTest.this,"该机器不支持头部运动",Toast.LENGTH_SHORT).show();
                }
            }
        });

        lookCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WriteUtils.setHeadAction(7)==-1){
                    Toast.makeText(HeadCtrlActivityTest.this,"该机器不支持头部运动",Toast.LENGTH_SHORT).show();
                }
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
        return R.layout.activity_head;
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

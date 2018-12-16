package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import butterknife.Bind;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;
import com.caration.robot.ctrl.RobotEyeManager;
import com.caration.robot.ctrl.utils.WriteUtils;

/**
 * Created by yt on 2017/2/14.
 */
public class LampActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.butOpenLamp)
    Button butOpenLamp;
    @Bind(R.id.butCloseLamp)
    Button butCloseLamp;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;


    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LampActivityTest.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings15));
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplorerActivity.openActivity(LampActivityTest.this);
                finish();
            }
        });

        butOpenLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id1 = WriteUtils.writeBreathinglamp("001");
                Logger.i(WriteUtils.DEVICES_CONTROL + "::" + id1);

                int id2 = WriteUtils.write5012Lamp(1);
                Logger.i(WriteUtils.DEV_BREATH_5012 + " :: " + id2);

                RobotEyeManager.getInstance().runEyeAnimation(
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111," +
                        "11111111");
            }

        });

        butCloseLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id1 = WriteUtils.writeBreathinglamp("000");
                Logger.i(WriteUtils.DEVICES_CONTROL+" :: "+id1);

                int id = WriteUtils.write5012Lamp(0);
                Logger.i(WriteUtils.DEV_BREATH_5012+" :: "+id);

                RobotEyeManager.getInstance().runEyeAnimation(
                        "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000," +
                                "00000000");
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
        return R.layout.activity_lamp;
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
        RobotEyeManager.getInstance().init(this);
    }
}

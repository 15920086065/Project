package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;
import com.caration.robot.ctrl.RobotManager;

import butterknife.Bind;

/**
 * 行走测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class WalkCtrlActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.turnProw)
    Button turnProw;
    @Bind(R.id.turnLeft)
    Button turnLeft;
    @Bind(R.id.turnRight)
    Button turnRight;
    @Bind(R.id.turnBack)
    Button turnBack;
    @Bind(R.id.turnStop)
    Button turnStop;
    @Bind(R.id.content)
    LinearLayout content;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, WalkCtrlActivityTest.class);
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
        turnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotManager.getInstance().robotWheelRun(RobotManager.ROBOT_WHEEL_RIGHT_ALWAY);
            }
        });

        turnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotManager.getInstance().robotWheelRun(RobotManager.ROBOT_WHEEL_LEFT_ALWAY);
            }
        });

        turnProw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotManager.getInstance().robotWheelRun(RobotManager.ROBOT_WHEEL_FRONT_ALWAY);
            }
        });

        turnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotManager.getInstance().robotWheelRun(RobotManager.ROBOT_WHEEL_BACK_ALWAY);
            }
        });

        turnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i("onClick");
                RobotManager.getInstance().robotWheelRun(RobotManager.ROBOT_WHEEL_STOP);
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
        return R.layout.activity_walk;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RobotManager.getInstance().robotWheelRun(RobotManager.ROBOT_WHEEL_STOP);
    }
}

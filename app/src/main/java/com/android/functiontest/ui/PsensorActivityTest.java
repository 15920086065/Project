package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;

import butterknife.Bind;

/**
 * 距离感应测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class PsensorActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.distanceText)
    TextView distanceText;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.distanceTexts)
    TextView distanceTexts;
    @Bind(R.id.psensormess)
    TextView psensormess;

    private SensorManager sensorManager;
    //private PowerManager powerManager;
    //private PowerManager.WakeLock wakeLock;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PsensorActivityTest.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings11));
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
                HdmiActivityTest.openActivity(PsensorActivityTest.this);
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
        return R.layout.activity_distance;
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
        // powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //wakeLock = this.powerManager.newWakeLock(32, "MyPower");//第一个参数为电源锁级别，第二个是日志tag
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] its = event.values;
            Logger.i("" + its[0]);
            if (its[0] == 0.0) {
                distanceText.setText("请挡住P-sensor");
                distanceTexts.setText("");
//                if (wakeLock.isHeld()) {
//                    return;
//                } else {
//                    wakeLock.acquire();// 申请设备电源锁
//                }
            } else {
                distanceTexts.setText("" + its[0]);
//                if (wakeLock.isHeld()) {
//                    return;
//                } else {
//                    wakeLock.setReferenceCounted(false);
//                    wakeLock.release(); // 释放设备电源锁
//                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Logger.i(sensor.toString());
            psensormess.setText("name=" + sensor.getName() + "\n" +
                    "vendor=" + sensor.getVendor() + "\n" +
                    "version=" + sensor.getVersion() + "\n" +
                    "type=" + sensor.getType() + "\n" +
                    "maxRange=" + sensor.getMaximumRange() + "\n" +
                    "resolution=" + sensor.getResolution() + "\n" +
                    "power=" + sensor.getPower() + "\n" +
                    "minDelay=" + sensor.getMinDelay());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) sensorManager.unregisterListener(listener);
        //if (wakeLock != null) wakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试
    }
}

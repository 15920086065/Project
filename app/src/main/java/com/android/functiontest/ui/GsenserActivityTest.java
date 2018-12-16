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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;

import butterknife.Bind;

/**
 * G-sensor测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class GsenserActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.sensorIocn)
    ImageView sensorIocn;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.gtextx)
    TextView gtextx;
    @Bind(R.id.gtexty)
    TextView gtexty;
    @Bind(R.id.gtextz)
    TextView gtextz;
    @Bind(R.id.gtextmess)
    TextView gtextmess;
    @Bind(R.id.content)
    LinearLayout content;

    private SensorManager sensorManager;
    private float startDegree = 0f;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, GsenserActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings13));
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
                //OtgHostActivityTest.openActivity(GsenserActivityTest.this);
                LampActivityTest.openActivity(GsenserActivityTest.this);
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
        return R.layout.activity_gsenser;
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

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Logger.i("onSensorChanged " + event.sensor.getType());
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                //gtextx.setText("X " + event.values[0]);
                //gtexty.setText("Y " + event.values[1]);
                //gtextz.setText("Z " + event.values[2]);
                //获取传感器的角度
//                float degree = Math.round(event.values[0]);
//                Logger.i("onSensorChanged startDegree=" + startDegree + ", degree=" + degree);
//                // 通过补间动画旋转角度 从上次的角度旋转
//                RotateAnimation ra = new RotateAnimation(-startDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                ra.setDuration(200);
//                sensorIocn.startAnimation(ra);
//                startDegree = degree;


            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Logger.i("onAccuracyChanged " + sensor.toString());
            //{Sensor name="LSM330 Gyroscope sensor", vendor="STMicroelectronics", version=1, type=4, maxRange=34.906586, resolution=0.0012217305, power=6.1, minDelay=10000}
            gtextmess.setText("name=" + sensor.getName() + "\n" +
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
    protected void onStop() {
        sensorManager.unregisterListener(listener);
        super.onStop();
    }
}

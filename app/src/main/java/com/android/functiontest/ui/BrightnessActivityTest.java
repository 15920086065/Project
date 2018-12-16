package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.functiontest.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 亮度测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class BrightnessActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.brightenssSeekbar)
    SeekBar brightenssSeekbar;
    @Bind(R.id.brightenssText)
    TextView brightenssText;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    private int intScreenBrightness;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, BrightnessActivityTest.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings2));
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
                TPActivityTest.openActivity(BrightnessActivityTest.this);
                finish();
            }
        });

        brightenssSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int cur = seekBar.getProgress();
                setScreenBrightness(cur);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        screenBrightnessCheck();
    }

    /**
     * 初始化布局文件
     *
     * @return
     */
    @Override
    public int initLayout() {
        return R.layout.activity_brightenss;
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


    //设置屏幕亮度的函数
    private void setScreenBrightness(int brightness) {
        //不让屏幕全暗
        if (brightness <= 1) {
            brightness = 1;
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = Float.valueOf(brightness / 255f);
        this.getWindow().setAttributes(lp);
        //保存为系统亮度方法1
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        brightenssText.setText("亮度 " + brightness * 100 / 255);
    }

    private void screenBrightnessCheck() {
        //先关闭系统的亮度自动调节
        try {
            if (Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //获取当前亮度,获取失败则返回255
        intScreenBrightness = (int) (Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                255));
        //文本、进度条显示
        brightenssSeekbar.setProgress(intScreenBrightness);
        Log.e("Function", "" + intScreenBrightness * 100 / 255);
        Log.e("Function", "" + brightenssText);
        brightenssText.setText("亮度 " + intScreenBrightness * 100 / 255);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

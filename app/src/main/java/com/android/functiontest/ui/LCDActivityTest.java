package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.functiontest.R;

import butterknife.Bind;

/**
 * LCD测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class LCDActivityTest extends BaseActivity {

    @Bind(R.id.content)
    LinearLayout content;
    @Bind(R.id.content1)
    LinearLayout content1;
    @Bind(R.id.content2)
    LinearLayout content2;
    @Bind(R.id.content3)
    LinearLayout content3;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    private int stat = 1;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LCDActivityTest.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        Toast.makeText(this, "点击屏幕切换", Toast.LENGTH_SHORT).show();
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stat == 0) {
                    content1.setBackgroundColor(Color.TRANSPARENT);
                    content2.setBackgroundColor(Color.TRANSPARENT);
                    content3.setBackgroundColor(Color.TRANSPARENT);
                    //红色
                    content.setBackgroundColor(Color.rgb(255, 0, 0));
                    stat = 1;
                } else if (stat == 1) {
                    //绿色
                    content.setBackgroundColor(Color.rgb(0, 255, 0));
                    stat = 2;
                } else if (stat == 2) {
                    //蓝色
                    content.setBackgroundColor(Color.rgb(0, 0, 255));
                    stat = 3;
                } else if (stat == 3) {
                    content1.setBackgroundColor(Color.rgb(255, 0, 0));
                    content2.setBackgroundColor(Color.rgb(0, 255, 0));
                    content3.setBackgroundColor(Color.rgb(0, 0, 255));
                    stat = 4;
                } else if (stat == 4) {
                    content1.setBackgroundColor(Color.TRANSPARENT);
                    content2.setBackgroundColor(Color.TRANSPARENT);
                    content3.setBackgroundColor(Color.TRANSPARENT);
                    content.setBackgroundColor(Color.rgb(255, 255, 255));
                    stat = 5;
                } else if (stat == 5) {
                    content.setBackgroundColor(Color.rgb(0, 0, 0));
                    stat = 6;
                } else if (stat == 6) {
                    content.setBackgroundColor(Color.rgb(128, 128, 128));
                    stat = 7;
                } else if (stat == 7) {
                    content1.setBackgroundColor(Color.rgb(255, 255, 255));
                    content2.setBackgroundColor(Color.rgb(0, 0, 0));
                    content3.setBackgroundColor(Color.rgb(128, 128, 128));
                    stat = 8;
                    Toast.makeText(LCDActivityTest.this, "完成", Toast.LENGTH_SHORT).show();
                    buttonNext.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrightnessActivityTest.openActivity(LCDActivityTest.this);
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
        return R.layout.activity_lcds;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

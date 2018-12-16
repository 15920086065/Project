package com.android.functiontest.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.android.crlibrary.wifi.WifiEnabler;
import com.android.crlibrary.wifi.WifiSettings;
import com.android.functiontest.R;

import butterknife.Bind;

/**
 * Created by yt on 2017/3/15.
 */
public class WifiDisplayTest extends BaseActivity {
    @Bind(R.id.buttonWeb)
    Button buttonWeb;
    @Bind(R.id.wifiSwitchText)
    TextView wifiSwitchText;
    @Bind(R.id.wifiSwitch)
    Switch wifiSwitch;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;

    private WifiEnabler mWifiEnabler;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, WifiDisplayTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiSettings wifiSettings = new WifiSettings();
        wifiSettings.registerCallback(new WifiSettings.Cllback() {
            @Override
            public void onConnected(boolean icConnected) {
                buttonWeb.setEnabled(icConnected);
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.content, wifiSettings).commit();
        mWifiEnabler = new WifiEnabler(this, wifiSwitchText, wifiSwitch);
        buttonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivityTest.openActivity(WifiDisplayTest.this);
            }
        });

    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings4));
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        return R.layout.activity_wf;
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
    protected void onResume() {
        super.onResume();
        if (mWifiEnabler != null) {
            mWifiEnabler.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWifiEnabler != null) {
            mWifiEnabler.pause();
        }
    }

}

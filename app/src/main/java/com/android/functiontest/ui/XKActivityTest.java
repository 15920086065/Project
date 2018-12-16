package com.android.functiontest.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;
import com.android.functiontest.Serial.SerialServer;

import butterknife.Bind;

/**
 * 距离感应测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class XKActivityTest extends BaseActivity implements SerialServer.CallBack {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.dangling1)
    Button dangling1;
    @Bind(R.id.dangling2)
    Button dangling2;
    @Bind(R.id.dangling3)
    Button dangling3;
    @Bind(R.id.dangling4)
    Button dangling4;

    private ServiceConnection serviceConnection;
    private SerialServer mSerialServer;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, XKActivityTest.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings17));
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
                finish();
                Intent intent = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
                startActivity(intent);
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
        return R.layout.activity_xk;
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
        bindService();
    }

    private void bindService() {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Logger.i("串口服务绑定成功");
                    mSerialServer = ((SerialServer.MYIBinder) service).getServer();
                    mSerialServer.setCallback(XKActivityTest.this);
                    mSerialServer.startSerial();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Logger.i("串口服务绑定断开");
                    serviceConnection = null;
                }
            };
            Intent intentService = new Intent();
            intentService.setClass(this, SerialServer.class);
            bindService(intentService, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onChange(final int code, final int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 3) {
                    Logger.i("底部悬空");
                    setui(true,true,true,true);
            } else {
                setui(false,false,false,false);
            }

                if (state == 5) {
                    Logger.i("7右后方悬空");
                    setui(false,true,true,false);
                }else if (state == 7) {
                    Logger.i("7右后方悬空");
                    setui(false,true,false,false);
                }  else if (state == 10) {
                    Logger.i("前方悬空");
                    setui(true,false,false,true);
                }else if (state == 11) {
                    Logger.i("11右前方悬空");
                    setui(true,false,false,false);
                } else if (state == 13) {
                    Logger.i("13左后方悬空");
                    setui(false,false,true,false);
                } else if (state == 14) {
                    Logger.i("14左前方悬空");
                    setui(false,false,false,true);
                }
            }
        });
    }

    @Override
    public void onError(String errorStr) {
        Toast.makeText(this,errorStr,Toast.LENGTH_LONG).show();
    }

    public void setui(boolean boolleft1,boolean boolleft2,boolean boolRight1,boolean boolRight2){
        dangling1.setPressed(boolleft1);
        dangling2.setPressed(boolleft2);
        dangling3.setPressed(boolRight1);
        dangling4.setPressed(boolRight2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e("onDestroy");
        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
    }

}

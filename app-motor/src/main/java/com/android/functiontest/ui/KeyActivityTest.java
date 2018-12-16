package com.android.functiontest.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;

import butterknife.Bind;

/**
 * Created by yt on 2017/2/14.
 */
public class KeyActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.keyDownCodeText)
    TextView keyDownCodeText;
    @Bind(R.id.keyUpCodeText)
    TextView keyUpCodeText;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.keyDownCodeName)
    TextView keyDownCodeName;

    private BroadcastReceiver keyBroadCast;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, KeyActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerKeyBroadcaset();
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText("按键测试");
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
                MotorActivityTest.openActivity(KeyActivityTest.this);
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
        return R.layout.activity_key;
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
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Logger.i("长按 keyCode " + keyCode);
        keyDownCodeText.setText("长按键值 keyCode " + keyCode);
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Logger.i("按键值 keyCode " + keyCode);

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i("按键值 keyCode " + keyCode);
        keyDownCodeText.setText("按键值 keyCode " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keyDownCodeName.setText("返回");
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            keyDownCodeName.setText("HOUME");
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            keyDownCodeName.setText("音量加");
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            keyDownCodeName.setText("音量减");
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            keyDownCodeName.setText("上一曲");
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            keyDownCodeName.setText("播放/暂停");
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            keyDownCodeName.setText("下一曲");
        } else {
            keyDownCodeName.setText("未知名");
        }
        return false;
    }

    private void registerKeyBroadcaset(){
        if (keyBroadCast==null){
            keyBroadCast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, final Intent intent) {
                    Logger.i(intent.getAction());
                    if (TextUtils.equals(intent.getAction(),"com.caration.robot.telephone.action.ReadButton")||
                            TextUtils.equals(intent.getAction(),"org.huiyu.honeybot.action.ReadButton")){
                        keyDownCodeName.setText("播放语音消息");
                        keyDownCodeText.setText("按键值 keyCode " );
                    }else if (TextUtils.equals(intent.getAction(),"com.caration.robot.telephone.action.RecButtonDown")||
                            TextUtils.equals(intent.getAction(),"org.huiyu.honeybot.action.RecButtonDown")){
                        keyDownCodeName.setText("开始说话");
                        keyDownCodeText.setText("按键值 keyCode " );
                    }else if (TextUtils.equals(intent.getAction(),"com.caration.robot.telephone.action.RecButtonUp")||
                            TextUtils.equals(intent.getAction(),"org.huiyu.honeybot.action.RecButtonUp")){
                        keyDownCodeName.setText("结束说话");
                        keyDownCodeText.setText("按键值 keyCode " );
                    }else if (TextUtils.equals(intent.getAction(),"COM.CARATION.ACTION.STARTSPEECH")){
                        keyDownCodeName.setText("语音对话");
                        keyDownCodeText.setText("按键值 keyCode " );
                    }else if (TextUtils.equals(intent.getAction(),"com.caration.keyevent.action.KARATION")){
                        keyDownCodeName.setText("卡拉OK");
                        keyDownCodeText.setText("按键值 keyCode " );
                    }
                    abortBroadcast();
                }
            };
        }

        IntentFilter intent = new IntentFilter();
        //语音对讲
        intent.addAction("com.caration.robot.telephone.action.ReadButton");
        intent.addAction("com.caration.robot.telephone.action.RecButtonDown");
        intent.addAction("com.caration.robot.telephone.action.RecButtonDown_Pre");
        intent.addAction("com.caration.robot.telephone.action.RecButtonUp");

        intent.addAction("org.huiyu.honeybot.action.ReadButton");
        intent.addAction("org.huiyu.honeybot.action.RecButtonDown");
        intent.addAction("org.huiyu.honeybot.action.RecButtonDown_Pre");
        intent.addAction("org.huiyu.honeybot.action.RecButtonUp");

        //语音对话
        intent.addAction("COM.CARATION.ACTION.STARTSPEECH");

        //卡拉Ok
        intent.addAction("com.caration.keyevent.action.KARATION");

        intent.setPriority(1000);
        registerReceiver(keyBroadCast, intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(keyBroadCast);
    }

}

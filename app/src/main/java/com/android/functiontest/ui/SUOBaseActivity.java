package com.android.functiontest.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by yt on 2017/3/15.
 */
public abstract class SUOBaseActivity extends BaseActivity {
    protected BroadcastReceiver mStorageBroadcast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageBroadcast = getmStorageBroadcast();
        registerReceiver(mStorageBroadcast, getIntentFilter());
    }

    protected IntentFilter getIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.addDataScheme("file");
        return intentFilter;
    }

    /**
     * 注册USBOTG，SD卡广播事件
     * @return
     */
    public abstract BroadcastReceiver getmStorageBroadcast();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStorageBroadcast);
    }
}

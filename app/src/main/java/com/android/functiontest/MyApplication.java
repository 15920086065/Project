package com.android.functiontest;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.crlibrary.logs.Logger;
import com.android.crlibrary.utils.NetworkUtils;


/**
 * Created by yt on 2017/2/14.
 */
public class MyApplication extends Application implements NetworkUtils.NetworkListener{
    private static MyApplication instance;
    private boolean isConnected;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NetworkUtils.getInstance(instance).registerNetworkListener(this);
    }

    @Override
    public void onNetwork(boolean connected) {
        isConnected = connected;
        Logger.v(isConnected ? "网络已连接" : "网络不可用，请检查网络");
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 获取当前程序的版本
     */
    public int getVersionCode() {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.e("获取当前程序的版本失败");
        }
        return packInfo.versionCode;
    }

    /**
     * 获取当前程序的版本号
     * 显示给用户
     */
    public final String getVersionName() {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo.versionName;
    }
}

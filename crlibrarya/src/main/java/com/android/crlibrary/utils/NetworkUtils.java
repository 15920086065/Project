package com.android.crlibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.android.crlibrary.logs.Logger;

/**
 * 监听网络变化
 * Created by yt on 2017/3/24.
 */
public class NetworkUtils {
    private Context mContext;
    private final ConnectivityManager connectMgr;
    private static NetworkUtils instance;
    private MyBroadcastReceiver myBroadcastReceiver;
    public static NetworkUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtils(context);
        }
        return instance;
    }

    NetworkUtils(Context context) {
        mContext = context;
        connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void registerNetworkListener(final NetworkListener listener) {
        if (listener==null)
            return;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (myBroadcastReceiver==null){
            myBroadcastReceiver = new MyBroadcastReceiver(listener);
        }
        mContext.registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public void unregisterNetworkListener(){
       if (myBroadcastReceiver!=null){
           mContext.unregisterReceiver(myBroadcastReceiver);
       }
    }

    /**
     * 移動網絡可用
     * @return
     */
    boolean isMobileNet(){
        NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobNetInfo==null){
            return false;
        }
        return mobNetInfo.isConnected();
    }

    /**
     * WIFI網絡可用
     * @return
     */
    boolean isWifiNet(){
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetInfo==null){
            return false;
        }
        return wifiNetInfo.isConnected();
    }

    class  MyBroadcastReceiver extends BroadcastReceiver {
        NetworkListener networkListener;
        public MyBroadcastReceiver(final NetworkListener listener) {
            networkListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                networkListener.onNetwork(!(!isWifiNet()&&!isMobileNet()));
            }
        }
    }



    public interface NetworkListener {
        void onNetwork(boolean connected);
    }

}

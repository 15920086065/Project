package com.android.crlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * 监听网络变化
 * Created by yt on 2017/3/24.
 */
public class NetworkUtils {
    private NetworkListener mNetworkListener;
    private Context mContext;
    private final ConnectivityManager connectMgr;
    private static NetworkUtils instance;

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

    public void registerNetworkListener(NetworkListener listener) {
        mNetworkListener = listener;
        if (mNetworkListener==null) return;
        Logger.v("mNetworkListener " + mNetworkListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                    NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (mobNetInfo!=null){
                        mNetworkListener.onNetwork(!(!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()));
                    }else {
                        mNetworkListener.onNetwork(!(!wifiNetInfo.isConnected()));
                    }
                }
            }
        }, intentFilter);
    }

    public interface NetworkListener {
        void onNetwork(boolean connected);
    }

}

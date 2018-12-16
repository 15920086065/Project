package com.android.crlibrary.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yt on 2017/3/22.
 */
public class WifiEnabler implements CompoundButton.OnCheckedChangeListener{
    private final Context mContext;
    private Switch mSwitch;
    private TextView mSwitchText;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private final WifiManager mWifiManager;
    private boolean mStateMachineEvent;
    private final IntentFilter mIntentFilter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                if (!mConnected.get()) {
                    //handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                mConnected.set(info.isConnected());
                //handleStateChanged(info.getDetailedState());
            }
        }
    };

    public WifiEnabler(Context context, TextView mSwitchText_, Switch switch_) {
        mContext = context;
        mSwitch = switch_;
        mSwitchText = mSwitchText_;

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // The order matters! We really should not depend on this. :(
        mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

    }

    public void resume() {
        // Wi-Fi state is sticky, so just let the receiver update UI
        mContext.registerReceiver(mReceiver, mIntentFilter);
        mSwitch.setOnCheckedChangeListener(this);
    }

    public void pause() {
        mContext.unregisterReceiver(mReceiver);
        mSwitch.setOnCheckedChangeListener(null);
    }

    public void setSwitch(Switch switch_) {
        if (mSwitch == switch_) return;
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch = switch_;
        mSwitch.setOnCheckedChangeListener(this);

        final int wifiState = mWifiManager.getWifiState();
        boolean isEnabled = wifiState == WifiManager.WIFI_STATE_ENABLED;
        boolean isDisabled = wifiState == WifiManager.WIFI_STATE_DISABLED;
        mSwitch.setChecked(isEnabled);
        mSwitch.setEnabled(isEnabled || isDisabled);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Do nothing if called as a result of a state machine event
        if (mStateMachineEvent) {
            return;
        }
        // Show toast message if Wi-Fi is not allowed in airplane mode
//        if (isChecked && !WirelessSettings.isRadioAllowed(mContext, Settings.Global.RADIO_WIFI)) {
//            Toast.makeText(mContext, R.string.wifi_in_airplane_mode, Toast.LENGTH_SHORT).show();
//            // Reset switch to off. No infinite check/listenenr loop.
//            buttonView.setChecked(false);
//        }

        // Disable tethering if enabling Wifi
//        int wifiApState = mWifiManager.getWifiApState();
//        if (isChecked && ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) ||
//                (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED))) {
//            mWifiManager.setWifiApEnabled(null, false);
//        }

        if (mWifiManager.setWifiEnabled(isChecked)) {
            // Intent has been taken into account, disable until new state is active
            mSwitch.setEnabled(false);
        } else {
            // Error
            Toast.makeText(mContext, "出错", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleWifiStateChanged(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                mSwitch.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                setSwitchChecked(true);
                mSwitch.setEnabled(true);
                mSwitchText.setText("开启");
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                mSwitch.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                setSwitchChecked(false);
                mSwitch.setEnabled(true);
                mSwitchText.setText("要查看可用网络，请打开WLAN");
                break;
            default:
                setSwitchChecked(false);
                mSwitch.setEnabled(true);
                break;
        }
    }

    private void setSwitchChecked(boolean checked) {
        if (checked != mSwitch.isChecked()) {
            mStateMachineEvent = true;
            mSwitch.setChecked(checked);
            mStateMachineEvent = false;
        }
    }
}

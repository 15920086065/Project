package com.android.crlibrary.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crlibrary.Logger;
import com.android.crlibrary.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by yt on 2017/3/15.
 */
public class WifiSettings extends PreferenceFragment implements WifiTracker.WifiListener{
    static final int BUTTON_SUBMIT = DialogInterface.BUTTON_POSITIVE;
    static final int BUTTON_FORGET = DialogInterface.BUTTON_NEUTRAL;
    static final int BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE;
    private static final int INVALID_NETWORK_ID = -1;
    WifiManager mWifiManager;
    private WifiTracker mWifiTracker = null;
    private AccessPoint mSelectedAccessPoint = null;
    private EditText mPasswordView;
    private CheckBox showPasswordChek;
    ViewGroup group ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_settings);
        mWifiTracker = new WifiTracker(getActivity(),WifiSettings.this);
        mWifiManager = mWifiTracker.getManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWifiTracker.startTracking();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWifiTracker.stopTracking();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof AccessPoint) {
            mSelectedAccessPoint = (AccessPoint) preference;
            Logger.v("mSelectedAccessPoint :: " + mSelectedAccessPoint.ssid + " , " + mSelectedAccessPoint.networkId);
            /** 无加密连接 */
            if (mSelectedAccessPoint.security == AccessPoint.SECURITY_NONE && mSelectedAccessPoint.networkId == -1 && !mSelectedAccessPoint.isActive()) {
                mSelectedAccessPoint.generateOpenNetworkConfig();
                Logger.v("无加密连接 :: " + mSelectedAccessPoint.ssid);
                if (connect(getConfig())) {
                    Logger.v("无法连接");
                }
            } else {//加密方式连接
                boolean isEdite = (mSelectedAccessPoint.security != AccessPoint.SECURITY_NONE) && mSelectedAccessPoint.networkId == -1;
                showDialog(mSelectedAccessPoint, isEdite);
            }
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    private void showDialog(AccessPoint accessPoint, boolean edit) {
        Logger.v("showDialog :: " + accessPoint.ssid);
        View view = getActivity().getLayoutInflater().inflate(R.layout.wifi_ap_dialog, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.parentPanel);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        showPasswordChek = (CheckBox)view.findViewById(R.id.show_password);
        group = (ViewGroup) view.findViewById(R.id.info);
        addRow(group, R.string.wifi_security, accessPoint.getSecurityString(false));
        if (edit) {
            linearLayout.setVisibility(View.VISIBLE);
            encryptedConnectionDialog(view, accessPoint);
        } else {
            linearLayout.setVisibility(View.GONE);
            reconnectionDialog(view, accessPoint);
        }
    }

    /**
     * 重连接或取消保存
     *
     * @param view
     * @param accessPoint
     */
    public void reconnectionDialog(View view, final AccessPoint accessPoint) {
        Logger.v("重连接或取消保存");
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCancelable(true);
        dialog.setTitle(accessPoint.ssid);
        dialog.setView(view);
        NetworkInfo.DetailedState state = accessPoint.getState();
        int level = accessPoint.getLevel();
        if (state == null && level != -1) {
            dialog.setButton(BUTTON_SUBMIT, "连接", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    final WifiConfiguration config = getConfig();
                    Logger.v("config :: " + config);
                    if (config == null) {
                        if (accessPoint != null && accessPoint.networkId != INVALID_NETWORK_ID) {
                            if (mWifiManager.enableNetwork(accessPoint.networkId, true)) {
                                mWifiTracker.resumeScanning();
                            }
                        }
                    }
                }
            });
        }else{
            if (state != null) {
                addRow(group, R.string.wifi_status, Summary.get(getActivity(), state));
            }

            if (level != -1) {
                String[] signal = getActivity().getResources().getStringArray(R.array.wifi_signal);
                addRow(group, R.string.wifi_signal, signal[level]);
            }

            WifiInfo info = accessPoint.getInfo();
            if (info != null && info.getLinkSpeed() != -1) {
                addRow(group, R.string.wifi_speed, info.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
            }
        }

        dialog.setButton(BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setButton(BUTTON_FORGET, "取消保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (accessPoint != null
                        && accessPoint.networkId != INVALID_NETWORK_ID) {
                    Logger.v("removeNetwork "+accessPoint.networkId);
                    mWifiManager.removeNetwork(accessPoint.networkId);
                    mWifiTracker.resumeScanning();
                }

            }
        });
        dialog.show();
    }

    /**
     * 加密方式连接
     *
     * @param view
     * @param accessPoint
     */
    public void encryptedConnectionDialog(View view, final AccessPoint accessPoint) {
        Logger.v("加密方式连接");
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCancelable(true);
        dialog .setTitle(accessPoint.ssid);
        dialog.setView(view);
        dialog.setButton(BUTTON_SUBMIT, "连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String password = mPasswordView.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    return;
                }
                WifiConfiguration config = getConfig();
                if (!connect(config)) {
                    Logger.v("无法连接");
                }
                mWifiTracker.resumeScanning();
            }
        });

        dialog.setButton(BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();
        dialog.getButton(BUTTON_SUBMIT).setEnabled(false);
        showPasswordChek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.v("isChecked :: "+isChecked);
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT|(isChecked?InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
                        InputType.TYPE_TEXT_VARIATION_PASSWORD));
            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)||s.length()<8){
                    dialog.getButton(BUTTON_SUBMIT).setEnabled(false);
                }else {
                    dialog.getButton(BUTTON_SUBMIT).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addRow(ViewGroup group, int name, String value) {
        View row = getActivity().getLayoutInflater().inflate(R.layout.wifi_dialog_row, group, false);
        ((TextView) row.findViewById(R.id.name)).setText(name);
        ((TextView) row.findViewById(R.id.value)).setText(value);
        group.addView(row);
    }

    /**
     * 连接
     *
     * @param netid
     * @return
     */
    public boolean connect(int netid) {
        return mWifiManager.enableNetwork(netid, true);
    }

    /**
     * 连接
     *
     * @param configuration
     * @return
     */
    public boolean connect(WifiConfiguration configuration) {
        if (configuration == null) return false;
        Logger.v("netid :: " + configuration.networkId + "," + configuration.SSID);
        int netid = -1;
        if ((netid = mWifiManager.addNetwork(configuration)) > 0) {
            mWifiManager.saveConfiguration();
            return mWifiManager.enableNetwork(netid, true);
        }
        return false;
    }

    WifiConfiguration getConfig() {
        if (mSelectedAccessPoint != null && mSelectedAccessPoint.networkId != -1) {
            return null;
        }

        WifiConfiguration config = new WifiConfiguration();
        if (mSelectedAccessPoint == null) {
            Logger.v("getConfig1 :: ");
            config.SSID = AccessPoint.convertToQuotedString(mSelectedAccessPoint.ssid);
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else if (mSelectedAccessPoint.networkId == -1) {
            Logger.v("getConfig2 :: ");
            config.SSID = AccessPoint.convertToQuotedString(
                    mSelectedAccessPoint.ssid);
        } else {
            Logger.v("getConfig3 :: " + mSelectedAccessPoint.ssid);
            config.networkId = mSelectedAccessPoint.networkId;
            config.SSID = AccessPoint.convertToQuotedString(mSelectedAccessPoint.ssid);
        }

        int mAccessPointSecurity = (mSelectedAccessPoint == null) ? AccessPoint.SECURITY_NONE : mSelectedAccessPoint.security;
        Logger.v("mAccessPointSecurity :: " + mAccessPointSecurity);

        switch (mAccessPointSecurity) {
            case AccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case AccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                if (mPasswordView.length() != 0) {
                    int length = mPasswordView.length();
                    String password = mPasswordView.getText().toString();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) &&
                            password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (mPasswordView.length() != 0) {
                    String password = mPasswordView.getText().toString();
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_EAP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                break;
        }
        return config;
    }

    @Override
    public void onWifiStateChanged(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                mWifiTracker.startTracking();
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                getPreferenceScreen().removeAll();
                break;
        }
    }

    @Override
    public void onConnectedChanged() {
        if (myCllback!=null)
            myCllback.onConnected(mWifiTracker.isConnected());
    }

    @Override
    public void onAccessPointsChanged() {
        final int wifiState = mWifiManager.getWifiState();
        switch (wifiState){
            case WifiManager.WIFI_STATE_ENABLED:
                // AccessPoints are automatically sorted with TreeSet.
                final Collection<AccessPoint> accessPoints = mWifiTracker.getAccessPoints();
                getPreferenceScreen().removeAll();
                for (AccessPoint accessPoint : accessPoints) {
                    getPreferenceScreen().addPreference(accessPoint);
                }
                break;
        }
    }

    private Cllback myCllback;
    public  void registerCallback(Cllback cllback){
        myCllback = cllback;
    }

    public interface Cllback{
        void onConnected(boolean icConnected);
    }

}

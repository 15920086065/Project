package com.android.crlibrary.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.crlibrary.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by yt on 2017/3/23.
 */
public class WifiTracker {
    private static final String TAG = "WifiTracker";
    // TODO: Allow control of this?
    // Combo scans can take 5-6s to complete - set to 10s.
    /// M: change interval time to 6s
    private static final int WIFI_RESCAN_INTERVAL_MS = 6 * 1000;

    private static final int INVALID_NETWORK_ID = -1;
    private static final String WIFI_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";
    private static final String SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.SCAN_RESULTS";
    private static final String NETWORK_IDS_CHANGED_ACTION = "android.net.wifi.NETWORK_IDS_CHANGED";
    private static final String SUPPLICANT_STATE_CHANGED_ACTION = "android.net.wifi.supplicant.STATE_CHANGE";
    private static final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
    private static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";
    private static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE";
    private static final String RSSI_CHANGED_ACTION = "android.net.wifi.RSSI_CHANGED";

    private final Context mContext;
    private final IntentFilter mFilter;
    private final BroadcastReceiver mReceiver;
    private WifiManager mWifiManager = null;
    private final WifiListener mListener;
    private final MainHandler mMainHandler;
    private final WorkHandler mWorkHandler;
    private boolean mRegistered;
    private WifiInfo mLastInfo;
    private NetworkInfo.DetailedState mLastState;
    Scanner mScanner;
    private ArrayList<AccessPoint> mAccessPoints = new ArrayList<>();

    private AtomicBoolean mConnected = new AtomicBoolean(false);

    public WifiTracker(Context context, WifiListener listener) {
        //Logger.v("--------WifiSettings--------");
        this(context, listener,(WifiManager) context.getSystemService(Context.WIFI_SERVICE), Looper.myLooper());
    }

    private WifiTracker(Context context, WifiListener wifiListener, WifiManager wifiManager, Looper currentLooper) {
        mContext = context;
        mWifiManager = wifiManager;
        if (currentLooper == null) {
            // When we aren't on a looper thread, default to the main.
            currentLooper = Looper.getMainLooper();
        }
        mMainHandler = new MainHandler(currentLooper);
        mWorkHandler = new WorkHandler(currentLooper);
        mListener = wifiListener;

        mFilter = new IntentFilter();
        mFilter.addAction(WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(RSSI_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }
        };

    }

    private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        if (WIFI_STATE_CHANGED_ACTION.equals(action)) {
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
        } else if (SCAN_RESULTS_AVAILABLE_ACTION.equals(action) ||
                CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action) ||
                LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
            mWorkHandler.sendEmptyMessage(WorkHandler.MSG_UPDATE_ACCESS_POINTS);
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            mMainHandler.sendEmptyMessage(MainHandler.MSG_CONNECTED_CHANGED);
            mWorkHandler.sendEmptyMessage(WorkHandler.MSG_UPDATE_ACCESS_POINTS);
            mWorkHandler.obtainMessage(WorkHandler.MSG_UPDATE_NETWORK_INFO, info).sendToTarget();
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            mWorkHandler.sendEmptyMessage(WorkHandler.MSG_UPDATE_NETWORK_INFO);
        }
    }

    public boolean isConnected() {
        return mConnected.get();
    }

    public WifiManager getManager() {
        return mWifiManager;
    }

    /**
     * Force a scan for wifi networks to happen now.
     */
    public void forceScan() {
        if (mWifiManager.isWifiEnabled() && mScanner != null) {
            mScanner.forceScan();
        }
    }

    /**
     * Temporarily stop scanning for wifi networks.
     */
    public void pauseScanning() {
        if (mScanner != null) {
            mScanner.pause();
            mScanner = null;
        }
    }

    /**
     * Resume scanning for wifi networks after it has been paused.
     */
    public void resumeScanning() {
        if (mScanner == null) {
            mScanner = new Scanner();
        }
        mWorkHandler.sendEmptyMessage(WorkHandler.MSG_RESUME);
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        mWorkHandler.sendEmptyMessage(WorkHandler.MSG_UPDATE_ACCESS_POINTS);
    }

    /**
     * Start tracking wifi networks.
     * Registers listeners and starts scanning for wifi networks. If this is not called
     * then forceUpdate() must be called to populate getAccessPoints().
     */
    public void startTracking() {
        resumeScanning();
        if (!mRegistered) {
            mContext.registerReceiver(mReceiver, mFilter);
            mRegistered = true;
        }
    }

    /**
     * Stop tracking wifi networks.
     * Unregisters all listeners and stops scanning for wifi networks. This should always
     * be called when done with a WifiTracker (if startTracking was called) to ensure
     * proper cleanup.
     */
    public void stopTracking() {
        if (mRegistered) {
            mWorkHandler.removeMessages(WorkHandler.MSG_UPDATE_ACCESS_POINTS);
            mWorkHandler.removeMessages(WorkHandler.MSG_UPDATE_NETWORK_INFO);
            mContext.unregisterReceiver(mReceiver);
            mRegistered = false;
        }
        pauseScanning();
    }

    /**
     * Gets the current list of access points.
     */
    public List<AccessPoint> getAccessPoints() {
        synchronized (mAccessPoints) {
            /// M: Add for Google original code shallow copy issue
            ArrayList<AccessPoint> cachedaccessPoints = new ArrayList<>();
            for (AccessPoint accessPoint : mAccessPoints) {
                cachedaccessPoints.add(accessPoint);
            }
            return cachedaccessPoints;
        }
    }

    /**
     * Returns sorted list of access points
     */
    private void updateAccessPoints() {
        ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
        /** Lookup table to more quickly update AccessPoints by only considering objects with the
         * correct SSID.  Maps SSID -> List of AccessPoints with the given SSID.  */
        Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();
        final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                AccessPoint accessPoint = new AccessPoint(mContext, config);
                accessPoint.update(mLastInfo, mLastState);
                accessPoints.add(accessPoint);
                apMap.put(accessPoint.ssid, accessPoint);
            }
        }

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                    continue;
                }

                boolean found = false;
                for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
                    if (accessPoint.update(result))
                        found = true;
                }
                if (!found) {
                    AccessPoint accessPoint = new AccessPoint(mContext, result);
                    accessPoints.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }
        // Pre-sort accessPoints to speed preference insertion
        Collections.sort(accessPoints);
        mAccessPoints = accessPoints;
        mMainHandler.sendEmptyMessage(MainHandler.MSG_ACCESS_POINT_CHANGED);
    }


    private void updateWifiState(int state) {
        if (state == WifiManager.WIFI_STATE_ENABLED) {
            if (mScanner != null) {
                mScanner.resume();
            }
        } else {
            if (mScanner != null) {
                mScanner.pause();
            }
        }
        mLastInfo = null;
        mLastState = null;
        mMainHandler.obtainMessage(MainHandler.MSG_WIFI_STATE_CHANGED, state).sendToTarget();
    }

    /**
     * A restricted multimap for use in constructAccessPoints
     */
    private class Multimap<K, V> {
        private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

        /**
         * retrieve a non-null list of values with key K
         */
        List<V> getAll(K key) {
            List<V> values = store.get(key);
            return values != null ? values : Collections.<V>emptyList();
        }

        void put(K key, V val) {
            List<V> curVals = store.get(key);
            if (curVals == null) {
                curVals = new ArrayList<V>(3);
                store.put(key, curVals);
            }
            curVals.add(val);
        }
    }

    private final class MainHandler extends Handler {
        private static final int MSG_CONNECTED_CHANGED = 0;
        private static final int MSG_WIFI_STATE_CHANGED = 1;
        private static final int MSG_ACCESS_POINT_CHANGED = 2;
        private static final int MSG_RESUME_SCANNING = 3;
        private static final int MSG_PAUSE_SCANNING = 4;

        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListener == null) {
                return;
            }
            switch (msg.what) {
                case MSG_CONNECTED_CHANGED:
                    mListener.onConnectedChanged();
                    break;
                case MSG_WIFI_STATE_CHANGED:
                    mListener.onWifiStateChanged((int)msg.obj);
                    break;
                case MSG_ACCESS_POINT_CHANGED:
                    mListener.onAccessPointsChanged();
                    break;
                case MSG_RESUME_SCANNING:
                    if (mScanner != null) {
                        mScanner.resume();
                    }
                    break;
                case MSG_PAUSE_SCANNING:
                    if (mScanner != null) {
                        mScanner.pause();
                    }
                    break;
            }
        }
    }

    private final class WorkHandler extends Handler {
        private static final int MSG_UPDATE_ACCESS_POINTS = 0;
        private static final int MSG_UPDATE_NETWORK_INFO = 1;
        private static final int MSG_RESUME = 2;

        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_ACCESS_POINTS:
                    updateAccessPoints();
                    break;
                case MSG_UPDATE_NETWORK_INFO:
                    NetworkInfo networkInfo =(NetworkInfo) msg.obj;
                    Log.v(TAG, "D " + networkInfo);
                    /* sticky broadcasts can call this when wifi is disabled */
                    if (!mWifiManager.isWifiEnabled()) {
                        mMainHandler.sendEmptyMessage(MainHandler.MSG_PAUSE_SCANNING);
                        return;
                    }

                    if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        mMainHandler.sendEmptyMessage(MainHandler.MSG_PAUSE_SCANNING);
                    } else {
                        mMainHandler.sendEmptyMessage(MainHandler.MSG_RESUME_SCANNING);
                    }

                    mLastInfo = mWifiManager.getConnectionInfo();
                    if (networkInfo != null) {
                        mLastState = networkInfo.getDetailedState();
                    }
                    for (int i = mAccessPoints.size() - 1; i >= 0; --i) {
                        mAccessPoints.get(i).update(mLastInfo, mLastState);
                    }
                    break;
                case MSG_RESUME:
                    handleResume();
                    break;
            }
        }
    }

    private void handleResume() {

    }

    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                if (mContext != null) {
                    Toast.makeText(mContext, mContext.getString(R.string.wifi_fail_to_scan), Toast.LENGTH_LONG).show();
                }
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    public interface WifiListener {
        /**
         * Called when the state of Wifi has changed, the state will be one of
         * the following.
         * <p/>
         * <li>{@link WifiManager#WIFI_STATE_DISABLED}</li>
         * <li>{@link WifiManager#WIFI_STATE_ENABLED}</li>
         * <li>{@link WifiManager#WIFI_STATE_DISABLING}</li>
         * <li>{@link WifiManager#WIFI_STATE_ENABLING}</li>
         * <li>{@link WifiManager#WIFI_STATE_UNKNOWN}</li>
         * <p/>
         *
         * @param state The new state of wifi.
         */
        void onWifiStateChanged(int state);

        /**
         * Called when the connection state of wifi has changed and isConnected
         * should be called to get the updated state.
         */
        void onConnectedChanged();

        /**
         * Called to indicate the list of AccessPoints has been updated and
         * getAccessPoints should be called to get the latest information.
         */
        void onAccessPointsChanged();

    }
}

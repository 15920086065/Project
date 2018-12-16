package com.android.crlibrary.utils;

import android.content.Context;
import android.os.PowerManager;

import com.android.crlibrary.logs.Logger;

/**
 * 锁住屏幕常亮
 * Created by yt on 2017/4/18.
 */
public class PowerUtils {
    static PowerUtils intance;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    public static PowerUtils getIntance() {
        return intance;
    }

    public static void init(Context context) {
        if (intance == null) {
            intance = new PowerUtils(context);
        }
    }

    PowerUtils(Context context) {
        try {
            mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "ExplorerActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LockScreen() {
        if (mWakeLock != null) {
            try {
                if (mWakeLock.isHeld() == false) {
                    mWakeLock.acquire();
                    Logger.e("------------WakeLock.LockScreen------------");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void UnLockScreen() {
        if (mWakeLock != null) {
            try {
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                    mWakeLock.setReferenceCounted(false);
                    Logger.e("------------WakeLock.UnLockScreen------------");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

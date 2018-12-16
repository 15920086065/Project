package com.android.functiontest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.control.FootManager;
import com.android.functiontest.control.HandManager;
import com.android.functiontest.control.HeadManager;

import java.util.IllegalFormatCodePointException;


/**
 * Created by yt on 2017/2/14.
 */
public class MainMotorPreFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    SwitchPreference preference1;
    SwitchPreference preference2;
    SwitchPreference preference3;
    SwitchPreference preference4;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.ui_motor_settings);
        init();
    }

    private void init() {
        preference1 = (SwitchPreference) findPreference("preference1");
        preference1.setOnPreferenceClickListener(this);
        preference1.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == preference1) {

        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Logger.i("newValue " + (Boolean) newValue);
        boolean stats = (Boolean) newValue;
        if (stats) {
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessage(0);
        } else {
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessage(-1);
        }
        return true;
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //手臂
                HandManager.getInstance().doActionArm(4);
                HandManager.getInstance().doActionArm(2);
                //头部
                HeadManager.getInstance().setHeadAction(HeadManager.ACTION_LEFT);
                //脚部
                FootManager.getInstance().doAction(FootManager.MADA_FORWARD);
                handler.sendEmptyMessageDelayed(1, 2000);
            } else if (msg.what == 1) {
                //手臂
                HandManager.getInstance().doActionArm(3);
                HandManager.getInstance().doActionArm(1);
                //头部
                HeadManager.getInstance().setHeadAction(HeadManager.ACTION_RIGHT);
                //脚部
                FootManager.getInstance().doAction(FootManager.MADA_BACKOFF);
                handler.sendEmptyMessageDelayed(0, 2000);
            } else if (msg.what == -1) {
                HandManager.getInstance().doActionArm(7);
                HandManager.getInstance().doActionArm(7);
                HeadManager.getInstance().setHeadAction(HeadManager.ACTION_CENTER);
                FootManager.getInstance().doAction(FootManager.MADA_STOP);
            }
        }
    };

}

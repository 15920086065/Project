package com.android.functiontest;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import com.android.functiontest.ui.CameraUseActivity;
import com.android.functiontest.ui.BrightnessActivityTest;
import com.android.functiontest.ui.ExplorerActivity;
import com.android.functiontest.ui.GsenserActivityTest;
import com.android.functiontest.ui.HdmiActivityTest;
import com.android.functiontest.ui.KeyActivityTest;
import com.android.functiontest.ui.LCDActivityTest;
import com.android.functiontest.ui.LampActivityTest;
import com.android.functiontest.ui.MemoryActivity;
import com.android.functiontest.ui.MotorActivityTest;
import com.android.functiontest.ui.OtgHostActivityTest;
import com.android.functiontest.ui.PsensorActivityTest;
import com.android.functiontest.ui.RecordActivityTest;
import com.android.functiontest.ui.TFActivityTest;
import com.android.functiontest.ui.TPActivityTest;
import com.android.functiontest.ui.VolumeActivityTest;
import com.android.functiontest.ui.WifiDisplayTest;

/**
 * Created by yt on 2017/2/14.
 */
public class MainPreFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{
    Preference preference1;
    Preference preference2;
    Preference preference3;
    Preference preference4;
    Preference preference5;
    Preference preference6;
    Preference preference7;
    Preference preference8;
    Preference preference9;
    //Preference preference10;
    Preference preference11;
    Preference preference12;
    Preference preference13;
    //Preference preference14;
    Preference preference15;
    Preference preference16;
    Preference preference17;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.ui_settings);
        init();
    }

    private void init(){
        preference1 = (Preference)findPreference("preference1");
        preference2 = (Preference)findPreference("preference2");
        preference3 = (Preference)findPreference("preference3");
        preference4 = (Preference)findPreference("preference4");
        preference5 = (Preference)findPreference("preference5");
        preference6 = (Preference)findPreference("preference6");
        preference7 = (Preference)findPreference("preference7");
        preference8 = (Preference)findPreference("preference8");
        preference9 = (Preference)findPreference("preference9");
        //  = (Preference)findPreference("preference10");
        preference11 = (Preference)findPreference("preference11");
        preference12 = (Preference)findPreference("preference12");
        preference13 = (Preference)findPreference("preference13");
        //preference14 = (Preference)findPreference("preference14");
        preference15 = (Preference)findPreference("preference15");
        preference16 = (Preference)findPreference("preference16");
        preference17 = (Preference)findPreference("preference17");

        preference1.setOnPreferenceClickListener(this);
        preference2.setOnPreferenceClickListener(this);
        preference3.setOnPreferenceClickListener(this);
        preference4.setOnPreferenceClickListener(this);
        preference5.setOnPreferenceClickListener(this);
        preference6.setOnPreferenceClickListener(this);
        preference7.setOnPreferenceClickListener(this);
        preference8.setOnPreferenceClickListener(this);
        preference9.setOnPreferenceClickListener(this);
        //preference10.setOnPreferenceClickListener(this);
        preference11.setOnPreferenceClickListener(this);
        preference12.setOnPreferenceClickListener(this);
        preference13.setOnPreferenceClickListener(this);
        //preference14.setOnPreferenceClickListener(this);
        preference15.setOnPreferenceClickListener(this);
        preference16.setOnPreferenceClickListener(this);
        preference17.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference==preference1){
            LCDActivityTest.openActivity(getActivity());
        }else if (preference==preference2){
            BrightnessActivityTest.openActivity(getActivity());
        }else if (preference==preference3){
            TPActivityTest.openActivity(getActivity());
        }else if (preference==preference4){
            WifiDisplayTest.openActivity(getActivity());
        }else if (preference==preference5){
            CameraUseActivity.openActivity(getActivity());
        }else if (preference==preference6){
            RecordActivityTest.openActivity(getActivity());
        }else if (preference==preference7){
            VolumeActivityTest.openActivity(getActivity());
        }else if (preference==preference8){
            KeyActivityTest.openActivity(getActivity());
        }else if (preference==preference9){
            MotorActivityTest.openActivity(getActivity());
        }/*else if (preference==preference10){
            TFActivityTest.openActivity(getActivity());
        }*/else if (preference==preference11){
            PsensorActivityTest.openActivity(getActivity());
        }else if (preference==preference12){
            HdmiActivityTest.openActivity(getActivity());
        }else if (preference==preference13){
            GsenserActivityTest.openActivity(getActivity());
        }/*else if (preference==preference14){
            OtgHostActivityTest.openActivity(getActivity());
        }*/else if (preference==preference15){
            LampActivityTest.openActivity(getActivity());
        }else if (preference==preference16){
            ExplorerActivity.openActivity(getActivity());
        }else if (preference==preference17){
            Intent intent = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
            startActivity(intent);
            //MemoryActivity.openActivity(getActivity());
        }
        return false;
    }

}

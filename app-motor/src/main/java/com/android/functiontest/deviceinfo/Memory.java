package com.android.functiontest.deviceinfo;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.android.functiontest.R;
import com.android.functiontest.utils.StorageUtils;

/**
 * 存储测试
 * Created by yt on 2017/3/24.
 */
public class Memory extends PreferenceFragment{
    private StorageVolumePreferenceCategory mCategorie ;
    private StorageVolumeRAMPreferenceCategory mRamCategorie ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.device_info_memory);
    }

    @Override
    @NonNull
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCategorie=StorageVolumePreferenceCategory.buildForPhysical(getActivity(), StorageUtils.INTERNAL_FILE);
        mRamCategorie=StorageVolumeRAMPreferenceCategory.buildForPhysical(getActivity(), StorageUtils.INTERNAL_FILE);
        getPreferenceScreen().addPreference(mCategorie);
        getPreferenceScreen().addPreference(mRamCategorie);
        mCategorie.init();
        mRamCategorie.init();
    }



}

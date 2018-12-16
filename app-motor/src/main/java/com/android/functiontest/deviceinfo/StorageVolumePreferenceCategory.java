package com.android.functiontest.deviceinfo;

import android.content.Context;
import android.preference.PreferenceCategory;

import com.android.functiontest.R;

import java.io.File;

/**
 * Created by yt on 2017/3/24.
 */
public class StorageVolumePreferenceCategory extends PreferenceCategory{
    //总容量
    private StorageItemPreference mItemTotal;
    //可用空间
    private StorageItemPreference mItemAvailable;
    private Context mContext;

    public StorageVolumePreferenceCategory(Context context, File file) {
        super(context);
        mContext = context;
        mItemTotal =  buildItem(R.string.memory_size, R.color.memory_apps_usage);
        mItemAvailable = buildItem(R.string.memory_available, R.color.memory_avail);
        setTitle(context.getText(R.string.internal_storage));
    }

    public void init(){
        addPreference(mItemTotal);
        addPreference(mItemAvailable);
        mItemTotal.setSummary(StorageMeasurement.getROMtotalMe(mContext));
        mItemAvailable.setSummary(StorageMeasurement.getROMavailMem(mContext));
    }

    private StorageItemPreference buildItem(int titleRes, int colorRes) {
        return new StorageItemPreference(getContext(), titleRes, colorRes);
    }

    /**
     * Build category to summarize specific physical {@link File}.
     */
    public static StorageVolumePreferenceCategory buildForPhysical(
            Context context,File file) {
        return new StorageVolumePreferenceCategory(context, file);
    }
}

package com.android.functiontest.deviceinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by yt on 2017/3/24.
 */
public class StorageMeasurement {
    /*显示RAM的可用和总容量，RAM相当于电脑的内存条*/
    public static String getRAMavailMem(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
       return Formatter.formatFileSize(context,mi.availMem);
    }

    /*显示RAM的可用和总容量，RAM相当于电脑的内存条*/
    public static String getRAMtotalMem(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context,mi.totalMem);
    }

    /*显示ROM的可用和总容量，ROM相当于电脑的C盘*/
    public static String getROMtotalMe(Context context){
        File file=Environment.getDataDirectory();
        StatFs statFs=new StatFs(file.getPath());
        long blockSize=statFs.getBlockSizeLong();
        long totalBlocks=statFs.getBlockCountLong();
        return Formatter.formatFileSize(context,statFs.getTotalBytes());
    }

    /*显示ROM的可用和总容量，ROM相当于电脑的C盘*/
    public static String getROMavailMem(Context context){
        File file=Environment.getDataDirectory();
        StatFs statFs=new StatFs(file.getPath());
        return Formatter.formatFileSize(context,statFs.getAvailableBytes());
    }

}

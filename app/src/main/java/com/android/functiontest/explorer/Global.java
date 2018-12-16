package com.android.functiontest.explorer;

import android.os.Environment;

/**
 * Created by yt on 2017/4/18.
 */
public class Global {
    public static String flash_dir = Environment.getExternalStorageDirectory().getPath();
    // Environment.getFlashStorageDirectory().getPath(); //"/mnt/sdcard";//
    public static String sdcard_dir = "/mnt/external_sd";// Environment.getExternalStorageDirectory().getPath();//"/mnt/external_sd";
    public static String usb_dir = "/mnt/usb_storage";// Environment.getHostStorageDirectory().getPath();//"/mnt/usb_storage";
    public static String internal_dir = "/mnt/internal_sd";
}

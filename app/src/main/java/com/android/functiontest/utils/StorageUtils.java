package com.android.functiontest.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.explorer.Global;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yt on 2017/2/16.
 */
public class StorageUtils {
    public static final File INTERNAL_FILE = Environment.getDataDirectory();
    public static final String SDCARD_DIR = "/mnt/external_sd";
    public static final String USB_DIR = "/mnt/usb_storage";

    /**
     * SD卡是否挂载
     *
     * @param context
     * @return
     */
    public static boolean isMountSDCARD(Context context) {
            try {
                StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                Method methed = StorageManager.class.getMethod("getVolumeState", String.class);
                String sdCardExist = (String) methed.invoke(storageManager, SDCARD_DIR);
                Logger.i("sdCardExist " + sdCardExist);
            return sdCardExist.equals(Environment.MEDIA_MOUNTED);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 外接USB是否挂载
     *
     * @param context
     * @return
     */
    public static boolean isMountUSB(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method methed = StorageManager.class.getMethod("getVolumeState", String.class);
            String isMountUSB = (String) methed.invoke(storageManager, USB_DIR);
            Logger.i("isMountUSB " + isMountUSB);
            return isMountUSB.equals(Environment.MEDIA_MOUNTED);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * FLASH
     *
     * @param context
     * @return
     */
    public static boolean isMountFLASH(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method methed = StorageManager.class.getMethod("getVolumeState", String.class);
            String isMountUSB = (String) methed.invoke(storageManager, Global.flash_dir);
            Logger.i("isMountUSB " + isMountUSB);
            return isMountUSB.equals(Environment.MEDIA_MOUNTED);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * INTERNAL
     *
     * @param context
     * @return
     */
    public static boolean isMountINTERNAL(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method methed = StorageManager.class.getMethod("getVolumeState", String.class);
            String isMountUSB = (String) methed.invoke(storageManager, Global.internal_dir);
            Logger.i("isMountINTERNAL " + isMountUSB);
            return isMountUSB.equals(Environment.MEDIA_MOUNTED);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

}

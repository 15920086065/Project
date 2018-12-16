package com.android.crlibrary.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yt on 2017/4/17.
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 */
public class CreateFileUtisl {
    static String TAG = "CreateFileUtisl";

    /**
     * 创建日志文件
     * @param pkg 包名
     * @param fileName 文件名
     * @return
     */
    public static File createFile(String pkg, String fileName) {
        File dirFile = createDirFile(pkg);
        if (dirFile != null) {
            return createNewFile(dirFile, fileName);
        }
        return null;
    }

    /**
     * 用来创建文件目录
     *
     * @param pkg ： 目录名
     * @return File
     */
    public static File createDirFile(String pkg) {
        if (!isMounted()) return null;
        //Android/data/pkg/xx.log
        String dirFilePath = Environment.getExternalStorageDirectory()+File.separator + "Android/data"+ File.separator+pkg;
        Log.v(TAG, "dirFilePath----" +dirFilePath);
        File dirFile = new File(dirFilePath);
        //新创建文件夹
        if (!dirFile.exists()) {
            if (dirFile.mkdirs()) {
                Log.v(TAG, pkg + " 目录创建成功");
                return dirFile;
            } else {
                Log.e(TAG, pkg + " 目录创建失败");
                return null;
            }
        }
        //文件夹存在
        return dirFile;
    }

    /**
     * 创建文件
     *
     * @param dirFile  ：目录
     * @param fileName ：文件名
     * @return File
     */
    public static File createNewFile(File dirFile, String fileName) {
        if (!isMounted() || dirFile == null) return null;
        File logFile = new File(dirFile, fileName);
        Log.v(TAG, "logFile----"+logFile.getAbsolutePath());
        //创建文件
        if (!logFile.exists()) {
            try {
                if (logFile.createNewFile()) {
                    Log.v(TAG, fileName + " 文件创建成功");
                    return logFile;
                }
                Log.e(TAG, fileName + " 文件创建失败");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, fileName + "创建文件异常:" + e.getMessage());
                return null;
            }
        }
        //文件存在
        return logFile;
    }

    /**
     * log日志文件名
     *
     * @param fileName ：文件名（不带后缀）
     * @return 文件名
     */
    public static String getLogFileName(String fileName) {
        return fileName + "-" + getDateFormat("yyyyMMdd") + ".log";
    }

    /**
     * 日期格式
     *
     * @param pattern
     * @return
     */
    public static String getDateFormat(String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Date());
    }

    /**
     * SD卡/flahs 是否可用
     *
     * @return
     */
    public static boolean isMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}

package com.android.functiontest.control;

import com.android.crlibrary.logs.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2016/3/17.
 */
public class HeadManager {

    /**
     * 左转头
     */
    public static final int ACTION_LEFT = 1;
    /**
     * 右转头
     */
    public static final int ACTION_RIGHT = 2;
    /**
     * 抬头
     */
    public static final int ACTION_UP = 3;
    /**
     * 低头
     */
    public static final int ACTION_DOWN = 4;
    /**
     * 点头
     */
    public static final int ACTION_NOD = 5;
    /**
     * 摇头
     */
    public static final int ACTION_SHAKE = 6;
    /**
     * 回归中心点
     */
    public static final int ACTION_CENTER = 7;
    /**
     * 最后一个动作
     */
    public static final int ACTION_END = ACTION_CENTER;

    private static HeadManager mInstance;

    private HeadManager() {

    }

    public static HeadManager getInstance() {
        if (mInstance == null) {
            mInstance = new HeadManager();
        }
        return mInstance;
    }

    /**
     * 设置头部动作
     *
     * @param action HeadManager.ACTION_*
     */
    public void setHeadAction(int action) {
        writeHeadFile(action);
    }

    /**
     * 写驱动文件控制头部动作
     *
     * @param i
     */
    private void writeHeadFile(int i) {
        try {
            RandomAccessFile file = new RandomAccessFile("/sys/bus/i2c/devices/0-0014/ctrl_head", "rw");
            file.write(String.valueOf(i).getBytes());
            file.close();
            Logger.e("写入成功"+i);
        } catch (FileNotFoundException e) {
           Logger.e("不支持头部动作");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

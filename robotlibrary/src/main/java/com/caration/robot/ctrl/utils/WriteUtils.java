package com.caration.robot.ctrl.utils;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WriteUtils {
    //private static final String HEAD_PATH = "/sys/bus/i2c/devices/0-0014/ctrlsport_head";
    //头部运动
    private static final String HEAD_PATH = "/sys/bus/i2c/devices/0-0014/ctrl_head";
    //5012的呼吸灯接口是dev/breath_leds，写入1呼吸，写入2常亮
    public static String DEV_BREATH_5012 = "dev/breath_leds";
    /**
     * /sys/devices/devices_control.18/devices_ctrl
     * 后面做的新软件，驱动都会加这个节点，来控制设备的呼吸灯和io口灯是否打开。
     * 对这个节点写000就是关闭，写001就是打开。   第一个0 是主设备号，第2个0是次设备号， 第3位是表示开关状态。
     */
    public static String DEVICES_CONTROL = "/dev/devices_ctrl";

    /**
     * 设置头部动作
     *
     * @param action HeadManager.ACTION_*
     *               上 3 ，下4 ，左 2 ， 右1 ，中7
     */
    public static int setHeadAction(int action) {
        return writeHeadFile(action);
    }

    /**
     * 写驱动文件控制头部动作
     *
     * @param i
     */
    private static int writeHeadFile(int i) {
        Log.e("WriteUtils", "writeHeadFile:" + i);
        try {
            RandomAccessFile file = new RandomAccessFile(HEAD_PATH, "rw");
            file.write(String.valueOf(i).getBytes());
            file.close();
            return 1;
        } catch (FileNotFoundException e) {
            Log.e("RobotManager", "不支持头部动作");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getCurrentHeadDirection() {
        String res = "";
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(HEAD_PATH, "rw");
            res = file.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            res = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return res;
    }

    public static int write5012Lamp(int i) {
        Log.e("WriteUtils", "writeHeadFile:" + i);
        try {
            RandomAccessFile file = new RandomAccessFile(DEV_BREATH_5012, "rw");
            file.write(String.valueOf(i).getBytes());
            file.close();
            return 1;
        } catch (FileNotFoundException e) {
            Log.e("RobotManager", "write5012Lamp写入失败");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 灯光控制
     *
     * @param i
     * @return
     */
    public static int writeBreathinglamp(String i) {
        Log.e("WriteUtils", "writeHeadFile:" + i);
        try {
            RandomAccessFile file = new RandomAccessFile(DEVICES_CONTROL, "rw");
            file.write(String.valueOf(i).getBytes());
            file.close();
            return 1;
        } catch (FileNotFoundException e) {
            Log.e("RobotManager", "writeBreathinglamp写入失败");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}

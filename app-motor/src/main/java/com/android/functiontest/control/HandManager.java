package com.android.functiontest.control;

import com.android.crlibrary.logs.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 手臂运动
 * 节点/sys/bus/i2c/devices/0-0016/ctrlsport_hand
 * u是最上了，d是最下 m是中间了。
 */
public class HandManager {
    //7071手臂节点
    private static final String DEV= "/sys/bus/i2c/devices/0-0016/ctrlsport_hand";
    private static HandManager instance;

    private HandManager() {
    }

    public static synchronized HandManager getInstance() {
        if (instance == null) {
            instance = new HandManager();
        }
        return instance;
    }

    /**
     * 写驱动文件控制手臂动作
     *
     * @param i
     */
    public boolean doActionArm(int i) {
        try {
            RandomAccessFile file = new RandomAccessFile(DEV, "rw");
            file.write(String.valueOf(i).getBytes());
            file.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.e("手臂节点文件不存在" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e("手臂节点文件写入失败"+e.getMessage());
        }
        return false;
    }

    /**
     * 查询手臂运行状态
     */
    public String checkHandStatus() {
        String prop = "d";// 默认值
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DEV));
            prop = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

}
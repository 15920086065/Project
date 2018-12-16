package com.android.functiontest.control;

import com.android.crlibrary.logs.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * 脚部电机
 */
public class FootManager {
    /**
     * 马达轮子
     */
    public static final String MADA_STOP = "2405F00000FF00EF";
    public static final String MADA_FORWARD = "2405F0C0C0FF80EF";
    public static final String MADA_BACKOFF = "2405F04040FF80EF";

    private static FootManager instance;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;

    private FootManager() {
        try {
            mSerialPort=getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e("IOException "+e.getMessage());
        }
    }

    public static synchronized FootManager getInstance() {
        if (instance == null) {
            instance = new FootManager();
        }
        return instance;
    }

    /**
     * 获取串口
     *
     * @return
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException
     */
    private SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            String path = "/dev/ttyMT0";
            int baudrate = 115200;

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void doAction(String data){
        writeDatas(data);
    }

    /**
     * 写串口数据
     *
     * @param data
     */
    private void writeDatas(String data) {
        Logger.e("input:" + data);
        byte[] mBuffer = new byte[data.length() / 2];
        for (int i = 0; i < data.length(); i = i + 2) {
            mBuffer[i / 2] = (byte) Integer.parseInt(data.substring(i, i + 2), 16);
        }
        try {
            if (mOutputStream==null){
                Logger.e("mOutputStream null");
                return;
            }
            mOutputStream.write(mBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e("IOException "+e.getMessage());
        }
    }

}
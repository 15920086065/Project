package com.android.functiontest.Serial;

import com.android.crlibrary.logs.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

public class SerialManager {
    private static SerialManager instance;
    private static SerialPort mSerialPort = null;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private OnDataReceiveListener mOnDataReceiveListener;

    private SerialManager(OnDataReceiveListener onDataReceiveListener) {
        mOnDataReceiveListener = onDataReceiveListener;
        try {
            mSerialPort = getSerialPort();
            mInputStream = mSerialPort.getFileInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e("IOException:" + e.getMessage());
            if (mOnDataReceiveListener != null)
                onDataReceiveListener.onError("IOException:" + e.getMessage());
        } catch (SecurityException e) {
            e.printStackTrace();
            Logger.e("SecurityException:" + e.getMessage());
            if (mOnDataReceiveListener != null)
                onDataReceiveListener.onError("SecurityException:" + e.getMessage());
        }
    }

    public static synchronized SerialManager getInstance(OnDataReceiveListener onDataReceiveListener) {
        if (instance == null) {
            instance = new SerialManager(onDataReceiveListener);
        }
        return instance;
    }

    public interface OnDataReceiveListener {
        void onDataReceived(byte[] buffer, int size);

        void onError(String s);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener onDataReceiveListener) {

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
            File dFile = new File(path);
            if (dFile.exists()) {
                mSerialPort = new SerialPort(new File(path), baudrate, 0);
            } else {
                throw new SecurityException("/dev/ttyMT0 is null");
            }
        }
        return mSerialPort;
    }

    /**
     * 读串口数据线程
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[5];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (mOnDataReceiveListener != null && size > 0) {
                        mOnDataReceiveListener.onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }

        instance = null;
        Logger.e("closeSerialPort");
    }
}
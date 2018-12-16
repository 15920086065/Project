package com.caration.robot.ctrl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.caration.robot.ctrl.utils.SerialUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;


/**
 * 机器人控制管理
 * <p/>
 * Created by Administrator on 2016/7/4.
 */
public class RobotManager {
    private static RobotManager mInstance;
    private static Context mContext;
    private SerialPort mSerialPort;

    /**
     * 停止行走
     */
    public static final int ROBOT_WHEEL_STOP = 0;
    /**
     * 一直前行
     */
    public static final int ROBOT_WHEEL_FRONT_ALWAY = 1;
    /**
     * 一直后退
     */
    public static final int ROBOT_WHEEL_BACK_ALWAY = 2;
    /**
     * 一直左转
     */
    public static final int ROBOT_WHEEL_LEFT_ALWAY = 3;
    /**
     * 一直右转
     */
    public static final int ROBOT_WHEEL_RIGHT_ALWAY = 4;


    /**
     * 初始化管理对象
     *
     * @param context
     * @return
     */
    public void init(Context context) {
        mContext = context;
    }
    /**
     * 获取控制管理对象
     *
     * @return
     */
    public static RobotManager getInstance() {
        if (mInstance == null) {
            mInstance = new RobotManager();
        }
        return mInstance;
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
        Log.i("RobotManager", "getSerialPort");
//        if (mSerialPort == null) {
        String path = "/dev/ttyMT0";
        int baudrate = 115200;

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
//        }
        return mSerialPort;
    }

    /**
     * 写串口数据
     *
     * @param mOutputStream
     * @param data
     */
    private void writeDatas(OutputStream mOutputStream, String data) {
        Log.e("RobotManager", "input:" + data);
        if (!com.caration.encryption.CREncryption.isEncryption()) {
            Toast.makeText(mContext, "该应用未授权，无法使用控制功能", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] mBuffer = new byte[data.length() / 2];

        for (int i = 0; i < data.length(); i = i + 2) {
            mBuffer[i / 2] = (byte) Integer.parseInt(data.substring(i, i + 2), 16);
        }
        try {
            mOutputStream.write(mBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动轮子运动命令
     *
     * @param action
     */
    public void robotWheelRun(int action) {
        Log.i("RobotManager","robotWheelRun");
        SerialPort serialPort = null;
        try {
            serialPort = getSerialPort();
            if (serialPort == null)
                return;
            switch (action) {
                case ROBOT_WHEEL_FRONT_ALWAY:
                    writeDatas(serialPort.getOutputStream(), SerialUtils.getMadaInputCommand(100, 100, 0));
                    break;
                case ROBOT_WHEEL_BACK_ALWAY:
                    writeDatas(serialPort.getOutputStream(), SerialUtils.getMadaInputCommand(-100, -100, 0));
                    break;
                case ROBOT_WHEEL_LEFT_ALWAY:
                    writeDatas(serialPort.getOutputStream(), SerialUtils.getMadaInputCommand(0, 100, 0));
                    break;
                case ROBOT_WHEEL_RIGHT_ALWAY:
                    writeDatas(serialPort.getOutputStream(), SerialUtils.getMadaInputCommand(100, 0, 0));
                    break;
                case ROBOT_WHEEL_STOP:
                default:
                    writeDatas(serialPort.getOutputStream(), SerialUtils.getMadaInputCommand(0, 0, 0));
                    break;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        } finally {
            if (serialPort != null) {
                serialPort.close();
                serialPort = null;
                mSerialPort = null;
            }
        }
    }
}

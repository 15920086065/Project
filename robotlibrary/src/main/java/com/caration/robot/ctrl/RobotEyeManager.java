/**
 * @author xuwk@caration.cn
 * @data 2016年12月23日
 */
package com.caration.robot.ctrl;

import android.content.Context;
import android.widget.Toast;

import com.caration.robot.ctrl.utils.SerialUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * @author xuwk@caration.cn
 *
 */
public class RobotEyeManager {
	private static RobotEyeManager mInstance;
	private static Context mContext;
	private SerialPort mSerialPort;

	public void init(Context context) {
		mContext = context;
	}

	/**
	 * <b>获取表情控制管理对象</b>
	 *
	 * @return
	 */
	public static RobotEyeManager getInstance() {
		if (mInstance == null) {
			mInstance = new RobotEyeManager();
		}
		return mInstance;
	}

	public void runEyeAnimation(String str) {
		writeDatas(SerialUtils.getFaceDataFromString(str));
	}

	/**
	 * <b>获取串口</b>
	 *
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 * @throws InvalidParameterException
	 */
	private SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		// if (mSerialPort == null) {
		String path = "/dev/ttyMT0";
		int baudrate = 115200;

		/* Check parameters */
		if ((path.length() == 0) || (baudrate == -1)) {
			throw new InvalidParameterException();
		}

		/* Open the serial port */
		mSerialPort = new SerialPort(new File(path), baudrate, 0);
		// }
		return mSerialPort;
	}

	/**
	 * <b>写串口数据</b>
	 *
	 * @param data
	 */
	private void writeDatas(String data) {
		if (!com.caration.encryption.CREncryption.isEncryption()) {
			Toast.makeText(mContext, "该应用未授权，无法使用控制功能", Toast.LENGTH_SHORT).show();
			return;
		}
		SerialPort serialPort = null;
		try {
			serialPort = getSerialPort();
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (serialPort == null)
			return;
		OutputStream mOutputStream = serialPort.getOutputStream();
		byte[] mBuffer = new byte[data.length() / 2];

		for (int i = 0; i < data.length(); i = i + 2) {
			mBuffer[i / 2] = (byte) Integer.parseInt(data.substring(i, i + 2), 16);
		}
		try {
			mOutputStream.write(mBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mOutputStream != null) {
				try {
					mOutputStream.close();
					mOutputStream = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (serialPort != null) {
				serialPort.close();
				serialPort = null;
			}
		}
	}
}

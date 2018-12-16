package com.android.functiontest.Serial;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceGroup;
import android.support.annotation.Nullable;

import com.android.crlibrary.logs.Logger;

/**
 * 串口读写服务
 * <p/>
 * Created by yt on 2017/10/31.
 */
public class SerialServer extends Service implements SerialManager.OnDataReceiveListener {
    private SerialManager mSerialManager;
    private int mSize = 0;
    private byte[] mBuffer;
    private CallBack mCallback;

    public interface CallBack {
        /**
         * @param code  1前方悬空，2后方悬空，3底部悬空，0正常
         * @param state 7右后方悬空，11前方悬空，13左后方悬空，14左前方悬空，15整除
         */
        void onChange(int code, int state);

        void onError(String errorStr);
    }

    public class MYIBinder extends Binder {
        public SerialServer getServer() {
            return SerialServer.this;
        }
    }

    public void setCallback(CallBack mCallback) {
        this.mCallback = mCallback;
    }

    public void startSerial(){
        Logger.i("startSerial");
        mSerialManager = SerialManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MYIBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        Logger.d("mSize:" + mSize + "; size:" + size);
        Logger.d("buffer:" + bytesToHexString(buffer));
        if (size == 5) {
            mSize = 0;
        }
        if (mSize == 0) {
            mBuffer = new byte[5];
        }
        if (mSize + size >= 5) {
            for (int i = 0; i < size && (i + mSize) < 5; i++) {
                mBuffer[i + mSize] = buffer[i];
            }
            mSize = 0;
            String bytesToHexString = bytesToHexString(mBuffer);
            int state = getState(bytesToHexString);
            int code = getCodex(bytesToHexString);
            Logger.e("code = " + code + "  state = " + state);
            if (mCallback!=null){
                mCallback.onChange(code,state);
            }else {
                Logger.e("mCallback is null");
            }
        } else {
            for (int i = 0; i < size; i++) {
                mBuffer[i + mSize] = buffer[i];
            }
            mSize = size;
        }
    }

    @Override
    public void onError(String errorStr) {
        if (mCallback!=null){
            mCallback.onError(errorStr);
        }
    }

    public int getState(String bytesToHexString) {
        int state = 15;
        if (bytesToHexString == null || bytesToHexString.length() != 10 || !bytesToHexString.startsWith("2402")) {
            Logger.e("非法串口返回值!");
            return state;
        }
        String code = bytesToHexString.substring(4, 6);
        String stateStr = bytesToHexString.substring(6, 8);
        Logger.i("stateStr:" + stateStr);
        if ("E0".equals(code)) {
            return Integer.parseInt(stateStr, 16);
        }
        return state;
    }

    public int getCodex(String bytesToHexString) {
        int index = 0;
        if (bytesToHexString == null || bytesToHexString.length() != 10 || !bytesToHexString.startsWith("2402")) {
            Logger.e("非法串口返回值!");
            return index;
        }

        Logger.i("串口输出:" + bytesToHexString);
        String code = bytesToHexString.substring(4, 6);
        String state = bytesToHexString.substring(6, 8);
        Logger.i("code:" + code + "  state:" + state);
        if ("E0".equals(code)) {
            int stat = Integer.parseInt(state, 16);
            if ((stat & 8) == 0 || (stat & 2) == 0) {
                index += 2;
            }
            if ((stat & 4) == 0 || (stat & 1) == 0) {
                index += 1;
            }
        }
        return index;
    }

    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray
     * @return
     */
    private String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("onDestroy");
        mSerialManager.closeSerialPort();
        mSerialManager.setOnDataReceiveListener(null);
        mSerialManager =null;
    }
}

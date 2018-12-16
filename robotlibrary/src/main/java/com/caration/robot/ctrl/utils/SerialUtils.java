package com.caration.robot.ctrl.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/4/22.
 */
public class SerialUtils {
    /**
     * 计算自定义的马达指令
     *
     * @param leftSpeed  左轮速度(大于等于-100，小于等于100)
     * @param rightSpeed 右轮速度(大于等于-100，小于等于100)
     * @param duration   持续时间(毫秒) （小于等于0表示永远）
     * @return
     */
    public static String getMadaInputCommand(int leftSpeed, int rightSpeed, long duration) {
        String res = null;
        leftSpeed = -leftSpeed;
        if (leftSpeed < -100 || leftSpeed > 100) {
            Log.e("", "左轮速度值非法");
            leftSpeed = 0;
        }

        if (rightSpeed < -100 || rightSpeed > 100) {
            Log.e("", "右轮速度值非法");
            rightSpeed = 0;
        }
        int time = (int) duration / 50;
        if (duration <= 0)
            time = 255;

        StringBuffer sb = new StringBuffer("2405F0");
        int sum = Integer.valueOf(Integer.toHexString(time), 16);
        String left = getMadaHex(leftSpeed);
        String right = getMadaHex(rightSpeed);
        sum += Integer.valueOf(left, 16);
        sum += Integer.valueOf(right, 16);
        sb.append(left);
        sb.append(right);
        sb.append(Integer.toHexString(time));
        int check = Integer.valueOf("3FF", 16) - sum;
        String chStr = Integer.toHexString(check);
        sb.append(chStr.substring(chStr.length() - 2, chStr.length()));
        sb.append("EF");
        res = sb.toString();
        return res.toUpperCase();
    }

    private static String getMadaHex(int speed) {
        if (speed < 0) {
            speed = Math.abs(speed) | 128;
        }
        String speedStr = Integer.toHexString(speed);
        if (speedStr.length() == 1) {
            speedStr = "0" + speedStr;
        }
        return speedStr;

    }



    /**
     *
     * @param face
     *            "00111100,01000010,00000000,00111100,00111100,00111100,00111100,0011110000,00111100,01000010,00000000,00111100,00111100,00111100,00111100,0011110000,00011000,00011000,0000000000,00000000,00000000,10000001,01000010,00111100";
     * @return
     */
    public static String getFaceDataFromString(String face) {
        String[] sa = face.split(",");
        StringBuffer sbf = new StringBuffer("2419f1");
        int sum = Integer.valueOf("f1", 16);
        for (int i = 0; i < sa.length; i++) {
            String s = sa[i];
            sbf.append(binaryString2hexString(s.toString()));
            sum += Integer.valueOf(s.toString(), 2);
        }
        String sumStr = Integer.toHexString(sum);
        sbf.append(sumStr.substring(sumStr.length() - 2, sumStr.length()));
        return sbf.toString().toUpperCase();
    }

    private static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }
}

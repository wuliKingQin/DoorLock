package cn.saiyi.doorlock.util;

import java.io.ByteArrayOutputStream;

/**
 * 描述：解码工具类，该类主要将进制转换回来
 * 创建作者：黎丝军
 * 创建时间：2016/10/26 16:03
 */

public class DecodeUtil {

    //十六进制字符串
    private final static String HEX_STRING = "0123456789ABCDEF";

    /**
     * 将16进制数组解码成字符串,适用于所有字符（包括中文）
     * @param bytes 16进制字节数组
     * @return 对应的字符串
     */
    public static String hexByteToStr(byte[] bytes) {
        return hexStrToStr(bytesToHexStr(bytes));
    }

    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     * @param byteStr 将十六进制字符串
     * @return 返回对应的字符串
     */
    public static String hexStrToStr(String byteStr) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(byteStr.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < byteStr.length(); i += 2) {
            byteStream.write((HEX_STRING.indexOf(byteStr.charAt(i)) << 4 |
                    HEX_STRING.indexOf(byteStr.charAt(i + 1))));
        }
        return byteStream.toString();
    }

    /**
     * 将字节数组转换成16进制字符串
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String bytesToHexStr(byte[] bytes){
        int temp;
        String valueTemp;
        final StringBuilder strBuilder = new StringBuilder();
        for(byte value:bytes) {
            temp = value & 0xFF;
            valueTemp = Integer.toHexString(temp);
            if(valueTemp.length() < 2) {
                strBuilder.append(0);
            }
            strBuilder.append(valueTemp);
        }
        return  strBuilder.toString();
    }

    /**
     * 将字节数组转换成16进制字符串
     * @param data 字节数据
     * @return 16进制字符串
     */
    public static String byteToHexStr(byte data) {
        return bytesToHexStr(new byte[]{data});
    }
}

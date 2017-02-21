package cn.saiyi.doorlock.util;

/**
 * 描述：编码工具，该类主要用在进制转换上
 * 创建作者：黎丝军
 * 创建时间：2016/10/26 16:02
 */

public class EncodeUtil {

    //十六进制字符串
    private final static String HEX_STRING = "0123456789ABCDEF";

    /**
     * 16进制字符串转换成16进制字节数组
     * @param hexStr 16进制字符串
     * @return 返回16进制字节
     */
    public  static byte[] hexStrToBytes(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return null;
        }
        hexStr = hexStr.toUpperCase().replace(" ", "");
        final char[] hexChars = hexStr.toCharArray();
        final byte[] byteData = new byte[hexStr.length() / 2];
        for (int i = 0; i < byteData.length; i++) {
            int pos = i * 2;
            byteData[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return byteData;
    }

    /**
     * 将字符串转换成16进制byte[]
     * @param str 字符串
     * @return 返回16进制字节
     */
    public static byte[] strToHexBytes(String str) {
        return hexStrToBytes(strToHexStr(str));
    }

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     * @param str 需要编码的字符串
     * @return 转换成的16进制的字符串
     */
    public static String strToHexStr(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(HEX_STRING.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(HEX_STRING.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 将字符串按位置转成对应位数的字节
     * @param str 字符串
     * @return byte[]
     */
    public static byte[] strToBytes(String str) {
        int index = 0;
        final char[] dataChars = str.toCharArray();
        final byte[] dataBytes = new byte[dataChars.length];
        for (; index < dataBytes.length; index++) {
            dataBytes[index] = charToByte(dataChars[index]);
        }
        return dataBytes;
    }

    /**
     *  将当个字符串转换成16进制字节
     * @param c 需要转换的字符
     * @return 转换后的字节
     */
    private static byte charToByte(char c) {
        return (byte) HEX_STRING.indexOf(c);
    }
}

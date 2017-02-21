package com.saiyi.framework.util;

import java.util.regex.Pattern;

/**
 * 描述：手机号处理工具类
 * 创建作者：黎丝军
 * 创建时间：2016/11/1 14:53
 */

public class PhoneUtils {
    //用于判断是否是手机号
    private final static Pattern mPhoneNumber = Pattern.compile("^((13[0-9])|(14[5,7])|17[0,6,7,8]|(15[^4,\\D])|(18[0-9]))\\d{8}$");

    /**
     * 判断是否是手机号
     * @param phoneNum 手机号
     * @return true表示是，false表示不是
     */
    public static boolean isPhone(String phoneNum) {
        if (phoneNum == null || phoneNum.trim().length() == 0)
            return false;
        return mPhoneNumber.matcher(phoneNum).matches();
    }
}

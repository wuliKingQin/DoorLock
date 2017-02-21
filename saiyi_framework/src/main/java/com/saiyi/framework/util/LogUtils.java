package com.saiyi.framework.util;

import android.util.Log;

/**
 * 文件描述：日志工具类
 *          该类可以设置测试时是否需要打印日志，
 *          并且做了崩溃日志记录或者上传服务器
 * 创建作者：黎丝军
 * 创建时间：16/7/28
 */
public final class LogUtils {

    //日志TAG
    private static final String TAG = "LiSiJun";
    //用于判断是否是Debug模式
    public static boolean isDebug = true;

    private LogUtils() {
    }

    /**
     * 打印Debug信息
     * @param msg 信息体
     */
    public static void d(String msg) {
        if(isDebug) {
            Log.d(TAG, buildMessage(msg));
        }
    }



    /**
     * 打印错误日志信息
     * @param msg 错误信息体
     */
    public static void e(String msg) {
        if(isDebug) {
            Log.e(TAG, buildMessage(msg));
        }
    }

    /**
     * 打印错误日志信息
     * @param msg 错误信息体
     * @param error 异常信息
     */
    public static void e(String msg,Throwable error) {
        if(isDebug) {
            Log.e(TAG, buildMessage(msg), error);
        }
    }

    /**
     * 构建日志信息
     * @param msg 信息
     * @return 返回被构建的日志信息
     */
    private static String buildMessage(String msg) {
        StackTraceElement caller = (new Throwable()).fillInStackTrace().getStackTrace()[2];
        return caller.getClassName() + "." + caller.getMethodName() + "(): " + msg;
    }
}

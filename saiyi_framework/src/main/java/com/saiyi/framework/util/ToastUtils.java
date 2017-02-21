package com.saiyi.framework.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 文件描述：弹出信息工具类
 * 创建作者：黎丝军
 * 创建时间：16/7/28
 */
public class ToastUtils {

    //Toast
    private static Toast mToast;

    /**
     * c
     * @param context 运行环境
     * @param msg 消息
     */
    public static void toast(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * toast
     * @param context 运行环境
     * @param resId 消息
     */
    public static void toast(Context context,int resId) {
        toast(context,context.getString(resId));
    }

    /**
     * 显示提示信息
     * @param gravity 方位
     * @param context 运行环境
     * @param msg 消息
     */
    public static void toast(int gravity, Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.setGravity(gravity, 0, 0);
        mToast.show();
    }

    /**
     * 显示提示信息
     * @param gravity 方位
     * @param context 运行环境
     * @param resId 消息
     */
    public static void toast(int gravity, Context context, int resId) {
        toast(gravity,context,context.getString(resId));
    }
}

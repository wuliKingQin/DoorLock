package com.lisijun.gesture.util;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * 描述：屏幕工具类
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 9:28
 */

public class ScreenUtil {

    /**
     * 获取屏幕分辨率
     * @param context 运行环境
     * @return 返回宽高数组
     */
    public static int[] getScreenDisplay(Context context) {
        return new int[]{getScreenWidth(context),getScreenHeight(context) };
    }

    /**
     * 屏幕宽
     * @param context 运行环境
     * @return 返回屏幕宽
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高
     * @param context 运行环境
     * @return 屏幕高
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
    }

    /**
     * 获取Display实例
     * @param context 运行环境
     * @return Display实例
     */
    public static Display getDisplay(Context context) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay();
    }
}

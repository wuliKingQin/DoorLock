package com.saiyi.framework.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * 文件描述：单位转换工具类
 * 创建作者：黎丝军
 * 创建时间：16/8/16
 */
public class DensityUtils {

    private DensityUtils(){}

    /**
     * dp转换为px
     * @param context 上下文
     * @param dp      单位为dp的值
     * @return context为null，返回-1，否则返回计算后的值
     */
    public static int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转换为dp
     * @param context 上下文
     * @param px      单位为px的值
     * @return context为null，返回-1，否则返回计算后的值
     */
    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue px值
     * @return context为null，返回-1，否则返回计算后的值
     */
    public static float pxToSp(Context context, float pxValue) {
        if (context == null) {
            return -1;
        }

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue sp值
     * @return context为null，返回-1，否则返回计算后的值
     */
    public static float spToPx(Context context, float spValue) {
        if (context == null) {
            return -1;
        }

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕信息
     * @param context Activity实例
     * @return DisplayMetrics对象
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 获取屏幕宽度
     * @param context Activity实例
     * @return 屏幕宽度
     */
    public static float getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context Activity实例
     * @return 屏幕高度
     */
    public static float getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        final Rect frame = new Rect();
        ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

}

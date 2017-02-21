package com.saiyi.framework.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 文件描述：通知工具类
 * 创建作者：黎丝军
 * 创建时间：16/8/16
 */
public class NotificationUtils {

    //通知管理器
    private static NotificationManager mNotificationMgr;
    //通知构造器
    private static Notification.Builder mBuilder;
    //通知
    private static Notification mNotification;

    private NotificationUtils() {
    }

    /**
     * 发送一个普通的通知
     * @param context 运行环境
     * @param title 标题
     * @param content 内容
     */
    public static void sendNotify(Context context,String title,String content,int iconId) {
        sendNotify(context,title,content,null,iconId);
    }

    /**
     * 发送一个普通的通知
     * @param context 运行环境
     * @param title 标题
     * @param content 内容
     */
    public static void sendNotify(Context context,String title,String content,Intent targetIntent,int iconId) {
        mNotificationMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE);
        if(iconId != 0) {
            mBuilder.setSmallIcon(iconId);
        }
        if(targetIntent != null) {
            mBuilder.setContentIntent(PendingIntent.getActivity(context,0,targetIntent,0));
        }
        mNotification = mBuilder.getNotification();
        mNotificationMgr.notify(1,mNotification);
    }

    /**
     * 发送一个普通的通知
     * @param context 运行环境
     * @param titleResId 标题
     * @param contentRedId 内容
     */
    public static void sendNotify(Context context, int titleResId,int contentRedId,int iconId) {
        sendNotify(context,context.getString(titleResId),context.getString(contentRedId),null,iconId);
    }

    /**
     * 发送一个普通的通知
     * @param context 运行环境
     * @param titleResId 标题
     * @param contentRedId 内容
     */
    public static void sendNotify(Context context, int titleResId,int contentRedId) {
        sendNotify(context,context.getString(titleResId),context.getString(contentRedId),null,0);
    }
}

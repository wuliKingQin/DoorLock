package cn.saiyi.doorlock.other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import cn.saiyi.doorlock.util.TimeUtil;

/**
 * 描述：文件管理器，该管理器主要实现app更新检测保存的文件和一些错误日志，或者图片之类
 * 创建作者：黎丝军 该单列属于饿汉式属于线程安全
 * 创建时间：2016/10/25 10:53
 */
public class FileMgr {

    //图片保存文件
    private File imgSavePath;
    //apk更新下载文件
    private File apkSavePath;
    //log日志保存文件
    private File logSavePath;
    //app更新路径
    public static String SDCARD_PATH;
    //log日志路径
    public static String APK_LOG_PATH;
    //图片保存路径
    public static String IMG_SAVE_PATH;
    //apk安装路径
    public static String APK_INSTALL_PATH;
    //实例
    private static FileMgr ourInstance = new FileMgr();
    //获取实例
    public static FileMgr instance() {
        return ourInstance;
    }

    private FileMgr() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SDCARD_PATH = Environment.getExternalStorageDirectory() + "/";
            IMG_SAVE_PATH = SDCARD_PATH + "**/images/";
            APK_INSTALL_PATH = SDCARD_PATH + "**/app/";
            APK_LOG_PATH = SDCARD_PATH + "**/log/";
            // 图片保存、缓存地址
            imgSavePath = new File(IMG_SAVE_PATH);
            if (!imgSavePath.exists()) {
                imgSavePath.mkdirs();
            }
            // 检测更新时保存路径
            apkSavePath = new File(APK_INSTALL_PATH);
            if (!apkSavePath.exists()) {
                apkSavePath.mkdirs();
            }
            // 异常保存路径
            logSavePath = new File(APK_LOG_PATH);
            if (!logSavePath.exists()) {
                logSavePath.mkdirs();
            }
        } else {
        }
    }

    public File getImgPath() {
        return imgSavePath;
    }

    public File getApkPath() {
        return apkSavePath;
    }

    public File getLogPath() {
        return logSavePath;
    }

    /**
     * 获取apk文件名
     * @return apk文件名
     */
    public String getApkFileName() {
        return "app_" + TimeUtil.getCurrentAllTime() + ".apk";
    }

    /**
     * 安装apk
     * @param context 运行环境
     * @param apkFile 需要安装的apk文件
     */
    public void installApk(Context context,File apkFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}

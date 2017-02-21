package cn.saiyi.doorlock.util;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.lisijun.websocket.interfaces.ISocketClient;
import com.saiyi.framework.util.PreferencesUtils;

/**
 * 描述：该工具类主要是获取app版本信息
 * 创建作者：黎丝军
 * 创建时间：2016/10/25 10:25
 */

public class AppUtil {

    //保存门锁状态
    private static String mLockState = "未开";
    //包管理
    private static PackageManager mPackageMgr;
    //包信息
    private static PackageInfo mPackageInfo;

    /**
     * 获取版本号
     * @param context 运行环境
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        try {
            mPackageMgr = context.getPackageManager();
            if(mPackageMgr != null) {
                mPackageInfo = mPackageMgr.getPackageInfo(context.getPackageName(), 0);
                if(mPackageInfo != null) {
                    return mPackageInfo.versionCode;
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 获取版本名
     * @param context 运行环境
     * @return 版本名
     */
    public static String getVersionName(Context context) {
        try {
            mPackageMgr = context.getPackageManager();
            if(mPackageMgr != null) {
                mPackageInfo = mPackageMgr.getPackageInfo(context.getPackageName(), 0);
                if(mPackageInfo != null) {
                    return mPackageInfo.versionName;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取包信息实例
     * @param context 运行环境
     * @return 包信息实例
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            mPackageMgr = context.getPackageManager();
            if(mPackageMgr != null) {
                mPackageInfo = mPackageMgr.getPackageInfo(context.getPackageName(), 0);
                return mPackageInfo;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 广播过滤器
     * @return IntentFilter 实例
     */
    public static IntentFilter makeMsgFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ISocketClient.ACTION_SOCKET_MSG);
        return intentFilter;
    }

    /**
     * 设置门锁状态值
     * @param state 状态值
     */
    public static void setLockState(String state) {
        mLockState = state;
    }

    /**
     * 获取门锁状态值
     * @return 状态值
     */
    public static String getLockState() {
        return mLockState;
    }
}

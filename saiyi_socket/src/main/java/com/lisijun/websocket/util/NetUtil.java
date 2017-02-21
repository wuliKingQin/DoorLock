package com.lisijun.websocket.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * 描述：网络工具
 * 创建作者：黎丝军
 * 创建时间：2016/10/18 10:33
 */

public class NetUtil {

    /**
     * 获取wifi名
     * @param context 运行环境
     * @return 返回当前连接wifi名
     */
    public static String getSSID(Context context) {
        final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiMgr != null) {
            final WifiInfo info = wifiMgr.getConnectionInfo();
            String wifiName = info != null ? info.getSSID() : null;
            if (wifiName != null && Build.VERSION.SDK_INT >= 17 && wifiName.startsWith("\"") && wifiName.endsWith("\""))
                wifiName = wifiName.replaceAll("^\"|\"$", "");
            return wifiName;
        }
        return "";
    }

    /**
     * 获取网络连接类型
     * @param context 运行环境
     * @return none、wifi和gprs
     */
    public static String getNetConnType(Context context) {
        final ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null == connManager) {
            return "none";
        } else {
            NetworkInfo info = null;
            info = connManager.getActiveNetworkInfo();
            NetworkInfo.State mobileState;
            if(null != info) {
                mobileState = info.getState();
                if(NetworkInfo.State.CONNECTED == mobileState) {
                    return "wifi";
                }
            }
            info = connManager.getNetworkInfo(0);
            if(null != info) {
                mobileState = info.getState();
                if(NetworkInfo.State.CONNECTED == mobileState) {
                    return "gprs";
                }
            }
            return "none";
        }
    }

    /**
     * 判断网络是否能用
     * @param ctx 运行环境
     * @return false表示不能用，true表示能用
     */
    public static boolean networkUsable(Context ctx) {
        String connType = getNetConnType(ctx);
        return !connType.equals("none");
    }

    /**
     * 判断wifi是否连接
     * @param context 运行环境
     * @return true表示连接，false表示未连接
     */
    public static boolean isWifiConnected(Context context) {
        if(context != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}

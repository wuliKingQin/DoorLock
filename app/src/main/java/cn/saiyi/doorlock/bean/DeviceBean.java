package cn.saiyi.doorlock.bean;

import android.text.TextUtils;

import com.saiyi.framework.bean.BaseBean;
import com.saiyi.framework.util.PreferencesUtils;

import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：设备bean
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 14:37
 */

public class DeviceBean extends BaseBean {

    //设备图标地址
    private String iconUrl;
    //设备昵称
    private String name;
    //是否在线
    private boolean isOnLine;
    //mac地址
    private String wifiMac;
    //状态值
    private String state;
    //保存蓝牙地址
    private String bleAddress;
    //出点
    private String adminPass;
    //是否是被分享的设备
    private boolean isShared = false;
    //无人模式是否打开
    private boolean isUnmanned = false;

    public DeviceBean() {
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String nickName) {
        this.name = nickName;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public String getAdminPass() {
        return adminPass;
    }

    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public boolean isUnmanned() {
        return isUnmanned;
    }

    public void setUnmanned(int unmanned) {
        isUnmanned = unmanned == 0 ? false:true;
    }

    /**
     * 设置一次性密码
     * @param onePass 一次性密码
     * @param date 时间
     */
    public void setOnePass(String onePass,String date) {
        PreferencesUtils.putString(wifiMac,onePass);
        PreferencesUtils.putString(wifiMac + Constant.TIME_KEY,date);
    }
}

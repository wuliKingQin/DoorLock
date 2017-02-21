package cn.saiyi.doorlock.util;

import com.lisijun.gesture.interfaces.GConstant;
import com.saiyi.framework.util.PreferencesUtils;

import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：登录工具类
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 9:55
 */

public class LoginUtil {
    //登录状态
    private final static String LOGIN_STATE = "loginState";
    //登录账户
    private final static String LOGIN_ACCOUNT = "loginAccount";
    //登录密码
    private final static String LOGIN_PASSWORD = "loginPassword";
    //用户头像
    private final static String USER_HEAD_URL = "userHeadUrl";
    //用户姓名
    private final static String USER_NAME = "userName";
    //用户地址
    private final static String USER_ADDRESS = "userAddress";
    /**
     * 设置登录状态
     * @param state 是否登录
     * @param account 账户
     * @param password 密码
     */
    public static void setLoginState(boolean state,String account,String password) {
        if(!state) {
            PreferencesUtils.remove(LOGIN_STATE);
            PreferencesUtils.remove(LOGIN_ACCOUNT);
            PreferencesUtils.remove(LOGIN_PASSWORD);
        } else {
            PreferencesUtils.putBoolean(LOGIN_STATE,state);
            PreferencesUtils.putString(LOGIN_ACCOUNT,account);
            PreferencesUtils.putString(LOGIN_PASSWORD,password);
        }
    }

    /**
     * 设置用户头像url
     */
    public static void setHeadUrl(String headUrl) {
        PreferencesUtils.putString(USER_HEAD_URL,headUrl);
    }

    /**
     * 获取用户头像url
     * @return url
     */
    public static String getHeadUrl() {
        return PreferencesUtils.getString(USER_HEAD_URL,"ic_head_1");
    }

    /**
     * 设置用户姓名
     * @param name 姓名
     */
    public static void setName(String name) {
        PreferencesUtils.putString(USER_NAME,name);
    }

    /**
     * 获取用户姓名
     * @return 姓名
     */
    public static String getName() {
        return PreferencesUtils.getString(USER_NAME, getAccount());
    }

    /**
     * 设置用户地址
     * @param address 地址
     */
    public static void setAddress(String address) {
        PreferencesUtils.putString(USER_ADDRESS,address);
    }

    /**
     * 获得用户地址
     * @return 地址
     */
    public static String getAddress() {
        return PreferencesUtils.getString(USER_ADDRESS,"未设置");
    }

    /**
     * 判断用户是否登录
     * @return true表示已经登录，否则表示没有登录
     */
    public static boolean isLogin() {
        return PreferencesUtils.getBoolean(LOGIN_STATE);
    }

    /**
     * 设置账户信息
     * @param account 账户
     */
    public static void setAccount(String account) {
        PreferencesUtils.putString(LOGIN_ACCOUNT,account);
    }

    /**
     * 获得登录账户
     * @return 登录账户信息，没有登录返回null
     */
    public static String getAccount() {
        return PreferencesUtils.getString(LOGIN_ACCOUNT);
    }

    /**
     * 获取登录密码
     * @return 登录密码信息，没有登录返回null
     */
    public static String getPassword() {
        return PreferencesUtils.getString(LOGIN_PASSWORD);
    }

    /**
     * 清除登录信息，在退出登录的时候被使用
     */
    public static void clearLoginInfo() {
        PreferencesUtils.remove(LOGIN_STATE);
        PreferencesUtils.remove(LOGIN_ACCOUNT);
        PreferencesUtils.remove(LOGIN_PASSWORD);
        PreferencesUtils.remove(USER_NAME);
        PreferencesUtils.remove(USER_ADDRESS);
        PreferencesUtils.remove(USER_HEAD_URL);
        PreferencesUtils.remove(GConstant.GESTURE_PASS);
        PreferencesUtils.remove(Constant.IS_FINGERPRINT_OPEN_LOCK);
    }
}

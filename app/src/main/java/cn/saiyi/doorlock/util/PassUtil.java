package cn.saiyi.doorlock.util;

import android.text.TextUtils;

import com.lisijun.gesture.interfaces.GConstant;
import com.saiyi.framework.util.PreferencesUtils;

import java.util.Random;

import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：密码生成工具类
 * 创建作者：黎丝军
 * 创建时间：2016/10/15 15:14
 */

public class PassUtil {

    /**
     * 密码生成方法
     * @param count 密码位数
     * @return 返回密码
     */
    public static String createPassword(int count) {
        int p = 0;
        final Random random = new Random();
        final StringBuilder pass = new StringBuilder(count);
        for(;p < count;p++) {
            pass.append(random.nextInt(9));
        }
        return pass.toString();
    }

    /**
     * 产生一个八位位数随机密码
     * @return 密码
     */
    public static String createPassword() {
        return createPassword(8);
    }

    /**
     * 判断是否用指纹开锁
     * @return true表示是，false表示不是
     */
    public static boolean isFingerOpenLock() {
        return PreferencesUtils.getBoolean(Constant.IS_FINGERPRINT_OPEN_LOCK);
    }

    /**
     * 是否需要手势密码解锁
     * @return true表示需要，false表示不需要
     */
    public static boolean isGestureOpenLock() {
        return PreferencesUtils.getBoolean(GConstant.IS_GESTURE_OPEN_LOCK,false);
    }

    /**
     * 是否设置手势密码
     * @return false表示没有设置,true表示已经设置
     */
    public static boolean isSettingGesturePass() {
        final String gesturePass = PreferencesUtils.getString(GConstant.GESTURE_PASS,null);
        if(TextUtils.isEmpty(gesturePass)) {
            return false;
        }
        return true;
    }

    /**
     * 是否需要假锁报警通知
     * @return false表示不需要通知,true表示通知
     */
    public static boolean isOpenFalseLockAlarm() {
        return PreferencesUtils.getBoolean(Constant.FALSE_LOCK_ALARM);
    }

    /**
     * 是否开启防撬报警
     * @return false表示不需要通知,true表示通知
     */
    public static boolean isOpenPryAlarm() {
        return PreferencesUtils.getBoolean(Constant.PRY_ALARM);
    }

    /**
     * 获取一次性密码
     * @return 密码
     */
    public static String getOnePass(String deviceMac) {
        if(getOnePassCreateTime(deviceMac).equals("未生成")) {
            PreferencesUtils.remove(deviceMac);
            return "未生成";
        }
        String pass = PreferencesUtils.getString(deviceMac,"未生成");
        if(TextUtils.equals(pass,"0") || TextUtils.equals(pass, "未生成")) {
            pass =  "未生成";
            PreferencesUtils.putString(deviceMac + Constant.TIME_KEY,"未生成");
        }
        return pass;
    }

    /**
     * 获取一次性密码创建的时间
     * @return 时间
     */
    public static String getOnePassCreateTime(String deviceMac) {
        String passTime = PreferencesUtils.getString(deviceMac + Constant.TIME_KEY,"未生成");
        if(TextUtils.equals(passTime,"0")) {
            passTime =  "未生成";
        }
        return passTime;
    }
}

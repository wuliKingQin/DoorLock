package cn.saiyi.doorlock.other;

/**
 * 描述：经常用到的一些常量字段
 * 创建作者：黎丝军
 * 创建时间：2016/9/28 17:55
 */

public interface Constant {

    //通用文本大小一般用在actionBar的标题或者其他
    int TEXT_SIZE = 18;
    //是否将指纹用于开锁
    String IS_FINGERPRINT_OPEN_LOCK = "isFingerPrintOpenLock";
    //假锁报警
    String FALSE_LOCK_ALARM = "falseLockAlarm";
    //防撬报警
    String PRY_ALARM = "pryAlarm";
    //无人模式
    String NONE_PEOPLE = "无人模式";
    //无人模式Id
    String NONE_PEOPLE_ID = "nonePeopleId";
    //一次性密码时间
    String TIME_KEY = "_timeKey";
    //用于网络请求错误
    int ERROR_CODE = -9999;
    //用于传wifi_mac值
    String WIFI_MAC = "wifiMac";
    //设备实例
    String DEVICE_BEAN = "deviceBean";
    //设备管理员需要修改的密码
    String DEVICE_PASS = "devicePass";
    //设备管理员原密码
    String DEVICE_ADMIN_PASS = "deviceAdminPass";
}

package cn.saiyi.doorlock.device;

/**
 * 描述：功能
 * 创建作者：黎丝军
 * 创建时间：2016/10/9 10:37
 */

public interface IFunc {
    //请求加密密匙
    byte REQUEST_KEY = (byte) 0x30;
    //远程开门
    byte OPEN_DOOR = (byte) 0x31;
    //修改app密码
    byte MODIFY_PASS = (byte) 0x32;
    //删除app账户
    byte DELETE_ACCOUNT = (byte) 0x33;
    //设置时效密码
    byte SETTING_AGING_PASS = (byte) 0x34;
    //模式设置
    byte SETTING_MODE = (byte) 0x35;
    //上传开门记录
    byte UPLOAD_RECORD = (byte) 0x36;
    //查询状态
    byte QUERY_STATE = (byte) 0x37;
    //自动上传状态
    byte AUTO_UPLOAD_STATE = (byte) 0x38;
    //门锁是否在线
    byte IS_ONLINE_LOCK = (byte) 0x39;
    //发送临时密匙
    byte TEMP_KEY = (byte) 0x3A;
    //恢复出厂设置
    byte FACTORY_SETTING = (byte) 0x3C;
    //远程开门请求
    byte DISTANCE_OPEN = (byte) 0x3D;
}

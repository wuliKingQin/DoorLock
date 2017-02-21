package cn.saiyi.doorlock.device;

/**
 * 描述：设备状态类型
 * 创建作者：黎丝军
 * 创建时间：2016/11/14 15:26
 */

public interface StateType {
    //电量
    byte BATTERY = 0x01;
    //门锁状态
    byte DOOR_STATE = 0x02;
    //门锁无人模式
    byte UNMANNED_PATTERN = 0x03;
    //小偷报警
    byte THIEF_ALARM = 0x04;
    //防撬报警
    byte PRY_ALARM = 0x05;
    //假锁报警
    byte FALSE_ALARM = 0x06;
    //防试报警
    byte TEST_ALARM = 0x07;
}

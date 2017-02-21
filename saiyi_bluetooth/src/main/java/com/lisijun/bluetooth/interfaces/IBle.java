package com.lisijun.bluetooth.interfaces;

/**
 * 描述：蓝牙基本信息
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 10:12
 */

public interface IBle {
    //UUID尾部
    String UUID_TAIL = "c63fd40c-6e55-caaf-6240-";
    //通知描述器UUID尾部
    String UUID_DESCR_TAIL = "-0000-1000-8000-00805f9b34fb";
    //用来广播蓝牙正在连接
    String ACTION_GATT_CONNECTING = "com.bluetooth.robot.ACTION_GATT_CONNECTING";
    //用来广播蓝牙已经连接
    String ACTION_GATT_CONNECTED  = "com.bluetooth.robot.ACTION_GATT_CONNECTED";
    //用来广播蓝牙断开连接
    String ACTION_GATT_DISCONNECTED = "com.bluetooth.robot.ACTION_GATT_DISCONNECTED";
    //蓝牙连接失败
    String ACTION_CONNECTED_FAIL = "com.bluetooth.robot.ACTION_CONNECTED_FAIL";
    //用来广播蓝牙服务被发现
    String ACTION_GATT_SERVICES_DISCOVERED = "com.bluetooth.robot.ACTION_GATT_SERVICES_DISCOVERED";
    //用来广播蓝牙数据可用
    String ACTION_DATA_AVAILABLE  = "com.bluetooth.robot.ACTION_DATA_AVAILABLE";
    //用于获取蓝牙设备的信号轻度值
    String ACTION_RSSI = "com.bluetooth.robot.ACTION_RSSI";
    //用来在广播里获取UUID
    String EXTRA_UUID = "charUUID";
    //用来在广播里获取字节数组数据的键
    String EXTRA_DATA_BYTE  = "dataByte";
    //用来在广播里获取数据结果字符串的键
    String EXTRA_DATA_STR = "dataStr";
    //设备rssi值
    String EXTRA_DATA_RSSI = "rssi";
    //是否发现设备，该键值在连接成功时需要去那这个boolean值来判断
    String EXTRA_IS_SERVICES_DISCOVERED = "isServicesDiscovered";
}

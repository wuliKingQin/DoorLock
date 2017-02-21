package com.lisijun.bluetooth.interfaces;

import android.bluetooth.BluetoothGattService;

import com.lisijun.bluetooth.impl.ByteParam;

import java.util.List;

/**
 * 描述：该接口是包括蓝牙连接和蓝牙数据传输的回调接口
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 9:50
 */

public interface IBleRunCallback {
    /**
     * 用于接收初始化蓝牙服务完成
     * 这个时候可以连接
     * @param info 消息
     */
    void onInitBleServiceFinish(String info);

    /**
     * 连接蓝牙成功
     * @param services 返回服务集合列表
     */
    void onConnSuccess(List<BluetoothGattService> services);

    /**
     * 连接蓝牙失败
     * @param failInfo 失败信息
     */
    void onConnFail(String failInfo);

    /**
     * 接收信号强度值
     * @param rssi 信号强度值
     */
    void onReceiveRssi(int rssi);

    /**
     * 接受蓝牙设备发送过来的数据
     * @param uuid UUID
     * @param data 字节数据
     */
    void onReceiveData(String uuid, ByteParam data);

    /**
     * 功能类型
     * @return 返回比如0x01
     */
    byte getFunc();

    /**
     * 获取功能号的位置
     * @return 默认是0
     */
    int getFuncPosition();
}

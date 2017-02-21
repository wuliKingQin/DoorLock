package com.lisijun.bluetooth.interfaces;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.List;

/**
 * 描述：蓝牙设备接口
 * 创建作者：黎丝军
 * 创建时间：2016/12/28 11:26
 */

public interface IBleGatt extends IBle {

    /**
     * 连接蓝牙设备
     * @param context 运行环境
     * @param adapter 蓝牙适配器
     * @param blAddress 蓝牙地址
     * @return true表示连接上
     */
    boolean connectBle(Context context, BluetoothAdapter adapter, String blAddress);

    /**
     * 读蓝牙特有的数据，该数据通过广播来接收
     * @param characteristic 蓝牙数据类型
     */
    void readCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 设置某个类型是否需要通知
     * @param characteristic 蓝牙数据类型
     * @param enabled true表示能，false表示不能
     */
    boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled);

    /**
     * 手机向蓝牙设备写数据据
     * @param characteristic 蓝牙数据类型
     */
    boolean writeCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 关闭蓝牙服务
     */
    void closeBle();

    /**
     * 获取当前连接蓝牙设备支持的服务
     * @return BluetoothGattService集合列表
     */
    List<BluetoothGattService> getGattServices();

    /**
     * 获取蓝牙Gatt
     * @return BluetoothGatt实例
     */
    BluetoothGatt getGatt();

    /**
     * 获取蓝牙地址
     * @return 蓝牙地址
     */
    String getAddress();
}

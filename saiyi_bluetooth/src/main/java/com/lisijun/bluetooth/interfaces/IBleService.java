package com.lisijun.bluetooth.interfaces;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * 描述：蓝牙服务接口，该接口只抽象出手机与蓝牙设备交互的方法
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 9:37
 */

public interface IBleService extends IBle {

    /**
     * 初始化蓝牙相关
     * @return true表示初始化成功，否则表示失败
     */
    boolean initBle();

    /**
     * 通过目标蓝牙地址连接蓝牙设备的方法
     * @param blAddress 目标蓝牙地址
     * @return true表示能正常连接，但不表示连接上。否则表示不能正常连接
     */
    boolean connectBleDevice(String blAddress);

    /**
     * 读蓝牙特有的数据，该数据通过广播来接收
     * @param characteristic 蓝牙数据类型
     */
    void readCharacteristic(BluetoothGattCharacteristic characteristic, String address);


    /**
     * 手机向蓝牙设备写数据据
     * @param characteristic 蓝牙数据类型
     */
    boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled, String address);

    /**
     * 手机向蓝牙设备写数据据
     * @param characteristic 蓝牙数据类型
     */
    boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, String address);

    /**
     * 根据蓝牙地址关闭蓝牙设备
     * @param bleAddress 蓝牙地址
     */
    void closeBle(String bleAddress);

    /**
     * 获取当前连接蓝牙设备支持的服务
     * @return BluetoothGattService集合列表
     */
    List<BluetoothGattService> getGattServices(String address);

    /**
     * 获取蓝牙Gatt
     * @return BluetoothGatt实例
     */
    BluetoothGatt getGatt(String address);

    /**
     * 检查蓝牙是否开启
     * @return true表示开启,false表示
     */
    boolean checkBluetoothEnable();
}

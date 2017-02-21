package com.lisijun.bluetooth.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * 描述：蓝牙扫描回调接口，将返回蓝牙设备数据,该接口是在主线程中被调用
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 10:23
 */

public interface IBleScanCallback {
    /**
     * 扫描的结果将走这个方法
     * @param device 设备
     * @param rssi 蓝牙信息强度
     */
    void onScanResult(BluetoothDevice device,int rssi);

    /***
     * 扫描失败方法，如果设备不支持就会这个方法
     * @param failInfo 错误信息
     */
    void onScanFail(String failInfo);

    /**
     * 超过10秒，扫描就会自动停止扫描，将会走这个方法
     */
    void onScanFinish();
}

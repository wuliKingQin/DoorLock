package com.lisijun.bluetooth.util;
import android.text.TextUtils;
import com.lisijun.bluetooth.interfaces.IBleDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：蓝牙设备工具类,该工具类提供基本的添加，移除，和获取等方法来操作某个蓝牙设备
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 15:34
 */

public class DeviceUtil {

    //用于存储全局的蓝牙设备
    private final static List<IBleDevice> mBleDevices = new ArrayList<>();

    private DeviceUtil() {
    }

    /**
     * 根据蓝牙地址获取对应的蓝牙设备实例
     * @param address 设备地址
     * @param <T> 实现类
     * @return 蓝牙实例
     */
    public static <T extends IBleDevice> T getBleDevice(String address) {
        for (IBleDevice device : mBleDevices) {
            if(TextUtils.equals(device.getAddress(),address)) {
                return (T)device;
            }
        }
        return null;
    }

    /**
     * 添加蓝牙设备
     * @param bleDevice 蓝牙设备实例
     */
    public static void putBleDevice(IBleDevice bleDevice) {
        boolean isExit = false;
        for (IBleDevice device : mBleDevices) {
            if(TextUtils.equals(device.getAddress(),bleDevice.getAddress())) {
                isExit = true;
                break;
            }
        }
        if(!isExit) mBleDevices.add(bleDevice);
    }

    /**
     * 获取所有的蓝牙设备
     * @param <T> 实现的设备子类
     * @return 列表实例
     */
    public static <T extends IBleDevice> List<T> getAllDevice() {
        return (List<T>)mBleDevices;
    }

    /**
     * 根据蓝牙地址去移除设备
     * @param address 蓝牙地址
     */
    public static void removeBleDevice(String address) {
        for (IBleDevice device : mBleDevices) {
            if(TextUtils.equals(device.getAddress(),address)) {
                device.onDestroy();
                mBleDevices.remove(device);
                break;
            }
        }
    }

    /**
     * 清除所有的数据
     */
    public static void clear() {
        for (IBleDevice device : mBleDevices) {
            device.onDestroy();
        }
        mBleDevices.clear();
    }

}

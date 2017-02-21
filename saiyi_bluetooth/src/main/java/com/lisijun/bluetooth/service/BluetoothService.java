package com.lisijun.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.lisijun.bluetooth.impl.BleGatt;
import com.lisijun.bluetooth.interfaces.IBleGatt;
import com.lisijun.bluetooth.interfaces.IBleService;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：该蓝牙服务既能实现多连也能实现单个连接，在使用的时候只需在Manifest.xml中配置
 * 创建作者：黎丝军
 * 创建时间：2016/12/28 10:16
 */
public class BluetoothService extends Service
        implements IBleService {

    //TAG
    private final static String TAG = BluetoothService.class.getSimpleName();
    //用来实例化BluetoothAdapter
    private BluetoothManager mBluetoothMgr;
    //用来实例化一个蓝牙设备BluetoothDevice
    private BluetoothAdapter mBleAdapter;
    //保存蓝牙连接的设备
    private final List<IBleGatt> mBleDevices = new ArrayList<>();
    //保存唯一的binder实例
    private final Binder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * 初始化蓝牙
     * @return true表示初始成功
     */
    public boolean initBle() {
        if(mBluetoothMgr == null) {
            mBluetoothMgr = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothMgr == null) {
                return false;
            }
        }
        mBleAdapter = mBluetoothMgr.getAdapter();
        if(mBleAdapter == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean connectBleDevice(String bleAddress) {
        IBleGatt bleDevice = getBleDevice(bleAddress);
        if(bleDevice == null) {
            bleDevice = new BleGatt();
            final boolean isConnected = bleDevice.connectBle(this, mBleAdapter,bleAddress);
            if(isConnected && !mBleDevices.contains(bleDevice)) {
                mBleDevices.add(bleDevice);
                return true;
            } else {
                return false;
            }
        } else {
            return bleDevice.connectBle(this, mBleAdapter,bleAddress);
        }
    }

    /**
     * 根据蓝牙地址关闭蓝牙设备
     * @param bleAddress 蓝牙地址
     */
    @Override
    public void closeBle(String bleAddress) {
        final IBleGatt bleDevice = getBleDevice(bleAddress);
        if(bleDevice != null) {
            bleDevice.closeBle();
            mBleDevices.remove(bleDevice);
        } else {
            Log.d(TAG,"蓝牙设备不存在");
        }
    }

    /**
     * 根据蓝牙地址找到已经连接过的蓝牙设备
     * @param bleAddress 蓝牙地址
     * @return 蓝牙实例
     */
    private IBleGatt getBleDevice(String bleAddress) {
        for (IBleGatt bleDevice : mBleDevices) {
            if(TextUtils.equals(bleAddress,bleDevice.getAddress())) {
                return bleDevice;
            }
        }
        return null;
    }

    @Override
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic,String address) {
        final IBleGatt bleDevice = getBleDevice(address);
        if(bleDevice != null) {
            return bleDevice.writeCharacteristic(characteristic);
        }
        return false;
    }

    @Override
    public void readCharacteristic(BluetoothGattCharacteristic characteristic, String address) {
        final IBleGatt bleDevice = getBleDevice(address);
        if(bleDevice != null) {
            bleDevice.readCharacteristic(characteristic);
        }
    }

    @Override
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled,String address) {
        final IBleGatt bleDevice = getBleDevice(address);
        if(bleDevice != null) {
            return bleDevice.setCharacteristicNotification(characteristic,enabled);
        }
        return false;
    }

    @Override
    public List<BluetoothGattService> getGattServices(String address) {
        final IBleGatt device = getBleDevice(address);
        if(device != null) {
            return device.getGattServices();
        }
        return null;
    }

    @Override
    public BluetoothGatt getGatt(String address) {
        final IBleGatt device = getBleDevice(address);
        if(device != null) {
            return device.getGatt();
        }
        return null;
    }

    /**
     * 检查蓝牙是否开启
     * @return true表示蓝牙能用，false表示不能用
     */
    public boolean checkBluetoothEnable() {
        if (mBleAdapter != null) {
            return mBleAdapter.isEnabled();
        }
        return false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        for (IBleGatt bleDevice: mBleDevices) {
            bleDevice.closeBle();
        }
        mBleDevices.clear();
        return super.onUnbind(intent);
    }

    /**
     * 用于在外面实例化服务
     */
    public class LocalBinder extends Binder {
        /**
         * 获取蓝牙服务实例
         * @return BluetoothService
         */
        public IBleService getBleService() {
            return BluetoothService.this;
        }
    }
}
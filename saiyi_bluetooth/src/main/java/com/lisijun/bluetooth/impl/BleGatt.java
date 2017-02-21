package com.lisijun.bluetooth.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lisijun.bluetooth.interfaces.IBleGatt;

import java.util.List;

/**
 * 描述：蓝牙设备，目的在于多设备连接使用
 * 创建作者：黎丝军
 * 创建时间：2016/12/28 10:25
 */

public final class BleGatt implements IBleGatt {

    //标签用于打印log日志
    private final static String TAG = BleGatt.class.getSimpleName();
    //保运行环境
    private Context mContext;
    //蓝牙地址
    private String mBleAddress;
    //蓝牙通道
    private BluetoothGatt mBleGatt;
    //该接口用于手机和蓝牙交互时，数据传输
    private final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    final boolean isDiscover = mBleGatt.discoverServices();
                    sendBroadcast(ACTION_GATT_CONNECTED,isDiscover);
                    Log.d(TAG + "-" + mBleAddress,"bluetooth service is ACTION_GATT_CONNECTED:") ;
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if(mBleGatt != null)mBleGatt.close();
                    sendBroadcast(ACTION_GATT_DISCONNECTED);
                    Log.d(TAG + "-" + mBleAddress,"bluetooth service is STATE_DISCONNECTED:") ;
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    sendBroadcast(ACTION_GATT_CONNECTING);
                    Log.d(TAG + "-" + mBleAddress,"bluetooth service is STATE_DISCONNECTING:") ;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            sendBroadcast(ACTION_DATA_AVAILABLE,characteristic);
            Log.d(TAG + "-" + mBleAddress,"============onCharacteristicChanged");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendBroadcast(ACTION_DATA_AVAILABLE,characteristic);
            }
            Log.d(TAG + "-" + mBleAddress,"============onCharacteristicRead");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendBroadcast(ACTION_GATT_SERVICES_DISCOVERED);
                Log.d(TAG + "-" + mBleAddress,"============onServicesDiscovered");
            } else {
                Log.d(TAG + "-" + mBleAddress, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            final Intent intent = new Intent(mBleAddress + "-" + ACTION_RSSI);
            intent.putExtra(EXTRA_DATA_RSSI,rssi);
            mContext.sendBroadcast(intent);
        }

    };

    @Override
    public boolean connectBle(Context context, BluetoothAdapter adapter, String blAddress) {
        mContext = context;
        if(adapter == null || blAddress == null) {
            return false;
        }
        if(mBleAddress != null && TextUtils.equals(mBleAddress,blAddress)
                && mBleGatt != null) {
            if(mBleGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice bleDevice = adapter.getRemoteDevice(blAddress);
        if(bleDevice == null) {
            return false;
        }
        if(mBleGatt != null)mBleGatt.close();
        mBleGatt = bleDevice.connectGatt(context,false,mBleCallback);
        mBleAddress = blAddress;
        return true;
    }

    @Override
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(mBleGatt != null) {
            mBleGatt.readCharacteristic(characteristic);
        }
    }

    @Override
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        boolean isEnableNotify = false;
        if(mBleGatt != null) {
            isEnableNotify =  mBleGatt.setCharacteristicNotification(characteristic, enabled);
        }
        return isEnableNotify;
    }

    @Override
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        boolean isFlag = false;
        if(mBleGatt != null) {
            //如果写成功将返回true，否则返回false
            //在连续写入的时候将出现后面的数据丢包情况
            //所以在这里每写一个信息将要做延迟操作否则会丢包，延迟间隔时间必须要有点距离
            isFlag = mBleGatt.writeCharacteristic(characteristic);
            Log.d(TAG + "-" + mBleAddress,"==============isWriteSuccess=" + isFlag);
        }
        return isFlag;
    }

    @Override
    public void closeBle() {
        if (mBleGatt == null) {
            return;
        }
        mBleGatt.close();
        mBleAddress = null;
        mBleGatt = null;
    }

    /**
     * 蓝牙回馈信息广播出去
     * @param action 广播类型
     */
    private void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    /**
     * 蓝牙回馈信息广播出去
     * @param action 广播类型
     * @param flag 消息
     */
    private void sendBroadcast(String action,boolean flag) {
        final Intent intent = new Intent(mBleAddress + "-" + action);
        intent.putExtra(EXTRA_IS_SERVICES_DISCOVERED,flag);
        mContext.sendBroadcast(intent);
    }

    /**
     * 广播带数据的消息
     * @param action 广播类型
     * @param characteristic 数据
     */
    private void sendBroadcast(String action,BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(mBleAddress + "-" + action);
        if(characteristic != null) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                intent.putExtra(EXTRA_DATA_BYTE,data);
                Log.d(TAG,stringBuilder.toString());
                intent.putExtra(EXTRA_UUID,characteristic.getUuid().toString());
                intent.putExtra(EXTRA_DATA_STR,stringBuilder.toString());
            }
        }
        mContext.sendBroadcast(intent);
    }

    @Override
    public List<BluetoothGattService> getGattServices() {
        if(mBleGatt == null) {
            return null;
        }
        return mBleGatt.getServices();
    }

    @Override
    public BluetoothGatt getGatt() {
        return mBleGatt;
    }

    @Override
    public String getAddress() {
        return mBleAddress;
    }
}

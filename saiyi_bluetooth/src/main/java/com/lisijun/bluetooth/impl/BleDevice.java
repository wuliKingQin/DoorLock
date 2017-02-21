package com.lisijun.bluetooth.impl;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.lisijun.bluetooth.interfaces.IBleRunCallback;
import com.lisijun.bluetooth.interfaces.IBleDevice;
import com.lisijun.bluetooth.interfaces.IByteParam;
import com.lisijun.bluetooth.interfaces.ISendCallback;

import java.util.List;

/**
 * 描述：蓝牙设备，该类在使用之前必须初始化蓝牙设备的UUID,再能调用connect()方法，
 *       否则能连接但发送信息将发送不出去的情况
 *       例如：BleDevice device = new BleDevice(xxx,rssid);
 *            //当然设置信息可以在子类里初始化的时候进行设置
 *            device.setUuidHeads("0000fff0","0000fff1","0000fff2");//必须设置
 *            device.setNotifyDUuidHead("00002902");//可选设置
 *            device.setReadUuidHead("0000fff4");//可选设置
 *            device.setSendDelayTime(1000);//可选设置
 *            device.connect(this);//连接蓝牙
 *         继承该类时如果需要更新列表某些控件的状态，则需要重写以下方法：
 *         1.onConnSuccess()方法
 *         2.onConnFail()方法
 *         3.connect()方法
 *         4.close()方法
 *         以上方法都需要保留super.onXX()语句
 *         列如：
 *          @Override
 *          public void onConnSuccess(List<BluetoothGattService> services) {
 *                 connectState = "连接成功";
 *                 super.onConnSuccess(services);
 *          }
 *          当然如果的你列表不需要任何状态的自动刷新，那么你就不需要重写以上方法，只需要提前设置设备的UUID和其他相关信息就Ok了。
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 12:41
 */

public class BleDevice implements IBleDevice {

    //信号强度
    private int rssi;
    //设备名
    private String name;
    //设备地址
    private String address;
    //读的UUID
    private String mReadUuid;
    //保存写特征的UUID
    private String mWriteUuid;
    //保存通知的UUID
    private String mNotifyUuid;
    //保存服务的UUID
    private String mServiceUuid;
    //设置通知的descrUUID
    private String mNotifyDescrUuid;
    //设置延迟发送时间
    private long mDelayedTime = 500;
    //发送数据超时时间
    private long mSendTimeOut = 0;
    //连接超时时间
    private long mConnectTimeOut = 0;
    //设备状态描述
    private boolean isConnected;
    //用于来更新设备列表
    private RecyclerView.Adapter adapter;
    //蓝牙控制器
    private BluetoothController mBleController;

    public BleDevice(String address) {
        this(null,address);
    }

    public BleDevice(String name,String address) {
        this.name = name;
        this.address = address;
    }

    public BleDevice(BluetoothDevice device,int rssi) {
        this.rssi = rssi;
        this.name = device.getName();
        this.address = device.getAddress();
    }

    @Override
    public void connect(Context context) {
        isConnected = false;
        if(mBleController == null) {
            //初始化
            mBleController = new BluetoothController(context,address);
            //注册监听器
            mBleController.registerCallback(this);
            //设置发送数据时的延迟时间，不设置为500毫秒
            mBleController.setDelayedTime(mDelayedTime);
            //设置连接超时时间
            mBleController.setConnectTimeOut(mConnectTimeOut);
            //设置发送数据超时
            mBleController.setSendTimeOut(mSendTimeOut);
            //设置连接成功后蓝牙设备服务、通知特征和写特征的UUID
            mBleController.setUuidHeads(mServiceUuid,mNotifyUuid,mWriteUuid);
            //设置读的的UUID
            mBleController.setReadUuidHead(mReadUuid);
            //设置通知描述器的UUID
            mBleController.setNotifyDescriptorUuidHead(mNotifyDescrUuid);
            //绑定蓝牙服务
            mBleController.bindBleService();
            mBleController.registerMsgReceiver();
        }
        Log.d("LiSiJun","connect connectState=" + mBleController.isConnected());
        if(mBleController.isInitBleFinish() && !mBleController.isConnected()) {
            mBleController.registerCallback(this);
            mBleController.connectBleDevice();
        }
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void close() {
        if (mBleController != null) {
            isConnected = false;
            mBleController.closeBle();
            mBleController.unRegisterCallback(this);
        }
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        if(mBleController != null) {
            isConnected = false;
            mBleController.closeBle();
            mBleController.unRegisterCallback(this);
            mBleController.unRegisterMsgReceiver();
            mBleController.unbindBleService();
            mBleController = null;
        }
    }

    @Override
    public void registerCallback(IBleRunCallback callback) {
        if (mBleController != null) {
            mBleController.registerCallback(callback);
        }
    }

    @Override
    public void unregisterCallback(IBleRunCallback callback) {
        if (mBleController != null) {
            mBleController.unRegisterCallback(callback);
        }
    }

    @Override
    public void sendData(byte[] data) {
        sendData(data,null);
    }

    @Override
    public void sendData(byte[] data, ISendCallback callback) {
        sendData(data,true,callback);
    }

    @Override
    public void sendData(IByteParam param, ISendCallback callback) {
        sendData(param,true,callback);
    }

    @Override
    public void sendData(byte[] data, boolean needTimeOut) {
        if (mBleController != null) {
            mBleController.sendData(data,needTimeOut);
        }
    }

    @Override
    public void sendData(byte[] data, boolean needTimeOut, ISendCallback callback) {
        if (mBleController != null) {
            mBleController.sendData(data,needTimeOut,callback);
        } else {
            callback.onFail();
        }
    }

    @Override
    public void sendData(IByteParam param, boolean needTimeOut, ISendCallback callback) {
        if (mBleController != null) {
            mBleController.sendData(param,needTimeOut,callback);
        } else {
            callback.onFail();
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public void readRemoteRssi() {
        if (mBleController != null) {
            mBleController.readRemoteRssi();
        }
    }

    @Override
    public void onInitBleServiceFinish(String info) {
        if(mBleController != null) {
            mBleController.connectBleDevice();
        }
    }

    @Override
    public void onConnSuccess(List<BluetoothGattService> services) {
        isConnected = true;
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnFail(String failInfo) {
        isConnected = false;
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
        Log.d("LiSiJun","onConnFail connected =" + mBleController.isConnected());
    }

    @Override
    public void onReceiveRssi(int rssi) {
        this.rssi = rssi;
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onReceiveData(String uuid, ByteParam data) {
    }

    @Override
    public byte getFunc() {
        return 0;
    }

    @Override
    public int getFuncPosition() {
        return 0;
    }

    @Override
    public int getRssi() {
        return rssi;
    }

    @Override
    public void setUuidHeads(String sUuidHead, String nUuidHead, String wUuidHead) {
        mServiceUuid = sUuidHead;
        mWriteUuid = wUuidHead;
        mNotifyUuid = nUuidHead;
    }

    @Override
    public void setNotifyDUuidHead(String nDUuidHead) {
        mNotifyDescrUuid = nDUuidHead;
    }

    @Override
    public void setReadUuidHead(String rUuidHead) {
        mReadUuid = rUuidHead;
    }

    @Override
    public void setSendDelayTime(long delayTime) {
        mDelayedTime = delayTime;
    }

    @Override
    public void setConnectTimeOut(long connectTimeOut) {
        mConnectTimeOut = connectTimeOut;
    }

    @Override
    public void setSendTimeOut(long sendTimeOut) {
        mSendTimeOut = sendTimeOut;
    }
}

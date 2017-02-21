package com.lisijun.bluetooth.interfaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * 描述：该接口是对外开放接口该接口继承于IBleRunCallback
 *       在该接口里可以接收到蓝牙连接、断开等数据
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 11:03
 */

public interface IBleDevice extends IBleRunCallback{

    /**
     * 连接设备
     * @param context 运行环境
     */
    void connect(Context context);

    /**
     * 关闭设备
     */
    void close();

    /**
     * 销毁资源
     */
    void onDestroy();

    /**
     * 注册设备回调接口
     * @param callback 回调接口实例
     */
    void registerCallback(IBleRunCallback callback);

    /**
     * 注销接口回调接口
     * @param callback 回调接口实例
     */
    void unregisterCallback(IBleRunCallback callback);

    /**
     * 发送数据到蓝牙设备
     * @param data 数据
     */
    void sendData(byte[] data);

    /**
     * 发送数据到蓝牙设备
     * @param data 数据
     * @param callback 发送回调接口
     */
    void sendData(byte[] data, ISendCallback callback);

    /**
     * 发送数据到蓝牙设备
     * @param param 数据
     * @param callback 发送回调接口
     */
    void sendData(IByteParam param, ISendCallback callback);

    /**
     * 发送数据到蓝牙设备
     * @param needTimeOut 是否需要超时处理
     * @param data 数据
     */
    void sendData(byte[] data,boolean needTimeOut);

    /**
     * 发送数据到蓝牙设备
     * @param data 数据
     * @param needTimeOut 是否需要超时处理
     * @param callback 发送回调接口
     */
    void sendData(byte[] data,boolean needTimeOut, ISendCallback callback);

    /**
     * 发送数据到蓝牙设备
     * @param param 数据
     * @param needTimeOut 是否需要超时处理
     * @param callback 发送回调接口
     */
    void sendData(IByteParam param, boolean needTimeOut,ISendCallback callback);

    /**
     * 设置设备名
     * @param name 设备名
     */
    void setName(String name);

    /**
     * 获取设备名
     * @return 设置名
     */
    String getName();

    /**
     * 设置设备地址
     * @param address 设备地址
     */
    void setAddress(String address);

    /**
     * 获取设备地址
     * @return 设备地址
     */
    String getAddress();

    /**
     * 用户判断设备是否已经连接
     * @return true表示已经连接，false表示未连接
     */
    boolean isConnected();

    /**
     * 设置列表适配器
     * @param adapter 列表适配器实例
     */
    void setAdapter(RecyclerView.Adapter adapter);

    /**
     * 获取设备列表适配器
     * @return 列表适配器实例
     */
    RecyclerView.Adapter getAdapter();

    /**
     * 该方法是主动触发，触发完了以后值在
     * onReceiveRssi中被接收到
     */
    void readRemoteRssi();

    /**
     * 获取设备的信号强度
     * @return 信号强度值
     */
    int getRssi();

    /**
     * 配置连接设备的时的UUID，该方法将只需要服务、通知、写的UUID
     * @param sUuidHead 服务UUID头部
     * @param nUuidHead 通知的UUID头部
     * @param wUuidHead 写的UUID头部
     */
    void setUuidHeads(String sUuidHead,String nUuidHead,String wUuidHead);

    /**
     * 设置通知描述器的UUID头
     * @param nDUuidHead 通知描述器的UUID头部
     */
    void setNotifyDUuidHead(String nDUuidHead);

    /**
     * 设置读取的UUID头
     * @param rUuidHead 读取的UUID头
     */
    void setReadUuidHead(String rUuidHead);

    /**
     * 设置发送数据延迟时间
     * @param delayTime 发送数据延迟时间，如果不设置将用默认时间500毫秒
     */
    void setSendDelayTime(long delayTime);

    /**
     * 设置连接设备超时时间，不设置默认为30秒
     * @param connectTimeOut 超时时间，单位为毫秒
     */
    void setConnectTimeOut(long connectTimeOut);

    /**
     * 设置发送超时时间，默认为1分钟
     * @param sendTimeOut 超时时间，单位为毫毛
     */
    void setSendTimeOut(long sendTimeOut);
}

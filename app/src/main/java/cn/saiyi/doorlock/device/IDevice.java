package cn.saiyi.doorlock.device;

import com.lisijun.websocket.socket.OnSocketMsgListener;

/**
 * 描述：设备接口
 * 创建作者：黎丝军
 * 创建时间：2016/10/9 11:02
 */

public interface IDevice {

    /**
     * 注册设备信息改变监听器
     * @param listener 监听实例
     */
    void registerMsgListener(OnSocketMsgListener listener);

    /**
     * 注销设置信息改变监听器
     * @param listener 监听实例
     */
    void unregisterMsgListener(OnSocketMsgListener listener);

    /**
     * 发送命令到服务器的方法
     * @param func 功能
     * @param sendBackCall 发送监听回调接口
     */
    void sendCmd(byte func,ISendCallback sendBackCall);

    /**
     * 发送命令到服务器的方法
     * @param func 功能
     * @param data 发送的数据
     * @param sendBackCall 发送监听回调接口
     */
    void sendCmd(byte func,String data,ISendCallback sendBackCall);

    /**
     * 发送命令到服务器的方法
     * @param func 功能
     * @param data 发送的数据
     * @param sendBackCall 发送监听回调接口
     */
    void sendCmd(byte func,byte[] data,ISendCallback sendBackCall);
}

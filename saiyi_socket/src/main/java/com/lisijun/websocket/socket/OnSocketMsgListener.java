package com.lisijun.websocket.socket;

/**
 * 描述：服务端发送消息到客户端的消息接收的监听器
 * 创建作者：黎丝军
 * 创建时间：2016/11/3 11:17
 */

public interface OnSocketMsgListener {

    /**
     * 接收字符串消息
     * @param deviceName 设备名
     * @param mac 设置mac
     * @param cmd 消息
     */
    void onReceiveMsg(String deviceName,String mac,String cmd);
}

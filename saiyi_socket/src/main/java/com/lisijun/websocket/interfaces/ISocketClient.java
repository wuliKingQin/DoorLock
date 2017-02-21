package com.lisijun.websocket.interfaces;

/**
 * 描述：socket抽象接口，该抽象接口主要抽象出socket连接，发送，重连，断开等操作
 * 创建作者：黎丝军
 * 创建时间：2016/11/3 10:22
 */

public interface ISocketClient {

    //用于socket信息分发的广播action
    String ACTION_SOCKET_MSG = "com.robot.socket.msg";
    //用于socket和服务器关闭的广播
    String ACTION_SOCKET_CLOSE = "com.robot.socket.close";
    //用于socket和服务器开启的广播
    String ACTION_SOCKET_OPEN = "com.robot.socket.open";
    //网络连接发生改变
    String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //socket接收数据字符类型的键值
    String SOCKET_MSG_STR = "socketMsgStr";
    //socket接收数据字节类型的键值
    String SOCKET_MSG_BYTE = "socketMsgByte";

    /**
     * 连接服务器方法
     */
    void connectServer();

    /**
     * 重连服务器方法
     */
    void reconnectServer();

    /**
     * 发送字节类型的消息
     * @param msg 发送的消息
     * @param callback 发送回调接口
     */
    void sendMsg(byte[] msg,ISendMsgCallback callback);

    /**
     * 发送字符串类型的消息
     * @param msg 消息
     * @param callback 回调接口
     */
    void sendMsg(String msg,ISendMsgCallback callback);

    /**
     * 断开连接服务器
     */
    void disconnectServer();
}

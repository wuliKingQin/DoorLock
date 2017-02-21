package com.lisijun.websocket.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lisijun.websocket.interfaces.ISendMsgCallback;
import com.lisijun.websocket.interfaces.ISocketClient;
import com.lisijun.websocket.interfaces.ISocketConnCallback;

import java.net.URISyntaxException;

/**
 * 描述：socket管理实现类，该类主要负责初始化socket的相关信息，
 *       比如在Application里面初始化与服务器的连接的等。
 * 创建作者：黎丝军
 * 创建时间：2016/11/2 16:21
 */
public class SocketManager {

    //保存Socket客户端
    private SocketClient mSocketClient;
    //保存服务连接地址
    private String mUrl;
    //运行环境
    private Context mContext;
    //服务
    private Intent mSocketService;
    //连接回调接口
    private ISocketConnCallback mConnCallback;
    //实例
    private static SocketManager ourInstance = new SocketManager();
    //获取实例
    public static SocketManager instance() {
        return ourInstance;
    }

    private SocketManager() {
    }

    /**
     * 将socket服务进行初始化
     */
    public void init(Context context,String url, ISocketConnCallback callback) {
        mUrl = url;
        mContext = context;
        mConnCallback = callback;
        initSocketClient();
        mSocketService = new Intent(context, SocketService.class);
        mContext.startService(mSocketService);
    }

    /**
     * 设置重连延迟时间
     * @param delayTime 延迟时间
     */
    public void setDelayTime(long delayTime) {
        if(mSocketClient != null) {
            mSocketClient.setDelayReconnTime(delayTime);
        }
    }

    /**
     * 初始换socket客户端
     * @return socket客户端实例
     */
    private boolean initSocketClient() {
        try {
            if(mSocketClient == null && !TextUtils.isEmpty(mUrl)) {
                mSocketClient = new SocketClient(mContext,mUrl,mConnCallback);
            } else if(TextUtils.isEmpty(mUrl)) {
                return false;
            }
            return true;
        } catch (URISyntaxException e) {
            if(mConnCallback != null) {
                mConnCallback.onConnFail(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 停止Socket服务
     */
    public void stopSocketService() {
        mContext.stopService(mSocketService);
    }

    /**
     * 连接服务器方法，该方法将在服务类里被调用
     */
    protected void connectServer() {
        if(initSocketClient()) {
            mSocketClient.connectServer();
        }
    }


    /**
     * 重连服务器
     */
    protected  void reconnectServer() {
        if(initSocketClient()) {
            mSocketClient.reconnectServer();
        }
    }

    /**
     * 关闭连接服务器
     */
    protected void closeConnServer() {
        if(mSocketClient != null) {
            mSocketClient.closeConnServer();
        }
    }

    /**
     * 发送字节类型的消息
     * @param msg 发送的消息
     * @param callback 发送回调接口
     */
    public void sendMsg(byte[] msg, ISendMsgCallback callback) {
        if(mSocketClient != null) {
            mSocketClient.sendMsg(msg,callback);
        }
    }

    /**
     * 发送字符串类型的消息
     * @param msg 消息
     * @param callback 回调接口
     */
    public void sendMsg(String msg, ISendMsgCallback callback) {
        if(mSocketClient != null) {
            mSocketClient.sendMsg(msg,callback);
        }
    }

    /**
     * 添加消息监听器
     * @param listener 监听实例
     */
    public void addMsgListener(OnSocketMsgListener listener) {
        if(mSocketClient != null) {
            mSocketClient.addMsgListener(listener);
        }
    }

    /**
     * 移除消息监听器
     * @param listener 监听实例
     */
    public void removeMsgListener(OnSocketMsgListener listener) {
        if(mSocketClient != null) {
            mSocketClient.removeMsgListener(listener);
        }
    }

    /**
     * 注册广播接收器
     * @param receiver 广播接收器
     */
    public void registerBroadcast(BroadcastReceiver receiver) {
        if(mSocketClient != null) {
            mSocketClient.registerBroadcast(receiver);
        }
    }

    /**
     * 注销广播接收器
     * @param receiver 广播接收器实例
     */
    public void unregisterBroadcast(BroadcastReceiver receiver) {
        if(mSocketClient != null) {
            mSocketClient.unregisterBroadcast(receiver);
        }
    }
}

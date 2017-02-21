package com.lisijun.websocket.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lisijun.websocket.interfaces.ISendMsgCallback;
import com.lisijun.websocket.interfaces.ISocketClient;
import com.lisijun.websocket.interfaces.ISocketConnCallback;
import com.lisijun.websocket.util.JSONUtil;
import com.lisijun.websocket.util.NetUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：socket客户端实现类
 * 创建作者：黎丝军
 * 创建时间：2016/11/2 16:24
 */

public class SocketClient implements ISocketClient{

    //用于提示
    private final static String TAG = SocketClient.class.getSimpleName();
    //默认延迟重连时间为10秒
    private final static long DEFAULT_DELAY_TIME = 10 * 1000;
    //连接是否被打开
    private boolean isOpen;
    //连接是否被关闭
    private boolean isClose = true;
    //用来发送广播
    private Context mContext;
    //保存服务端地址
    private URI mTargetUri;
    //服务端协议
    private Draft mServerDraft;
    //延迟重连时间
    private long mDelayReconnTime;
    //WebSocket客户端
    private WebSocketClient mWebSocketClient;
    //连接回调
    private ISocketConnCallback mConnCallback;
    //监听实例，用于处理消息接收器
    private final List<OnSocketMsgListener> msgListeners = new ArrayList<>();
    //是否重连
    private boolean isReconn = true;

    public SocketClient(Context context,String url, ISocketConnCallback connCallback) throws URISyntaxException {
        this(context,url, new Draft_17(),connCallback);
    }

    public SocketClient(Context context,String url, Draft draft,ISocketConnCallback connCallback) throws URISyntaxException {
        mContext = context;
        mConnCallback = connCallback;
        mTargetUri = new URI(url);
        mServerDraft = draft;
        mDelayReconnTime = DEFAULT_DELAY_TIME;
        mWebSocketClient = getWebSocketClient();
    }

    /**
     * 发送广播数据
     * @param action 广播类型
     */
    private void sendBroadcast(String action) {
        mContext.sendBroadcast(new Intent(action));
    }

    /**
     * 发送广播数据
     * @param action 广播类型
     * @param msg 数据
     */
    private void sendBroadcast(String action,String msg) {
        final Intent intent = new Intent(action);
        if(msg != null) {
            intent.putExtra(SOCKET_MSG_STR,msg);
        }
        mContext.sendBroadcast(intent);
    }

    /**
     * 发送广播数据
     * @param action 广播类型
     * @param msg 数据
     */
    private void sendBroadcast(String action,ByteBuffer msg) {
        final Intent intent = new Intent(action);
        intent.putExtra(SOCKET_MSG_BYTE,msg.array());
        mContext.sendBroadcast(intent);
    }

    @Override
    public void connectServer() {
        if(!isOpen && mWebSocketClient != null) {
            if(NetUtil.networkUsable(mContext)) {
                mWebSocketClient.connect();
            } else {
                Log.d(TAG,"网络不可用");
                if(mConnCallback != null) {
                    mConnCallback.onConnFail("网络不可用");
                }
            }
        } else {
        }
    }

    @Override
    public void reconnectServer() {
        if(isClose) {
            if(NetUtil.networkUsable(mContext)) {
                if(isReconn) {
                    isReconn = false;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"开始重连,连接状态isOpen=" + isOpen);
                            if(isClose) {
                                mWebSocketClient = getWebSocketClient();
                                mWebSocketClient.connect();
                            } else {
                                Log.d(TAG,"重连被取消");
                            }
                            isReconn = true;
                        }
                    },mDelayReconnTime);
                } else {
                }
            } else {
                Log.d(TAG,"网络不可用");
                if(mConnCallback != null) {
                    mConnCallback.onConnFail("网络不可用");
                }
            }
        } else {
        }
    }

    /**
     * 关闭连接服务器
     */
    public void closeConnServer() {
        if(mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    @Override
    public void sendMsg(byte[] msg, ISendMsgCallback callback) {
        try {
            if(NetUtil.networkUsable(mContext)) {
                mWebSocketClient.send(msg);
                if(callback != null) {
                    callback.onSuccess();
                }
            } else {
                if(callback != null) {
                    callback.onFail(-1,"网络不可用");
                }
            }
        } catch (Exception e) {
            if(callback != null) {
                callback.onFail(-1,"发送失败");
            }
        }
    }

    @Override
    public void sendMsg(String msg, ISendMsgCallback callback) {
        try {
            if(NetUtil.networkUsable(mContext)) {
                mWebSocketClient.send(msg);
                if(callback != null) {
                    callback.onSuccess();
                }
            } else {
                if(callback != null) {
                    callback.onFail(-1,"网络不可用");
                }
            }
        } catch (Exception e) {
            if(callback != null) {
                callback.onFail(-1,"发送失败");
            }
        }
    }

    @Override
    public void disconnectServer() {
        if(isOpen && mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    /**
     * 连接时是否被关闭
     * @return true表示被关闭，false表示没有关闭
     */
    public boolean isClose() {
        return isClose;
    }

    /**
     * 连接是否被打开
     * @return true表示打开，false表示没有被打开
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 添加消息监听器
     * @param listener 监听实例
     */
    public void addMsgListener(OnSocketMsgListener listener) {
        if(!msgListeners.contains(listener)) {
            msgListeners.add(listener);
        }
    }

    /**
     * 移除消息监听器
     * @param listener 监听实例
     */
    public void removeMsgListener(OnSocketMsgListener listener) {
        if(msgListeners.contains(listener)) {
            msgListeners.remove(listener);
        }
    }

    /**
     * 注册广播接收器
     * @param receiver 广播接收器
     */
    public void registerBroadcast(BroadcastReceiver receiver) {
        mContext.registerReceiver(receiver,actionSocketIntentFilter());
    }

    /**
     * 注销广播接收器
     * @param receiver 广播接收器实例
     */
    public void unregisterBroadcast(BroadcastReceiver receiver) {
        mContext.unregisterReceiver(receiver);
    }

    /**
     * 设置延迟重连时间
     * @param delayReconnTime 重连延迟时间
     */
    public void setDelayReconnTime(long delayReconnTime) {
        mDelayReconnTime = delayReconnTime;
    }

    /**
     * 广播过滤器
     * @return IntentFilter 实例
     */
    private IntentFilter actionSocketIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SOCKET_MSG);
        return intentFilter;
    }

    //用于消息转发
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mConnCallback != null) {
                String info;
                switch (msg.what) {
                    case -1:
                        info = (String)msg.obj;
                        mConnCallback.onConnFail(info);
                        break;
                    case 1:
                        mConnCallback.onConnSuccess();
                        break;
                    case 2:
                        info = (String)msg.obj;
                        String deviceMac = null;
                        String deviceMsg = info;
                        String deviceName = null;
                        final JSONObject jsonObject = JSONUtil.initJson(info);
                        if(jsonObject != null) {
                            deviceMac = jsonObject.optString("mac",null);
                            deviceMsg = jsonObject.optString("message",null);
                            deviceName = jsonObject.optString("name",null);
                        }
                        for (OnSocketMsgListener msgListener : msgListeners) {
                            msgListener.onReceiveMsg(deviceName,deviceMac,deviceMsg);
                        }
                        break;
                    case 3:
//                        final ByteBuffer data = (ByteBuffer) msg.obj;
//                        for (OnSocketMsgListener msgListener : msgListeners) {
//                            msgListener.onReceiveMsg(data);
//                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    /**
     * 获取WebSocketClient实例
     * @return WebSocketClient
     */
    private final WebSocketClient getWebSocketClient() {
        return new WebSocketClient(mTargetUri, mServerDraft) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                mHandler.obtainMessage(1).sendToTarget();
                isOpen = true;
                isClose = false;
                Log.d(TAG, "连接成功");
                sendBroadcast(ACTION_SOCKET_OPEN);
            }

            @Override
            public void onMessage(String info) {
                mHandler.obtainMessage(2, info).sendToTarget();
                sendBroadcast(ACTION_SOCKET_MSG, info);
                Log.d(TAG, info);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                isClose = true;
                isOpen = false;
                sendBroadcast(ACTION_SOCKET_CLOSE);
                mHandler.obtainMessage(-1, s).sendToTarget();
                Log.d(TAG, "断开连接");
            }

            @Override
            public void onError(Exception e) {
                mHandler.obtainMessage(-1, e.getMessage()).sendToTarget();
                Log.d(TAG, "连接出错");
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                mHandler.obtainMessage(3, bytes).sendToTarget();
                sendBroadcast(ACTION_SOCKET_MSG, bytes);
            }
        };
    }
}

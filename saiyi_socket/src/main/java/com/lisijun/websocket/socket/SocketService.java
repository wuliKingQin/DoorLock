package com.lisijun.websocket.socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.lisijun.websocket.interfaces.ISocketClient;
import com.lisijun.websocket.util.NetUtil;

/**
 * 描述：socket服务，主要用来建立连接,网络可用时重连，服务端掉线重连等。
 * 创建作者：黎丝军
 * 创建时间：2016/11/3 17:46
 */

public class SocketService extends Service {

    //用于打印日志
    private final static String TAG = SocketService.class.getSimpleName();
    //保存注册的广播实例
    private Intent mIntentBroadcast;
    //socket管理器
    private SocketManager mSocketMgr;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocketMgr = SocketManager.instance();
        mSocketMgr.connectServer();
    }

    /**
     * 注册网络改变广播
     */
    private void registerReceiver() {
        if(mIntentBroadcast == null) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ISocketClient.ACTION_SOCKET_CLOSE);
            intentFilter.addAction(ISocketClient.CONNECTIVITY_ACTION);
            mIntentBroadcast = registerReceiver(mServerStateReceiver,intentFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocketMgr.closeConnServer();
        unregisterReceiver(mServerStateReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 用于服务在的时候网络变化监听
     */
    public final BroadcastReceiver mServerStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(TextUtils.equals(action,ConnectivityManager.CONNECTIVITY_ACTION) ||
                    ISocketClient.ACTION_SOCKET_CLOSE.equals(action)) {
                if(NetUtil.networkUsable(context)) {
                    Log.d(TAG,"进入重连");
                    mSocketMgr.reconnectServer();
                } else {
                    Log.d(TAG,"网络断开");
                }
            }
        }
    };
}

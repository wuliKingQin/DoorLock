package cn.saiyi.doorlock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.lisijun.websocket.interfaces.ISocketClient;
import com.lisijun.websocket.util.JSONUtil;
import com.lisijun.websocket.util.NetUtil;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.NotificationUtils;
import com.saiyi.framework.util.PreferencesUtils;
import com.saiyi.framework.util.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.device.StateType;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.RetryUtil;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.util.PassUtil;
import cn.saiyi.doorlock.util.TimeUtil;
import okhttp3.Headers;

/**
 * 描述：用于接收web socket的消息广播
 * 创建作者：黎丝军
 * 创建时间：2016/11/22 9:43
 */

public class WebSocketBroadcast extends BroadcastReceiver {

    //用于通知标题
    private static final String NOTIFY_TITLE = "警报通知";
    //请求开门通知
    private static final String OPEN_NOTIFY_TITLE = "开门通知";
    //消息类型
    private String action = null;
    //设备mac
    private String deviceMac;
    //设备消息
    private String deviceMsg;
    //设备名
    private String deviceName;
    //设备
    private Device mDevice;
    //用于存放实现该接口类
    private final static List<INetworkListener> mListeners = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        if(ISocketClient.ACTION_SOCKET_MSG.equals(action)) {
            final JSONObject jsonObject = JSONUtil.initJson(intent.getStringExtra(ISocketClient.SOCKET_MSG_STR));
            if(jsonObject != null) {
                deviceMac = jsonObject.optString("mac",null);
                deviceMsg = jsonObject.optString("message",null);
                deviceName = jsonObject.optString("name",null);
                if(deviceMsg != null) {
                    final byte[] content = EncodeUtil.hexStrToBytes(deviceMsg);
                    if(content.length > 4) {
                        if(content[4] == IFunc.AUTO_UPLOAD_STATE) {
                            resultHandle(context,content[6]);
                        } else if(content[4] == IFunc.TEMP_KEY) {
                            mDevice = new Device();
                            mDevice.setDeviceMac(deviceMac);
                            tempPassHandle(context,content[6]);
                        } else if(content[4] == IFunc.DISTANCE_OPEN) {
                            NotificationUtils.sendNotify(context,OPEN_NOTIFY_TITLE,deviceName + "门锁，有人请求开门。",R.mipmap.ic_app);
                        }
                    } else {
                    }
                }
            }
        } else if(ISocketClient.ACTION_SOCKET_OPEN.equals(action)) {
            networkChangeHandle(true);
        } else if (ISocketClient.CONNECTIVITY_ACTION.equals(action)) {
            if(NetUtil.networkUsable(context)) {
                networkChangeHandle(true);
            } else {
                networkChangeHandle(false);
            }
        }
    }

    /**
     * 网络处理转发
     */
    private void networkChangeHandle(boolean onLine) {
        for (INetworkListener listener : mListeners) {
            if(onLine) {
                listener.onConnected();
            } else {
                listener.onDisconnect();
            }
        }
    }

    /**
     * 处理警报通知
     * @param result 警报类型
     */
    private void resultHandle(Context context,byte result) {
        switch (result) {
            case StateType.THIEF_ALARM:
                break;
            case StateType.PRY_ALARM:
                if(PassUtil.isOpenPryAlarm()) {
                    NotificationUtils.sendNotify(context,NOTIFY_TITLE,deviceName + "设备发出防撬报警",R.mipmap.ic_app);
                } else {
                    LogUtils.d(deviceName + "设备发出防撬报警");
                }
                break;
            case StateType.FALSE_ALARM:
                if(PassUtil.isOpenFalseLockAlarm()) {
                    NotificationUtils.sendNotify(context,NOTIFY_TITLE,deviceName + "设备发出假锁报警",R.mipmap.ic_app);
                } else {
                    LogUtils.d(deviceName + "设备发出假锁报警");
                }
                break;
            case StateType.TEST_ALARM:
                break;
            default:
                break;
        }
    }

    /**
     * 处理临时密码
     */
    private void tempPassHandle(Context context,byte responseCode) {
        String result = null;
        switch (responseCode) {
            case 0x0E:
            case 0x0F:
                RetryUtil.startRetry(10 * 1000, new RetryUtil.IRetryCallback() {
                    @Override
                    public void onRetry() {
                        final String tempPass = PassUtil.createPassword();
                        PreferencesUtils.putString(deviceMac,tempPass);
                        mDevice.sendCmd(IFunc.TEMP_KEY, EncodeUtil.strToBytes(tempPass), new ISendCallback() {
                            @Override
                            public void onSuccess() {
                                LogUtils.d("发送临时密匙成功");
                            }

                            @Override
                            public void onError(int errorCode, String errorInfo) {
                                LogUtils.d("发送临时密匙失败");
                            }
                        });
                    }
                });
                break;
            case 0x00:
                result = "临时秘钥生成成功";
                break;
            case 0x02:
                result = "临时秘钥为空";
                break;
            case 0x03:
                result = "临时秘钥失效";
                break;
            case 0x0b:
                result = "操作失败";
                break;
            default:
                break;
        }
        if(result != null) {
            RetryUtil.cancel();
            LogUtils.d("生成临时秘钥结果：" + result );
            if("临时秘钥生成成功".equals(result)) {
                PreferencesUtils.putString(deviceMac + Constant.TIME_KEY,TimeUtil.getCurrentTime());
                final JSONParam jsonParam = new JSONParam();
                jsonParam.putJSONParam("phone", LoginUtil.getAccount());
                jsonParam.putJSONParam("mac",deviceMac);
                jsonParam.putJSONParam("ycxpwd",PreferencesUtils.getString(deviceMac));
                jsonParam.putJSONParam("ycxdate",PassUtil.getOnePassCreateTime(deviceMac));
                HttpRequest.post(URL.MODIFY_DEVICE_NAME,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
                    @Override
                    protected void onSuccess(JSONObject jsonObject) {
                        LogUtils.d("保存一次性密码成功");
                    }
                    @Override
                    public void onFailure(int errorCode, String msg) {
                        LogUtils.d("保存一次性密码失败");
                    }
                });
            } else {
            }
        }
    }

    /**
     * 添加注册网络监听
     * @param listener 监听器
     */
    public static void registerNetworkListener(INetworkListener listener) {
        if(!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * 注销广播监听器
     * @param listener 监听
     */
    public static void unRegisterNetworkListener(INetworkListener listener) {
        if(mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }
}

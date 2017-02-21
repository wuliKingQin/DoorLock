package cn.saiyi.doorlock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lisijun.websocket.interfaces.ISocketClient;
import com.lisijun.websocket.util.JSONUtil;
import com.saiyi.framework.util.LogUtils;
import org.json.JSONObject;
import cn.saiyi.doorlock.util.EncodeUtil;

/**
 * 描述：用于写接收WebSocket发来的信息
 * 创建作者：黎丝军
 * 创建时间：2017/2/16 15:45
 */

public abstract class AbsSocketMsgReceiver extends BroadcastReceiver {

    //设备mac
    private String deviceMac;
    //设备消息
    private String deviceMsg;
    //设备名
    private String deviceName;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ISocketClient.ACTION_SOCKET_MSG.equals(intent.getAction())) {
            final JSONObject jsonObject = JSONUtil.initJson(intent.getStringExtra(ISocketClient.SOCKET_MSG_STR));
            if(jsonObject != null) {
                deviceMac = jsonObject.optString("mac",null);
                deviceMsg = jsonObject.optString("message",null);
                deviceName = jsonObject.optString("name",null);
                if(deviceMsg != null) {
                    if(!deviceMsg.startsWith("AA55")) {
                        socketDataString(context,deviceMsg);
                    } else {
                        final byte[] data = EncodeUtil.hexStrToBytes(deviceMsg.toUpperCase());
                        if((data.length > 4 && data[4] == getFunc()) || getFunc() == Byte.MAX_VALUE) {
                            socketDataBytes(context,data);
                        }
                    }
                } else {
                    LogUtils.d("接收消息失败");
                }
            } else {
                LogUtils.d("接收消息失败");
            }
        }
    }

    /**
     * 接收字符串信息
     * @param data 字符串数据
     */
    public void socketDataString(Context context,String data) {

    }

    /**
     * 接收socket数据
     * @param data 数据
     */
    public abstract void socketDataBytes(Context context,byte[] data);

    /**
     * 获取功能好
     * @return 返回功能号
     */
    public byte getFunc() {
        return Byte.MAX_VALUE;
    }

    /**
     * 获取设备mac
     * @return 设备mac
     */
    public String getDeviceMac() {
        return deviceMac;
    }

    /**
     * 获取设备名
     * @return 设备名
     */
    public String getDeviceName() {
        return deviceName;
    }
}

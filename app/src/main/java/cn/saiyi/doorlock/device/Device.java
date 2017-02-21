package cn.saiyi.doorlock.device;
import com.alibaba.fastjson.JSONObject;
import com.lisijun.websocket.socket.OnSocketMsgListener;
import com.lisijun.websocket.socket.SocketManager;

import java.util.ArrayList;
import java.util.List;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：设备实现类
 * 创建作者：黎丝军
 * 创建时间：2016/10/9 11:14
 */

public class Device implements IDevice {

    //请求地址
    private String mUrl;
    //请求参数
    private JSONParam mParams;
    //要发送的数据
    private List<Byte> mContent;
    //管理
    private SocketManager mSocketMgr;
    //设备mac
    private String mDeviceMac;

    public Device() {
        mContent = new ArrayList<>();
        mUrl = URL.SEND_TO_DEVICE;
        mSocketMgr = SocketManager.instance();
    }

    @Override
    public void registerMsgListener(OnSocketMsgListener listener) {
        mSocketMgr.addMsgListener(listener);
    }

    @Override
    public void unregisterMsgListener(OnSocketMsgListener listener) {
        mSocketMgr.removeMsgListener(listener);
    }

    @Override
    public void sendCmd(byte func,ISendCallback sendBackCall) {
       sendCmd(func,new byte[]{},sendBackCall);
    }

    @Override
    public void sendCmd(byte func,String data, final ISendCallback sendBackCall) {
        sendCmd(func,data.getBytes(),sendBackCall);
    }

    @Override
    public void sendCmd(byte func,byte[] data, final ISendCallback sendBackCall) {
        packageData(func,data);
        mParams = new JSONParam();
        mParams.putJSONParam("phone", LoginUtil.getAccount());
        mParams.putJSONParam("message",getSendData());
        mParams.putJSONParam("mac",mDeviceMac);
        HttpRequest.post(mUrl,mParams.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject data) {
                try {
                    final int result = data.getInteger("result");
                    if(result == 1) {
                        sendBackCall.onSuccess();
                    } else {
                        onFailure(Constant.ERROR_CODE,"发送失败");
                    }
                } catch (Exception e) {
                    onFailure(Constant.ERROR_CODE,"服务器错误");
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                if(errorCode == Constant.ERROR_CODE) {
                    sendBackCall.onError(errorCode,msg);
                } else  {
                    sendBackCall.onError(errorCode,"网络错误");
                }
            }
        });
    }

    /**
     * 组装数据
     * @param func 功能
     * @param data 发送的内容
     */
    private void packageData(byte func,byte[] data) {
        mContent.clear();
        mContent.add((byte)0xAA);
        mContent.add((byte)0x55);
        mContent.add((byte)0x02);
        mContent.add((byte)0x01);
        mContent.add(func);
        if(data != null && data.length > 0) {
            mContent.add((byte)(data.length));
            for(byte byteData:data) {
                mContent.add(byteData);
            }
        } else {
            mContent.add((byte)0);
        }
        xorCheckout();
    }

    /**
     * 异或检验
     */
    private void xorCheckout() {
        byte tempt = mContent.get(0);
        int position = 1;
        for(;position < mContent.size();position ++) {
            tempt ^= mContent.get(position);
        }
        mContent.add(tempt);
    }

    /**
     * 获取发送的字符串数据
     * @return 数据
     */
    private String getSendData() {
        int temp;
        String valueTemp;
        final StringBuilder strBuilder = new StringBuilder();
        for(byte value:mContent) {
            temp = value & 0xFF;
            valueTemp = Integer.toHexString(temp);
            if(valueTemp.length() < 2) {
                strBuilder.append(0);
            }
            strBuilder.append(valueTemp);
        }
        return strBuilder.toString();
    }

    public String getDeviceMac() {
        return mDeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        mDeviceMac = deviceMac;
    }
}

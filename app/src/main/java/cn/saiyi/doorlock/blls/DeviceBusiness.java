package cn.saiyi.doorlock.blls;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lisijun.websocket.interfaces.ISocketClient;
import com.saiyi.framework.blls.AbsBaseBusiness;
import com.saiyi.framework.interfaces.ICancelRequestCallBack;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.PreferencesUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.activity.AgingPassActivity;
import cn.saiyi.doorlock.activity.AuthorityMgrActivity;
import cn.saiyi.doorlock.activity.BluetoothSharkActivity;
import cn.saiyi.doorlock.activity.CorrelationSettingActivity;
import cn.saiyi.doorlock.activity.DeviceControlActivity;
import cn.saiyi.doorlock.activity.DistanceOpenLockActivity;
import cn.saiyi.doorlock.activity.OnePassActivity;
import cn.saiyi.doorlock.activity.OpenLockRecordActivity;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.broadcast.AbsSocketMsgReceiver;
import cn.saiyi.doorlock.broadcast.INetworkListener;
import cn.saiyi.doorlock.broadcast.WebSocketBroadcast;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.DeviceUtil;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.AppUtil;
import cn.saiyi.doorlock.util.DecodeUtil;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：设备业务逻辑类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 15:08
 */

public class DeviceBusiness extends AbsBaseBusiness
        implements INetworkListener{

    //消息接收广播
    private DeviceMsgReceiver mMsgReceiver;
    //管理关系
    private Device mDevice = null;
    //获取设备列表
    private List<DeviceBean> mDeviceList;
    //更新设备回调接口
    private IUpdateDeviceCallBack mCallBack;

    @Override
    public void initObject() {
        mMsgReceiver = new DeviceMsgReceiver();
        mDeviceList = new ArrayList<>();
        mDevice = new Device();
        WebSocketBroadcast.registerNetworkListener(this);
    }

    @Override
    public void initData(Bundle bundle) {
        getContext().registerReceiver(mMsgReceiver,makeMsgFilter());
    }

    @Override
    public void onResume() {
        requestDeviceList();
    }

    /**
     * 请求设备列表
     */
    public void requestDeviceList() {
        HttpRequest.get(URL.PHONE_QUERY_BIND_DEVICE + LoginUtil.getAccount(),new BaseHttpRequestCallback<JSONArray>() {
            @Override
            protected void onSuccess(JSONArray resultInfo) {
                try {
                    mDeviceList.clear();
                    int index = 0;
                    DeviceBean deviceBean;
                    JSONObject deviceItem;
                    for(;index < resultInfo.size();index ++) {
                        deviceItem = resultInfo.getJSONObject(index);
                        if(deviceItem != null) {
                            deviceBean = new DeviceBean();
                            deviceBean.setName(deviceItem.getString("name"));
                            deviceBean.setWifiMac(deviceItem.getString("mac"));
                            deviceBean.setAdminPass(deviceItem.getString("pwd"));
                            deviceBean.setBleAddress(deviceItem.getString("bluetooth"));
                            deviceBean.setOnLine(deviceItem.getInteger("mark") == 0 ? false:true);
                            deviceBean.setIconUrl(deviceItem.getString("dimg"));
                            deviceBean.setOnePass(deviceItem.getString("ycxpwd"),deviceItem.getString("ycxdate"));
                            deviceBean.setUnmanned(deviceItem.getInteger("unmanned"));
                            mDeviceList.add(deviceBean);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.d(e.getMessage());
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                LogUtils.d(msg);
            }

            @Override
            public void onFinish() {
                if(mCallBack != null) {
                    mCallBack.onUpdateDevice(mDeviceList);
                }
            }
        });
    }

    /**
     * 设备详情处理方法
     * @param deviceBean 设备
     */
    public void deviceDetailsHandle(final DeviceBean deviceBean) {
        if(!TextUtils.equals(deviceBean.getAdminPass(),"0")) {
            final Intent intent = new Intent(getContext(), DeviceControlActivity.class);
            intent.putExtra("device",deviceBean);
            getContext().startActivity(intent);
        } else {
            DeviceUtil.modifyDevicePassHandle(mDevice,getContext(), "添加", true, deviceBean, new ISendCallback() {
                @Override
                public void onSuccess() {
                    LogUtils.d("发送加密请求成功");
                }

                @Override
                public void onError(int errorCode, String errorInfo) {
                    ToastUtils.toast(getContext(),errorInfo);
                    ProgressUtils.dismissDialog();
                }
            });
        }
    }

    /**
     * 处理扫描到的蓝牙地址,激活蓝牙功能
     * 该方法需要修改请求地址，服务器还没有给接口
     * @param blueAddress 蓝牙地址
     * @param wifiMac wifi mac地址
     */
    public void scanBlueAddressHandle(String blueAddress,String wifiMac) {
        ProgressUtils.showDialog(getContext(), "正在激活蓝牙，……", new ICancelRequestCallBack() {
            @Override
            public void onCancel() {
                HttpRequest.cancel(URL.ROOT_RUL);
            }
        });
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone",LoginUtil.getAccount());
        jsonParam.putJSONParam("mac",wifiMac);
        jsonParam.putJSONParam("bluetooth",blueAddress);
        HttpRequest.post(URL.MODIFY_DEVICE_NAME,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                try {
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        requestDeviceList();
                        ToastUtils.toast(getContext(),"蓝牙激活成功");
                    } else {
                        onFailure(Constant.ERROR_CODE,null);
                    }
                } catch (Exception e){
                    onFailure(Constant.ERROR_CODE,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getContext(),"蓝牙激活失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }


    /**
     * 更新设备回调接口设置
     * @param callBack 实例
     */
    public void setUpdateDeviceCallBack(IUpdateDeviceCallBack callBack) {
        mCallBack = callBack;
        ListenersMgr.getInstance().registerListener(IUpdateDeviceCallBack.class,mCallBack);
    }

    @Override
    public void onDestroy() {
    }

    /**
     * 销毁在程序退出的时候
     */
    public void destroy() {
        getContext().unregisterReceiver(mMsgReceiver);
        WebSocketBroadcast.unRegisterNetworkListener(this);
        ListenersMgr.getInstance().unRegisterListener(IUpdateDeviceCallBack.class);
    }

    /**
     * 结束所有的设置控制界面
     */
    private void finishDeviceControlAllActivity() {
        appHelper.finishActivity(DeviceControlActivity.class);
        appHelper.finishActivity(CorrelationSettingActivity.class);
        appHelper.finishActivity(OnePassActivity.class);
        appHelper.finishActivity(DistanceOpenLockActivity.class);
        appHelper.finishActivity(BluetoothSharkActivity.class);
        appHelper.finishActivity(AuthorityMgrActivity.class);
        appHelper.finishActivity(AgingPassActivity.class);
        appHelper.finishActivity(OpenLockRecordActivity.class);
    }

    /**
     * 发送管理员密码到设备
     * @param k1 加密方式一
     * @param k2 加密方式二
     * @param pass 密码
     * @param yPass 原密码
     */
    private void sendAdminPassToDevice(byte k1,byte k2,String pass,String yPass) {
        if(TextUtils.equals(yPass,"0")) {
            yPass = "00000000";
        }
        byte[] yPassBytes = EncodeUtil.strToBytes(yPass);
        byte[] passBytes = EncodeUtil.strToBytes(pass);
        yPassBytes = EncryptUtil.encrypt(k1,k2,yPassBytes);
        passBytes = EncryptUtil.encrypt(k1,k2,passBytes);
        final byte[] ePassBytes =  EncryptUtil.mergeArray(yPassBytes, passBytes);
        LogUtils.d(DecodeUtil.bytesToHexStr(ePassBytes));
        mDevice.setDeviceMac(PreferencesUtils.getString(Constant.WIFI_MAC));
        mDevice.sendCmd(IFunc.MODIFY_PASS,EncryptUtil.mergeArray(new byte[]{0x01},ePassBytes), new ISendCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d("加完密码后发送修改密码的请求成功");
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                ToastUtils.toast(getContext(),errorInfo);
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 处理删除设备等结果
     * @param result 结果类型
     * @param successInfo 成功信息
     * @param failInfo 失败信息
     */
    private  void appAccountDeleteOrAddHandle(byte result, final String successInfo, String failInfo) {
        if(result == 0x00) {
            final JSONParam jsonparam = new JSONParam();
            jsonparam.putJSONParam("phone", LoginUtil.getAccount());
            jsonparam.putJSONParam("mac",PreferencesUtils.getString(Constant.WIFI_MAC));
            HttpRequest.post(URL.DELETE_DEVICE,jsonparam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
                @Override
                protected void onSuccess(JSONObject jsonObject) {
                    try {
                        final int result = jsonObject.getInteger("result");
                        if(result == 1) {
                            requestDeviceList();
                            ToastUtils.toast(getContext(),successInfo);
                        } else {
                            onFailure(-1,null);
                        }
                    } catch (Exception e){
                        onFailure(-1,null);
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    ToastUtils.toast(getContext(),"删除设备失败");
                    ProgressUtils.dismissDialog();
                }

                @Override
                public void onFinish() {
                    ProgressUtils.dismissDialog();
                }
            });
        } else {
            ToastUtils.toast(getContext(),failInfo);
            ProgressUtils.dismissDialog();
        }
    }

    /**
     * 发送管理员密码到服务器
     */
    private void sendAdminPassToServer() {
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone",LoginUtil.getAccount());
        jsonParam.putJSONParam("mac",PreferencesUtils.getString(Constant.WIFI_MAC));
        jsonParam.putJSONParam("pwd", PreferencesUtils.getString(Constant.DEVICE_PASS));
        HttpRequest.post(URL.MODIFY_DEVICE_NAME,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                try{
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        appHelper.finishActivity(CorrelationSettingActivity.class);
                        appHelper.finishActivity(DeviceControlActivity.class);
                        requestDeviceList();
                        ToastUtils.toast(getContext(),"修改密码成功");
                    } else {
                        onFailure(-1,null);
                    }
                }catch (Exception e) {
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getContext(),"修改密码失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }

    @Override
    public void onConnected() {
        requestDeviceList();
    }

    @Override
    public void onDisconnect() {

    }

    /**
     * 广播过滤器
     * @return IntentFilter 实例
     */
    private IntentFilter makeMsgFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ISocketClient.ACTION_SOCKET_MSG);
        return intentFilter;
    }

    /**
     * 更新设备回调接口
     */
    public interface IUpdateDeviceCallBack {
        /**
         * 更新设备列表方法
         * @param deviceList
         */
        void onUpdateDevice(List<DeviceBean> deviceList);

        /**
         * 重新请求数据
         */
        void onRequestData();
    }

    /**
     * 接收服务器发送过来的消息
     */
    class DeviceMsgReceiver extends AbsSocketMsgReceiver {

        @Override
        public void socketDataString(Context context,String data) {
            requestDeviceList();
        }

        @Override
        public void socketDataBytes(Context context,byte[] data) {
            switch (data[4]) {
                case IFunc.FACTORY_SETTING:
                    requestDeviceList();
                    break;
                case IFunc.IS_ONLINE_LOCK:
                    requestDeviceList();
                    AppUtil.setLockState("未开");
                    if(data[6] == 0x00) {
                        finishDeviceControlAllActivity();
                    }
                    break;
                case IFunc.MODIFY_PASS:
                    if(data[7] == 0x00) {
                        sendAdminPassToServer();
                    } else {
                        ToastUtils.toast(getContext(),"修改密码失败");
                        ProgressUtils.dismissDialog();
                    }
                    break;
                case IFunc.DELETE_ACCOUNT:
                    appAccountDeleteOrAddHandle(data[7],"删除成功","删除设备失败");
                    break;
                case IFunc.REQUEST_KEY:
                    if(data[6] == IFunc.MODIFY_PASS) {
                        sendAdminPassToDevice(data[7],data[8],
                                PreferencesUtils.getString(Constant.DEVICE_PASS),
                                PreferencesUtils.getString(Constant.DEVICE_ADMIN_PASS));
                    }
                    break;
                default:
                    break;

            }
        }
    }
}

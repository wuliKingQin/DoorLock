package cn.saiyi.doorlock.blls;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.blls.AbsBaseBusiness;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.activity.DeviceControlActivity;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.DeviceUtil;
import cn.saiyi.doorlock.device.ICheckAdminCallback;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.view.CInformDialog;

/**
 * 描述：设备控制业务实现类
 * 创建作者：黎丝军
 * 创建时间：2016/10/14 17:03
 */

public class DeviceControlBusiness extends AbsBaseBusiness {

    //设备实例
    private Device mDevice;
    //输入弹出框
    private CInformDialog mInputDialog;
    //UI更新回调接口
    private IUpdateUICallBack mUICallback;

    @Override
    public void initObject() {
        mDevice = new Device();
        mInputDialog = new CInformDialog();
    }

    @Override
    public void initData(Bundle bundle) {
        mInputDialog.create(getContext());
    }

    /**
     * 修改设备名方法
     * @param deviceBean 设备实例
     */
    public void modifyDeviceNameHandle(final DeviceBean deviceBean) {
        mInputDialog.setInputType(InputType.TYPE_CLASS_TEXT);
        mInputDialog.setSureButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mInputDialog.getInputText())) {
                    mInputDialog.cancelDialog();
                    requestModifyDeviceName(deviceBean);
                } else {
                    ToastUtils.toast(getContext(),"请先输入再按确认");
                }
            }
        });
        mInputDialog.showDialog();
    }

    /**
     * 请求修改设备名
     * @param deviceBean 设备实例
     */
    private void requestModifyDeviceName(DeviceBean deviceBean) {
        ProgressUtils.showDialog(getContext(),"正在修改中，……",null);
        JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone",LoginUtil.getAccount());
        jsonParam.putJSONParam("mac",deviceBean.getWifiMac());
        jsonParam.putJSONParam("name",mInputDialog.getInputText());
        HttpRequest.post(URL.MODIFY_DEVICE_NAME,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                try {
                    final int result = jsonObject.getInteger("result");
                    if (result == 1) {
                        //通知界面更新
                        mUICallback = ListenersMgr.getInstance().getListener(IUpdateUICallBack.class);
                        if (mUICallback != null) {
                            mUICallback.onUpdateView(mInputDialog.getInputText());
                        }
                        ToastUtils.toast(getContext(), "修改成功");
                    } else {
                        onFailure(-1, null);
                    }
                }catch (Exception e){
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getContext(),"修改失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 进入权限管理界面处理，主要判断用户是否是设备的主人
     * 如果不是则提示没有权限操作
     * @param deviceBean 设备实例
     */
    public void authorityCheckHandler(final DeviceBean deviceBean, final Intent intent) {
        DeviceUtil.authorityCheckHandler(getContext(), deviceBean.getWifiMac(), new ICheckAdminCallback() {
            @Override
            public void onAdmin() {
                getContext().startActivity(intent);
            }

            @Override
            public void onNotAdmin() {

            }
        });
    }

    /**
     * 查询门锁状态值
     */
    private void queryLockState() {
        mDevice.setDeviceMac(((DeviceControlActivity)getContext()).getDeviceBean().getWifiMac());
        mDevice.sendCmd(IFunc.QUERY_STATE, new ISendCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d("门锁状态查询发送成功");
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                LogUtils.d("门锁状态查询发送失败");
            }
        });
    }

    @Override
    public void onResume() {
        queryLockState();
    }

    /**
     * 获取设备实例
     * @return Device实例
     */
    public Device getDevice() {
        return mDevice;
    }
}

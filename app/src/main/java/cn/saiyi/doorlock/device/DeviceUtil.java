package cn.saiyi.doorlock.device;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.lisijun.websocket.interfaces.ISendMsgCallback;
import com.saiyi.framework.interfaces.ICancelRequestCallBack;
import com.saiyi.framework.util.PreferencesUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.view.CInformDialog;

/**
 * 描述：设备工具类，主要用来检查设备权限
 * 创建作者：黎丝军
 * 创建时间：2016/11/14 10:05
 */

public class DeviceUtil {

    /**
     * 检查设备权限处理方法
     * @param context 上下文
     * @param deviceMac 设备bean
     * @param callback 检查回调
     */
    public static void authorityCheckHandler(final Context context, String deviceMac, final ICheckAdminCallback callback) {
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone", LoginUtil.getAccount());
        jsonParam.putJSONParam("mac",deviceMac);
        ProgressUtils.showDialog(context, "权限检查中，……", new ICancelRequestCallBack() {
            @Override
            public void onCancel() {
                HttpRequest.cancel(URL.CHECKOUT_PHONE_MASTER);
            }
        });
        HttpRequest.post(URL.CHECKOUT_PHONE_MASTER,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>(){
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                try {
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        if(callback != null) {
                            callback.onAdmin();
                        }
                    } else {
                        if(callback != null) {
                            callback.onNotAdmin();
                        }
                        onFailure(Constant.ERROR_CODE,"你不是设备的主人，无权操作");
                    }
                } catch (Exception e){
                    onFailure(Constant.ERROR_CODE,"服务器错误");
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                if(errorCode == Constant.ERROR_CODE) {
                    ToastUtils.toast(context,msg);
                } else {
                    ToastUtils.toast(context,"网络错误");
                }
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 添加设备到服务器
     * @param deviceName 设备名
     * @param deviceMac 设备mac
     * @param callback 发送回调
     */
    public static void addDeviceToServerHandler(String deviceName, String deviceMac, final ISendMsgCallback callback) {
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone", LoginUtil.getAccount());
        jsonParam.putJSONParam("mac", deviceMac);
        jsonParam.putJSONParam("dimg","ic_device_icon");
        jsonParam.putJSONParam("name",deviceName == null ? "门锁_" + deviceMac:deviceName);
        HttpRequest.post(URL.ADD_DEVICE,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject resultInfo) {
                try {
                    final int result = resultInfo.getInteger("result");
                    if(result == 1) {
                        if(callback != null) {
                            callback.onSuccess();
                        }
                    } else if(result == 2){
                        onFailure(result,"设备已经被其他用户绑定");
                    } else {
                        onFailure(Constant.ERROR_CODE,"绑定设备失败");
                    }
                } catch (Exception e) {
                    onFailure(Constant.ERROR_CODE,"绑定设备失败");
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                if(callback != null) {
                    callback.onFail(errorCode,msg);
                }
            }
        });
    }

    /**
     * 修改设备管理员密码处理方法，该方是直接先和服务器交互，根据结果再和设备交互
     * @param context 运行环境
     * @param hintTitle 提示标题 ，目前只有添加或者修改
     * @param isClearInput 是否清空输入框
     * @param deviceBean 设备实例
     * @param callback 接口回调
     */
    public static void modifyDevicePassHandle(final Context context, final String hintTitle,
                                              boolean isClearInput, final DeviceBean deviceBean,
                                              final ISendCallback callback) {
        if(callback == null || deviceBean == null) return;
        final CInformDialog inputDialog = new CInformDialog();
        inputDialog.create(context);
        inputDialog.setInputHintText("请输入8位数密码");
        if(isClearInput) {
            inputDialog.setInputText("");
        } else {
            inputDialog.setInputText(deviceBean.getAdminPass());
        }
        inputDialog.setDialogTitle(hintTitle + "管理员密码");
        inputDialog.setSureButtonText("确认" + hintTitle);
        inputDialog.setSureButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(inputDialog.getInputText())) {
                    if(inputDialog.getInputText().length() == 8 &&
                            !TextUtils.equals(inputDialog.getInputText(),deviceBean.getAdminPass())) {
                        inputDialog.cancelDialog();
                        ProgressUtils.showDialog(context, "正在" + hintTitle + "中，……", new ICancelRequestCallBack() {
                            @Override
                            public void onCancel() {
                                HttpRequest.cancel(URL.ROOT_RUL);
                            }
                        });
                        final JSONParam jsonParam = new JSONParam();
                        jsonParam.putJSONParam("phone",LoginUtil.getAccount());
                        jsonParam.putJSONParam("mac",deviceBean.getWifiMac());
                        jsonParam.putJSONParam("ypwd",deviceBean.getAdminPass());
                        jsonParam.putJSONParam("pwd",inputDialog.getInputText());
                        HttpRequest.post(URL.MODIFY_DEVICE_NAME,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
                            @Override
                            protected void onSuccess(JSONObject jsonObject) {
                                try {
                                    final int result = jsonObject.getInteger("result");
                                    if (result == 1) {
                                        //添加成功
                                        callback.onSuccess();
                                    } else {
                                        onFailure(Constant.ERROR_CODE, null);
                                    }
                                }catch (Exception e){
                                    onFailure(Constant.ERROR_CODE,null);
                                }
                            }

                            @Override
                            public void onFailure(int errorCode, String msg) {
                                String errorInfo = hintTitle + "管理员密码失败";
                                if(errorCode != Constant.ERROR_CODE) {
                                    errorInfo = "网络错误";
                                }
                                callback.onError(Constant.ERROR_CODE,errorInfo);
                            }
                        });
                    } else {
                        callback.onError(Constant.ERROR_CODE,"管理员密码必须8位");
                    }
                } else {
                    callback.onError(Constant.ERROR_CODE,"请先输入再按确认");
                }
            }
        });
        inputDialog.showDialog();
    }

    /**
     * 修改设备管理员密码处理方法，该方是直接先和设备交互，根据结果再和服务器交互
     * @param context 运行环境
     * @param hintTitle 提示标题 ，目前只有添加或者修改
     * @param isClearInput 是否清空输入框
     * @param deviceBean 设备实例
     * @param callback 接口回调
     */
    public static void modifyDevicePassHandle(final Device device,final Context context, final String hintTitle,
                                              boolean isClearInput, final DeviceBean deviceBean,
                                              final ISendCallback callback) {
        if (callback == null || deviceBean == null) return;
        final CInformDialog inputDialog = new CInformDialog();
        device.setDeviceMac(deviceBean.getWifiMac());
        inputDialog.create(context);
        inputDialog.setInputHintText("请输入8位数密码");
        if (isClearInput) {
            inputDialog.setInputText("");
        } else {
            inputDialog.setInputHintText(deviceBean.getAdminPass());
        }
        inputDialog.setDialogTitle(hintTitle + "管理员密码");
        inputDialog.setSureButtonText("确认" + hintTitle);
        inputDialog.setSureButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(inputDialog.getInputText())) {
                    if (inputDialog.getInputText().length() == 8) {
                        inputDialog.cancelDialog();
                        device.sendCmd(IFunc.REQUEST_KEY, new byte[]{0x32}, new ISendCallback() {
                            @Override
                            public void onSuccess() {
                                ProgressUtils.showDialog(context, "正在" + hintTitle + "中，……", new ICancelRequestCallBack() {
                                    @Override
                                    public void onCancel() {
                                        HttpRequest.cancel(URL.ROOT_RUL);
                                    }
                                });
                                PreferencesUtils.putString(Constant.WIFI_MAC,deviceBean.getWifiMac());
                                PreferencesUtils.putString(Constant.DEVICE_PASS,inputDialog.getInputText());
                                PreferencesUtils.putString(Constant.DEVICE_ADMIN_PASS,deviceBean.getAdminPass());
                                callback.onSuccess();
                            }

                            @Override
                            public void onError(int errorCode, String errorInfo) {
                                callback.onError(errorCode,errorInfo);
                            }
                        });
                    } else {
                        if(inputDialog.getInputText().length() != 8 ) {
                            callback.onError(Constant.ERROR_CODE,"管理员密码必须8位");
                        } else {
                            callback.onError(Constant.ERROR_CODE,"请先修改密码再试");
                        }
                    }
                } else {
                    callback.onError(Constant.ERROR_CODE,"请先输入再按确认");
                }
            }
        });
        inputDialog.showDialog();
    }
}

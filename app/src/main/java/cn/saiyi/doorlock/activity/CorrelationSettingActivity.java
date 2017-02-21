package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lisijun.websocket.socket.OnSocketMsgListener;
import com.saiyi.framework.AppHelper;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.PreferencesUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.bean.SettingBean;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.DeviceUtil;
import cn.saiyi.doorlock.device.ICheckAdminCallback;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.device.Result;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.view.CInformDialog;

/**
 * 描述：相关设置界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 14:40
 */

public class CorrelationSettingActivity extends AbsBaseActivity
        implements OnSocketMsgListener{

    //查看密码
    private TextView mLookPassBtn;
    //无人模式设置
    private CheckBox mNonePeopleCkb;
    //设备bean
    private DeviceBean mDeviceBean;
    //设备
    private Device mDevice;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_correlation_setting);
    }

    @Override
    public void findViews() {
        mLookPassBtn = getViewById(R.id.tv_setting_look_pass);
        mNonePeopleCkb = getViewById(R.id.cb_correlation_none_people);
    }

    @Override
    public void initObjects() {
        mDeviceBean = (DeviceBean)getIntent().getSerializableExtra("deviceBean");
        mDevice = new Device();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.control_setting);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mDevice.setDeviceMac(mDeviceBean.getWifiMac());
        initSwitchValue();
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNonePeopleCkb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data;
                if(mNonePeopleCkb.isChecked()) {
                    data = new byte[]{0x03};
                } else {
                    data = new byte[]{0x0c};
                }
                mDevice.sendCmd(IFunc.SETTING_MODE, data, new ISendCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.d("发送设置无人模式成功");
                    }

                    @Override
                    public void onError(int errorCode, String errorInfo) {
                        mNonePeopleCkb.setChecked(!mNonePeopleCkb.isChecked());
                        ToastUtils.toast(getBaseContext(),"设置失败");
                    }
                });
            }
        });
        mLookPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtil.authorityCheckHandler(CorrelationSettingActivity.this, mDeviceBean.getWifiMac(), new ICheckAdminCallback() {
                    @Override
                    public void onAdmin() {
                        modifyDevicePassHandle();
                    }

                    @Override
                    public void onNotAdmin() {
                    }
                });
            }
        });
        mDevice.registerMsgListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.unregisterMsgListener(this);
    }

    /**
     * 修改管理员密码
     */
    private void modifyDevicePassHandle() {
        DeviceUtil.modifyDevicePassHandle(mDevice,this, "修改", false, mDeviceBean, new ISendCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d("发送加密请求成功");
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                ToastUtils.toast(getBaseContext(),errorInfo);
                ProgressUtils.dismissDialog();
            }
        });

//        DeviceUtil.modifyDevicePassHandle(this, "修改", false, mDeviceBean, new ISendCallback() {
//            @Override
//            public void onSuccess() {
//                ToastUtils.toast(getBaseContext(),"修改管理员密码成功");
//            }
//
//            @Override
//            public void onError(int errorCode, String errorInfo) {
//                ToastUtils.toast(getBaseContext(),errorInfo);
//                ProgressUtils.dismissDialog();
//            }
//        });
    }

    /**
     * 初始化开关值
     */
    private void initSwitchValue() {
        mNonePeopleCkb.setChecked(mDeviceBean.isUnmanned());
    }


    @Override
    protected boolean isActionBar() {
        return true;
    }

    /**
     * 处理无人模式点击事件
     * @param isChecked
     */
    private void nonePeopleHandle(final boolean isChecked) {
        JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("mac",mDeviceBean.getWifiMac());
        jsonParam.putJSONParam("phone",LoginUtil.getAccount());
        jsonParam.putJSONParam("unmanned",isChecked == true ? 1:0);
        HttpRequest.post(URL.MODIFY_DEVICE_NAME,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
               try {
                   final int result = jsonObject.getInteger("result");
                   if(result != 1) {
                       onFailure(result,null);
                   } else {
                       AppHelper.instance().finishActivity(DeviceControlActivity.class);
                       finish();
                       ToastUtils.toast(getBaseContext(),"设置成功");
                   }
               } catch (Exception e) {
                   onFailure(-1,null);
               }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                mNonePeopleCkb.setChecked(!isChecked);
                ToastUtils.toast(getBaseContext(),"设置失败");
            }

        });
    }

    @Override
    public void onReceiveMsg(String deviceName, String mac, String cmd) {
        if(cmd != null && cmd.toUpperCase().startsWith("AA55")) {
            final byte[] result = EncodeUtil.hexStrToBytes(cmd.toUpperCase());
            if(result[4] == IFunc.SETTING_MODE) {
                if(result[6] == 0x00) {
                    nonePeopleHandle(mNonePeopleCkb.isChecked());
                } else {
                    setNonePeople(result[7]);
                }
            }
        }
    }

    private void setNonePeople(byte result) {
        if(result == 0x03) {
            mNonePeopleCkb.setChecked(true);
        } else {
            mNonePeopleCkb.setChecked(false);
        }
    }
}

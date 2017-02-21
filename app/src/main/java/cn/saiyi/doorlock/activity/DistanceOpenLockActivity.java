package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lisijun.fingerprint.dialog.FingerprintDialog;
import com.lisijun.fingerprint.work.FingerprintBusiness;
import com.lisijun.fingerprint.work.IFingerBussCallback;
import com.lisijun.websocket.socket.OnSocketMsgListener;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.device.Result;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;
import cn.saiyi.doorlock.util.PassUtil;

/**
 * 描述：远程开锁
 * 创建作者：黎丝军
 * 创建时间：2016/10/17 14:05
 */

public class DistanceOpenLockActivity extends AbsBaseActivity
        implements OnSocketMsgListener,IFingerBussCallback {

    //锁状态
    private TextView mLockStateTv;
    //开锁密码
    private EditText mPassEdt;
    //拒绝按钮
    private Button mRefuseBtn;
    //开锁按钮
    private Button mOpenLockBtn;
    //判断是否是远程
    private boolean isDistance = false;
    //设备实例
    private Device mDevice;
    //设备实例
    private DeviceBean mDeviceBean;
    //开门密码
    private String mOpenDoorPass;
    //指纹识别业务
    private FingerprintBusiness mFingerBuss;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_distance_open_lock);
    }

    @Override
    public void findViews() {
        mPassEdt = getViewById(R.id.edt_distance_password);
        mRefuseBtn = getViewById(R.id.btn_distance_refuse);
        mOpenLockBtn = getViewById(R.id.btn_distance_open_lock);
        mLockStateTv = getViewById(R.id.tv_distance_lock_state);
    }

    @Override
    public void initObjects() {
        mDevice = new Device();
        mFingerBuss = new FingerprintBusiness(this);
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra(Constant.DEVICE_BEAN);
        isDistance = getIntent().getBooleanExtra("isDistance",false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.control_long_distance);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mLockStateTv.setText(mDeviceBean.isOnLine() == true ? "已激活":"未激活");

        mDevice.setDeviceMac(mDeviceBean.getWifiMac());
        if(PassUtil.isFingerOpenLock() && !isDistance) {
            mFingerBuss.setIFingerBussCallback(this);
            mFingerBuss.startFingerDetection();
        }
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        mRefuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        mOpenLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLockHandle();
            }
        });
        mDevice.registerMsgListener(this);
    }

    //判断是否是远程命令开锁
    private void closeActivity() {
        if(isDistance) {
            startActivity(new Intent(DistanceOpenLockActivity.this,MainActivity.class));
        } else {
            finish();
        }
    }

    /**
     * 开锁处理方法
     */
    private void openLockHandle() {
        mOpenDoorPass = mPassEdt.getText().toString().trim();
        if(!TextUtils.isEmpty(mOpenDoorPass)) {
            sendEncryptHandle();
        } else {
            ToastUtils.toast(this,"你还没有输入密码");
        }
    }

    /**
     * 发送加密请求
     */
    private void sendEncryptHandle() {
        ProgressUtils.showDialog(this,"正在开门中，……",true,null);
        mDevice.sendCmd(IFunc.REQUEST_KEY,new byte[]{IFunc.OPEN_DOOR},new ISendCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d("发送远程开门加密成功");
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                ProgressUtils.dismissDialog();
                ToastUtils.toast(getBaseContext(),"远程开门失败");
            }
        });
    }

    /**
     * 发送远程开门
     * @param k1 密匙k1
     * @param k2 密匙k2
     */
    private void sendDistanceOpenDoorHandle(byte k1, byte k2) {
        final byte[] data = new byte[]{0x01};
        byte[] ePass = EncodeUtil.strToBytes(mOpenDoorPass);
        ePass = EncryptUtil.encrypt(k1, k2, ePass);
        mDevice.sendCmd(IFunc.OPEN_DOOR, EncryptUtil.mergeArray(data,ePass), new ISendCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                ProgressUtils.dismissDialog();
                ToastUtils.toast(getBaseContext(),"远程开门失败");
            }
        });
    }


    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.unregisterMsgListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivity();
        }
        return true;
    }

    @Override
    public void onReceiveMsg(String deviceName, String mac, String cmd) {
        final byte[] result = EncodeUtil.hexStrToBytes(cmd.toUpperCase());
        switch (result[4]) {
            case IFunc.REQUEST_KEY:
                if(result[6] == IFunc.OPEN_DOOR) {
                    sendDistanceOpenDoorHandle(result[7],result[8]);
                }
                break;
            case IFunc.OPEN_DOOR:
                ProgressUtils.dismissDialog();
                final Result result1 = new Result() {
                    @Override
                    public void onSuccess(String hintInfo) {
                        ToastUtils.toast(getBaseContext(), hintInfo);
                        finish();
                    }

                    @Override
                    protected void onFail(String failInfo) {
                        ToastUtils.toast(getBaseContext(), failInfo);
                    }
                };
                result1.resultHandle(result[7]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPre(FingerprintDialog hintDialog) {
    }

    @Override
    public void onSuccess(FingerprintManagerCompat.AuthenticationResult result) {
        if(mDeviceBean != null) {
            mOpenDoorPass = mDeviceBean.getAdminPass();
        }
        sendEncryptHandle();
    }

    @Override
    public boolean onFail(FingerprintDialog hintDialog, int failCode, String failInfo) {
        hintDialog.setResultHint(R.string.fingerprint_error);
        return false;
    }

    @Override
    public void onCheckSupport(String info) {
        ToastUtils.toast(this,info);
    }
}

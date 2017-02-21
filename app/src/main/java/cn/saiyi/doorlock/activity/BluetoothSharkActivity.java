package cn.saiyi.doorlock.activity;

import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisijun.bluetooth.impl.BluetoothController;
import com.lisijun.bluetooth.impl.BluetoothScanner;
import com.lisijun.bluetooth.impl.ByteParam;
import com.lisijun.bluetooth.interfaces.IBleRunCallback;
import com.lisijun.bluetooth.interfaces.ISendCallback;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.interfaces.ICancelRequestCallBack;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.BleByteParams;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.sensor.AccelerateSensor;
import cn.saiyi.doorlock.sensor.ISensorResultCallback;
import cn.saiyi.doorlock.sensor.SimpleResultCallback;
import cn.saiyi.doorlock.util.AppUtil;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;

/**
 * 描述：蓝牙部分摇一摇开锁
 * 创建作者：黎丝军
 * 创建时间：2016/10/31 11:25
 */

public class BluetoothSharkActivity extends AbsBaseActivity
        implements IBleRunCallback {

    //蓝牙地址
    private String mBleAddress;
    //摇一摇图片，用来使动起来
    private ImageView mSharkIconIv;
    //开锁状态
    private TextView mLockStateTv;
    //保存管理员密码
    private String mAdminPass;
    //用来标记是否发送开锁命令
    private boolean isSendOpenLock = false;
    //加速传感器
    private AccelerateSensor mAccelerateSensor;
    //蓝牙控制器
    private BluetoothController mBleController;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_bluetooth_shark);
    }

    @Override
    public void findViews() {
        mSharkIconIv = getViewById(R.id.iv_bluetooth_shark_lock);
        mLockStateTv = getViewById(R.id.tv_bluetooth_lock_state);
    }

    @Override
    public void initObjects() {
        mBleAddress = getIntent().getStringExtra("bleAddress");
        mAdminPass = getIntent().getStringExtra(Constant.DEVICE_ADMIN_PASS);
        mBleController = new BluetoothController(this,mBleAddress);
        mAccelerateSensor = new AccelerateSensor(this,mSharkCallback);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.bluetooth_shark_lock);
        setTitleSize(Constant.TEXT_SIZE);
        setTitleColor(R.color.color7);
        setActionBarBackgroundColor(Color.WHITE);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mBleController.registerCallback(this);
        mBleController.setDelayedTime(600);
        mBleController.setSendTimeOut(5 * 1000);
        mBleController.setUuidHeads("753f1997af00","753f1997af01","753f1997af02");
        mBleController.setNotifyDescriptorUuidHead("00002902");
        mBleController.bindBleService();

        mLockStateTv.setText(AppUtil.getLockState());
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBleController.registerMsgReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleController.closeBle();
        mBleController.unRegisterMsgReceiver();
        mBleController.unbindBleService();
        mAccelerateSensor.unRegisterListener();

    }

    /**
     * 实现摇一摇回调接口
     */
    private final ISensorResultCallback mSharkCallback = new SimpleResultCallback() {
        @Override
        public void onSharkResult(double speed) {
            if(!isSendOpenLock) {
                sharkAnimation();
                final BleByteParams byteParams = new BleByteParams();
                byteParams.setHeadByte(0xa5,0x5a);
                byteParams.setTypeByte(0x02,0x01);
                byteParams.setCmdByte(0x30);
                byteParams.setFrameNum(0x00);
                isSendOpenLock = true;
                isSendOpenLock = true;
                ProgressUtils.showDialog(BluetoothSharkActivity.this, "正在开门中，……",true,null);
                mBleController.sendData(byteParams.getValues(), new ISendCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFail() {
                        isSendOpenLock = false;
                        ToastUtils.toast(getBaseContext(),"发送失败");
                        ProgressUtils.dismissDialog();
                    }
                });
            }
        }
    };

    /**
     * 摇一摇结果动画
     */
    public void sharkAnimation() {
        mAccelerateSensor.playShark();
        mSharkIconIv.startAnimation(AnimationUtils.loadAnimation(this, com.lisijun.fingerprint.R.anim.shake));
    }

    @Override
    public void onInitBleServiceFinish(String info) {
        connectBluetooth();
    }

    @Override
    public void onConnSuccess(List<BluetoothGattService> services) {
        ProgressUtils.dismissDialog();
        ToastUtils.toast(this,"连接成功");
        mAccelerateSensor.registerListener();
    }

    @Override
    public void onConnFail(String failInfo) {
        ProgressUtils.dismissDialog();
        ToastUtils.toast(this,failInfo);
        finish();
    }

    @Override
    public void onReceiveRssi(int rssi) {

    }

    @Override
    public void onReceiveData(String uuid, ByteParam data) {
        if(data.size() > 4) {
            if(data.getByte(4) == 0x30) {
                sendOpenLockPassHandle(data.getByte(7),data.getByte(8));
            } else if(data.getByte(4) == 0x34) {
                final byte result = data.getByte(8);
                switch (result) {
                    case 0x00:
                        ToastUtils.toast(this,"开锁成功");
                        mLockStateTv.setText("已开");
                        finish();
                        break;
                    case 0x03:
                        ToastUtils.toast(this,"密码错误");
                        break;
                    case 0x04:
                        ToastUtils.toast(this,"无权开锁");
                        finish();
                        break;
                    case 0x08:
                    default:
                        ToastUtils.toast(this,"开锁失败");
                        break;
                }
            }
            isSendOpenLock = false;
            ProgressUtils.dismissDialog();
        }
    }

    @Override
    public byte getFunc() {
        return Byte.MAX_VALUE;
    }

    @Override
    public int getFuncPosition() {
        return 0;
    }

    /**
     * 发送加密后的密码给蓝牙外设
     * @param k1 加密方式一
     * @param k2 加密方式二
     */
    private void sendOpenLockPassHandle(byte k1, byte k2) {
        final byte[] ePass = EncryptUtil.encrypt(k1, k2, EncodeUtil.strToBytes(mAdminPass));
        final BleByteParams byteParams = new BleByteParams();
        byteParams.setHeadByte(0xa5, 0x5a);
        byteParams.setTypeByte(0x02, 0x01);
        byteParams.setCmdByte(0x34);
        byteParams.setFrameNum(0x00);
        byteParams.putInt(0x01);
        byteParams.putBytes(ePass);
        mBleController.sendData(byteParams.getValues(), new ISendCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail() {
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果手机没有打开蓝牙，那么调用checkBluetoothEnable()方法后会走到这里，
        //所以你这里需要简单处理一下,如下：
        if (requestCode == BluetoothScanner.REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            //处理如果用户选择取消打开蓝牙，那么你要结束掉该界面
            ToastUtils.toast(this,"请打开蓝牙，否则无法连接设备");
            finish();
        } else {
            connectBluetooth();
        }
    }

    /**
     * 连接蓝牙
     */
    private void connectBluetooth() {
        ProgressUtils.showDialog(this, "正在连接设备，……", new ICancelRequestCallBack() {
            @Override
            public void onCancel() {
                mBleController.closeBle();
            }
        });
        try {
            //如果用户选择打开的处理，这个按自己需要进行处理
            mBleController.connectBleDevice();
        } catch (Exception e) {
            onConnFail("蓝牙地址不正确");
        }
    }
}

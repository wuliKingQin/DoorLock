package cn.saiyi.doorlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lisijun.websocket.socket.OnSocketMsgListener;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.blls.DeviceControlBusiness;
import cn.saiyi.doorlock.broadcast.AbsSocketMsgReceiver;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.StateType;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.listenerimpl.DeviceControlListenerImpl;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.AppUtil;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.view.BatteryShowView;

/**
 * 描述：设备控制界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 17:48
 */

public class DeviceControlActivity extends AbsBaseActivity
        implements IUpdateUICallBack{

    //设备实例
    private Device mDevice;
    //设备数据
    private DeviceBean mDeviceBean;
    //wifi状态显示
    private CheckBox mWifiStateCkb;
    //蓝色状态显示
    private CheckBox mBluetoothStateCkb;
    //设置电池电量显示
    private BatteryShowView mBatteryShowView;
    //锁状态显示
    private TextView mLockStateTv;
    //锁状态图显示
    private CheckBox mLockStateCkb;
    //改名
    private TextView mChangeNameBtn;
    //一次性密码
    private TextView mOnePassBtn;
    //远程开锁
    private TextView mLongDistanceBtn;
    //摇一摇开锁
    private TextView mSharkBtn;
    //开锁记录
    private TextView mRecordBtn;
    //时效密码
    private TextView mAgingPassBtn;
    //开锁记录
    private TextView mOpenLockBtn;
    //消息接收器
    private ControllerMsgReceiver mMsgReceiver;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_device_control);
    }

    @Override
    public void findViews() {
        mWifiStateCkb = getViewById(R.id.ckb_control_wifi);
        mBluetoothStateCkb = getViewById(R.id.ckb_control_bluetooth);
        mBatteryShowView = getViewById(R.id.btv_batteryShow);
        mLockStateTv = getViewById(R.id.tv_control_lock_state);
        mLockStateCkb = getViewById(R.id.ckb_control_lock_state);
        mChangeNameBtn = getViewById(R.id.tv_control_change_name);

        mOnePassBtn = getViewById(R.id.tv_control_one_pass);
        mLongDistanceBtn = getViewById(R.id.tv_control_long_distance);
        mSharkBtn = getViewById(R.id.tv_control_shark);
        mRecordBtn = getViewById(R.id.tv_control_authority_mgr);
        mAgingPassBtn = getViewById(R.id.tv_control_aging_pass);
        mOpenLockBtn = getViewById(R.id.tv_control_open_lock_record);
    }

    @Override
    public void initObjects() {
        mDevice = new Device();
        mMsgReceiver = new ControllerMsgReceiver();
        ListenersMgr.getInstance().registerListener(IUpdateUICallBack.class,this);
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra("device");
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        registerReceiver(mMsgReceiver, AppUtil.makeMsgFilter());
        if(mDeviceBean != null) {
            setTitle(mDeviceBean.getName());
            mChangeNameBtn.setText(mDeviceBean.getName());
        } else {
            setTitle(R.string.control_title);
            mChangeNameBtn.setText(R.string.control_title);
        }
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_white_back,25,25);
        actionBar.setRightButtonBackground(R.mipmap.ic_setting,25,25);

        mBatteryShowView.setGradePowerValue((byte)0x03);

        mBluetoothStateCkb.setChecked(TextUtils.equals(mDeviceBean.getBleAddress(),"0") == true ? false:true);
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_ACTION_BAR_LEFT_CLICK,actionBar);
        registerListener(IListener.ON_ACTION_BAR_RIGHT_CLICK,actionBar);
        registerListener(IListener.ON_CLICK, mChangeNameBtn);
        registerListener(IListener.ON_CLICK,mOnePassBtn);
        registerListener(IListener.ON_CLICK,mLongDistanceBtn);
        registerListener(IListener.ON_CLICK,mSharkBtn);
        registerListener(IListener.ON_CLICK,mRecordBtn);
        registerListener(IListener.ON_CLICK,mAgingPassBtn);
        registerListener(IListener.ON_CLICK,mOpenLockBtn);
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new DeviceControlListenerImpl();
        }
        return super.getListener();
    }

    @Override
    protected IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new DeviceControlBusiness();
        }
        return super.getBusiness();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMsgReceiver);
        ListenersMgr.getInstance().unRegisterListener(IUpdateUICallBack.class);
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    /**
     * 获取设备实例
     * @return 设备实例
     */
    public DeviceBean getDeviceBean() {
        return mDeviceBean;
    }

    @Override
    public void onUpdateView(Object data) {
        mDeviceBean.setName(String.valueOf(data));
        setTitle(mDeviceBean.getName());
        mChangeNameBtn.setText(mDeviceBean.getName());
    }

    /**
     * 控制界面消息接收器
     */
    class ControllerMsgReceiver extends AbsSocketMsgReceiver {

        @Override
        public void socketDataBytes(Context context,byte[] data) {
            switch (data[4]) {
                case IFunc.AUTO_UPLOAD_STATE:
                    if(data[6] == StateType.BATTERY) {
                        mBatteryShowView.setGradePowerValue(data[7]);
                    }
                    if(data[6] == StateType.DOOR_STATE){
                        String state = "已开";
                        if(data[7] == (byte) 0x01) {
                            state = "未开";
                            mLockStateCkb.setChecked(false);
                        } else if(data[7] == (byte) 0x00){
                            mLockStateCkb.setChecked(true);
                        }
                        AppUtil.setLockState(state);
                        mLockStateTv.setText(state);
                    }
                    break;
                case IFunc.QUERY_STATE:
                    mBatteryShowView.setGradePowerValue(data[6]);
                    String state = "已开";
                    if(data[7] == (byte) 0x01) {
                        state = "未开";
                        mLockStateCkb.setChecked(false);
                    } else {
                        mLockStateCkb.setChecked(true);
                    }
                    AppUtil.setLockState(state);
                    mLockStateTv.setText(state);
                    break;
                case IFunc.IS_ONLINE_LOCK:
                    if(data[6] == 0x01) {
                        ToastUtils.toast(context,"设备在线了");
                    } else {
                        ToastUtils.toast(context,"设备开始休眠");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

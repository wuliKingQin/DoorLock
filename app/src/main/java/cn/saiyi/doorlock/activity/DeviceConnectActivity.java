package cn.saiyi.doorlock.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;
import com.hiflying.smartlink.v7.MulticastSmartLinker;
import com.lisijun.websocket.interfaces.ISendMsgCallback;
import com.saiyi.framework.AppHelper;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ToastUtils;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.device.DeviceUtil;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.view.CircleStaticProgressView;

/**
 * 描述：设备连接界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 8:52
 */

public class DeviceConnectActivity extends AbsBaseActivity
        implements OnSmartLinkListener {

    //保存设备mac
    private String mDeviceMac;
    //设备名
    private String mDeviceName;
    //用于进度
    private int sumProgress;
    //进度定时器
    private Timer mProgressTimer;
    //wifi名
    private String wifiName;
    //wifi密码
    private String wifiPass;
    //取消按钮
    private Button mCancelBtn;
    //用于更新界面
    private Handler mViewHandler = new Handler();
    //wifi配置
    protected ISmartLinker mSnifferSmartLinker;
    //进度按钮
    private CircleStaticProgressView mProgressCp;
    //进度任务
    private TimerTask mProgressTask = new TimerTask() {

        public void run() {
            if(sumProgress < 40) {
                sumProgress += new Random().nextInt(10);
            } else if(sumProgress >= 40 && sumProgress < 80) {
                sumProgress += new Random().nextInt(5);
            } else if(sumProgress >= 80 && sumProgress < 99){
                sumProgress += new Random().nextInt(2);
            } else {
            }
            mViewHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressCp.setTextProgress(sumProgress);
                }
            });
        }
    };

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_device_connect);
    }

    @Override
    public void findViews() {
        mProgressCp = getViewById(R.id.cp_connect_progress);
        mCancelBtn = getViewById(R.id.btn_connect_cancel);

    }

    @Override
    public void initObjects() {
        mProgressTimer = new Timer("ProgressTimer");
        wifiName = getIntent().getStringExtra("wifiName");
        wifiPass = getIntent().getStringExtra("wifiPass");
        mDeviceName = getIntent().getStringExtra("deviceName");
        mSnifferSmartLinker = MulticastSmartLinker.getInstance();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.device_connect_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mSnifferSmartLinker.setTimeoutPeriod(30 * 1000);
        mSnifferSmartLinker.setOnSmartLinkListener(this);
        try {
            mSnifferSmartLinker.start(this,wifiPass,wifiName);
            mProgressTimer.schedule(mProgressTask,0,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelConfigHandle();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelConfigHandle();
            }
        });
    }

    //停值
    private void stopTimer() {
        if(mProgressTimer != null) {
            mProgressTask.cancel();
            mProgressTimer.cancel();
            mProgressTimer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelConfigHandle();
        }
        return true;
    }

    /**
     * 取消配网处理方法
     */
    private void cancelConfigHandle() {
        stopTimer();
        mSnifferSmartLinker.setOnSmartLinkListener(null);
        mSnifferSmartLinker.stop();
        finish();
    }

    @Override
    public void onLinked(final SmartLinkedModule smartLinkedModule) {
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                mDeviceMac = smartLinkedModule.getMac();
                ToastUtils.toast(getBaseContext(),"发现设备");
            }
        });
    }

    @Override
    public void onCompleted() {
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toast(getBaseContext(),"设备配网完成");
                if(!TextUtils.isEmpty(mDeviceName)) {
                    addDeviceToServer(mDeviceMac);
                } else {
                    cancelConfigHandle();
                    final Activity activity  = AppHelper.instance().getActivity(WifiBindActivity.class);
                    if(activity != null) {
                        activity.finish();
                    }
                }
            }
        });
    }

    @Override
    public void onTimeOut() {
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelConfigHandle();
                ToastUtils.toast(getBaseContext(),"配置网络超时");
            }
        });
    }

    /**
     * 添加设备到服务器
     */
    private void addDeviceToServer(String deviceMac) {
        DeviceUtil.addDeviceToServerHandler(mDeviceName, deviceMac, new ISendMsgCallback() {
            @Override
            public void onSuccess() {
                mProgressCp.setTextProgress(100);
                cancelConfigHandle();
                final Activity activity  = AppHelper.instance().getActivity(WifiBindActivity.class);
                if(activity != null) {
                    activity.finish();
                }
                final AddDeviceActivity addDeviceActivity = (AddDeviceActivity)AppHelper.instance().getActivity(AddDeviceActivity.class);
                if(addDeviceActivity != null) {
                    addDeviceActivity.setResult(20,null);
                    addDeviceActivity.finish();
                }
                ToastUtils.toast(getBaseContext(),"添加设备成功");
            }

            @Override
            public void onFail(int errorCode,String error) {
                cancelConfigHandle();
                if(errorCode == 2 || errorCode == Constant.ERROR_CODE) {
                    ToastUtils.toast(getBaseContext(),error);
                } else {
                    ToastUtils.toast(getBaseContext(),"请求失败");
                }
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

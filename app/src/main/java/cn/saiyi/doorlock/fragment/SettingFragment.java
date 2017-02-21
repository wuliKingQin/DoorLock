package cn.saiyi.doorlock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lisijun.gesture.interfaces.GConstant;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.fragment.BaseFragment;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.util.PreferencesUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.SettingBusiness;
import cn.saiyi.doorlock.listenerimpl.SettingListenerImpl;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.PassUtil;

/**
 * 描述：设置界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/11 18:06
 */

public class SettingFragment extends BaseFragment {

    //添加手势密码
    private TextView mAddGesturePassBtn;
    //app更新检测
    private TextView mAppUpdateBtn;
    //帮助
    private TextView mHelperBtn;
    //关于我们
    private TextView mAboutUsBtn;
    //意见反馈
    private TextView mOpinionBtn;
    //假锁报警
    private CheckBox mFalseLockAlarmCkb;
    //防撬报警
    private CheckBox mPryAlarmCkb;
    //指纹开锁开关
    private CheckBox mFingerprintSwitchChb;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void findViews() {
        mAddGesturePassBtn = getViewById(R.id.tv_add_gesture_pass);
        mAppUpdateBtn = getViewById(R.id.tv_setting_app_update);
        mHelperBtn = getViewById(R.id.tv_setting_helper);
        mAboutUsBtn = getViewById(R.id.tv_setting_about);
        mFalseLockAlarmCkb = getViewById(R.id.cb_correlation_false_lock_alarm);
        mPryAlarmCkb = getViewById(R.id.cb_correlation_pry_alarm);
        mFingerprintSwitchChb = getViewById(R.id.cb_fingerprint_switch);
        mOpinionBtn = getViewById(R.id.tv_setting_opinion);
    }

    @Override
    public void initObjects() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.setting_title);
        setTitleSize(Constant.TEXT_SIZE);
        setTitleColor(R.color.color7);
        setActionBarBackgroundColor(Color.WHITE);

        mFingerprintSwitchChb.setChecked(PassUtil.isFingerOpenLock());
        mFalseLockAlarmCkb.setChecked(PassUtil.isOpenFalseLockAlarm());
        mPryAlarmCkb.setChecked(PassUtil.isOpenPryAlarm());
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_CLICK,mAddGesturePassBtn);
        registerListener(IListener.ON_CLICK,mAppUpdateBtn);
        registerListener(IListener.ON_CLICK,mHelperBtn);
        registerListener(IListener.ON_CLICK,mAboutUsBtn);
        registerListener(IListener.ON_CLICK,mOpinionBtn);
        registerListener(IListener.ON_CHECK,mFingerprintSwitchChb);
        registerListener(IListener.ON_CHECK,mFalseLockAlarmCkb);
        registerListener(IListener.ON_CHECK,mPryAlarmCkb);
    }


    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new SettingListenerImpl();
        }
        return mListener;
    }

    @Override
    public IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new SettingBusiness();
        }
        return mBusiness;
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK) {
                    final String password = data.getStringExtra(GConstant.GESTURE_PASS);
                    if(!TextUtils.isEmpty(password)) {
                        PreferencesUtils.putString(GConstant.GESTURE_PASS,password);
                    }
                    if(PassUtil.isSettingGesturePass()) {
                        PreferencesUtils.putBoolean(GConstant.IS_GESTURE_OPEN_LOCK,
                                data.getBooleanExtra(GConstant.IS_GESTURE_OPEN_LOCK,true));
                    }
                }
                break;
            default:
                break;
        }
    }
}

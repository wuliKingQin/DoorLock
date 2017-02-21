package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.SettingBusiness;
import cn.saiyi.doorlock.listenerimpl.SettingListenerImpl;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：设置界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/29 17:30
 */

public class SettingActivity extends AbsBaseActivity {

    //添加手势密码
    private TextView mAddGesturePassBtn;
    //app更新检测
    private TextView mAppUpdateBtn;
    //帮助
    private TextView mHelperBtn;
    //关于我们
    private TextView mAboutUsBtn;
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
        mFingerprintSwitchChb = getViewById(R.id.cb_fingerprint_switch);
    }

    @Override
    public void initObjects() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.setting_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_ACTION_BAR_LEFT_CLICK,actionBar);
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new SettingListenerImpl();
        }
        return mListener;
    }

    @Override
    protected IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new SettingBusiness();
        }
        return mBusiness;
    }
}

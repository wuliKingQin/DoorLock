package cn.saiyi.doorlock.listenerimpl;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lisijun.gesture.activity.AddGesturePassActivity;
import com.lisijun.gesture.interfaces.GConstant;
import com.saiyi.framework.listenerimpl.BaseListenerImpl;
import com.saiyi.framework.util.PreferencesUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.AboutActivity;
import cn.saiyi.doorlock.activity.HelperActivity;
import cn.saiyi.doorlock.activity.OpinionActivity;
import cn.saiyi.doorlock.blls.SettingBusiness;
import cn.saiyi.doorlock.fragment.SettingFragment;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.PassUtil;

/**
 * 描述：设置监听实例
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 9:59
 */

public class SettingListenerImpl extends BaseListenerImpl<SettingFragment,SettingBusiness> implements CompoundButton.OnCheckedChangeListener{

    @Override
    public void register(int eventType, View registerView, Object registerObj) {
        super.register(eventType, registerView, registerObj);
        switch (eventType) {
            case ON_CHECK:
                ((CheckBox)registerView).setOnCheckedChangeListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_add_gesture_pass:
                final Intent intent = new Intent(getContext(),AddGesturePassActivity.class);
                intent.putExtra(GConstant.IS_GESTURE_OPEN_LOCK, PassUtil.isGestureOpenLock());
                getFragment().startActivityForResult(intent,1);
                break;
            case R.id.tv_setting_app_update:
                getBusiness().checkAppUpdate();
                break;
            case R.id.tv_setting_about:
                getFragment().startActivity(AboutActivity.class);
                break;
            case R.id.tv_setting_opinion:
                getFragment().startActivity(OpinionActivity.class);
                break;
            case R.id.tv_setting_helper:
                getFragment().startActivity(HelperActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_fingerprint_switch:
                PreferencesUtils.putBoolean(Constant.IS_FINGERPRINT_OPEN_LOCK,isChecked);
                break;
            case R.id.cb_correlation_false_lock_alarm:
                PreferencesUtils.putBoolean(Constant.FALSE_LOCK_ALARM,isChecked);
                break;
            case R.id.cb_correlation_pry_alarm:
                PreferencesUtils.putBoolean(Constant.PRY_ALARM,isChecked);
                break;
            default:
                break;
        }
    }
}

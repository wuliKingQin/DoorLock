package cn.saiyi.doorlock.listenerimpl;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioGroup;

import com.lisijun.websocket.interfaces.ISocketConnCallback;
import com.saiyi.framework.listenerimpl.BaseListenerImpl;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.MainActivity;
import cn.saiyi.doorlock.blls.MainBusiness;

/**
 * 描述：主界面监听实现类
 * 创建作者：黎丝军
 * 创建时间：2016/9/29 17:17
 */

public class MainListenerImpl extends BaseListenerImpl<MainActivity,MainBusiness>
        implements RadioGroup.OnCheckedChangeListener{

    @Override
    public void register(int eventType, View registerView, Object registerObj) {
        super.register(eventType, registerView, registerObj);
        switch (eventType) {
            case ON_SELECT:
                ((RadioGroup) registerView).setOnCheckedChangeListener(this);
                break;
            default:
                break;
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Fragment fragment = getActivity().getDeviceFragment();
        switch (checkedId) {
            case R.id.rdb_tab_setting:
                fragment = getActivity().getSetFragment();
                break;
            case R.id.rdb_tab_user:
                fragment = getActivity().getUserFragment();
                break;
            case R.id.rdb_tab_mall:
                fragment = getActivity().getMallFragment();
                break;
            case R.id.rdb_tab_device:
                fragment = getActivity().getDeviceFragment();
                break;
            default:
                break;
        }
        getActivity().replaceFragment(R.id.fl_fragment,fragment);
    }
}

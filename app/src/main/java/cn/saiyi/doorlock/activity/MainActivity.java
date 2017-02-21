package cn.saiyi.doorlock.activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.MainBusiness;
import cn.saiyi.doorlock.fragment.DeviceFragment;
import cn.saiyi.doorlock.fragment.MallFragment;
import cn.saiyi.doorlock.fragment.SettingFragment;
import cn.saiyi.doorlock.fragment.UserFragment;
import cn.saiyi.doorlock.listenerimpl.MainListenerImpl;

/**
 * 描述：主界面，包含底部导航栏
 * 创建作者：黎丝军
 * 创建时间：2016/9/28 17:30
 */
public class MainActivity extends AbsBaseActivity {

    //设备标签
    private RadioGroup mMenuView;
    //设备界面碎片
    private DeviceFragment mDeviceFragment;
    //商城界面碎片
    private MallFragment mMallFragment;
    //设置碎片
    private SettingFragment mSetFragment;
    //用户碎片
    private UserFragment mUserFragment;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void findViews() {
        mMenuView = getViewById(R.id.rdg_bottomMenu);
    }

    @Override
    public void initObjects() {
        mMallFragment = new MallFragment();
        mDeviceFragment = new DeviceFragment();
        mSetFragment = new SettingFragment();
        mUserFragment = new UserFragment();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, getPermissions(), REQUEST_CODE);
        replaceFragment(R.id.fl_fragment,mDeviceFragment);
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_SELECT,mMenuView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ((MainBusiness)getBusiness()).exitBackApp();
        }
        return true;
    }

    public DeviceFragment getDeviceFragment() {
        return mDeviceFragment;
    }

    public MallFragment getMallFragment() {
        return mMallFragment;
    }

    public SettingFragment getSetFragment() {
        return mSetFragment;
    }

    public UserFragment getUserFragment() {
        return mUserFragment;
    }

    @Override
    protected IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new MainBusiness();
        }
        return mBusiness;
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new MainListenerImpl();
        }
        return mListener;
    }

    @Override
    protected void onDestroy() {
        if(mDeviceFragment != null) {
            mDeviceFragment.destroy();
        }
        super.onDestroy();
    }
}

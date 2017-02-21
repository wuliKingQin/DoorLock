package cn.saiyi.doorlock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.fragment.BaseFragment;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.TwoCodeActivity;
import cn.saiyi.doorlock.adapter.DeviceAdapter;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.blls.DeviceBusiness;
import cn.saiyi.doorlock.listenerimpl.DeviceListenerImpl;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：设备碎片界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/29 14:56
 */

public class DeviceFragment extends BaseFragment {

    //添加设备回调
    public final static int ADD_DEVICE_CODE = 2;
    //配置蓝牙
    public final static int CONFIG_BLUE_CODE = 3;
    //无设备界面
    private View mNoDeviceView;
    //有设置界面
    private View mHasDeviceView;
    //设备适配器
    private DeviceAdapter mDeviceAdapter;
    //设备列表视图
    private RecyclerView mDeviceListRv;

    @Override
    public void onContentView() {
        setContentView(R.layout.fragment_device);
    }

    @Override
    public void findViews() {
        mNoDeviceView = getViewById(R.id.tv_no_device);
        mHasDeviceView = getViewById(R.id.ll_have_device);
        mDeviceListRv = getViewById(R.id.rv_device);
    }

    @Override
    public void initObjects() {
        mDeviceAdapter = new DeviceAdapter(this,getContext());
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.device_no_bind_title);
        setTitleColor(Color.BLACK);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        actionBar.setRightButtonBackground(R.mipmap.ic_add_device,25,25);

        //初始化列表
        mDeviceListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeviceListRv.setAdapter(mDeviceAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_DEVICE_CODE:
                if(resultCode == 20) {
                    ((DeviceBusiness)mBusiness).requestDeviceList();
                }
                break;
            case CONFIG_BLUE_CODE:
                if(resultCode == TwoCodeActivity.SCAN_RESULT_OK) {
                    final String wifiMac = data.getStringExtra(TwoCodeActivity.TEMPT_INFO);
                    final String result = data.getStringExtra(TwoCodeActivity.SCAN_RESULT);
                    ((DeviceBusiness)mBusiness).scanBlueAddressHandle(result,wifiMac);
                } else {
                    ToastUtils.toast(getContext(),"扫描失败，请重新扫描");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    public void setListeners() {
        mDeviceAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        registerListener(DeviceListenerImpl.ON_UPDATE,mBusiness);
        registerListener(IListener.ON_ACTION_BAR_RIGHT_CLICK,actionBar);
        registerListener(IListener.ON_ITEM_CLICK,mDeviceAdapter);
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new DeviceListenerImpl();
        }
        return super.getListener();
    }

    @Override
    public IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new DeviceBusiness();
        }
        return super.getBusiness();
    }

    /**
     * 更新视图
     * @param isHasDevice 有设备显示设备视图否则不显示
     */
    public void updateViewChange(boolean isHasDevice) {
        if (isHasDevice) {
            mNoDeviceView.setVisibility(View.GONE);
            mHasDeviceView.setVisibility(View.VISIBLE);
        } else {
            mNoDeviceView.setVisibility(View.VISIBLE);
            mHasDeviceView.setVisibility(View.GONE);
        }
    }

    public DeviceAdapter getDeviceAdapter() {
        return mDeviceAdapter;
    }

    //数据列表变化监听
    private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            updateView(mDeviceAdapter.getItemCount());
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateView(itemCount);
        }

        /**
         * 视图更新
         * @param itemCount 列表个数
         */
        private void updateView(int itemCount) {
            if(itemCount > 0) {
                updateViewChange(true);
            } else {
                updateViewChange(false);
            }
        }
    };

    /**
     * 在主界面调用销毁
     */
    public void destroy() {
        ((DeviceBusiness)getBusiness()).destroy();
    }
}

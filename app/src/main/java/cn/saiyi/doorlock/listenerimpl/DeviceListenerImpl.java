package cn.saiyi.doorlock.listenerimpl;

import android.content.Intent;
import android.view.View;

import com.saiyi.framework.adapter.AbsBaseAdapter;
import com.saiyi.framework.listenerimpl.BaseListenerImpl;
import com.saiyi.framework.util.ToastUtils;

import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.AddDeviceActivity;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.blls.DeviceBusiness;
import cn.saiyi.doorlock.fragment.DeviceFragment;

/**
 * 描述：设备监听实例
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 15:09
 */

public class DeviceListenerImpl extends BaseListenerImpl<DeviceFragment,DeviceBusiness>
        implements DeviceBusiness.IUpdateDeviceCallBack,AbsBaseAdapter.OnItemClickListener<DeviceBean>{

    //更新设备监听使用
    public final static int ON_UPDATE = ON_OTHER + 1;

    @Override
    public void register(int eventType, View registerView, Object registerObj) {
        super.register(eventType, registerView, registerObj);
        switch (eventType) {
            case ON_UPDATE:
                getBusiness().setUpdateDeviceCallBack(this);
                break;
            case ON_ITEM_CLICK:
                ((AbsBaseAdapter)registerObj).setOnItemClickListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void actionBarRightClick(View rightView) {
        getFragment().startActivityForResult(new Intent(getContext(),AddDeviceActivity.class),DeviceFragment.ADD_DEVICE_CODE);
    }

    @Override
    public void onUpdateDevice(List<DeviceBean> deviceList) {
        if(deviceList.size() > 0) {
            getFragment().setTitle(R.string.device_bind_title);
        } else {
            getFragment().setTitle(R.string.device_no_bind_title);
        }
        getFragment().getDeviceAdapter().setListData(deviceList);
    }

    @Override
    public void onRequestData() {
        getBusiness().requestDeviceList();
    }

    @Override
    public void onItemClick(View view, DeviceBean bean, int position) {
        if(bean.isOnLine()) {
            getBusiness().deviceDetailsHandle(bean);
        } else {
            ToastUtils.toast(getContext(),"设备不在线，请唤醒设备");
        }
    }
}

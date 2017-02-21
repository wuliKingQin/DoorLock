package cn.saiyi.doorlock.listenerimpl;

import android.content.Intent;
import android.view.View;

import com.saiyi.framework.listenerimpl.BaseListenerImpl;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.AgingPassActivity;
import cn.saiyi.doorlock.activity.AuthorityMgrActivity;
import cn.saiyi.doorlock.activity.BluetoothSharkActivity;
import cn.saiyi.doorlock.activity.CorrelationSettingActivity;
import cn.saiyi.doorlock.activity.DeviceControlActivity;
import cn.saiyi.doorlock.activity.DistanceOpenLockActivity;
import cn.saiyi.doorlock.activity.OnePassActivity;
import cn.saiyi.doorlock.activity.OpenLockRecordActivity;
import cn.saiyi.doorlock.blls.DeviceControlBusiness;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：设置控制界面监听实现
 * 创建作者：黎丝军
 * 创建时间：2016/10/14 17:02
 */

public class DeviceControlListenerImpl extends BaseListenerImpl<DeviceControlActivity,DeviceControlBusiness> {

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            //开锁记录
            case R.id.tv_control_open_lock_record:
                final Intent lockRecord = new Intent(getContext(), OpenLockRecordActivity.class);
                lockRecord.putExtra(Constant.WIFI_MAC,getActivity().getDeviceBean().getWifiMac());
                getActivity().startActivity(lockRecord);
                break;
            //修改设备昵称
            case R.id.tv_control_change_name:
                getBusiness().modifyDeviceNameHandle(getActivity().getDeviceBean());
                break;
            //一次性密码
            case R.id.tv_control_one_pass:
                final Intent onePass = new Intent(getContext(), OnePassActivity.class);
                onePass.putExtra(Constant.WIFI_MAC,getActivity().getDeviceBean().getWifiMac());
                getBusiness().authorityCheckHandler(getActivity().getDeviceBean(),onePass);
                break;
            //远程开锁
            case R.id.tv_control_long_distance:
                final Intent openLock = new Intent(getContext(), DistanceOpenLockActivity.class);
                openLock.putExtra(Constant.DEVICE_BEAN,getActivity().getDeviceBean());
                getActivity().startActivity(openLock);
                break;
            //摇一摇开锁
            case R.id.tv_control_shark:
                final String bleAddress = getActivity().getDeviceBean().getBleAddress();
                if(bleAddress != null) {
                    final Intent sharkActivity = new Intent(getContext(),BluetoothSharkActivity.class);
                    sharkActivity.putExtra("bleAddress",bleAddress);
                    sharkActivity.putExtra(Constant.DEVICE_ADMIN_PASS,getActivity().getDeviceBean().getAdminPass());
                    getActivity().startActivity(sharkActivity);
                } else {
                    ToastUtils.toast(getContext(),"蓝牙未配置");
                }
                break;
            //权限管理
            case R.id.tv_control_authority_mgr:
                final Intent authority = new Intent(getContext(), AuthorityMgrActivity.class);
                authority.putExtra(Constant.WIFI_MAC,getActivity().getDeviceBean().getWifiMac());
                getBusiness().authorityCheckHandler(getActivity().getDeviceBean(),authority);
                break;
            //时效密码
            case R.id.tv_control_aging_pass:
                final Intent agingPass = new Intent(getContext(), AgingPassActivity.class);
                agingPass.putExtra(Constant.WIFI_MAC,getActivity().getDeviceBean().getWifiMac());
                agingPass.putExtra(Constant.DEVICE_PASS,getActivity().getDeviceBean().getAdminPass());
                getBusiness().authorityCheckHandler(getActivity().getDeviceBean(),agingPass);
                break;
            default:
                break;
        }
    }

    @Override
    protected void actionBarLeftClick(View leftView) {
        getActivity().finish();
    }

    @Override
    protected void actionBarRightClick(View rightView) {
        final Intent intent = new Intent(getContext(),CorrelationSettingActivity.class);
        intent.putExtra("deviceBean",getActivity().getDeviceBean());
        getActivity().startActivity(intent);
    }
}

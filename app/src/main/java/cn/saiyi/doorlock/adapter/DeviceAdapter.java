package cn.saiyi.doorlock.adapter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.daimajia.swipe.SwipeLayout;
import com.saiyi.framework.adapter.AbsSwipeAdapter;
import com.saiyi.framework.adapter.BaseViewHolder;
import com.saiyi.framework.interfaces.ICancelRequestCallBack;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.PreferencesUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;
import com.saiyi.framework.view.InfoHintDialog;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.BluetoothSharkActivity;
import cn.saiyi.doorlock.activity.TwoCodeActivity;
import cn.saiyi.doorlock.activity.WifiBindActivity;
import cn.saiyi.doorlock.bean.DeviceBean;
import cn.saiyi.doorlock.blls.DeviceBusiness;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.DeviceUtil;
import cn.saiyi.doorlock.device.ICheckAdminCallback;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.fragment.DeviceFragment;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.DecodeUtil;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：设置列表适配器
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 14:37
 */

public class DeviceAdapter extends AbsSwipeAdapter<DeviceBean,DeviceAdapter.DeviceViewHolder>{

    //碎片
    private Fragment mFragment;
    //删除提示
    private InfoHintDialog mHintDialog;
    //列表更新
    private DeviceBusiness.IUpdateDeviceCallBack mCallback;

    public DeviceAdapter(Fragment fragment,Context context) {
        super(context, R.layout.listview_device_item);
        mFragment = fragment;
        mHintDialog = new InfoHintDialog(context);
        mHintDialog.setTitle(R.string.dialog_hint);
    }

    @Override
    public DeviceViewHolder onCreateVH(View itemView, int ViewType) {
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindDataForItem(DeviceViewHolder viewHolder, DeviceBean bean, int position) {
        viewHolder.deviceName.setText(bean.getName());
        viewHolder.deviceState.setText(bean.isOnLine() == true ? "设备在线":"设备不在线");
        bindSwipeLayout(viewHolder.swipeLayout,position);
    }

    @Override
    protected void setItemListeners(final DeviceViewHolder holder, final DeviceBean deviceBean, final int position) {
        setSwipeListener(holder.swipeLayout);
        super.setItemListeners(holder, deviceBean, position);
        holder.changeNetworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllItems();
                DeviceUtil.authorityCheckHandler(getContext(), deviceBean.getWifiMac(), new ICheckAdminCallback() {
                    @Override
                    public void onAdmin() {
                        mHintDialog.setContentText("确定要切换网络吗？");
                        mHintDialog.setSureListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHintDialog.dismiss();
                                closeAllItems();
                                getContext().startActivity(new Intent(getContext(), WifiBindActivity.class));
                            }
                        });
                        mHintDialog.show();
                    }

                    @Override
                    public void onNotAdmin() {
                        closeAllItems();
                    }
                });
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllItems();
                DeviceUtil.authorityCheckHandler(getContext(), deviceBean.getWifiMac(), new ICheckAdminCallback() {
                    @Override
                    public void onAdmin() {
                        mHintDialog.setContentText("确定要删除该设备吗？");
                        mHintDialog.setSureListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHintDialog.dismiss();
                                deleteDeviceHandleTemp(deviceBean);
                            }
                        });
                        mHintDialog.show();
                    }

                    @Override
                    public void onNotAdmin() {
                        closeAllItems();
                    }
                });
            }
        });
        holder.bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllItems();
                if(TextUtils.equals(deviceBean.getBleAddress(),"0")) {
                    mHintDialog.setContentText("请扫描指定的门锁");
                    mHintDialog.setCancelBtnVisibility(View.GONE);
                    mHintDialog.setSureListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHintDialog.dismiss();
                            closeAllItems();
                            final Intent intent = new Intent(getContext(), TwoCodeActivity.class);
                            intent.putExtra(TwoCodeActivity.TEMPT_INFO,deviceBean.getWifiMac());
                            mFragment.startActivityForResult(intent, DeviceFragment.CONFIG_BLUE_CODE);
                        }
                    });
                    mHintDialog.show();
                } else {
                    if(!TextUtils.equals(deviceBean.getAdminPass(),"0")) {
                        final Intent sharkActivity = new Intent(getContext(),BluetoothSharkActivity.class);
                        sharkActivity.putExtra("bleAddress",deviceBean.getBleAddress());
                        sharkActivity.putExtra(Constant.DEVICE_ADMIN_PASS,deviceBean.getAdminPass());
                        getContext().startActivity(sharkActivity);
                    } else {
                        ToastUtils.toast(getContext(),"请先添加管理员密码");
                    }
                }
            }
        });
    }

    /**
     * 删除设备的处理方法，该方法是先和设备交互，在和服务器交互
     */
    private void deleteDeviceHandleTemp(final DeviceBean deviceBean) {
        ProgressUtils.showDialog(getContext(), "正在删除中，……", true,null);
        final Device device = new Device();
        device.setDeviceMac(deviceBean.getWifiMac());
        byte[] ePass ;
        if(deviceBean.getAdminPass().equals("0")) {
            ePass = EncodeUtil.strToBytes("00000000");
        } else {
            ePass = EncodeUtil.strToBytes(deviceBean.getAdminPass());
        }
        LogUtils.d(DecodeUtil.bytesToHexStr(ePass));
        device.sendCmd(IFunc.DELETE_ACCOUNT, EncryptUtil.mergeArray(new byte[]{0x01},ePass), new ISendCallback() {
            @Override
            public void onSuccess() {
                closeAllItems();
                isSwipeOpen = false;
                LogUtils.d("删除设备发送命令成功");
                PreferencesUtils.putString(Constant.WIFI_MAC,deviceBean.getWifiMac());
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                closeAllItems();
                isSwipeOpen = false;
                LogUtils.d("删除设备发送命令失败");
                ToastUtils.toast(getContext(),"删除设备失败");
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 删除设备的处理方法，该方法是先和服务器交互，再处理设备执行结果
     * 该方是暂时没有被用到
     */
    private void deleteDeviceHandle(DeviceBean deviceBean) {
        ProgressUtils.showDialog(getContext(),"正在删除中，……",null);
        final JSONParam jsonparam = new JSONParam();
        jsonparam.putJSONParam("phone", LoginUtil.getAccount());
        jsonparam.putJSONParam("mac",deviceBean.getWifiMac());
        jsonparam.putJSONParam("pwd",deviceBean.getAdminPass());
        HttpRequest.post(URL.DELETE_DEVICE,jsonparam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
               try {
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        closeAllItems();
                        isSwipeOpen = false;
                        mCallback = ListenersMgr.getInstance().getListener(DeviceBusiness.IUpdateDeviceCallBack.class);
                        if (mCallback != null) {
                            mCallback.onRequestData();
                        }
                    } else {
                        onFailure(-1,null);
                    }
                } catch (Exception e){
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                closeAllItems();
                isSwipeOpen = false;
                ToastUtils.toast(getContext(),"删除设备失败");
                ProgressUtils.dismissDialog();
            }
        });
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.sl_device;
    }

    /**
     * 设备视图缓存器
     */
    public class DeviceViewHolder extends BaseViewHolder {
        //设备图标
        ImageView deviceIcon;
        //设备名
        TextView deviceName;
        //设备状态
        TextView deviceState;
        //删除按钮
        Button deleteBtn;
        //更换网络按钮
        Button changeNetworkBtn;
        //蓝牙
        Button bluetoothBtn;
        //侧滑布局
        SwipeLayout swipeLayout;
        //底部线视图
        View bottomLineView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            deviceIcon = getViewById(R.id.iv_device_icon);
            deviceName = getViewById(R.id.tv_device_name);
            deviceState = getViewById(R.id.tv_device_state);
            deleteBtn = getViewById(R.id.btn_delete);
            changeNetworkBtn = getViewById(R.id.btn_change);
            swipeLayout = getViewById(R.id.sl_device);
            bottomLineView = getViewById(R.id.v_bottom_line);
            bluetoothBtn = getViewById(R.id.btn_bluetooth);
        }
    }
}

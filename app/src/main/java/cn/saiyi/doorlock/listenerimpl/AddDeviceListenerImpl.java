package cn.saiyi.doorlock.listenerimpl;

import android.content.Intent;
import android.view.View;

import com.saiyi.framework.adapter.AbsBaseAdapter;
import com.saiyi.framework.listenerimpl.BaseListenerImpl;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.AddDeviceActivity;
import cn.saiyi.doorlock.activity.TwoCodeActivity;
import cn.saiyi.doorlock.activity.WifiBindActivity;
import cn.saiyi.doorlock.bean.ProductBean;
import cn.saiyi.doorlock.blls.AddDeviceBusiness;

/**
 * 描述：添加设备监听实现类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 15:45
 */

public class AddDeviceListenerImpl extends BaseListenerImpl<AddDeviceActivity,AddDeviceBusiness>
        implements AbsBaseAdapter.OnItemClickListener<ProductBean>{

    @Override
    public void register(int eventType, View registerView, Object registerObj) {
        super.register(eventType, registerView, registerObj);
        switch (eventType) {
            case ON_ITEM_CLICK:
                ((AbsBaseAdapter)registerObj).setOnItemClickListener(this);
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
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_scan_bind:
                getActivity().startActivityForResult(new Intent(getContext(),TwoCodeActivity.class),getActivity().ADD_DEVICE_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, ProductBean bean, int position) {
        final Intent intent = new Intent(getContext(),WifiBindActivity.class);
        intent.putExtra(WifiBindActivity.DEVICE_NAME,bean.getName());
        getActivity().startActivity(intent);
    }
}

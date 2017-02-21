package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.adapter.ProductAdapter;
import cn.saiyi.doorlock.bean.ProductBean;
import cn.saiyi.doorlock.blls.AddDeviceBusiness;
import cn.saiyi.doorlock.listenerimpl.AddDeviceListenerImpl;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：添加设备界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 15:27
 */

public class AddDeviceActivity extends AbsBaseActivity {

    //添加设备请求码
    public static final int ADD_DEVICE_CODE = 31;
    //扫码绑定
    private TextView mScanBindBtn;
    //产品列表视图
    private RecyclerView mProductListView;
    //产品样例适配器
    private ProductAdapter mAdapter;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_add_device);
    }

    @Override
    public void findViews() {
        mScanBindBtn = getViewById(R.id.tv_scan_bind);
        mProductListView = getViewById(R.id.rv_product_list);
    }

    @Override
    public void initObjects() {
        mAdapter = new ProductAdapter(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.add_device_title);
        setTitleSize(Constant.TEXT_SIZE);
        setTitleColor(Color.BLACK);
        setActionBarBackgroundColor(Color.WHITE);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);


        List<ProductBean> data = new ArrayList<>();
        ProductBean productBean;
        for(int i = 0;i < 5;i++) {
            productBean = new ProductBean();
            productBean.setName("门锁" + i);
            data.add(productBean);
        }

        mProductListView.setLayoutManager(new GridLayoutManager(this,4));
        mProductListView.setAdapter(mAdapter);

        mAdapter.setListData(data);
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_ACTION_BAR_LEFT_CLICK,actionBar);
        registerListener(IListener.ON_CLICK,mScanBindBtn);
        registerListener(IListener.ON_ITEM_CLICK,mAdapter);
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_DEVICE_CODE:
                if(resultCode == TwoCodeActivity.SCAN_RESULT_OK) {
                    final String result = data.getStringExtra(TwoCodeActivity.SCAN_RESULT);
                    final Intent intent = new Intent(this,WifiBindActivity.class);
                    intent.putExtra(WifiBindActivity.DEVICE_NAME,result);
                    startActivity(intent);
                } else {
                    ToastUtils.toast(this,"扫描失败");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new AddDeviceListenerImpl();
        }
        return mListener;
    }

    @Override
    protected IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new AddDeviceBusiness();
        }
        return mBusiness;
    }
}

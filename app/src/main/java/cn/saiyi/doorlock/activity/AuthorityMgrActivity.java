package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.AppHelper;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ResourcesUtils;
import com.saiyi.framework.util.ToastUtils;
import com.saiyi.framework.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.adapter.ShareUserAdapter;
import cn.saiyi.doorlock.bean.ContactBean;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：权限管理界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 17:19
 */

public class AuthorityMgrActivity extends AbsBaseActivity
        implements ShareUserAdapter.OnSlideClickListener,IUpdateUICallBack{

    //保存当前设备mac
    private String mCurrentDeviceMac;
    //没有数据视图
    private View mNoDataView;
    //头像
    private CircleImageView mHeadCiv;
    //权限管理头像和姓名信息
    private TextView mMgrInfoTv;
    //分享用户适配器
    private ShareUserAdapter mAdapter;
    //用于装载分享用户
    private RecyclerView mShareUserView;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_authority_mgr);
    }

    @Override
    public void findViews() {
        mHeadCiv = getViewById(R.id.civ_head);
        mNoDataView = getViewById(R.id.tv_authority_no_data);
        mMgrInfoTv = getViewById(R.id.tv_authority_user_info);
        mShareUserView = getViewById(R.id.ryv_authority_share_user);
    }

    @Override
    public void initObjects() {
        mAdapter = new ShareUserAdapter(this);
        mCurrentDeviceMac = getIntent().getStringExtra(Constant.WIFI_MAC);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.mgr_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        actionBar.setRightButtonBackground(R.mipmap.ic_add_device,25,25);

        mShareUserView.setLayoutManager(new LinearLayoutManager(this));
        mShareUserView.setAdapter(mAdapter);

        mMgrInfoTv.setText(LoginUtil.getName());
        int headResId = R.mipmap.ic_head_1;
        try {
            headResId = ResourcesUtils.getMipmapResId(this,LoginUtil.getHeadUrl());
        } catch (Exception e) {
        }
        mHeadCiv.setImageResource(headResId);

        requestShareUserData();
    }

    @Override
    public void setListeners() {
        mAdapter.setOnSlideClickListener(this);
        mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> phone = new ArrayList<>();
                for(ContactBean bean:mAdapter.getData()) {
                    phone.add(bean.getPhone());
                }
                final Intent intent = new Intent(AuthorityMgrActivity.this,AddressBookActivity.class);
                intent.putStringArrayListExtra("exitAccount",phone);
                intent.putExtra("mac",mCurrentDeviceMac);
                startActivityForResult(intent,13);
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    //数据列表变化监听
    private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            updateView(mAdapter.getItemCount());
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
                viewChange(View.GONE,View.VISIBLE);
            } else {
                viewChange(View.VISIBLE,View.GONE);
            }
        }
    };

    /**
     * 视图变化
     * @param noDataView 没有数据视图
     * @param hasDataView 有数据视图
     */
    private void viewChange(int noDataView,int hasDataView) {
        mNoDataView.setVisibility(noDataView);
        mShareUserView.setVisibility(hasDataView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 13:
                if(resultCode == 31) {
                    requestShareUserData();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 请求分享用户数据
     */
    private void requestShareUserData() {
        HttpRequest.get(URL.MAC_QUERY_SHARE_USER + mCurrentDeviceMac,new BaseHttpRequestCallback<JSONArray>() {
            @Override
            protected void onSuccess(JSONArray jsonResult) {
                String name;
                String phone;
                String headIcon;
                int index = 0;
                JSONObject jsonItem;
                ContactBean contactBean;
                List<ContactBean> data = new ArrayList<>();
                for(;jsonResult != null && index < jsonResult.size();index ++) {
                    contactBean = new ContactBean();
                    jsonItem = jsonResult.getJSONObject(index);
                    if(jsonItem != null) {
                        name = jsonItem.getString("name");
                        phone = jsonItem.getString("phone");
                        headIcon = jsonItem.getString("headimg");
                        if(name == null) {
                            name = phone;
                        }
                        if(headIcon == null) {
                            headIcon = "ic_head_1";
                        }
                        contactBean.setName(name);
                        contactBean.setPhone(phone);
                        contactBean.setMac(mCurrentDeviceMac);
                        contactBean.setHeadIconUrl(headIcon);
                        data.add(contactBean);
                    }
                }
                mAdapter.setListData(data);
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getBaseContext(),msg);
            }
        });
    }

    @Override
    public void onDeleteClick(ShareUserAdapter.ShareUserViewHolder holder, ContactBean bean) {
        requestShareUserData();
    }

    @Override
    public void onChangeClick(ShareUserAdapter.ShareUserViewHolder holder, ContactBean bean) {
        AppHelper.instance().finishActivity(DeviceControlActivity.class);
        finish();
    }

    @Override
    public void onUpdateView(Object data) {
    }
}

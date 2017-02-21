package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;

import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.adapter.AddressBookAdapter;
import cn.saiyi.doorlock.blls.AddressBookBusiness;
import cn.saiyi.doorlock.listenerimpl.AddressBookListenerImpl;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：通讯录界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 15:07
 */

public class AddressBookActivity extends AbsBaseActivity {

    //联系人列表视图
    private RecyclerView mContactView;
    //数据适配器
    private AddressBookAdapter mAdapter;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_address_book);
    }

    @Override
    public void findViews() {
        mContactView = getViewById(R.id.ryv_contact);
    }

    @Override
    public void initObjects() {
        mAdapter = new AddressBookAdapter(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.address_book_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        actionBar.setRightButtonText("完成");
        actionBar.setRightButtonTextColor(Color.BLACK);

        final List<String> existUser = getIntent().getStringArrayListExtra("exitAccount");
        final String mac = getIntent().getStringExtra("mac");
        mAdapter.setListData(((AddressBookBusiness)mBusiness).getContactList(existUser,mac));
        mContactView.setLayoutManager(new LinearLayoutManager(this));
        mContactView.setAdapter(mAdapter);
        setResult(31,null);
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_ACTION_BAR_LEFT_CLICK,actionBar);
        registerListener(IListener.ON_ACTION_BAR_RIGHT_CLICK,actionBar);
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new AddressBookListenerImpl();
        }
        return super.getListener();
    }

    @Override
    protected IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new AddressBookBusiness();
        }
        return super.getBusiness();
    }

    public AddressBookAdapter getContactAdapter() {
        return mAdapter;
    }
}

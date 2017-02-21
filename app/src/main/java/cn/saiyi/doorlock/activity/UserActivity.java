package cn.saiyi.doorlock.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.UserBusiness;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.listenerimpl.UserListenerImpl;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：用户界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/29 17:30
 */

public class UserActivity extends AbsBaseActivity
        implements IUpdateUICallBack{

    //用户头像
    private ImageView mHeadIconIv;
    //账号
    private TextView mAccountTv;
    //更换头像
    private TextView mChangeHeadIconBtn;
    //用户姓名
    private TextView mUserNameTv;
    //地址
    private TextView mAddressTv;
    //联系方式
    private TextView mContactTv;
    //修改密码
    private TextView mModifyPassTv;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_user);
    }

    @Override
    public void findViews() {
        mHeadIconIv = getViewById(R.id.iv_user_head_icon);
        mAccountTv = getViewById(R.id.tv_user_account);
        mChangeHeadIconBtn = getViewById(R.id.tv_user_change_head);
        mUserNameTv = getViewById(R.id.tv_user_name);
        mAddressTv = getViewById(R.id.tv_user_address);
        mContactTv = getViewById(R.id.tv_user_contact);
        mModifyPassTv = getViewById(R.id.tv_user_modify_pass);
    }

    @Override
    public void initObjects() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.user_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(R.color.color13);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mAccountTv.setText(LoginUtil.getAccount());
        mUserNameTv.setText(LoginUtil.getName());
        mAddressTv.setText(LoginUtil.getAddress());
        mContactTv.setText(LoginUtil.getAccount());
    }

    @Override
    public void setListeners() {

    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    public IListener getListener() {
        if(mListener == null) {
            mListener = new UserListenerImpl();
        }
        return mListener;
    }

    @Override
    protected IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new UserBusiness();
        }
        return mBusiness;
    }

    @Override
    public void onUpdateView(Object data) {

    }
}

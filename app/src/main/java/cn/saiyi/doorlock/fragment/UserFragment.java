package cn.saiyi.doorlock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.fragment.BaseFragment;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.ResourcesUtils;
import com.saiyi.framework.view.CircleImageView;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.PhotoBean;
import cn.saiyi.doorlock.blls.UserBusiness;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.listenerimpl.UserListenerImpl;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：
 * 创建作者：黎丝军
 * 创建时间：2016/10/11 18:10
 */

public class UserFragment extends BaseFragment
        implements IUpdateUICallBack{

    //用于判断是否用用户信息进入修改密码
    public final static String USER_MODIFY_PASS = "userModifyPass";
    //用于修改信息请求码
    public final static int REQUEST_CODE = 15;
    //用户头像
    private CircleImageView mHeadIconIv;
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
    //退出登录
    private Button mExitBtn;

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
        mExitBtn = getViewById(R.id.btn_user_exit);
    }

    @Override
    public void initObjects() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.user_title);
        setTitleSize(Constant.TEXT_SIZE);
        setTitleColor(R.color.color7);
        setActionBarBackgroundColor(Color.WHITE);
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        mAccountTv.setText(LoginUtil.getAccount());
        mUserNameTv.setText(LoginUtil.getName());
        mAddressTv.setText(LoginUtil.getAddress());
        mContactTv.setText(LoginUtil.getAccount());
        int headResId = R.mipmap.ic_head_1;
        try {
            headResId = ResourcesUtils.getMipmapResId(getContext(),LoginUtil.getHeadUrl());
        } catch (Exception e) {
        }
        mHeadIconIv.setImageResource(headResId);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
    }

    @Override
    public void setListeners() {
        registerListener(IListener.ON_CLICK,mExitBtn);
        registerListener(IListener.ON_CLICK,mChangeHeadIconBtn);
        registerListener(IListener.ON_CLICK,mUserNameTv);
        registerListener(IListener.ON_CLICK,mAddressTv);
        registerListener(IListener.ON_CLICK,mContactTv);
        registerListener(IListener.ON_CLICK,mModifyPassTv);
        ListenersMgr.getInstance().registerListener(UserFragment.class,this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if(resultCode == getActivity().RESULT_OK) {
                    ((UserBusiness)getBusiness()).requestUserInfo();
                }
                break;
            //选择头像
            case 16:
                if(resultCode == 61) {
                    final PhotoBean photoBean = (PhotoBean)data.getSerializableExtra("selectedPhoto");
                    ((UserBusiness)getBusiness()).changeHeadIcon(mHeadIconIv,photoBean);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public IBusiness getBusiness() {
        if(mBusiness == null) {
            mBusiness = new UserBusiness();
        }
        return mBusiness;
    }

    @Override
    public void onUpdateView(Object data) {
        initUserInfo();
    }
}

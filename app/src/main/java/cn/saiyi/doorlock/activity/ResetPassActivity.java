package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lisijun.gesture.activity.VerifyGesturePassActivity;
import com.saiyi.framework.AppHelper;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.ResetPassBusiness;
import cn.saiyi.doorlock.fragment.UserFragment;
import cn.saiyi.doorlock.interfaces.IRequestCallBack;
import cn.saiyi.doorlock.other.BusinessType;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：重设密码界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 10:37
 */

public class ResetPassActivity extends AbsBaseActivity
        implements IRequestCallBack<JSONObject>{

    //账户
    private EditText mAccountEdt;
    //新密码
    private EditText mPasswordEdt;
    //验证码
    private EditText mVerifyCodeEdt;
    //获取验证码按钮
    private TextView mGainVerifyCodeBtn;
    //重置密码按钮
    private Button mResetBtn;
    //重置密码业务类
    private ResetPassBusiness mResetPassBusiness;
    //用于判断是否从手势密码进入
    private boolean isGesturePass = false;
    //是否是从用户信息界面进入
    private boolean isModifyPass = false;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_reset_pass);
    }

    @Override
    public void findViews() {
        mAccountEdt = getViewById(R.id.edt_reset_account);
        mPasswordEdt = getViewById(R.id.edt_reset_pass);
        mVerifyCodeEdt = getViewById(R.id.edt_reset_verify_code);
        mGainVerifyCodeBtn = getViewById(R.id.tv_reset_verify_code);
        mResetBtn = getViewById(R.id.btn_reset);
    }

    @Override
    public void initObjects() {
        isModifyPass = getIntent().getBooleanExtra(UserFragment.USER_MODIFY_PASS,false);
        isGesturePass = getIntent().getBooleanExtra(VerifyGesturePassActivity.IS_GESTURE_PASS,false);
        mResetPassBusiness = new ResetPassBusiness(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.reset_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mResetPassBusiness.setAccountEdt(mAccountEdt);
        mResetPassBusiness.setPasswordEdt(mPasswordEdt);
        mResetPassBusiness.setVerifyCodeEdt(mVerifyCodeEdt);
        mResetPassBusiness.setGainVerifyCodeBtn(mGainVerifyCodeBtn);
        mResetPassBusiness.setMainBtn(mResetBtn);

        mResetPassBusiness.setMainUrl(URL.MODIFY_PASS);
        mResetPassBusiness.setVerifyUrl(URL.VERIFY_CODE);
        mResetPassBusiness.setAccountParamKey("phone");
        mResetPassBusiness.setPassParamKey("pwd");
        mResetPassBusiness.setVerifyParamKey("code");
        mResetPassBusiness.setRequestCallBack(this);
        mResetPassBusiness.setProgressInfoHint(R.string.reset_pass_progress_hint);
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onSuccess(BusinessType type, JSONObject resultInfo, String... otherInfo) {
        if(resultInfo != null) {
            final int result = resultInfo.getInteger("result");
            switch (type) {
                case VERIFY:
                    if(result == 1) {
                        ToastUtils.toast(this,"获取验证码成功");
                    } else {
                        onError(type,Constant.ERROR_CODE,"获取验证码失败");
                    }
                    break;
                case FIND_PASS:
                    if(result == 1) {
                        if(isModifyPass || isGesturePass) {
                            LoginUtil.clearLoginInfo();
                            AppHelper.instance().finishAllActivity();
                            startActivity(LoginActivity.class);
                        }
                        finish();
                        onError(type,Constant.ERROR_CODE,"修改密码成功");
                    } else {
                        onError(type,Constant.ERROR_CODE,"修改密码失败");
                    }
                    break;
                default:
                    break;
            }
        } else {
            onError(type,Constant.ERROR_CODE,"修改密码失败");
        }
    }

    @Override
    public void onError(BusinessType type, int errorCode, String errorInfo) {
        if(errorCode == Constant.ERROR_CODE) {
            ToastUtils.toast(this,errorInfo);
        } else {
            ToastUtils.toast(this,"网络错误");
        }
    }
}

package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.RegisterBusiness;
import cn.saiyi.doorlock.interfaces.IRequestCallBack;
import cn.saiyi.doorlock.other.BusinessType;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;

/**
 * 描述：注册界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 10:36
 */

public class RegisterActivity extends AbsBaseActivity
        implements IRequestCallBack<JSONObject>{

    //账户
    private EditText mAccountEdt;
    //密码
    private EditText mPasswordEdt;
    //确认密码
    private EditText mSurePassEdt;
    //验证码
    private EditText mVerifyCodeEdt;
    //获取验证码按钮
    private TextView mGainVerifyCodeBtn;
    //注册按钮
    private Button mRegisterBtn;
    //注册业务实现
    private RegisterBusiness mRegisterBusiness;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void findViews() {
        mAccountEdt = getViewById(R.id.edt_register_account);
        mPasswordEdt = getViewById(R.id.edt_register_pass);
        mSurePassEdt = getViewById(R.id.edt_register_sure_pass);
        mVerifyCodeEdt = getViewById(R.id.edt_register_verify_code);
        mGainVerifyCodeBtn = getViewById(R.id.tv_register_verify);
        mRegisterBtn = getViewById(R.id.btn_register);
    }

    @Override
    public void initObjects() {
        mRegisterBusiness = new RegisterBusiness(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.register_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mRegisterBusiness.setAccountEdt(mAccountEdt);
        mRegisterBusiness.setPasswordEdt(mPasswordEdt);
        mRegisterBusiness.setSurePassEdt(mSurePassEdt);
        mRegisterBusiness.setVerifyCodeEdt(mVerifyCodeEdt);
        mRegisterBusiness.setGainVerifyCodeBtn(mGainVerifyCodeBtn);
        mRegisterBusiness.setMainBtn(mRegisterBtn);

        mRegisterBusiness.setMainUrl(URL.REGISTER);
        mRegisterBusiness.setVerifyUrl(URL.VERIFY_CODE);
        mRegisterBusiness.setAccountParamKey("phone");
        mRegisterBusiness.setPassParamKey("pwd");
        mRegisterBusiness.setVerifyParamKey("code");
        mRegisterBusiness.setRequestCallBack(this);
        mRegisterBusiness.setProgressInfoHint(R.string.register_progress_hint);
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
        try {
            final int result = resultInfo.getInteger("result");
            switch (type) {
                case VERIFY:
                    if(result == 1) {
                        ToastUtils.toast(this,"获取验证码成功");
                    } else {
                        onError(type,Constant.ERROR_CODE,"获取验证码失败");
                    }
                    break;
                case REGISTER:
                    if(result == 1) {
                        finish();
                    } else {
                        onError(type,Constant.ERROR_CODE,"注册失败");
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e){
            onError(type,Constant.ERROR_CODE,"注册失败");
        }
    }

    @Override
    public void onError(BusinessType type, int errorCode, String errorInfo) {
        if(Constant.ERROR_CODE == errorCode) {
            ToastUtils.toast(this,errorInfo);
        } else {
            ToastUtils.toast(this,"网络错误");
        }
    }
}

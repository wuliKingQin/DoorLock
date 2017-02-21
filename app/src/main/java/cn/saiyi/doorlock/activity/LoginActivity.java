package cn.saiyi.doorlock.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.blls.LoginBusiness;
import cn.saiyi.doorlock.interfaces.IRequestCallBack;
import cn.saiyi.doorlock.other.BusinessType;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：登录界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/28 17:52
 */

public class LoginActivity extends AbsBaseActivity {

    //用户头像
    private TextView mUserHeadIcon;
    //账号
    private EditText mAccountEdt;
    //密码
    private EditText mPasswordEdt;
    //注册
    private TextView mRegisterBtn;
    //忘记密码
    private TextView mResetPassBtn;
    //登录按钮
    private Button mLoginBtn;
    //登录业务
    private LoginBusiness mLoginBusiness;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void findViews() {
        mUserHeadIcon = getViewById(R.id.tv_login_head);
        mAccountEdt = getViewById(R.id.edt_login_account);
        mPasswordEdt = getViewById(R.id.edt_login_pass);
        mRegisterBtn = getViewById(R.id.tv_login_register);
        mResetPassBtn = getViewById(R.id.tv_login_reset_pass);
        mLoginBtn = getViewById(R.id.btn_login);
    }

    @Override
    public void initObjects() {
        mLoginBusiness = new LoginBusiness(this,mAccountEdt,mPasswordEdt,mLoginBtn);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mLoginBusiness.setURL(URL.LOGIN);
        mLoginBusiness.setAccountParamKey("phone");
        mLoginBusiness.setPassParamKey("pwd");
    }

    @Override
    public void setListeners() {
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.class);
            }
        });
        mResetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ResetPassActivity.class);
            }
        });
        mLoginBusiness.setRequestCallBack(new IRequestCallBack<JSONObject>() {

            @Override
            public void onSuccess(BusinessType type, JSONObject resultInfo, String... otherInfo) {
                try{
                    final int result = resultInfo.getInteger("result");
                    if(result == 1) {
                        LoginUtil.setLoginState(true,otherInfo[0],otherInfo[1]);
                        startActivity(MainActivity.class);
                        finish();
                    } else {
                        onError(type, Constant.ERROR_CODE,getString(R.string.login_fail_hint));
                    }
                } catch (Exception e){
                    onError(type,Constant.ERROR_CODE,getString(R.string.login_fail_hint));
                }
            }

            @Override
            public void onError(BusinessType type, int errorCode, String errorInfo) {
                LoginUtil.setLoginState(false,null,null);
                if(errorCode == Constant.ERROR_CODE) {
                    ToastUtils.toast(LoginActivity.this,errorInfo);
                } else {
                    ToastUtils.toast(LoginActivity.this,"网络错误");
                }
            }
        });
    }
}

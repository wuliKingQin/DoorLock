package cn.saiyi.doorlock.blls;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.interfaces.ICancelRequestCallBack;
import com.saiyi.framework.util.ProgressUtils;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import cn.saiyi.doorlock.interfaces.IRequestCallBack;
import cn.saiyi.doorlock.other.BusinessType;

/**
 * 描述：登录业务类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 11:23
 */

public class LoginBusiness<T> extends BaseBusiness implements ICancelRequestCallBack {

    //请求类型
    private RequestType mType;
    //保存请求url
    private String mURL;
    //保存账户请求参数key
    private String mAccountKey;
    //保存密码请求参数key
    private String mPassKey;
    //登录账户
    private String mAccount;
    //登录密码
    private String mPassword;
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

    public LoginBusiness(Context context) {
        super(context);
        mType = RequestType.POST;
        setCallBackType(BusinessType.LOGIN);
    }

    public LoginBusiness(Context context,EditText accountEdt,EditText passEdt,Button loginBtn) {
        this(context);
        mAccountEdt = accountEdt;
        mPasswordEdt = passEdt;
        setLoginBtn(loginBtn);
    }

    /**
     * 处理登录点击事件
     */
    private void loginHandle() {
        if(mAccountEdt != null) mAccount = mAccountEdt.getText().toString().trim();
        if(mPasswordEdt != null) mPassword = mPasswordEdt.getText().toString().trim();
        if(!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mPassword)) {
            if(isPhone(mAccount)) {
                ProgressUtils.showDialog(getContext(), "正在登录中，……",this);
                if(mType == RequestType.GET) {
                    HttpRequest.get(getBundleUrl(mAccount,mPassword),requestCallBack);
                } else {
                    final JSONObject jsonParam = new JSONObject();
                    jsonParam.put(mAccountKey,mAccount);
                    jsonParam.put(mPassKey,mPassword);
                    final RequestParams params = new RequestParams();
                    params.applicationJson(jsonParam);
                    HttpRequest.post(mURL,params,requestCallBack);
                }
            } else {
                showToast("您输入的手机号不正确");
            }
        } else {
            showToast("输入信息不全");
        }
    }

    /**
     * 请求回调
     */
    private BaseHttpRequestCallback requestCallBack = new BaseHttpRequestCallback<T>() {
        @Override
        protected void onSuccess(T jsonResult) {
            requestCallBackHandle(getCallBackType(),IRequestCallBack.SUCCESS,jsonResult,mAccount,mPassword);
        }
        @Override
        public void onFailure(int errorCode, String msg) {
            requestCallBackHandle(getCallBackType(),IRequestCallBack.FAIL,errorCode,msg);
        }

        @Override
        public void onFinish() {
            ProgressUtils.dismissDialog();
        }
    };

    /**
     * 构建url
     * @param account 账户
     * @param password 密码
     * @return 请求地址
     */
    private String getBundleUrl(String account,String password) {
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(mURL);
        urlBuilder.append("?");
        urlBuilder.append(mAccountKey);
        urlBuilder.append("=");
        urlBuilder.append(account);
        urlBuilder.append("&");
        urlBuilder.append(mPassKey);
        urlBuilder.append("=");
        urlBuilder.append(password);
        return urlBuilder.toString();
    }

    /**
     * 设置请求类型
     * @param type 类型值
     */
    public void setType(RequestType type) {
        mType = type;
    }

    /**
     * 设置请求地址
     * @param url 地址
     */
    public void setURL(String url) {
        mURL = url;
    }

    /**
     * 设置账户参数键值
     * @param accountParamKey 键值
     */
    public void setAccountParamKey(String accountParamKey) {
        mAccountKey = accountParamKey;
    }

    /**
     * 设置密码参数键值
     * @param passParamKey 键值
     */
    public void setPassParamKey(String passParamKey) {
        mPassKey = passParamKey;
    }

    /**
     * 设置输入账号框视图实例
     * @param accountEdt 实例
     */
    public void setAccountEdt(EditText accountEdt) {
        mAccountEdt = accountEdt;
    }

    /**
     * 设置输入密码框视图实例
     * @param passwordEdt 密码框输入实例
     */
    public void setPasswordEdt(EditText passwordEdt) {
        mPasswordEdt = passwordEdt;
    }

    /**
     * 设置登录视图按钮
     * @param loginBtn 实例
     */
    public void setLoginBtn(Button loginBtn) {
        mLoginBtn = loginBtn;
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginHandle();
            }
        });
    }

    /**
     * 设置注册按钮视图实例
     * @param registerBtn 视图实例
     */
    public void setRegisterBtn(TextView registerBtn) {
        mRegisterBtn = registerBtn;
    }

    /**
     * 设置忘记面视图实例
     * @param resetPassBtn 忘记密码视图实例
     */
    public void setResetPassBtn(TextView resetPassBtn) {
        mResetPassBtn = resetPassBtn;
    }

    @Override
    public void onCancel() {
        if(mURL != null) {
            HttpRequest.cancel(mURL);
        }
    }
}

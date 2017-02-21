package cn.saiyi.doorlock.blls;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
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
 * 描述：注册业务类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 11:24
 */

public class RegisterBusiness extends BaseBusiness {

    //进度提示信息
    private String mProgressInfoHint;
    //注册请求地址
    private String mRegisterUrl;
    //验证码请求地址
    private String mVerifyUrl;
    //账户参数key
    private String mAccountParamKey;
    //密码参数key
    private String mPassParamKey;
    //验证码参数key
    private String mVerifyParamKey;
    //保存倒计时总时间
    private long mCountDownTime;
    //重发提示字符串
    private String mCountDownHint;
    //保存之前的提示文本颜色
    private int mOldHintTextColor;
    //保存按下的文本颜色，默认是灰色
    private int mPressTextColor;
    //保存之前的提示信息
    private String mOldHintInfo;
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
    //用于倒计时
    private CountDownTimer mCountDownTimer;

    public RegisterBusiness(Context context) {
        super(context);
        mCountDownTime = 2 * 60 * 1000;
        mCountDownHint = "s";
        mPressTextColor = Color.GRAY;
        setCallBackType(BusinessType.REGISTER);
    }

    /**
     * 请求注册事件
     */
    private void requestRegisterHandle() {
        String account = null;
        String password = null;
        String surePass = null;
        String verifyCode = null;
        if(mAccountEdt != null) {
            account = mAccountEdt.getText().toString().trim();
        }
        if(mPasswordEdt != null) {
            password = mPasswordEdt.getText().toString().trim();
        }
        if(mSurePassEdt != null) {
            surePass = mSurePassEdt.getText().toString().trim();
        }
        if(mVerifyCodeEdt != null) {
            verifyCode = mVerifyCodeEdt.getText().toString().trim();
        }
        if(!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(verifyCode) && !TextUtils.isEmpty(surePass)) {
            if(TextUtils.equals(password,surePass)) {
                requestRegister(account,password,verifyCode);
            } else {
                requestCallBackHandle(getCallBackType(),IRequestCallBack.FAIL,-1,"两次输入密码不一致");
            }
        } else if(!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(verifyCode) && mSurePassEdt == null){
            requestRegister(account,password,verifyCode);
        } else {
            requestCallBackHandle(getCallBackType(),IRequestCallBack.FAIL,-1,"输入不全");
        }
    }

    /**
     * 请求注册处理方法
     * @param account 账户
     * @param password 密码
     * @param verifyCode 验证码
     */
    private void requestRegister(String account,String password,String verifyCode) {
        if(isPhone(account)) {
            ProgressUtils.showDialog(getContext(), mProgressInfoHint, new ICancelRequestCallBack() {
                @Override
                public void onCancel() {
                    if(mRegisterUrl != null) {
                        HttpRequest.cancel(mVerifyUrl);
                    }
                }
            });
            final JSONObject jsonParam = new JSONObject();
            jsonParam.put(mAccountParamKey,account);
            jsonParam.put(mPassParamKey,password);
            jsonParam.put(mVerifyParamKey,verifyCode);
            final RequestParams requestParams = new RequestParams();
            requestParams.applicationJson(jsonParam);
            HttpRequest.post(mRegisterUrl,requestParams,new BaseHttpRequestCallback<JSONObject>() {
                @Override
                protected void onSuccess(JSONObject successInfo) {
                    requestCallBackHandle(getCallBackType(),IRequestCallBack.SUCCESS,successInfo);
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    requestCallBackHandle(getCallBackType(),IRequestCallBack.FAIL,errorCode,msg);
                }

                @Override
                public void onFinish() {
                    ProgressUtils.dismissDialog();
                }
            });
        } else {
            requestCallBackHandle(getCallBackType(),IRequestCallBack.FAIL,-1,"您输入的手机号不正确");
        }
    }

    /**
     * 请求验证码
     */
    private void requestVerifyCode() {
        String account = null;
        if(mAccountEdt != null) {
            account = mAccountEdt.getText().toString().trim();
        }
        if(!TextUtils.isEmpty(account)) {
            if(isPhone(account)) {
                countDownTimeHandle();
                HttpRequest.get(mVerifyUrl + account,new BaseHttpRequestCallback<JSONObject>() {
                    @Override
                    protected void onSuccess(JSONObject jsonObject) {
                        requestCallBackHandle(BusinessType.VERIFY,IRequestCallBack.SUCCESS,jsonObject);
                    }
                    @Override
                    public void onFailure(int errorCode, String msg) {
                        hintTextHandle(true,mOldHintTextColor,mOldHintInfo);
                        cancelCountDownTimer();
                        requestCallBackHandle(BusinessType.VERIFY,IRequestCallBack.FAIL,errorCode,msg);
                    }
                });
            } else {
                requestCallBackHandle(BusinessType.VERIFY,IRequestCallBack.FAIL,-1,"您输入的手机号不正确");
            }
        } else {
            requestCallBackHandle(BusinessType.VERIFY,IRequestCallBack.FAIL,-1,"您还没有输入手机号");
        }
    }

    /**
     * 倒计时处理
     */
    private void countDownTimeHandle() {
        mCountDownTimer = new CountDownTimer(mCountDownTime,1000) {
            @Override
            public void onTick(long second) {
                hintTextHandle(false,mPressTextColor,second / 1000 + mCountDownHint);
            }

            @Override
            public void onFinish() {
                hintTextHandle(true,mOldHintTextColor,mOldHintInfo);
                cancelCountDownTimer();
            }
        };
        mCountDownTimer.start();
    }

    /**
     * 处理提示文本
     * @param enabled 状态
     * @param color 颜色值
     * @param result 文本结果
     */
    private void hintTextHandle(boolean enabled,int color,String result) {
        if(mGainVerifyCodeBtn != null) {
            mGainVerifyCodeBtn.setEnabled(enabled);
            mGainVerifyCodeBtn.setTextColor(color);
            mGainVerifyCodeBtn.setText(result);
        }
    }

    /**
     * 取消定时器
     */
    private void cancelCountDownTimer() {
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public void setAccountEdt(EditText accountEdt) {
        mAccountEdt = accountEdt;
    }

    public void setPasswordEdt(EditText passwordEdt) {
        mPasswordEdt = passwordEdt;
    }

    public void setSurePassEdt(EditText surePassEdt) {
        mSurePassEdt = surePassEdt;
    }

    public void setVerifyCodeEdt(EditText verifyCodeEdt) {
        mVerifyCodeEdt = verifyCodeEdt;
    }

    public void setGainVerifyCodeBtn(TextView gainVerifyCodeBtn) {
        mGainVerifyCodeBtn = gainVerifyCodeBtn;
        mOldHintInfo = gainVerifyCodeBtn.getText().toString();
        mOldHintTextColor = gainVerifyCodeBtn.getCurrentTextColor();
        gainVerifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVerifyCode();
            }
        });
    }

    public void setMainBtn(Button button) {
        mRegisterBtn = button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRegisterHandle();
            }
        });
    }

    public void setMainUrl(String registerUrl) {
        mRegisterUrl = registerUrl;
    }

    public void setVerifyUrl(String verifyUrl) {
        mVerifyUrl = verifyUrl;
    }

    public void setAccountParamKey(String accountParamKey) {
        mAccountParamKey = accountParamKey;
    }

    public void setPassParamKey(String passParamKey) {
        mPassParamKey = passParamKey;
    }

    public void setVerifyParamKey(String verifyParamKey) {
        mVerifyParamKey = verifyParamKey;
    }

    public void setCountDownTime(long countDownTime) {
        mCountDownTime = countDownTime;
    }

    public void setCountDownHint(String countDownHint) {
        mCountDownHint = countDownHint;
    }

    public void setPressTextColor(int pressTextColor) {
        mPressTextColor = pressTextColor;
    }

    public void setProgressInfoHint(String progressInfoHint) {
        mProgressInfoHint = progressInfoHint;
    }

    public void setProgressInfoHint(int resId) {
        setProgressInfoHint(getContext().getString(resId));
    }
}

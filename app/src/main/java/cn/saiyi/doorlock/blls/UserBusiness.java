package cn.saiyi.doorlock.blls;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.blls.AbsBaseBusiness;
import com.saiyi.framework.interfaces.ICancelRequestCallBack;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;
import com.saiyi.framework.view.CircleImageView;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.LoginActivity;
import cn.saiyi.doorlock.bean.PhotoBean;
import cn.saiyi.doorlock.fragment.UserFragment;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：用户业务类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 10:01
 */

public class UserBusiness extends AbsBaseBusiness {

    @Override
    public void initObject() {
    }

    @Override
    public void initData(Bundle bundle) {
        requestUserInfo();
    }

    /**
     * 退出登录
     */
    public void existLogin() {
        showHintDialog(R.string.user_exit_title, R.string.user_exit_hint,true);
        mHintDialog.setSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtil.clearLoginInfo();
                appHelper.finishAllActivity();
                getContext().startActivity(new Intent(getContext(),LoginActivity.class));
                mHintDialog.dismiss();
            }
        });
    }

    /**
     * 请求用户信息
     */
    public void requestUserInfo() {
        ProgressUtils.showDialog(getContext(), "正在初始化，……", new ICancelRequestCallBack() {
            @Override
            public void onCancel() {
                HttpRequest.cancel(URL.QUERY_USER_INFO);
            }
        });
        HttpRequest.get(URL.QUERY_USER_INFO + LoginUtil.getAccount(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                if (jsonObject != null) {
                    try {
                        LoginUtil.setName(jsonObject.getString("name"));
                        LoginUtil.setAddress(jsonObject.getString("address"));
                        LoginUtil.setHeadUrl(jsonObject.getString("headimg"));
                        LoginUtil.setAccount(jsonObject.getString("phone"));
                        final IUpdateUICallBack callBack = ListenersMgr.getInstance().getListener(UserFragment.class);
                        if(callBack != null) {
                            callBack.onUpdateView(null);
                        }
                    }catch (Exception e) {
                        onFailure(Constant.ERROR_CODE,"更新信息失败");
                    }

                } else {
                   onFailure(Constant.ERROR_CODE,"更新信息失败");
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                if(Constant.ERROR_CODE == errorCode) {
                    ToastUtils.toast(getContext(),msg);
                } else {
                    ToastUtils.toast(getContext(),"网络错误");
                }
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
                ListenersMgr.getInstance().unRegisterListener(IUpdateUICallBack.class);
            }
        });
    }

    /**
     * 修改头像
     * @param headIcon 头像视图
     * @param photoBean 头像信息
     */
    public void changeHeadIcon(final CircleImageView headIcon, final PhotoBean photoBean) {
        ProgressUtils.showDialog(getContext(), "正在修改中，……", new ICancelRequestCallBack() {
            @Override
            public void onCancel() {
                HttpRequest.cancel(URL.QUERY_USER_INFO);
            }
        });
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone",LoginUtil.getAccount());
        jsonParam.putJSONParam("headimg",photoBean.getPhotoName());
        HttpRequest.post(URL.MODIFY_USER_INFO,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                try {
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        LoginUtil.setHeadUrl(photoBean.getPhotoName());
                        headIcon.setImageResource(photoBean.getPhotoResId());
                        ToastUtils.toast(getContext(),"修改头像成功");
                    } else {
                        onFailure(-1,null);
                    }
                } catch (Exception e) {
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getContext(),"修改头像失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }
}

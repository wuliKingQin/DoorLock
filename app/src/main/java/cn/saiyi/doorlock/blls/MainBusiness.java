package cn.saiyi.doorlock.blls;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lisijun.websocket.interfaces.ISocketConnCallback;
import com.lisijun.websocket.socket.OnSocketMsgListener;
import com.lisijun.websocket.socket.SocketManager;
import com.saiyi.framework.blls.AbsBaseBusiness;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.PreferencesUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.IResult;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.device.Result;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.util.PassUtil;
import cn.saiyi.doorlock.util.TimeUtil;

/**
 * 描述：主界面业务类
 * 创建作者：黎丝军
 * 创建时间：2016/9/29 17:19
 */

public class MainBusiness extends AbsBaseBusiness
        implements ISocketConnCallback{

    //退出时间
    private long mExitTime = 0;
    @Override
    public void initObject() {

    }

    @Override
    public void initData(Bundle bundle) {
        final String url = URL.SERVER_URL + LoginUtil.getAccount();
        SocketManager.instance().init(getContext(),url,this);
        SocketManager.instance().setDelayTime(5 * 1000);
    }

    /**
     * 退出应用程序，用于按返回键时调用
     */
    public void exitBackApp() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mExitTime < 2000) {
            SocketManager.instance().stopSocketService();
            appHelper.exitApp();
        } else {
            mExitTime = currentTime;
            ToastUtils.toast(getContext(), R.string.device_back_exit);
        }
    }

    @Override
    public void onConnSuccess() {
        ToastUtils.toast(getContext(),"连接服务器成功");
        getContext().sendBroadcast(new Intent());
    }

    @Override
    public void onConnFail(String failInfo) {
        ToastUtils.toast(getContext(), TextUtils.equals(failInfo,"") == true ? "连接服务器失败":failInfo);
        LogUtils.d(failInfo);
    }
}

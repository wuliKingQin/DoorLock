package com.lisijun.fingerprint.work;

import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

/**
 * 描述：指纹识别接口回调类
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 16:07
 */

public class FingerprintCallback extends FingerprintManagerCompat.AuthenticationCallback {

    //用于将子线程中的消息发送到主线程
    private Handler handler = null;

    public FingerprintCallback(Handler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
        if (handler != null) {
            handler.obtainMessage(FConstant.MSG_AUTH_ERROR, errMsgId, 0,errString).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);
        if (handler != null) {
            handler.obtainMessage(FConstant.MSG_AUTH_HELP, helpMsgId, 0,helpString).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        if (handler != null) {
            handler.obtainMessage(FConstant.MSG_AUTH_SUCCESS,0,0,result).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        if (handler != null) {
            handler.obtainMessage(FConstant.MSG_AUTH_FAILED,0,0,"failed").sendToTarget();
        }
    }
}

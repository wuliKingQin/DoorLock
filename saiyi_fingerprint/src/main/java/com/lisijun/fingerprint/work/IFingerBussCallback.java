package com.lisijun.fingerprint.work;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.lisijun.fingerprint.dialog.FingerprintDialog;

/**
 * 描述：指纹业务回调接口
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 17:16
 */

public interface IFingerBussCallback {
    /**
     * 指纹识别预处理
     * 通常情况就是改变一下dialog提示信息
     * @param hintDialog 提示信息dialog实例
     */
    void onPre(FingerprintDialog hintDialog);

    /**
     * 指纹识别成功
     */
    void onSuccess(FingerprintManagerCompat.AuthenticationResult result);

    /**
     * 指纹识别失败
     * @param hintDialog 提示dialog
     * @param failCode 失败码
     * @param failInfo 失败信息
     */
    boolean onFail(FingerprintDialog hintDialog,int failCode,String failInfo);

    /**
     * 检查指纹识别是否支持的方法
     * 如果不支持该方法将调用，处理提示信息
     */
    void onCheckSupport(String info);
}

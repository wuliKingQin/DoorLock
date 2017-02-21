package com.lisijun.bluetooth.interfaces;

/**
 * 描述：发送数据回调接口
 * 创建作者：黎丝军
 * 创建时间：2016/12/29 10:25
 */

public interface ISendCallback {
    /**
     * 发送成功
     */
    void onSuccess();

    /**
     * 发送失败
     */
    void onFail();
}

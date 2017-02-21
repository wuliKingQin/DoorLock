package com.lisijun.websocket.interfaces;

/**
 * 描述：发送信息回调接口
 * 创建作者：黎丝军
 * 创建时间：2016/11/3 10:27
 */

public interface ISendMsgCallback {
    /**
     * 发送成功
     */
    void onSuccess();

    /**
     * 发送失败
     * @param error 错误信息
     */
    void onFail(int errorCode,String error);
}

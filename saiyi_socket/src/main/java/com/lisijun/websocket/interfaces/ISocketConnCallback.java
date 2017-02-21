package com.lisijun.websocket.interfaces;

/**
 * 描述：socket连接回调接口，包括连接失败，连接断开和连接成功等操作。
 * 创建作者：黎丝军
 * 创建时间：2016/11/3 9:28
 */

public interface ISocketConnCallback {

    /**
     * 连接成功
     */
    void onConnSuccess();

    /**
     * 连接失败的方法
     * @param failInfo 失败信息
     */
    void onConnFail(String failInfo);
}

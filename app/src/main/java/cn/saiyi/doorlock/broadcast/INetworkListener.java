package cn.saiyi.doorlock.broadcast;

/**
 * 描述：监听网络变化
 * 创建作者：黎丝军
 * 创建时间：2016/12/6 17:13
 */

public interface INetworkListener {

    /**
     * 在网方法
     */
    void onConnected();

    /**
     * 不在网方法
     */
    void onDisconnect();
}

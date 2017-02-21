package cn.saiyi.doorlock.device;

/**
 * 描述：检测设备是否是分享用户还是管理员
 * 创建作者：黎丝军
 * 创建时间：2016/11/14 9:55
 */

public interface ICheckAdminCallback {
    /**
     * 是管理员
     */
    void onAdmin();

    /**
     * 不是管理员
     */
    void onNotAdmin();
}

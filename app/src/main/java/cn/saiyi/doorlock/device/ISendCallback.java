package cn.saiyi.doorlock.device;

/**
 * 描述：发送数据回调接口
 * 创建作者：黎丝军
 * 创建时间：2016/10/9 11:09
 */

public interface ISendCallback {

    /**
     * 发送成功
     */
    void onSuccess();

    /**
     * 发送失败
     * @param errorCode 错误代码
     * @param errorInfo 错信息
     */
    void onError(int errorCode,String errorInfo);
}

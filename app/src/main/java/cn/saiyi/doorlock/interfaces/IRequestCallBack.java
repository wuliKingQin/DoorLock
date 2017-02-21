package cn.saiyi.doorlock.interfaces;

import cn.saiyi.doorlock.other.BusinessType;

/**
 * 描述：请求回调接口
 * 创建作者：黎丝军
 * 创建时间：2016/10/10 17:46
 */

public interface IRequestCallBack<T> {

    //成功
    int SUCCESS = 1;
    //失败
    int FAIL = 2;

    /**
     * 请求成功
     * @param type 请求类型
     * @param resultInfo 返回结果信息
     */
    void onSuccess(BusinessType type, T resultInfo, String...otherInfo);

    /**
     * 请求失败
     * @param type 请求类型
     * @param errorCode 错误代码
     * @param errorInfo 错信息
     */
    void onError(BusinessType type, int errorCode, String errorInfo);
}

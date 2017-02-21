package cn.saiyi.doorlock.blls;

import android.content.Context;
import cn.saiyi.doorlock.interfaces.IRequestCallBack;

/**
 * 描述：业务类接口
 * 创建作者：黎丝军
 * 创建时间：2016/10/11 13:38
 */

public interface IBusiness{

    /**
     * 获取监听实例接口
     * @return 监听实例接口
     */
    IRequestCallBack getRequestCallBack();
    /**
     * 设置请求回调接口
     * @param callBack 接口实例
     */
    void setRequestCallBack(IRequestCallBack callBack);
    /**
     * 获取上下文环境
     */
    Context getContext();

    /**
     * 设置上下环境
     * @param context 运行时
     */
    void setContext(Context context);
}

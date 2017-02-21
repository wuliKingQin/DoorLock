package cn.saiyi.doorlock.interfaces;

/**
 * 描述：更新UI回调接口，主要用在上层界面对下层界面的回调
 * 创建作者：黎丝军
 * 创建时间：2016/10/13 17:31
 */

public interface IUpdateUICallBack {
    /**
     * 更新视图方法
     */
    void onUpdateView(Object data);
}

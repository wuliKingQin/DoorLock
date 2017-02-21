package com.saiyi.framework.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 文件描述：用于维护Item
 * 创建作者：黎丝军
 * 创建时间：16/8/1 AM11:18
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    //用于getViewById
    private View mItemView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
    }

    /**
     * 通过id获取控件实例
     * @param viewId 控件id
     * @param <T> 返回类型
     * @return 返回控件实例
     */
    public <T extends View> T getViewById(int viewId) {
        return (T)mItemView.findViewById(viewId);
    }

}

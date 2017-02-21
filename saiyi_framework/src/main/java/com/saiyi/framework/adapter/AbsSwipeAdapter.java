package com.saiyi.framework.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;

import java.util.List;

/**
 * 文件描述：适配列表item带侧滑布局的数据适配器
 * 创建作者：黎丝军
 * 创建时间：16/8/1 PM1:46
 */
public abstract class AbsSwipeAdapter<T,VH extends RecyclerView.ViewHolder> extends AbsBaseAdapter<T,VH>
        implements SwipeItemMangerInterface, SwipeAdapterInterface {

    //判断是否有侧滑视图打开
    protected boolean isSwipeOpen = false;
    //用于管理item收放
    private SwipeItemRecyclerMangerImpl mItemManger;

    public AbsSwipeAdapter(Context context, int layoutId) {
        super(context, layoutId);
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
    }

    @Override
    protected void setItemListeners(VH holder, final T t, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen(position)) {
                    if(mItemClickListener != null) {
                        mItemClickListener.onItemClick(v,getItemBean(position),position);
                    }
                } else {
                    closeItem(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!isOpen(position)) {
                    if(mItemLongListener != null) {
                        return mItemLongListener.onItemLong(v,getItemBean(position),position);
                    }
                } else {
                    closeItem(position);
                }
                return true;
            }
        });
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }

    /**
     * 绑定视图
     * @param swipeLayout swipe视图
     * @param position 位置
     */
    protected void bindSwipeLayout(View swipeLayout,int position) {
        mItemManger.bindView(swipeLayout,position);
    }

    /**
     * 判断是item的侧滑视图是否打开，用于按返回键时判断
     * @return false表示没有打开，否则表示打开
     */
    public boolean isSwipeOpen() {
        return isSwipeOpen;
    }

    /**
     * 用于监听侧滑过程
     */
    private SwipeLayout.SwipeListener mSwipeListener = new SimpleSwipeListener() {
        @Override
        public void onStartOpen(SwipeLayout swipeLayout) {
            closeAllItems();
        }

        @Override
        public void onOpen(SwipeLayout layout) {
            isSwipeOpen = true;
        }

        @Override
        public void onStartClose(SwipeLayout layout) {
            isSwipeOpen = false;
        }
    };

    /**
     * 设置swipe监听器，该类需要在子类中调用
     * @param swipeLayout swipeLayout实例
     */
    protected void setSwipeListener(SwipeLayout swipeLayout) {
        swipeLayout.addSwipeListener(mSwipeListener);
    }
}

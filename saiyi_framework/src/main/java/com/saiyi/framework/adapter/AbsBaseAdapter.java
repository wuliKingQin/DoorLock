package com.saiyi.framework.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述：基本数据适配器，目前只使用RecyclerView衍生的列表
 *         该抽象类废弃了原来的onCreateViewHolder，onBindViewHolder两个方法，使用了onCreateVH，onBindDataForItem来替代。
 *         在重写该类时，需要传两个泛型参数，一个是列表数据bean或者任何类型的其他，还有就是实现了ViewHolder的类
 * 创建作者：黎丝军
 * 创建时间：16/7/28 PM1:43
 */
public abstract class AbsBaseAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    //Item布局Id
    private int mLayoutId;
    //运行环境
    private Context mContext;
    //用于存数列表数据
    private List<T> mData = new ArrayList<>();
    //每一项的点击监听器
    protected OnItemClickListener mItemClickListener;
    //item长按监听器
    protected OnItemLongListener mItemLongListener;

    public AbsBaseAdapter(Context context,int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
    }

    /**
     * 该方法已经被我废弃，使用onCreateVH方法来代替
     * @param parent
     * @param viewType
     * @return
     */
    @Deprecated
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext).inflate(mLayoutId,parent,false);
        return onCreateVH(itemView,viewType);
    }

    /**
     * 使用ItemView来创建ViewHolder实例,并返回
     * @param itemView 子项布局实例
     * @param ViewType 视图类型
     * @return 返回VH实例
     */
    public abstract VH onCreateVH(View itemView,int ViewType);

    /**
     * 该方法被我废弃，使用onBindDataForItem()方法来替代了
     * @param holder
     * @param position
     */
    @Deprecated
    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindDataForItem(holder,mData.get(position),position);
        setItemListeners(holder,mData.get(position),position);
    }

    /**
     * 设置Item里面按钮的监听器
     * @param holder 视图views
     * @param t 实例对象
     * @param position 位置
     */
    protected void setItemListeners(VH holder, final T t, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(v,mData.get(position),position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mItemLongListener != null) {
                    return mItemLongListener.onItemLong(v,mData.get(position),position);
                }
                return true;
            }
        });
    }

    /**
     * 为子项布局视图填充数据
     * @param viewHolder ViewHolder实例
     * @param bean bean实例
     * @param position 位置
     */
    public abstract void onBindDataForItem(VH viewHolder,T bean,int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 获取item实例
     * @param position 位置
     * @return bean实例
     */
    public T getItemBean(int position) {
        return mData.get(position);
    }

    public Context getContext() {
        return mContext;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongListener(OnItemLongListener listener) {
        mItemLongListener = listener;
    }

    /**
     * 设置列表数据，此方法是初始化时调用
     * 因为ListData会覆盖原来的所有数据
     * @param listData 列表数据
     */
    public void setListData(List<T> listData) {
        mData.clear();
        mData.addAll(listData);
        notifyDataSetChanged();
    }

    /**
     * 根据位置来添加数据
     * @param position 位置，使用getItemCount来获取
     * @param listData 需要添加的数据
     */
    public void addListData(int position,List<T> listData) {
        mData.addAll(position,listData);
    }

    /**
     * 根据位置来添加一项数据
     * @param data 数据
     */
    public void addData(T data) {
        if(!mData.contains(data)) {
            mData.add(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 根据位置来添加一项数据
     * @param position 位置，使用getItemCount来获取
     * @param data 数据
     */
    public void addData(int position,T data) {
        if(!mData.contains(data)) {
            mData.add(position,data);
            notifyItemInserted(position);
        }
    }

    /**
     * 根据位置移除数据
     * @param position 位置
     */
    public void removeData(int position) {
        if(position >= 0 && position < mData.size()) {
            mData.remove(position);
            notifyItemRangeRemoved(position,getItemCount());
        }
    }

    /**
     * 获取数据集合
     * @return 集合数据
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 清除数据
     */
    public void clearData() {
        mData.clear();
    }

    /**
     * 点击每一个Item是的监听器
     */
    public interface OnItemClickListener<T> {
        /**
         * 处理点击事情
         * @param view item视图
         * @param bean 数据实例
         * @param position 位置
         */
        void onItemClick(View view, T bean, int position);
    }

    /**
     * 长按item视图监听器
     */
    public interface OnItemLongListener<T> {
        /**
         * item长按事件
         * @param view item视图
         * @param bean 数据实例
         * @param position 位置
         * @return false or true
         */
        boolean onItemLong(View view, T bean, int position);
    }
}

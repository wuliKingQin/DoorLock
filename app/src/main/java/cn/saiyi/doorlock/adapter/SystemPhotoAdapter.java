package cn.saiyi.doorlock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.PhotoBean;

/**
 * 描述：系统头像适配器
 * 创建作者：黎丝军
 * 创建时间：2016/10/20 13:55
 */

public class SystemPhotoAdapter extends BaseAdapter{

    private List<PhotoBean> mData;
    //用于生成item
    private LayoutInflater inflater;
    //运行环境
    private Context mContext;

    public SystemPhotoAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View itemView = inflater.inflate(R.layout.listview_system_photo_item,null);
        final ImageView photoIv = (ImageView)itemView.findViewById(R.id.iv_system_photo);
        if(mData.get(position).isSelected()) {
            itemView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_system_photo_bg));
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
        photoIv.setImageResource(mData.get(position).getPhotoResId());
        return itemView;
    }

    /**
     * 设置数据
     * @param listData 列表
     */
    public void setListData(List<PhotoBean> listData) {
        mData.clear();
        mData.addAll(listData);
        notifyDataSetChanged();
    }

    /**
     * 设置选中状态
     * @param position 位置
     */
    public void setSelectedState(int position) {
        int index = 0;
        final int length = getCount();
        for (; index < length; index++) {
            if(index == position) {
                mData.get(position).setSelected(true);
            } else {
                mData.get(index).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }
}

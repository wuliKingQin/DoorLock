package cn.saiyi.doorlock.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.saiyi.framework.adapter.AbsBaseAdapter;
import com.saiyi.framework.adapter.BaseViewHolder;
import com.saiyi.framework.util.ResourcesUtils;
import com.saiyi.framework.view.CircleImageView;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.OpenLockRecordBean;

/**
 * 描述：开锁记录列表适配器
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 17:55
 */

public class OpenLockRecordAdapter extends AbsBaseAdapter<OpenLockRecordBean,OpenLockRecordAdapter.RecordViewHolder>{

    public OpenLockRecordAdapter(Context context) {
        super(context, R.layout.listview_open_lock_record_item);
    }

    @Override
    public RecordViewHolder onCreateVH(View itemView, int ViewType) {
        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindDataForItem(RecordViewHolder viewHolder, OpenLockRecordBean bean, int position) {
        viewHolder.describeTv.setText(bean.getDescribe());
        viewHolder.dateTv.setText(bean.getDate());
        viewHolder.timeTv.setText(bean.getTime());
        viewHolder.headIcon.setImageResource(ResourcesUtils.getMipmapResId(getContext(),bean.getHeadUrl()));
        if(bean.getTypeIconUrl() != null) {
            viewHolder.typeIcon.setImageResource(ResourcesUtils.getMipmapResId(getContext(),bean.getTypeIconUrl()));
        }
    }

    /**
     * 开锁记录视图
     */
    public class RecordViewHolder extends BaseViewHolder {
        //开锁的人物头像
        CircleImageView headIcon;
        //开锁描述
        TextView describeTv;
        //开锁类型图片
        ImageView typeIcon;
        //开锁日期
        TextView dateTv;
        //开锁时间
        TextView timeTv;

        public RecordViewHolder(View itemView) {
            super(itemView);
            headIcon = getViewById(R.id.iv_record_head_icon);
            describeTv = getViewById(R.id.tv_open_lock_name);
            typeIcon = getViewById(R.id.iv_type_image);
            dateTv = getViewById(R.id.tv_open_lock_date);
            timeTv = getViewById(R.id.tv_open_lock_time);
        }
    }

}

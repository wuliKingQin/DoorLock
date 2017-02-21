package cn.saiyi.doorlock.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.saiyi.framework.adapter.AbsBaseAdapter;
import com.saiyi.framework.adapter.BaseViewHolder;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.ProductBean;

/**
 * 描述：产品适配器
 * 创建作者：黎丝军
 * 创建时间：2016/10/18 8:39
 */

public class ProductAdapter extends AbsBaseAdapter<ProductBean,ProductAdapter.ProductViewHolder> {


    public ProductAdapter(Context context) {
        super(context, R.layout.listview_product_item);
    }

    @Override
    public ProductViewHolder onCreateVH(View itemView, int ViewType) {
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindDataForItem(ProductViewHolder viewHolder, ProductBean bean, int position) {
        viewHolder.nameTv.setText(bean.getName());
    }

    /**
     * 产品视图支持
     */
    public class ProductViewHolder extends BaseViewHolder {
        //产品图片
        ImageView productIcon;
        //产品名字
        TextView nameTv;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productIcon = getViewById(R.id.iv_product_icon);
            nameTv = getViewById(R.id.tv_product_name);
        }
    }
}

package cn.saiyi.doorlock.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.adapter.AbsBaseAdapter;
import com.saiyi.framework.adapter.BaseViewHolder;
import com.saiyi.framework.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.ContactBean;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;

/**
 * 描述：通讯录列表适配器
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 15:49
 */

public class AddressBookAdapter extends AbsBaseAdapter<ContactBean,AddressBookAdapter.AddressBookViewHolder> {

    //用于保存被选中的联系人数据
    private List<ContactBean> mSelectedData;

    public AddressBookAdapter(Context context) {
        super(context, R.layout.listview_address_book_item);
        mSelectedData = new ArrayList<>();
    }

    @Override
    public AddressBookViewHolder onCreateVH(View itemView, int ViewType) {
        return new AddressBookViewHolder(itemView);
    }

    @Override
    public void onBindDataForItem(AddressBookViewHolder viewHolder, ContactBean bean, int position) {
        viewHolder.nameTv.setText(bean.getPhone());
        if(bean.isShared()) {
            viewHolder.selectCkb.setEnabled(false);
            viewHolder.selectCkb.setChecked(true);
        }
    }

    @Override
    protected void setItemListeners(final AddressBookViewHolder holder, final ContactBean contactBean, int position) {
        super.setItemListeners(holder, contactBean, position);
        holder.selectCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
                if(isChecked) {
                   if(!contactBean.isShared()) {
                       hasRegisterHandle(holder,contactBean);
                   }
                } else {
                    mSelectedData.remove(contactBean);
                }
            }
        });
    }

    /**
     * 处理请求验证该用户是否已经注册
     * @param holder 视图
     * @param contactBean bean
     */
    private void hasRegisterHandle(final AddressBookViewHolder holder, final ContactBean contactBean) {
        HttpRequest.get(URL.IS_USER_EXIST + contactBean.getPhone(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject resultInfo) {
                try {
                    int result = resultInfo.getInteger("result");
                    if(result == 1) {
                        mSelectedData.add(contactBean);
                    } else if(result == 2){
                        holder.selectCkb.setChecked(true);
                        onFailure(Constant.ERROR_CODE,"该用户已经在分享列表");
                    } else {
                        holder.selectCkb.setChecked(false);
                        onFailure(Constant.ERROR_CODE,"该用户没有注册！");
                    }
                } catch (Exception e) {
                    onFailure(Constant.ERROR_CODE,"服务器错误");
                }

            }
            @Override
            public void onFailure(int errorCode, String msg) {
                holder.selectCkb.setChecked(false);
                if(errorCode == Constant.ERROR_CODE) {
                    ToastUtils.toast(getContext(),msg);
                } else {
                    ToastUtils.toast(getContext(),"网络出错");
                }
            }
        });
    }

    /**
     * 获取选中的人系人数据
     * @return 数据
     */
    public List<ContactBean> getSelectedData() {
        return mSelectedData;
    }

    /**
     * 通讯录视图
     */
    public class AddressBookViewHolder extends BaseViewHolder {
        //名字
        TextView nameTv;
        //选择
        CheckBox selectCkb;

        public AddressBookViewHolder(View itemView) {
            super(itemView);
            nameTv = getViewById(R.id.tv_address_item_name);
            selectCkb = getViewById(R.id.ckb_address_item_select);
        }
    }
}

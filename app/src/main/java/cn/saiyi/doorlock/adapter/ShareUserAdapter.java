package cn.saiyi.doorlock.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.saiyi.framework.adapter.AbsSwipeAdapter;
import com.saiyi.framework.adapter.BaseViewHolder;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ResourcesUtils;
import com.saiyi.framework.util.ToastUtils;
import com.saiyi.framework.view.CircleImageView;
import com.saiyi.framework.view.InfoHintDialog;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.bean.ContactBean;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：分享用户列表适配器
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 17:39
 */

public class ShareUserAdapter extends AbsSwipeAdapter<ContactBean,ShareUserAdapter.ShareUserViewHolder> {

    //判断是否有侧滑视图打开
    private boolean isSwipeOpen = false;
    //侧滑点击监听
    private OnSlideClickListener mListener;
    //提示dialog
    private InfoHintDialog mHintDialog;

    public ShareUserAdapter(Context context) {
        super(context, R.layout.listview_authority_item);
        mHintDialog = new InfoHintDialog(context);
        mHintDialog.setTitle(R.string.dialog_hint);
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.sl_authority_share_user;
    }

    @Override
    public ShareUserViewHolder onCreateVH(View itemView, int ViewType) {
        return new ShareUserViewHolder(itemView);
    }

    @Override
    public void onBindDataForItem(ShareUserViewHolder viewHolder, ContactBean bean, int position) {
        viewHolder.nameTv.setText(bean.getName());
        viewHolder.headIconIv.setImageResource(ResourcesUtils.getMipmapResId(getContext(),bean.getHeadIconUrl()));
        bindSwipeLayout(viewHolder.itemSwipeView,position);
    }

    @Override
    protected void setItemListeners(final ShareUserViewHolder holder, final ContactBean contactBean, final int position) {
        super.setItemListeners(holder, contactBean, position);
        holder.itemSwipeView.addSwipeListener(mSwipeListener);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.setContentText("确定要删除嘛？");
                mHintDialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHintDialog.dismiss();
                        ProgressUtils.showDialog(getContext(),"正在删除中，……",null);
                        deleteHandle(holder,contactBean);
                    }
                });
                mHintDialog.show();
            }
        });
        holder.turnOverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.setContentText("确定要将权限移交给该用户吗？");
                mHintDialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHintDialog.dismiss();
                        turnOverHandle(holder,contactBean,position);
                    }
                });
                mHintDialog.show();
            }
        });
    }

    /**
     * 处理删除按钮
     * @param contactBean 联系人
     */
    private void deleteHandle(final ShareUserViewHolder holder, final ContactBean contactBean) {
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone",contactBean.getPhone());
        jsonParam.putJSONParam("mac",contactBean.getMac());
        HttpRequest.post(URL.DELETE_SHARE_USER,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                final int result = jsonObject.getInteger("result");
                if(result == 1) {
                    closeAllItems();
                    if(mListener != null) {
                        mListener.onDeleteClick(holder,contactBean);
                    }
                    ToastUtils.toast(getContext(),"删除成功");
                } else {
                    onFailure(result,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                closeAllItems();
                ToastUtils.toast(getContext(),"删除失败");
            }

            @Override
            public void onFinish() {
                isSwipeOpen = false;
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 处理移交管理权限操作
     * @param contactBean 联系人
     * @param position 位置
     */
    private void turnOverHandle(final ShareUserViewHolder holder,final ContactBean contactBean, int position) {
        ProgressUtils.showDialog(getContext(),"正在移交权限，……",null);
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("phone", LoginUtil.getAccount());
        jsonParam.putJSONParam("mac",contactBean.getMac());
        jsonParam.putJSONParam("newphone",contactBean.getPhone());
        HttpRequest.post(URL.SHIFT_DEVICE,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                if(jsonObject != null) {
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        closeAllItems();
                        if(mListener != null) {
                            mListener.onChangeClick(holder,contactBean);
                        }
                        ToastUtils.toast(getContext(),"移交权限成功");
                    } else {
                        onFailure(result,null);
                    }
                } else {
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getContext(),"移交权限失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 分享视图
     */
    public class ShareUserViewHolder extends BaseViewHolder {
        //分享用户名
        TextView nameTv;
        //头像
        CircleImageView headIconIv;
        //删除
        Button deleteBtn;
        //移交管理
        Button turnOverBtn;
        //侧滑布局
        SwipeLayout itemSwipeView;

        public ShareUserViewHolder(View itemView) {
            super(itemView);
            nameTv = getViewById(R.id.tv_share_user_name);
            headIconIv = getViewById(R.id.iv_share_user_head);
            deleteBtn = getViewById(R.id.btn_authority_delete);
            turnOverBtn = getViewById(R.id.btn_authority_turn_over);
            itemSwipeView = getViewById(R.id.sl_authority_share_user);
        }
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
     * 判断是item的侧滑视图是否打开，用于按返回键时判断
     * @return false表示没有打开，否则表示打开
     */
    public boolean isSwipeOpen() {
        return isSwipeOpen;
    }

    /**
     * 设置侧滑监听器实例
     * @param listener 监听实例
     */
    public void setOnSlideClickListener(OnSlideClickListener listener) {
        mListener = listener;
    }

    /**
     * 侧滑点击监听器
     */
    public interface OnSlideClickListener {

        /**
         * 点击侧滑删除监听方法
         * @param holder viewHolder
         */
        void onDeleteClick(ShareUserViewHolder holder,ContactBean bean);

        /**
         * 点击侧滑其他按钮监听方法
         * @param holder ViewHolder
         * @param bean bean
         */
        void onChangeClick(ShareUserViewHolder holder,ContactBean bean);
    }
}

package com.saiyi.framework.blls;

import android.content.Context;
import android.view.View;

import com.saiyi.framework.AppHelper;
import com.saiyi.framework.view.InfoHintDialog;

/**
 * 文件描述:处理界面的业务逻辑抽象基础类
 *         该业务类实现了基本的点击事件监听，主要处理了导航栏左右点击事件
 * 创建作者:黎丝军
 * 创建时间:16/7/28
 */
public abstract class AbsBaseBusiness implements IBusiness {

    //全局帮助类
    protected AppHelper appHelper;
    //提示Dialog
    protected InfoHintDialog mHintDialog;
    //运行环境
    private Context mContext;

    public AbsBaseBusiness() {
        appHelper = AppHelper.instance();
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
        mHintDialog = new InfoHintDialog(context);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 显示提示框
     * @param titleResId 标题资源Id
     * @param content 内容
     * @param isShowCancel 是否显示取消按钮
     */
    protected void showHintDialog(int titleResId,String content,boolean isShowCancel) {
        mHintDialog.setTitle(titleResId);
        mHintDialog.setContentText(content);
        if(isShowCancel) {
            mHintDialog.setCancelBtnVisibility(View.VISIBLE);
        } else {
            mHintDialog.setCancelBtnVisibility(View.GONE);
        }
        mHintDialog.show();
    }


    /**
     * 显示提示框
     * @param titleResId 标题资源Id
     * @param contentResId 内容资源Id
     * @param isShowCancel 是否显示取消按钮
     */
    protected void showHintDialog(int titleResId,int contentResId,boolean isShowCancel) {
        showHintDialog(titleResId,getContext().getString(contentResId),isShowCancel);
    }


    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }
}

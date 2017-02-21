package com.saiyi.framework.listenerimpl;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.view.ActionBarView;

/**
 * 文件描述：实现基础监听器，该类在被继承时需要传两个泛型参数，第一个泛型是监听器对应的activity
 *           第二个泛型是控制器对于的服务类，这样实现的目的就是将业务和界面分割开来，互不影响，
 *           即便是界面变化了，也不需要大概业务类。
 * 创建作者：黎丝军
 * 创建时间：2016/8/27 14:52
 */
public class BaseListenerImpl<T,B extends IBusiness> implements IListener<T,B>,View.OnClickListener{

    //目标Ui
    private T mUi;
    //业务类接口
    protected B mBusiness;

    @Override
    public void setUI(T ui) {
        mUi = ui;
    }

    @Override
    public T getActivity() {
        if(mUi instanceof Activity) {
            return mUi;
        }
        return null;
    }

    @Override
    public T getFragment() {
        if(mUi instanceof Fragment) {
            return mUi;
        }
        return null;
    }

    @Override
    public Context getContext() {
        if(mUi instanceof Activity) {
            return (Context) mUi;
        } else if(mUi instanceof Fragment) {
            return ((Fragment) mUi).getContext();
        }
        return null;
    }

    @Override
    public void register(int eventType, View registerView, Object registerObj) {
        switch (eventType) {
            case ON_CLICK:
                registerView.setOnClickListener(this);
                break;
            case ON_ACTION_BAR_LEFT_CLICK:
                ((ActionBarView)registerView).setLeftClickListener(this);
                break;
            case ON_ACTION_BAR_RIGHT_CLICK:
                ((ActionBarView)registerView).setRightClickListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void setBusiness(B business) {
        mBusiness = business;
    }

    @Override
    public B getBusiness() {
        return mBusiness;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case ActionBarView.LEFT_BUTTON_ID:
                actionBarLeftClick(view);
                break;
            case ActionBarView.RIGHT_BUTTON_ID:
                actionBarRightClick(view);
                break;
            default:
                break;
        }
    }

    /**
     * 头部按钮左边点击事件
     * @param leftView
     */
    protected void actionBarLeftClick(View leftView) {
    }

    /**
     * 头部按钮右边点击事件
     * @param rightView
     */
    protected void actionBarRightClick(View rightView) {
    }
}

package com.saiyi.framework.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.saiyi.framework.blls.IBusiness;
import com.saiyi.framework.interfaces.IListener;
import com.saiyi.framework.interfaces.IUi;
import com.saiyi.framework.other.SystemBarTintManager;
import com.saiyi.framework.view.ActionBarView;


/**
 * 文件描述：基本碎片Fragment,该类是抽象类
 *          该碎片抽象出了一个onCreateUI()方法，用来初始化碎片UI控件布局
 *          设置布局时，调用setContentView()方法，和你在Activity里使用一样。
 *          获取布局里的某个控件实例的时候，你直接使用getViewById()这个方法就可以了。
 *          该碎片也实现了头部栏，但是有时候大多是又不需要，那么本类是通过isActionBar方法去判断是否需要头部的
 *          该方法默认是放回false,如果需要你重写返回true就可以了，当然你也可以直接使用ActionBarFragment
 * 创建作者：机器人
 * 创建时间：16/4/26
 */
public abstract class BaseFragment extends Fragment implements IUi {

    //监听实例
    protected  IListener mListener;
    //业务接口实例
    protected IBusiness mBusiness;
    //头部导航栏
    protected ActionBarView actionBar;
    //跟布局用于缓存
    private LinearLayout mRootContainer;
    //子容器
    private FrameLayout mChildContainer;
    //获取布局实例的句柄
    private LayoutInflater mLayoutInflater;

    /**
     * 在后面子类中必须使用onCreateUi方法来替代,
     * 否则运行或许会出错
     */
    @Deprecated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootContainer == null) {
            mLayoutInflater = inflater;
            initListener();
            initBaseLayout();
            onContentView();
            findViews();
            initObjects();
            initBusinessObjects();
            initData(savedInstanceState);
            initBusinessData(savedInstanceState);
            setListeners();
        } else {
            removeView(mRootContainer);
        }
        return mRootContainer;
    }

    /**
     * 初始化监听接口
     */
    private void initListener() {
        if(mListener == null) {
            mListener = getListener();
            if(mListener != null) {
                mListener.setUI(this);
                if(mBusiness == null) {
                    mBusiness = getBusiness();
                    if(mBusiness != null) {
                        mBusiness.setContext(getContext());
                    }
                }
                mListener.setBusiness(mBusiness);
            }
        }
    }

    /**
     * 初始化基本布局，带有自定义头部导航栏
     */
    private void initBaseLayout() {
        mRootContainer = new LinearLayout(getContext());
        mRootContainer.setOrientation(LinearLayout.VERTICAL);
        mRootContainer.setFitsSystemWindows(true);
        mRootContainer.setClipToPadding(false);
        mRootContainer.setBackgroundColor(Color.WHITE);
        mRootContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if(isActionBar()) {
            actionBar = new ActionBarView(getContext());
            actionBar.setBackgroundColor(Color.GRAY);
            mRootContainer.addView(actionBar);
        }
        mChildContainer = new FrameLayout(getContext());
        mChildContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mRootContainer.addView(mChildContainer);
        if(isChangeStateColor()) {
            setStatusBarBackgroundColor(Color.GRAY);
        }
    }

    /**
     * 初始化业务类里的对象
     */
    private void initBusinessObjects() {
        if (mBusiness != null) {
            mBusiness.initObject();
        }
    }

    /**
     * 初始化业务类里的数据
     */
    private void initBusinessData(Bundle bundle) {
        if (mBusiness != null) {
            mBusiness.initData(bundle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    /**
     * 获取业务类
     * @return IBusiness实例
     */
    public IBusiness getBusiness() {
        return mBusiness;
    }

    /**
     * 设置导航栏标题
     * @param title 标题信息
     */
    public void setTitle(CharSequence title) {
        if(actionBar !=  null) {
            actionBar.setActionBarTitle(title);
        }
    }

    /**
     * 使用资源Id来设置头部导航栏标题
     * @param titleId 标题资源Id
     */
    public void setTitle(int titleId) {
        if(actionBar !=  null) {
            actionBar.setActionBarTitle(titleId);
        }
    }

    /**
     * 设置标题颜色
     * @param textColor 颜色值
     */
    public void setTitleColor(int textColor) {
        if(actionBar !=  null) {
            int colorTemp;
            try {
                colorTemp = getResources().getColor(textColor);
            } catch (Exception e) {
                colorTemp = textColor;
            }
            actionBar.setActionBarTitleColor(colorTemp);
        }
    }

    /**
     * 设置标题文本大小
     * @param textSize 文本大小
     */
    public void setTitleSize(float textSize) {
        if(actionBar !=  null) {
            actionBar.setActionBarTitleSize(textSize);
        }
    }

    /**
     * 设置头部横条的背景颜色
     * @param colorResId 颜色值
     */
    protected void setActionBarBackgroundColor(int colorResId) {
        if(actionBar != null) {
            int color;
            try {
                color = getResources().getColor(colorResId);
            } catch (Exception e) {
                color = colorResId;
            }
            actionBar.setBackgroundColor(color);
//            setStatusBarBackgroundColor(getResources().getColor(colorResId));
        }
    }

    /**
     * 是否需要有头部栏
     * @return 返回true表示需要，false表示不需要
     */
    protected boolean isActionBar() {
        return false;
    }


    /**
     * 配置的颜色Id设置状态背景颜色
     * @param color 颜色值
     */
    protected void setStatusBarBackgroundColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }
            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(color);
        }
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 通过Id设置Fragment的内容视图
     * @param id 布局Id
     */
    protected void setContentView(int id) {
        final View view = mLayoutInflater.inflate(id,null);
        mChildContainer.addView(view);
    }

    /**
     * 设置碎片内容布局视图
     * @param view 需要设置的视图
     */
    protected void setContentView(View view) {
        mChildContainer.addView(view);
    }

    /**
     * 设置碎片内容布局视图
     * @param view 需要设置的视图
     * @param layoutParams 布局参数
     */
    protected void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        mChildContainer.addView(view,layoutParams);
    }

    /**
     * 从父视图中移除该视图
     * @param view 需要移除View
     */
    protected void removeView(View view) {
        final ViewGroup viewGroup = (ViewGroup) view.getParent();
        if(viewGroup != null) {
            viewGroup.removeView(view);
        }
    }

    @Override
    public <T> T getViewById(int id) {
        return (T) mRootContainer.findViewById(id);
    }

    /**
     * 启动另一个Activity
     * @param activityCl 需要启动的Activity
     */
    public void startActivity(Class<?> activityCl) {
        final Intent intent = new Intent(getContext(),activityCl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 启动另一个Activity
     * @param activityCl 需要启动的Activity
     */
    public void startActivity(Class<?> activityCl,String key,String value) {
        final Intent intent = new Intent(getContext(),activityCl);
        intent.putExtra(key,value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 注册视图监听对象
     * @param eventType 事件类型
     * @param registerView 注册视图
     */
    protected void registerListener(int eventType,View registerView) {
        if(mListener != null) {
            mListener.register(eventType,registerView,null);
        }
    }

    /**
     * 注册视图监听对象
     * @param eventType 事件类型
     * @param registerObj 需要注册的对象
     */
    protected void registerListener(int eventType,Object registerObj) {
        if(mListener != null) {
            mListener.register(eventType,null,registerObj);
        }
    }

    /**
     * 根据指定视图容器Id来添加碎片
     * @param containId      视图容器Id
     * @param targetFragment 需要添加的碎片对象
     */
    public void addFragment(int containId, Fragment targetFragment) {
        addFragment(containId, targetFragment, null);
    }

    /**
     * 根据指定视图容器Id来添加碎片
     * @param containId      视图容器Id
     * @param targetFragment 需要添加的碎片对象
     * @param tagName        碎片标签名
     */
    public void addFragment(int containId, Fragment targetFragment, String tagName) {
        if (tagName != null) {
            getFragmentManager().beginTransaction().add(containId, targetFragment, tagName).commit();
        } else {
            getFragmentManager().beginTransaction().add(containId, targetFragment).commit();
        }
    }

    /**
     * 隐藏指定的碎片
     * @param targetFragment 需要隐藏的碎片对象
     */
    public void hideFragment(Fragment targetFragment) {
        getFragmentManager().beginTransaction().hide(targetFragment).commit();
    }

    /**
     * 移除指定碎片
     * @param targetFragment 需要移除的碎片对象
     */
    public void removeFragment(Fragment targetFragment) {
        getFragmentManager().beginTransaction().remove(targetFragment).commit();
    }

    /**
     * 替换某个视图Id对应位置的碎片
     * @param containId      需要替换的视图容器Id
     * @param targetFragment 需要替换的碎片对象
     */
    public void replaceFragment(int containId, Fragment targetFragment) {
        replaceFragment(containId, targetFragment, null);
    }

    /**
     * 显示目标碎片
     * @param targetFragment 目标碎片
     */
    public void showFragment(Fragment targetFragment) {
        getFragmentManager().beginTransaction().show(targetFragment).commit();
    }

    /**
     * 替换某个视图Id对应位置的碎片
     * @param containId      需要替换的视图容器Id
     * @param targetFragment 需要替换的碎片对象
     * @param tagName        碎片标签名
     */
    public void replaceFragment(int containId, Fragment targetFragment, String tagName) {
        if (tagName != null) {
            getFragmentManager().beginTransaction().replace(containId, targetFragment, tagName).commit();
        } else {
            getFragmentManager().beginTransaction().replace(containId, targetFragment).commit();
        }
    }

    /**
     * 设置Fragment进入或退出的方式
     * @param enterResId 进入动画资源Id
     * @param exitResId  退出时需要播放的资源Id
     */
    public void setCustomAnimations(int enterResId, int exitResId) {
        getFragmentManager().beginTransaction().setCustomAnimations(enterResId, exitResId).commit();
    }

    /**
     * 添加碎片，此方法在添加碎片时会把原来的删除
     *
     * @param targetFragment 目标碎片
     */
    public void detach(Fragment targetFragment) {
        getFragmentManager().beginTransaction().detach(targetFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBusiness != null) {
            mBusiness.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mBusiness != null) {
            mBusiness.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBusiness != null) {
            mBusiness.onDestroy();
        }
    }

    //是否需要改变状态栏颜色
    public boolean isChangeStateColor() {
        return false;
    }

    public IListener getListener() {
        return mListener;
    }
}

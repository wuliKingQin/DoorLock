package com.saiyi.framework.view.slidemenu;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.DensityUtils;

/**
 * <p/>
 * 描述: 左菜单构造器
 * </P>
 */
public class SlideMenuMgr {

    //侧滑菜单
    private SlideMenu mSlideMenu = null;
    //用于加载侧滑菜单内容
    private FrameLayout mSlideMenuLayout;
    //主界面实例
    private AbsBaseActivity mActivity = null;
    //侧滑布局容器Id
    private static final int SLIDE_LAYOUT_ID = 0x100004;
    //唯一实例
    private static SlideMenuMgr ourInstance;

    public static SlideMenuMgr instance() {
        if(ourInstance == null) {
            ourInstance = new SlideMenuMgr();
        }
        return ourInstance;
    }

    private SlideMenuMgr() {
    }

    /**
     * 该方法主要用在第一次创建使用
     * @param activity 目标activity
     */
    public void createSlideMenu(AbsBaseActivity activity) {
        mActivity = activity;
        mSlideMenu = new SlideMenu(activity);
        mSlideMenu.setFitsSystemWindows(true);
        mSlideMenu.setClipToPadding(false);
        mSlideMenu.setBackgroundColor(Color.WHITE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSlideMenu.setLayoutParams(params);
        //初始化侧滑菜单布局容器
        mSlideMenuLayout = new FrameLayout(activity);
    }

    /**
     * 构建滑动菜单
     */
    public void builderSlideMenu(View mainView, Fragment leftMenFragment) {
        addLeftView(mSlideMenuLayout,0.8f,SLIDE_LAYOUT_ID);
        addMiddleView(mainView);
        mActivity.replaceFragment(SLIDE_LAYOUT_ID,leftMenFragment);
    }

    /**
     * 添加中间视图，也就是主要视图
     * @param view 主要视图
     */
    private void addMiddleView(View view) {
        SlideMenu.LayoutParams params = new SlideMenu.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                SlideMenu.LayoutParams.ROLE_CONTENT);
        mSlideMenu.addView(view,params);
    }

    /**
     * 添加左菜单视图
     * @param leftMenu 左菜单视图
     * @param menuAreaRate 侧滑菜单所占比率
     */
    private void addLeftView(View leftMenu,float menuAreaRate,int layoutId) {
        addView(leftMenu,layoutId, menuAreaRate, SlideMenu.LayoutParams.ROLE_LEFT_MENU);
    }

    /**
     * 添加右菜单视图
     * @param rightMenu 右菜单视图
     * @param menuAreaRate 侧滑菜单所占比率
     */
    private void addRightView(View rightMenu,float menuAreaRate,int layoutId) {
        addView(rightMenu,layoutId,menuAreaRate,SlideMenu.LayoutParams.ROLE_RIGHT_MENU);
    }

    /**
     * 添加视图
     * @param view 需要添加的视图
     * @param menuAreaRate 侧滑菜单所占比率
     * @param menuType 菜单类型，目前只有左右菜单
     */
    private void addView(View view,int layoutId,float menuAreaRate,int menuType) {
        view.setId(layoutId);
        SlideMenu.LayoutParams params = new SlideMenu.LayoutParams(
                (int) (DensityUtils.getScreenWidth(mActivity) * menuAreaRate),
                ViewGroup.LayoutParams.MATCH_PARENT,menuType);
        mSlideMenu.addView(view,params);
    }

    /**
     * 获取滑动菜单
     * @return SlideMenu
     */
    public SlideMenu getSlideMenu() {
        return mSlideMenu;
    }

    /**
     * 打开侧滑菜单
     */
    public void openSlideMenu() {
        if(mSlideMenu != null) {
            mSlideMenu.open(false,true);
        }
    }

    /**
     * 关闭侧滑菜单
     */
    public void closeSlideMenu() {
        if(mSlideMenu != null) {
            mSlideMenu.close(true);
        }
    }

    /**
     * 判断侧滑菜单是否被打开
     * @return false表示没有打开，否则打开
     */
    public boolean isOpen() {
        if(mSlideMenu != null) {
            return mSlideMenu.isOpen();
        }
        return false;
    }

    /**
     * 获取侧滑布局容器的Id
     * @return 布局Id
     */
    public int getSlideLayoutId() {
        return SLIDE_LAYOUT_ID;
    }

}

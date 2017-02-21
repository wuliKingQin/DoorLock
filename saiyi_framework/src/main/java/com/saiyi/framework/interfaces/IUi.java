package com.saiyi.framework.interfaces;

import android.os.Bundle;

/**
 * 文件描述：UI初始化接口
 * 创建作者：黎丝军
 * 创建时间：16/7/28 PM12:27
 */
public interface IUi {

    /**
     * 创建Ui布局
     */
    void onContentView();
    /**
     * 通过Id初始化View实例
     */
    void findViews();

    /**
     * 初始化对象方法
     */
    void initObjects();

    /**
     * 初始化数据方法
     */
    void initData(Bundle savedInstanceState);

    /**
     * 给需要设置监听的设置监听器
     */
    void setListeners();

    /**
     * 通过Id获取试图对象
     * @param id 控件再布局里Id
     * @param <T> 泛型
     * @return 对应的控件实例
     */
    <T> T getViewById(int id);
}

package com.saiyi.framework.blls;

import android.content.Context;
import android.os.Bundle;

/**
 * 文件描述:业务类接口,该业务类继承于监听器接口，
 *         主要是方便点击和业务一起开发方便
 * 创建作者:黎丝军
 * 创建时间:16/7/28
 */
public interface IBusiness{

    /**
     * 初始化基本数据对象
     */
    void initObject();

    /**
     * 初始化数据方法
     */
    void initData(Bundle bundle);

    /**
     * 生命周期方法和界面类里的onResume对应
     */
    void onResume();

    /**
     * 生命周期方法和界面里的onPause对应
     */
    void onPause();

    /**
     * 生命周期方法和界面里的onDestroy对应
     */
    void onDestroy();

    /**
     * 设置运行环境
     * @param context 运行环境
     */
    void setContext(Context context);

    /**
     * 获取运行环境
     * @return 运行环境
     */
    Context getContext();
}

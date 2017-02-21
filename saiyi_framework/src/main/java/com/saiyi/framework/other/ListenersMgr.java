package com.saiyi.framework.other;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件描述：监听管理器,该管理器主要用于托管监听器，这样注册和实现不受制于某个地方
 *           该单列类包含三个方法，一个是注册监听器registerListener()方法，一个是获取监听实例getListener()方法
 *           还有一个就是在退出或者不需要时卸载监听器unRegisterListener()方法。
 * 创建作者：黎丝军
 * 创建时间：16/8/9 AM11:38
 */
public class ListenersMgr {

    //存储监听器的集合
    private Map<Class<?>,Object> mListeners = new HashMap<>();
    //单列
    private static ListenersMgr ourInstance = new ListenersMgr();
    //获取单列的方法
    public static ListenersMgr getInstance() {
        return ourInstance;
    }

    private ListenersMgr() {
    }

    /**
     * 添加监听器
     * @param listener 监听实例
     */
    public void registerListener(Class<?> listenerCl,Object listener) {
        if(!mListeners.containsKey(listenerCl)) {
            mListeners.put(listenerCl,listener);
        }
    }

    /**
     * 获取监听器
     * @param listenerCl 监听class
     * @param <T> 泛型
     * @return 返回具体的监听实例
     */
    public <T> T getListener(Class<?> listenerCl) {
        return (T)mListeners.get(listenerCl);
    }

    /**
     * 移除监听器
     * @param listenerCl 监听class
     */
    public void unRegisterListener(Class<?> listenerCl) {
        if(mListeners.containsKey(listenerCl)) {
            mListeners.remove(listenerCl);
        }
    }

    /**
     * 清除内存
     */
    public void clear() {
        mListeners.clear();
        mListeners = null;
    }

}

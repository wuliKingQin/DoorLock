package com.saiyi.framework.view.slidemenu.scrolldetectors;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;

import java.util.WeakHashMap;

/**
 * 滑动检测器，该滑动检测主要是用来检测是左滑还是右滑
 */
public class ScrollDetectors {
    //用于保存view的滑动检测实例
    private static final WeakHashMap<Class<? extends View>, ScrollDetector> DETECTOR_INSTANCES =
            new WeakHashMap<Class<? extends View>, ScrollDetector>();
    //滑动检测工厂
    private static ScrollDetectorFactory mFactory = null;

    /**
     * 是否能横向滑动
     * @param v 需要检测视图
     * @param direction 滑动方向
     * @return 返回true表示能，否则不能
     */
    public static boolean canScrollHorizontal(View v, int direction) {
        ScrollDetector impl = getImplements(v);
        if (null == impl) {
            return false;
        }
        return impl.canScrollHorizontal(v, direction);
    }

    /**
     * 是否能竖向滑动
     * @param v 需要检测视图
     * @param direction 滑动方向
     * @return 返回true表示能，否则不能
     */
    public static boolean canScrollVertical(View v, int direction) {
        ScrollDetector impl = getImplements(v);
        if (null == impl) {
            return false;
        }
        return impl.canScrollVertical(v, direction);
    }

    /**
     * 实例化检测器接口实例
     * @param v 需要检测视图
     * @return 检测器实例
     */
    private static ScrollDetector getImplements(View v) {
        Class<? extends View> clazz = v.getClass();
        ScrollDetector detector = DETECTOR_INSTANCES.get(clazz);
        if (null != detector) {
            return detector;
        }
        if (v instanceof ViewPager) {
            detector = new ViewPagerScrollDetector();
        } else if (v instanceof HorizontalScrollView) {
            detector = new HorizontalScrollViewScrollDetector();
        } else if (v instanceof WebView) {
            detector = new WebViewScrollDetector();
        } else if (null != mFactory) {
            detector = mFactory.newScrollDetector(v);
        } else {
            return null;
        }
        DETECTOR_INSTANCES.put(clazz, detector);
        return detector;
    }

    /**
     * <p/>
     * 描述: 滑动检测器接口,检测是否能左滑还是右滑
     * </P>
     * time:2015/6/30
     * developer:大成（LiSiJun)
     * company:投融天下
     */
    public interface ScrollDetector {
        /**
         * 判断view是否支持横滑
         * @param v 需要检测view
         * @param direction 滑动方向
         * @return 返回true，表示能，否则不能
         */
        boolean canScrollHorizontal(View v, int direction);

        /**
         * 判断view是否支持竖滑
         * @param v 需要检测view
         * @param direction 滑动方向
         * @return 返回true，表示能，否则不能
         */
        boolean canScrollVertical(View v, int direction);
    }
}

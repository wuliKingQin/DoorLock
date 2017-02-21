package com.saiyi.framework.view.slidemenu.scrolldetectors;

import android.view.View;

/**
 * <p/>
 * 描述: 滑动检测接口，用于生产滑动检测实例
 * </P>
 */
public interface ScrollDetectorFactory {
    /**
     * 实例化检测器方法
     * @param view 需要检测的视图View
     * @return 返回检测器实例
     */
    ScrollDetectors.ScrollDetector newScrollDetector(View view);
}

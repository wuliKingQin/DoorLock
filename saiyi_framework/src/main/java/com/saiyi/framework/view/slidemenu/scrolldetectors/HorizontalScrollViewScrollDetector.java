package com.saiyi.framework.view.slidemenu.scrolldetectors;

import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * <p/>
 * 描述: 横滑视图检测器
 * </P>
 */
public class HorizontalScrollViewScrollDetector implements ScrollDetectors.ScrollDetector{

    @Override
    public boolean canScrollHorizontal(View v, int direction) {
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) v;
        final int scrollX = horizontalScrollView.getScrollX();
        // Without scroll wrapper, can't scroll
        if (0 == horizontalScrollView.getChildCount()) {
            return false;
        }
        return (direction < 0 && scrollX < horizontalScrollView.getChildAt(
                0).getWidth()
                - horizontalScrollView.getWidth())
                || (direction > 0 && scrollX > 0);
    }

    @Override
    public boolean canScrollVertical(View v, int direction) {
        return false;
    }

}

package com.saiyi.framework.view.slidemenu.scrolldetectors;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerScrollDetector implements ScrollDetectors.ScrollDetector {

    @Override
    public boolean canScrollHorizontal(View v, int direction) {
        ViewPager viewPager = (ViewPager) v;
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (null == pagerAdapter || 0 == pagerAdapter.getCount()) {
            return false;
        }
        final int currentItem = viewPager.getCurrentItem();
        return (direction < 0 && currentItem < pagerAdapter.getCount() - 1)
                || (direction > 0 && currentItem > 0);
    }

    @Override
    public boolean canScrollVertical(View v, int direction) {
        return false;
    }
}

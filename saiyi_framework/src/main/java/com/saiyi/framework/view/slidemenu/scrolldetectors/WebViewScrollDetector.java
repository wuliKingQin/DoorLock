package com.saiyi.framework.view.slidemenu.scrolldetectors;

import android.view.View;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WebViewScrollDetector implements ScrollDetectors.ScrollDetector {

    @Override
    public boolean canScrollHorizontal(View v, int direction) {
        try {
            // Because this method is protected
            Method computeHorizontalScrollOffsetMethod = WebView.class
                    .getDeclaredMethod("computeHorizontalScrollOffset");
            Method computeHorizontalScrollRangeMethod = WebView.class
                    .getDeclaredMethod("computeHorizontalScrollRange");
            computeHorizontalScrollOffsetMethod.setAccessible(true);
            computeHorizontalScrollRangeMethod.setAccessible(true);
            final int horizontalScrollOffset = (Integer) computeHorizontalScrollOffsetMethod
                    .invoke(v);
            final int horizontalScrollRange = (Integer) computeHorizontalScrollRangeMethod
                    .invoke(v);
            return (direction > 0 && v.getScrollX() > 0)
                    || (direction < 0 && horizontalScrollOffset < horizontalScrollRange
                    - v.getWidth());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean canScrollVertical(View v, int direction) {
        return false;
    }
}

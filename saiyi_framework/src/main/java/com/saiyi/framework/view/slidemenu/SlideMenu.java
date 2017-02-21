package com.saiyi.framework.view.slidemenu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.saiyi.framework.util.DensityUtils;


/**
 * <p/>
 * 描述: 侧滑菜单栏，该侧滑菜单栏可以通过配置文件进行配置
 *       侧滑菜单分为左滑和右滑，该侧滑菜单有功能
 * </P>
 */
public class SlideMenu extends ViewGroup {
    //左滑动
    public final static int FLAG_DIRECTION_LEFT = 1 << 0;
    //右滑动
    public final static int FLAG_DIRECTION_RIGHT = 1 << 1;
    //window滑动模式
    public final static int MODE_SLIDE_WINDOW = 1;
    //content滑动模式
    public final static int MODE_SLIDE_CONTENT = 2;
    //菜单关闭
    public final static int STATE_CLOSE = 1 << 0;
    //打开左菜单
    public final static int STATE_OPEN_LEFT = 1 << 1;
    //打开右菜单
    public final static int STATE_OPEN_RIGHT = 1 << 2;
    //拖动
    public final static int STATE_DRAG = 1 << 3;
    //滑动
    public final static int STATE_SCROLL = 1 << 4;
    //打开遮蔽
    public final static int STATE_OPEN_MASK = 6;
    //未滑动状态
    public static final int SCROLL_STATE_IDLE = 0;
    //正在拖动状态
    public static final int SCROLL_STATE_DRAGGING = 1;
    //滑动固定状态
    public static final int SCROLL_STATE_SETTLING = 2;
    //最大持续值
    private final static int MAX_DURATION = 500;
    //左页面
    private final static int POSITION_LEFT = -1;
    //中间页面
    private final static int POSITION_MIDDLE = 0;
    //右页面
    private final static int POSITION_RIGHT = 1;
    //初始化滑动状态，默认为未滑动状态
    private int mScrollState = SCROLL_STATE_IDLE;
    //当前页面位置
    private int mCurrentContentPosition = POSITION_MIDDLE;
    //当前菜单是否打开的状态值
    private int mCurrentState = STATE_CLOSE;
    //内容页
    private View mContent = null;
    //左侧菜单
    private View mLeftMenu = null;
    //右侧菜单
    private View mRightMenu = null;
    //触摸偏移值
    private int mTouchSlop = 0;
    //按下的X轴值
    private float mPressedX = 0;
    //上一次的触摸x和y值
    private float mLastMotionX = 0, mLastMotionY = 0;
    //当前主界面滑动偏移量值
    private volatile int mCurrentContentOffset = 0;
    //主界面左边缘值
    private int mContentBoundsLeft = 0;
    //主界面右边缘值
    private int mContentBoundsRight = 0;
    //是否是标签内容
    private boolean mIsTapInContent = false;
    //用于内容隐藏框
    private Rect mContentHitRect = null;
    //用于左菜单阴影
    private Drawable mLeftShadowDrawable = null;
    //用于右菜单阴影
    private Drawable mRightShadowDrawable = null;
    //左菜单阴影宽度值
    private float mLeftShadowWidth = 0f;
    //右菜单阴影宽度值
    private float mRightShadowWidth = 0f;
    //保存菜单滑动方向，默认为右滑
    private int mSlideDirectionFlag = FLAG_DIRECTION_RIGHT;
    //判断是否有没有设置的滑动模式
    private boolean mIsPendingResolveSlideMode = false;
    //滑动模式，默认是滑动中间的内容页
    private int mSlideMode = MODE_SLIDE_CONTENT;
    //边缘是否能够滑动
    private boolean mIsEdgeSlideEnable = true;
    //边缘滑动宽度
    private int mEdgeSlideWidth = 0;
    //边缘检测框
    private Rect mEdgeSlideDetectRect = null;
    //是否在滑动标签内
    private boolean mIsTapInEdgeSlide;
    //保存视图宽
    private int mWidth = 0;
    //保存视图高
    private int mHeight = 0;
    //速度跟踪器
    private VelocityTracker mVelocityTracker = null;
    //滑动器
    private Scroller mScroller = null;
    //默认插入器
    private Interpolator mInterpolator = DEFAULT_INTERPOLATOR;
    //非拦截区域，通过此区域来动态设置使用者不希望被截取的区域，从而留给使用者做手势操作
    private RectF mNonInterceptArea = new RectF(0, 0, 0, 0);
    //滑动菜单标志
    private static final String TAG = "SlideMenu";
    //是否是Debug模式
    private static final boolean DEBUG = false;
    //无效指针
    private static final int INVALID_POINTER = -1;
    //状态条高度
    private static int STATUS_BAR_HEIGHT = 0;
    //滑动状态，用来控制是否能够滑动
    public int slideState = STATE_DRAG;
    //是否开始拖动
    private boolean mIsBeingDragged = false;
    //是否能够拖动
    private boolean mIsUnableToDrag = false;
    //活动指针
    private int mActivePointerId = INVALID_POINTER;
    //滑动配置，是否启用滑动事件拦截
    private String mSlideConfig = "true";
    //监听器
    private onSlideMenuListener mListener = null;
    //默认插入器
    public final static Interpolator DEFAULT_INTERPOLATOR = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    
    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 8;
        mVelocityTracker = VelocityTracker.obtain();
        mContentHitRect = new Rect();
        mEdgeSlideDetectRect = new Rect();
        STATUS_BAR_HEIGHT = (int) getStatusBarHeight(context);
        setWillNotDraw(false);
        setLeftShadowWidth(DensityUtils.dpToPx(context,7));
        setRightShadowWidth(DensityUtils.dpToPx(context,7));
        Drawable leftShadowDrawable = null;
        Drawable rightShadowDrawable;
        if (null == leftShadowDrawable) {
            leftShadowDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, new int[] {
                    Color.TRANSPARENT,
                    Color.argb(99, 0, 0, 0)
            });
            rightShadowDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.RIGHT_LEFT, new int[] {
                    Color.TRANSPARENT,
                    Color.argb(99, 0, 0, 0)
            });
            setLeftShadowDrawable(leftShadowDrawable);
            setRightShadowDrawable(rightShadowDrawable);
        }
        int interpolatorResId = -1;
        setInterpolator(-1 == interpolatorResId ? DEFAULT_INTERPOLATOR
                : AnimationUtils.loadInterpolator(context, interpolatorResId));
        mSlideDirectionFlag = FLAG_DIRECTION_LEFT | FLAG_DIRECTION_RIGHT;
        setEdgeSlideEnable(false);
        setEdgeSlideWidth(DensityUtils.dpToPx(getContext(), 50));
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    
    /**
     * Retrieve the height of status bar that defined in system
     * @param context
     * @return
     */
    public static float getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int statusBarIdentifier = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (0 != statusBarIdentifier) {
            return resources.getDimension(statusBarIdentifier);
        }
        return 0;
    }

    /**
     * Remove view child it's parent node, if the view does not have parent.
     * ignore
     * @param view
     */
    public static void removeViewFromParent(View view) {
        if (null == view) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (null == parent) {
            return;
        }
        parent.removeView(view);
    }

    /**
     * 获取默认的主界面背景
     * @param context 上下文
     * @return Drawable
     */
    protected Drawable getDefaultContentBackground(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.windowBackground, value, true);
        return context.getResources().getDrawable(value.resourceId);
    }

    /**
     * Resolve the attribute slideMode
     */
    protected void resolveSlideMode() {
        final ViewGroup decorView = (ViewGroup) getRootView();
        final ViewGroup contentContainer = (ViewGroup) decorView.findViewById(android.R.id.content);
        final View content = mContent;
        if (null == decorView || null == content || 0 == getChildCount()) {
            return;
        }
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.windowBackground, value, true);
        switch (mSlideMode) {
            case MODE_SLIDE_WINDOW: {
                removeViewFromParent(this);
                LayoutParams contentLayoutParams = new LayoutParams(content.getLayoutParams());
                removeViewFromParent(content);
                contentContainer.addView(content);
                View decorChild = decorView.getChildAt(0);
                decorChild.setBackgroundResource(0);
                removeViewFromParent(decorChild);
                addView(decorChild, contentLayoutParams);
                decorView.addView(this);
                setBackgroundResource(value.resourceId);
            }
            break;
            case MODE_SLIDE_CONTENT: {
                setBackgroundResource(0);
                removeViewFromParent(this);
                View originContent = contentContainer.getChildAt(0);
                View decorChild = mContent;
                LayoutParams layoutParams = (LayoutParams) decorChild.getLayoutParams();
                removeViewFromParent(originContent);
                removeViewFromParent(decorChild);
                decorChild.setBackgroundResource(value.resourceId);
                decorView.addView(decorChild);
                contentContainer.addView(this);
                addView(originContent, layoutParams);
            }
            break;
        }
    }

    @Override
    public void addView(View child, int index,ViewGroup.LayoutParams params) {
        if (!(params instanceof LayoutParams)) {
            throw new IllegalArgumentException(
                    "The parameter params must a instance of " +
                            "com.trtx.android.com.android.framework.views.slidemenu.SlideMenu$LayoutParams");
        }
        if (null == params) {
            return;
        }
        LayoutParams layoutParams = (LayoutParams) params;
        switch (layoutParams.role) {
            case LayoutParams.ROLE_CONTENT:
                removeView(mContent);
                mContent = child;
                break;
            case LayoutParams.ROLE_LEFT_MENU:
                removeView(mLeftMenu);
                mLeftMenu = child;
                break;
            case LayoutParams.ROLE_RIGHT_MENU:
                removeView(mRightMenu);
                mRightMenu = child;
                break;
            default:
                return;
        }
        invalidateMenuState();
        super.addView(child, index, params);
    }

    /**
     * Get the animation interpolator
     * @return
     */
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * Set animation interpolator when SlideMenu scroll
     * @param interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        mScroller = new Scroller(getContext(), interpolator);
    }

    /**
     * Get the shadow drawable of left side
     * @return
     */
    public Drawable getLeftShadowDrawable() {
        return mLeftShadowDrawable;
    }

    /**
     * Set the shadow drawable of left side
     * @param shadowDrawable
     */
    public void setLeftShadowDrawable(Drawable shadowDrawable) {
        mLeftShadowDrawable = shadowDrawable;
    }

    /**
     * Get the shadow drawable of right side
     * @return
     */
    public Drawable getRightShadowDrawable() {
        return mRightShadowDrawable;
    }

    /**
     * Set the shadow drawable of right side
     * @param secondaryShadowDrawable
     */
    public void setRightShadowDrawable(Drawable secondaryShadowDrawable) {
        this.mRightShadowDrawable = secondaryShadowDrawable;
    }

    /**
     * Get the slide mode current specified
     * @return
     */
    public int getSlideMode() {
        return mSlideMode;
    }

    /**
     * Set the slide mode:<br/> {@link #MODE_SLIDE_CONTENT}
     * {@link #MODE_SLIDE_WINDOW}
     * @param slideMode
     */
    public void setSlideMode(int slideMode) {
        if (isInContentView()) {
            throw new IllegalStateException(
                    "SlidingMenu must be the root of layout");
        }
        if (mSlideMode == slideMode) {
            return;
        }
        mSlideMode = slideMode;
        if (0 == getChildCount()) {
            mIsPendingResolveSlideMode = true;
        } else {
            resolveSlideMode();
        }
    }

    /**
     * Indicate user can only open SlideMenu from the edge
     * @return boolean
     */
    public boolean isEdgeSlideEnable() {
        return mIsEdgeSlideEnable;
    }

    /**
     * Toggle the edge slide
     * @param enable
     */
    public void setEdgeSlideEnable(boolean enable) {
        mIsEdgeSlideEnable = enable;
    }

    /**
     * Set edge slide width next left and right side of SlideMenu
     * @param width
     */
    public void setEdgeSlideWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("Edge slide width must above 0");
        }
        mEdgeSlideWidth = width;
    }

    /**
     * Get the edge slide width
     * @return float
     */
    public float getEdgeSlideWidth() {
        return mEdgeSlideWidth;
    }

    /**
     * Indicate this SlideMenu is open
     * @return true open, otherwise false
     */
    public boolean isOpen() {
        return (STATE_OPEN_MASK & mCurrentState) != 0;
    }

    /**
     * Open the SlideMenu
     * @param isSlideLeft --true打开右侧菜单，false打开左侧菜单
     * @param isAnimated  --是否显示动画
     */
    public void open(boolean isSlideLeft, boolean isAnimated) {
        if (isOpen()) {
            return;
        }
        int targetOffset = isSlideLeft ? mContentBoundsLeft : mContentBoundsRight;
        if (isAnimated) {
            smoothScrollContentTo(targetOffset);
        } else {
            mScroller.abortAnimation();
            setCurrentOffset(targetOffset);
            setCurrentState(isSlideLeft ? STATE_OPEN_LEFT : STATE_OPEN_RIGHT);
        }
    }

    /**
     * Close the SlideMenu
     * @param isAnimated
     */
    public void close(boolean isAnimated) {
        if (STATE_CLOSE == mCurrentState) {
            return;
        }
        if (isAnimated) {
            smoothScrollContentTo(0);
        } else {
            mScroller.abortAnimation();
            setCurrentOffset(0);
            setCurrentState(STATE_CLOSE);
        }
    }

    /**
     * Get current slide direction, {@link #FLAG_DIRECTION_LEFT},
     * {@link #FLAG_DIRECTION_RIGHT} or {@link #FLAG_DIRECTION_LEFT}|
     * {@link #FLAG_DIRECTION_RIGHT}
     * @return
     */
    public int getSlideDirection() {
        return mSlideDirectionFlag;
    }

    /**
     * Set slide direction
     * @param slideDirectionFlag
     */
    public void setSlideDirection(int slideDirectionFlag) {
        this.mSlideDirectionFlag = slideDirectionFlag;
    }

    /**
     * Get current state
     * @return
     */
    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * Set current state
     * @param currentState
     */
    public void setCurrentState(int currentState) {
        //状态监听器
        if(mListener != null) {
            mListener.onStateChange(currentState,0);
        }
        this.mCurrentState = currentState;
    }

    /**
     * Equals invoke {@link #smoothScrollContentTo(int, float)} with 0 velocity
     * @param targetOffset
     */
    public void smoothScrollContentTo(int targetOffset) {
        smoothScrollContentTo(targetOffset, 0);
    }

    /**
     * Perform a smooth slide of content, the offset of content will limited to
     * menu width
     * @param targetOffset
     * @param velocity
     */
    public void smoothScrollContentTo(int targetOffset, float velocity) {
        setCurrentState(STATE_SCROLL);
        int distance = targetOffset - mCurrentContentOffset;
        velocity = Math.abs(velocity);
        int duration = 400;
        if (velocity > 0) {
            duration = 3 * Math.round(1000 * Math.abs(distance / velocity));
        }
        duration = Math.min(duration, MAX_DURATION);
        mScroller.abortAnimation();
        mScroller.startScroll(mCurrentContentOffset, 0, distance, 0, duration);
        invalidate();
    }

    /**
     * 是否是触摸在内容滑动区域
     * @param x x轴坐标
     * @param y y轴坐标
     * @return 是触摸在内容区域返回true，否则返回false
     */
    private boolean isTapInContent(float x, float y) {
        final View content = mContent;
        if (null != content) {
            content.getHitRect(mContentHitRect);
            return mContentHitRect.contains((int) x, (int) y);
        }
        return false;
    }

    /**
     * 是否是触摸在边缘滑动区域
     * @param x x轴坐标
     * @param y y轴坐标
     * @return 是触摸在边缘滑动区域返回true，否则返回false
     */
    private boolean isTapInEdgeSlide(float x, float y) {
        final Rect rect = mEdgeSlideDetectRect;
        boolean result = false;
        if (null != mLeftMenu) {
            getHitRect(rect);
            rect.right = mEdgeSlideWidth;
            result |= rect.contains((int) x, (int) y);
        }
        if (null != mRightMenu) {
            getHitRect(rect);
            rect.left = rect.right - mEdgeSlideWidth;
            result |= rect.contains((int) x, (int) y);
        }
        return result;
    }

    /**
     * 是否触摸在非截取区域
     * @param x x轴坐标
     * @param y y轴坐标
     * @return 是触摸在非截取区域返回true，否则返回false
     */
    private boolean isTabInNonInterceptArea(float x, float y) {
        return mNonInterceptArea.contains(x, y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null == ev) {
            return true;
        }
        if (null != mSlideConfig && mSlideConfig.equals("false")) {
            return false;
        }
        //如果是触摸在非拦截区域，就不截取事件，交给子View去处理
        if (isTabInNonInterceptArea(ev.getX(), ev.getY())) {
            return false;
        }
        final int currentState = mCurrentState;
        if (STATE_DRAG == currentState || STATE_SCROLL == currentState) {
            return true;
        }
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if (DEBUG)
                Log.v(TAG, "Intercept done!");
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            mActivePointerId = INVALID_POINTER;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                if (DEBUG)
                    Log.v(TAG, "Intercept returning true!");
                return true;
            }
            if (mIsUnableToDrag) {
                if (DEBUG)
                    Log.v(TAG, "Intercept returning false!");
                return false;
            }
            final float x = ev.getX();
            final float y = ev.getY();

            mIsTapInContent = isTapInContent(x, y);
            mIsTapInEdgeSlide = isTapInEdgeSlide(x, y);
            //如果左右菜单已打开，而且点击了主页面，则处理此事件
            if (isOpen() && mIsTapInContent) {
                return true;
            }
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (STATE_CLOSE == slideState) {
                    return false;
                }
                if (mIsEdgeSlideEnable && !mIsTapInEdgeSlide
                        && mCurrentState == STATE_CLOSE) {
                    return false;
                }
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }
                try {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float dx = x - mLastMotionX;
                    final float xDiff = Math.abs(dx);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float yDiff = Math.abs(y - mLastMotionY);
                    if (DEBUG)
                        Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
                    if (canScroll(this, false, (int) dx, (int) x, (int) y)) {
                        mLastMotionX = x;
                        mLastMotionY = y;
                        return false;
                    }
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        if (DEBUG)
                            Log.v(TAG, "Starting drag!");
                        mIsBeingDragged = true;
                        setScrollState(SCROLL_STATE_DRAGGING);
                        mLastMotionX = x;
                        setCurrentState(STATE_DRAG);
                    } else {
                        if (yDiff > mTouchSlop) {
                            if (DEBUG)
                                Log.v(TAG, "Starting unable to drag!");
                            mIsUnableToDrag = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                if (mScrollState == SCROLL_STATE_SETTLING) {
                    mIsBeingDragged = true;
                    mIsUnableToDrag = false;
                    setScrollState(SCROLL_STATE_DRAGGING);
                } else {
                    mIsBeingDragged = false;
                    mIsUnableToDrag = false;
                }
                if (DEBUG)
                    Log.v(TAG, "Down at " + mLastMotionX + "," + mLastMotionY
                            + " mIsBeingDragged=" + mIsBeingDragged
                            + "mIsUnableToDrag=" + mIsUnableToDrag);
                break;
            }
        }
        return mIsBeingDragged;
    }

    /**
     * 设置滚动状态值
     * @param newState 状态值
     */
    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }

        mScrollState = newState;
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     * @param v      View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx     Delta scrolled in pixels
     * @param x      X coordinate of the active touch point
     * @param y      Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int currentState = mCurrentState;

        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressedX = mLastMotionX = x;
                mIsTapInContent = isTapInContent(x, y);
                mIsTapInEdgeSlide = isTapInEdgeSlide(x, y);

                if (mIsTapInContent) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                if (STATE_CLOSE == slideState) {
                    return false;
                }
                if (mIsEdgeSlideEnable && !mIsTapInEdgeSlide
                        && mCurrentState == STATE_CLOSE) {
                    return false;
                }

                if (Math.abs(x - mPressedX) >= mTouchSlop && mIsTapInContent
                        && currentState != STATE_DRAG) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setCurrentState(STATE_DRAG);
                }
                if (STATE_DRAG != currentState) {
                    mLastMotionX = x;
                    return false;
                }
                drag(mLastMotionX, x);
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (STATE_DRAG == currentState) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    endDrag(x, mVelocityTracker.getXVelocity());
                } else if (mIsTapInContent && MotionEvent.ACTION_UP == action) {
                    //点击侧滑菜单的内容布局时是否关闭侧滑菜单 performContentTap()
                }
                mVelocityTracker.clear();
                getParent().requestDisallowInterceptTouchEvent(false);
                mIsTapInContent = mIsTapInEdgeSlide = false;
                break;
        }
        return true;
    }

    /**
     * Get current left menu
     * @return View
     */
    public View getLeftMenu() {
        return mLeftMenu;
    }

    /**
     * Get current right menu
     * @return View
     */
    public View getRightMenu() {
        return mRightMenu;
    }

    /**
     * Perform click on the content
     */
    public void performContentTap() {
        if (isOpen()) {
            smoothScrollContentTo(0);
            return;
        }
    }

    /**
     * 拖
     * @param lastX 上次的X值
     * @param x 现在的x值
     */
    protected void drag(float lastX, float x) {
        mCurrentContentOffset += (int) (x - lastX);
        setCurrentOffset(mCurrentContentOffset);
    }

    /**
     * 绘制菜单状态
     */
    private void invalidateMenuState() {
        mCurrentContentPosition = mCurrentContentOffset < 0 ? POSITION_LEFT
                : (mCurrentContentOffset == 0 ? POSITION_MIDDLE
                : POSITION_RIGHT);
        switch (mCurrentContentPosition) {
            case POSITION_LEFT:
                invalidateViewVisibility(mLeftMenu, View.INVISIBLE);
                invalidateViewVisibility(mRightMenu, View.VISIBLE);
                break;
            case POSITION_MIDDLE:
                invalidateViewVisibility(mLeftMenu, View.INVISIBLE);
                invalidateViewVisibility(mRightMenu, View.INVISIBLE);
                break;
            case POSITION_RIGHT:
                invalidateViewVisibility(mLeftMenu, View.VISIBLE);
                invalidateViewVisibility(mRightMenu, View.INVISIBLE);
                break;
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * 绘制视图是否可见
     * @param view 视图
     * @param visibility 是否可见值
     */
    private void invalidateViewVisibility(View view, int visibility) {
        if (null != view && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 结束拖
     * @param x x位置
     * @param velocity 速度
     */
    protected void endDrag(float x, float velocity) {
        final int currentContentPosition = mCurrentContentPosition;
        boolean velocityMatched = Math.abs(velocity) > 400;
        switch (currentContentPosition) {
            case POSITION_LEFT:
                if ((velocity < 0 && velocityMatched) || (velocity >= 0 && !velocityMatched)) {
                    smoothScrollContentTo(mContentBoundsLeft, velocity);
                } else if ((velocity > 0 && velocityMatched) || (velocity <= 0 && !velocityMatched)) {
                    smoothScrollContentTo(0, velocity);
                }
                break;
            case POSITION_MIDDLE:
                setCurrentState(STATE_CLOSE);
                break;
            case POSITION_RIGHT:
                if ((velocity > 0 && velocityMatched) || (velocity <= 0 && !velocityMatched)) {
                    smoothScrollContentTo(mContentBoundsRight, velocity);
                } else if ((velocity < 0 && velocityMatched) || (velocity >= 0 && !velocityMatched)) {
                    smoothScrollContentTo(0, velocity);
                }
                break;
        }
    }

    /**
     * 设置主界面移动偏移量
     * @param currentOffset 当前偏移量
     */
    private void setCurrentOffset(int currentOffset) {
        final int slideDirectionFlag = mSlideDirectionFlag;
        final int middle = (slideDirectionFlag & FLAG_DIRECTION_LEFT) == FLAG_DIRECTION_LEFT ? mContentBoundsLeft : 0;
        final int max =  Math.max(currentOffset, middle);
        final int min = (slideDirectionFlag & FLAG_DIRECTION_RIGHT) == FLAG_DIRECTION_RIGHT ? mContentBoundsRight : 0;
        final int currentContentOffset = mCurrentContentOffset = Math.min(min, max);
        if(mListener != null) {
            mListener.onStateChange(mCurrentState,currentContentOffset);
        }
        invalidateMenuState();
        invalidate();
        requestLayout();
    }

    @Override
    public void computeScroll() {
        if (STATE_SCROLL == mCurrentState || isOpen()) {
            if (mScroller.computeScrollOffset()) {
                setCurrentOffset(mScroller.getCurrX());
            } else {
                setCurrentState(mCurrentContentOffset == 0 ?
                        STATE_CLOSE : (mCurrentContentOffset > 0 ? STATE_OPEN_LEFT : STATE_OPEN_RIGHT));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        final int slideMode = mSlideMode;
        final int statusBarHeight = STATUS_BAR_HEIGHT;
        int maxChildWidth = 0, maxChildHeight = 0;
        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            switch (layoutParams.role) {
                case LayoutParams.ROLE_CONTENT:
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    break;
                case LayoutParams.ROLE_LEFT_MENU:
                case LayoutParams.ROLE_RIGHT_MENU:
                    measureChild(
                            child,
                            widthMeasureSpec,
                            slideMode == MODE_SLIDE_WINDOW ? MeasureSpec
                                    .makeMeasureSpec(
                                            MeasureSpec.getSize(heightMeasureSpec)
                                                    - statusBarHeight,
                                            MeasureSpec.getMode(heightMeasureSpec))
                                    : heightMeasureSpec);
                    break;
            }
            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
            maxChildHeight = Math
                    .max(maxChildHeight, child.getMeasuredHeight());
        }
        maxChildWidth += getPaddingLeft() + getPaddingRight();
        maxChildHeight += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(maxChildWidth, widthMeasureSpec),
                resolveSize(maxChildHeight, heightMeasureSpec));
    }

    /**
     * 是否是主要界面
     * @return boolean
     */
    private boolean isInContentView() {
        View parent = (View) getParent();
        return null != parent && (android.R.id.content == parent.getId() && MODE_SLIDE_CONTENT == mSlideMode)
                && (getRootView() == parent && MODE_SLIDE_WINDOW == mSlideMode);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int statusBarHeight =
                mSlideMode == MODE_SLIDE_WINDOW ? STATUS_BAR_HEIGHT : 0;
        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            final int measureWidth = child.getMeasuredWidth();
            final int measureHeight = child.getMeasuredHeight();
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            switch (layoutParams.role) {
                case LayoutParams.ROLE_CONTENT:
                    child.bringToFront();
                    child.layout(mCurrentContentOffset + paddingLeft, paddingTop,
                            paddingLeft + measureWidth + mCurrentContentOffset,
                            paddingTop + measureHeight);
                    break;
                case LayoutParams.ROLE_LEFT_MENU:
                    mContentBoundsRight = measureWidth;
                    child.layout(paddingLeft, statusBarHeight + paddingTop,
                            paddingLeft + measureWidth, statusBarHeight
                                    + paddingTop + measureHeight);
                    break;
                case LayoutParams.ROLE_RIGHT_MENU:
                    mContentBoundsLeft = -measureWidth;
                    child.layout(r - l - paddingRight - measureWidth,
                            statusBarHeight + paddingTop, r - l - paddingRight,
                            statusBarHeight + paddingTop + measureHeight);
                    break;
                default:
                    continue;
            }
        }
    }

    /**
     * 获取左菜单阴影宽度
     * @return float
     */
    public float getLeftShadowWidth() {
        return mLeftShadowWidth;
    }

    /**
     * 设置左菜单阴影宽度
     * @param leftShadowWidth
     */
    public void setLeftShadowWidth(float leftShadowWidth) {
        this.mLeftShadowWidth = leftShadowWidth;
        invalidate();
    }

    /**
     * 获取右菜单阴影宽度
     * @return float
     */
    public float getRightShadowWidth() {
        return mRightShadowWidth;
    }

    /**
     * 设置右菜单阴影宽度
     * @param rightShadowWidth
     */
    public void setRightShadowWidth(float rightShadowWidth) {
        this.mRightShadowWidth = rightShadowWidth;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawShadow(canvas);
    }

    /**
     * 绘制阴影
     * @param canvas 画布
     */
    private void drawShadow(Canvas canvas) {
        if (null == mContent) {
            return;
        }
        final int left = mContent.getLeft();
        final int width = mWidth;
        final int height = mHeight;
        if (null != mLeftShadowDrawable) {
            mLeftShadowDrawable.setBounds(
                    (int) (left - mLeftShadowWidth), 0, left, height);
            mLeftShadowDrawable.draw(canvas);
        }
        if (null != mRightShadowDrawable) {
            mRightShadowDrawable.setBounds(left + width, 0, (int) (width
                    + left + mRightShadowWidth), height);
            mRightShadowDrawable.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (mIsPendingResolveSlideMode) {
            resolveSlideMode();
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        LayoutParams layoutParams = new LayoutParams(getContext(), attrs);
        return layoutParams;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.leftShadowWidth = mLeftShadowWidth;
        savedState.rightShadowWidth = mRightShadowWidth;
        savedState.slideDirectionFlag = mSlideDirectionFlag;
        savedState.slideMode = mSlideMode;
        savedState.currentState = mCurrentState;
        savedState.currentContentOffset = mCurrentContentOffset;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mLeftShadowWidth = savedState.leftShadowWidth;
        mRightShadowWidth = savedState.rightShadowWidth;
        mSlideDirectionFlag = savedState.slideDirectionFlag;
        setSlideMode(savedState.slideMode);
        mCurrentState = savedState.currentState;
        mCurrentContentOffset = savedState.currentContentOffset;

        invalidateMenuState();
        requestLayout();
        invalidate();
    }

    /**
     * 获取当前内容位置
     * @return int
     */
    public int getCurrentContentPosition() {
        return mCurrentContentPosition;
    }

    /**
     * 保存状态值
     */
    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public float leftShadowWidth;
        public float rightShadowWidth;
        public int slideDirectionFlag;
        public int slideMode;
        public int currentState;
        public int currentContentOffset;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            leftShadowWidth = in.readFloat();
            rightShadowWidth = in.readFloat();
            slideDirectionFlag = in.readInt();
            slideMode = in.readInt();
            currentState = in.readInt();
            currentContentOffset = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(leftShadowWidth);
            out.writeFloat(rightShadowWidth);
            out.writeInt(slideDirectionFlag);
            out.writeInt(slideMode);
            out.writeInt(currentState);
            out.writeInt(currentContentOffset);
        }
    }

    /**
     * Add view role for {@link #SlideMenu}
     * @author Tank
     */
    public static class LayoutParams extends MarginLayoutParams {
        public final static int ROLE_CONTENT = 0;
        public final static int ROLE_LEFT_MENU = 1;
        public final static int ROLE_RIGHT_MENU = 2;
        public int role;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            role = ROLE_LEFT_MENU;
            switch (role) {
                case ROLE_CONTENT:
                    width = MATCH_PARENT;
                    height = MATCH_PARENT;
                    break;
                case ROLE_RIGHT_MENU:
                case ROLE_LEFT_MENU:
                    break;
                default:
                    throw new IllegalArgumentException(
                            "You must specified a layout_role for this view");
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int role) {
            super(width, height);
            this.role = role;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            if (layoutParams instanceof LayoutParams) {
                role = ((LayoutParams) layoutParams).role;
            }
        }
    }

    /**
     * 设置侧滑菜单监听器
     * @param listener 监听实例类
     */
    public void setOnSlideMenuListener(onSlideMenuListener listener) {
        mListener = listener;
    }

    /**
     * 获取非触摸区域
     * @return RectF
     */
    public RectF getNonInterceptArea() {
        return mNonInterceptArea;
    }

    /**
     * <p/>
     * 描述:滑动菜单状态监听接口
     * </P>
     * developer:黎丝军
     */
    public interface onSlideMenuListener {
        /**
         * 状态改变监听方法
         * @param currentState 当前状态值
         * @param currentOffset 当前侧滑菜单主界面偏移量值
         */
        void onStateChange(int currentState, int currentOffset);
    }
}

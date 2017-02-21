package com.saiyi.framework.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.saiyi.framework.R;
import com.saiyi.framework.util.DensityUtils;


/**
 * 文件描述：头部标题栏。该标题栏默认实现了三个功能
 *          一个是左边默认的返回按钮，一个是中间的标题显示信息，另一个是右边的其他功能按钮。
 *          当然你可以自定你自己的actionBar，比如替换中间的显示栏信息视图等，替换时，直接使用
 *          replaceMiddleView(),replaceLeftView()和replaceRightView()
 * 创建作者：黎丝军
 * 创建时间：16/4/26
 */
public class ActionBarView extends RelativeLayout {

    //左容器布局ID
    public static final int LEFT_LAYOUT_ID = 0x10112;
    //中间容器ID
    public static final int MIDDLE_LAYOUT_ID = 0x10113;
    //右容器布局ID
    public static final int RIGHT_LAYOUT_ID = 0x10114;
    //左按钮ID
    public static final int LEFT_BUTTON_ID = 0x10115;
    //中间标题ID
    public static final int MIDDLE_TITLE_ID = 0x10116;
    //右按钮ID
    public static final int RIGHT_BUTTON_ID = 0x10117;
    //上下文
    private Context mContext = null;
    //左边按钮
    private Button mLeftBtn = null;
    //中间显示标题栏
    private TextView mMiddleTv = null;
    //右边按钮
    private Button mRightBtn = null;
    //左边按钮容器布局
    private FrameLayout mLeftLayout = null;
    //中间按钮容器布局
    private FrameLayout mMiddleLayout = null;
    //右边按钮容器布局
    private FrameLayout mRightLayout = null;
    //布局参数
    private LayoutParams mParams = null;

    public ActionBarView(Context context) {
        this(context, null);
    }

    public ActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, LEFT_LAYOUT_ID);
    }

    /**
     * 初始化基本布局
     *
     * @param context 上下文
     */
    private void init(Context context, int layoutId) {
        mContext = context;
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtils.dpToPx(context, 48));
        setLayoutParams(mParams);

        mMiddleLayout = new FrameLayout(context);
        mMiddleLayout.setId(layoutId);
        mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mMiddleLayout.setLayoutParams(mParams);
        addView(mMiddleLayout);

        mLeftLayout = new FrameLayout(context);
        mLeftLayout.setId(++layoutId);
        mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mParams.addRule(RelativeLayout.LEFT_OF, mMiddleLayout.getId());
        final int margin = DensityUtils.dpToPx(context, 2);
        mParams.setMargins(DensityUtils.dpToPx(context,10), margin, margin, margin);
        mLeftLayout.setLayoutParams(mParams);
        addView(mLeftLayout);

        mRightLayout = new FrameLayout(context);
        mRightLayout.setId(++layoutId);
        mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mParams.addRule(RelativeLayout.RIGHT_OF, mMiddleLayout.getId());
        mParams.setMargins(margin, margin, DensityUtils.dpToPx(context,10),margin);
        mRightLayout.setLayoutParams(mParams);
        addView(mRightLayout);

        //初始默认按钮
        initDefaultActionBar(LEFT_BUTTON_ID);
    }

    /**
     * 初始化默认头部栏
     */
    private void initDefaultActionBar(int layoutId) {
        mLeftBtn = new Button(mContext);
        mLeftBtn.setId(layoutId);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        mLeftBtn.setLayoutParams(params);
        mLeftBtn.setTextColor(Color.WHITE);
        mLeftBtn.setTextSize(15f);
        mLeftBtn.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        mLeftBtn.setBackgroundResource(android.R.color.transparent);
        mLeftLayout.addView(mLeftBtn);

        mMiddleTv = new TextView(mContext);
        mMiddleTv.setId(++layoutId);
        params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mMiddleTv.setGravity(Gravity.CENTER);
        mMiddleTv.setTextColor(Color.WHITE);
        mMiddleTv.setTextSize(18f);
        mMiddleLayout.addView(mMiddleTv);

        mRightBtn = new Button(mContext);
        mRightBtn.setId(++layoutId);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        mRightBtn.setLayoutParams(params);
        mRightBtn.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        mRightBtn.setTextColor(Color.WHITE);
        mRightBtn.setTextSize(15f);
        mRightBtn.setBackgroundResource(android.R.color.transparent);
        mRightLayout.addView(mRightBtn);
    }

    /**
     * 设置左按钮是否可见
     *
     * @param visibility 是否可见值
     */
    public void setLeftButtonVisibility(int visibility) {
        mLeftBtn.setVisibility(visibility);
    }

    /**
     * 设置右按钮是否可见
     *
     * @param visibility 是否可见值
     */
    public void setRightButtonVisibility(int visibility) {
        mRightBtn.setVisibility(visibility);
    }

    /**
     * 替换左视图
     *
     * @param leftView 需要替换的左边视图
     */
    public void replaceLeftView(View leftView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                DensityUtils.dpToPx(mContext, 60),
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.LEFT;
        replaceLeftView(leftView, params);
    }

    /**
     * 替换左视图
     *
     * @param leftView 需要替换的左边视图
     */
    public void replaceLeftView(View leftView, FrameLayout.LayoutParams params) {
        mLeftLayout.removeAllViews();
        mLeftLayout.addView(leftView, params);
    }

    /**
     * 替换左视图
     *
     * @param resId 需要替换的左边视图资源Id
     */
    public void replaceLeftView(int resId) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.LEFT;
        replaceLeftView(resId, params);
    }

    /**
     * 替换左视图
     *
     * @param resId 需要替换的左边视图资源Id
     */
    public void replaceLeftView(int resId, FrameLayout.LayoutParams params) {
        mLeftLayout.removeAllViews();
        mLeftLayout.addView(LayoutInflater.from(mContext).inflate(resId, null), params);
    }

    /**
     * 替换中间视图
     *
     * @param middleView 视图
     */
    public void replaceMiddleView(View middleView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        final int margin = DensityUtils.dpToPx(mContext, 5);
        params.setMargins(0, margin, 0, margin);
        replaceMiddleView(middleView, params);
    }

    /**
     * 替换中间视图
     *
     * @param middleView 视图
     */
    public void replaceMiddleView(View middleView, FrameLayout.LayoutParams params) {
        mMiddleLayout.removeAllViews();
        mMiddleLayout.addView(middleView, params);
    }

    /**
     * 替换中间视图
     *
     * @param resId 视图资源Id
     */
    public void replaceMiddleView(int resId) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        final int margin = DensityUtils.dpToPx(mContext, 5);
        params.gravity = Gravity.CENTER;
        params.setMargins(margin, margin, margin, margin);
        replaceMiddleView(resId, params);
    }

    /**
     * 替换中间视图
     *
     * @param resId 视图资源Id
     */
    public void replaceMiddleView(int resId, FrameLayout.LayoutParams params) {
        mMiddleLayout.removeAllViews();
        mMiddleLayout.addView(LayoutInflater.from(mContext).inflate(resId, null), params);
    }

    /**
     * 替换右边视图
     *
     * @param rightView 视图
     */
    public void replaceRightView(View rightView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;
        replaceRightView(rightView, params);
    }

    /**
     * 替换右边视图
     *
     * @param rightView 视图
     */
    public void replaceRightView(View rightView, FrameLayout.LayoutParams params) {
        mRightLayout.removeAllViews();
        mRightLayout.addView(rightView, params);
    }

    /**
     * 替换右边视图
     *
     * @param resId 视图资源Id
     */
    public void replaceRightView(int resId) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;
        replaceRightView(resId, params);
    }

    /**
     * 替换右边视图
     *
     * @param resId 视图资源Id
     */
    public void replaceRightView(int resId, FrameLayout.LayoutParams params) {
        mRightLayout.removeAllViews();
        mRightLayout.addView(LayoutInflater.from(mContext).inflate(resId, null), params);
    }

    /**
     * 设置标题信息
     *
     * @param text      文本
     * @param textColor 文本颜色
     * @param textSize  文本大小
     */
    public void setActionBarTitle(String text, int textColor, float textSize) {
        mMiddleTv.setText(text);
        mMiddleTv.setTextColor(textColor);
        mMiddleTv.setTextSize(textSize);
    }

    /**
     * 设置右边按钮信息
     *
     * @param text         文本
     * @param textColor    文本颜色
     * @param textSize     文本大小
     * @param backgroundId 背景颜色或者背景资源Id
     */
    public void setRightButton(String text, int textColor, float textSize, int backgroundId) {
        mRightBtn.setText(text);
        mRightBtn.setTextColor(textColor);
        mRightBtn.setTextSize(textSize);
        try {
            mRightBtn.setBackgroundResource(backgroundId);
        } catch (Exception e) {
            try {
                mRightBtn.setBackgroundColor(backgroundId);
            } catch (Exception e3) {
            }
        }
    }

    /**
     * 设置左边按钮
     *
     * @param text         文本
     * @param textColor    文本颜色
     * @param textSize     文本大小
     * @param backgroundId 背景资源ID
     */
    public void setLeftButton(String text, int textColor, float textSize, int backgroundId) {
        mLeftBtn.setText(text);
        mLeftBtn.setTextColor(textColor);
        mLeftBtn.setTextSize(textSize);
        try {
            mLeftBtn.setBackgroundResource(backgroundId);
        } catch (Exception e) {
            try {
                mLeftBtn.setBackgroundColor(backgroundId);
            } catch (Exception e3) {
            }
        }
    }

    /**
     * 设置默认的左按钮监听
     *
     * @param listener 监听器实例
     */
    public void setLeftClickListener(OnClickListener listener) {
        mLeftBtn.setOnClickListener(listener);
    }

    /**
     * 设置左边按钮文本
     *
     * @param text 文本
     */
    public void setLeftButtonText(String text) {
        mLeftBtn.setText(text);
    }

    /**
     * 设置左边按钮文本
     *
     * @param resId 资源ID
     */
    public void setLeftButtonText(int resId) {
        setLeftButtonText(mContext.getString(resId));
    }

    /**
     * 设置左边按钮文本颜色
     *
     * @param color 颜色值
     */
    public void setLeftButtonTextColor(int color) {
        mLeftBtn.setTextColor(color);
    }

    /**
     * 设置左边按钮文本大小
     *
     * @param textSize 大小值
     */
    public void setLeftButtonTextSize(float textSize) {
        mLeftBtn.setTextSize(textSize);
    }

    /**
     * 设置左边按钮背景
     *
     * @param resId  资源Id
     * @param width  组件宽
     * @param height 组件高
     */
    public void setLeftButtonBackground(int resId, int width, int height) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    DensityUtils.dpToPx(mContext, width), DensityUtils.dpToPx(mContext, height));
            params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            mLeftBtn.setLayoutParams(params);
            mLeftLayout.removeAllViews();
            mLeftLayout.addView(mLeftBtn);
            mLeftBtn.setBackgroundResource(resId);
        } catch (Exception e) {
            try {
                mLeftBtn.setBackgroundColor(resId);
            } catch (Exception e3) {
            }
        }
    }

    /**
     * 设置左边按钮背景
     *
     * @param resId
     */
    public void setLeftButtonBackground(int resId) {
        try {
            mLeftBtn.setBackgroundResource(resId);
        } catch (Exception e) {
            try {
                mLeftBtn.setBackgroundColor(resId);
            } catch (Exception e3) {
            }
        }
    }

    /**
     * 通过资源ID设置控件边缘图片
     *
     * @param left   左图片资源ID
     * @param top    顶部图片资源ID
     * @param right  右图片资源ID
     * @param bottom 底部图片资源ID
     */
    public void setLeftButtonDrawable(int left, int top, int right, int bottom) {
        Drawable leftDraw = getDrawable(left);
        Drawable topDraw = getDrawable(top);
        Drawable rightDraw = getDrawable(right);
        Drawable bottomDraw = getDrawable(bottom);
        if (leftDraw != null) {
            leftDraw.setBounds(2, 2, leftDraw.getIntrinsicWidth(), leftDraw.getIntrinsicHeight());
        }
        if (rightDraw != null) {
            rightDraw.setBounds(2, 2, rightDraw.getIntrinsicWidth(), rightDraw.getIntrinsicHeight());
        }
        if (topDraw != null) {
            topDraw.setBounds(2, 0, topDraw.getIntrinsicWidth(), topDraw.getIntrinsicHeight());
        }
        if (bottomDraw != null) {
            bottomDraw.setBounds(2, 0, bottomDraw.getIntrinsicWidth(), bottomDraw.getIntrinsicHeight());
        }
        mLeftBtn.setCompoundDrawables(leftDraw, topDraw, rightDraw, bottomDraw);
    }

    /**
     * 设置左边按钮图标和字的距离
     * @param padding
     */
    public void setLeftBtnDrawablePadding(int padding) {
        mLeftBtn.setCompoundDrawablePadding(padding);
    }

    /**
     * 通过资源ID设置控件边缘图片
     *
     * @param left   左图片资源ID
     * @param top    顶部图片资源ID
     * @param right  右图片资源ID
     * @param bottom 底部图片资源ID
     */
    public void setRightButtonDrawable(int left, int top, int right, int bottom) {
        Drawable leftDraw = getDrawable(left);
        Drawable topDraw = getDrawable(top);
        Drawable rightDraw = getDrawable(right);
        Drawable bottomDraw = getDrawable(bottom);
        if (leftDraw != null) {
            leftDraw.setBounds(0, 5, leftDraw.getIntrinsicWidth(), leftDraw.getIntrinsicHeight());
        }
        if (rightDraw != null) {
            rightDraw.setBounds(0, 5, rightDraw.getIntrinsicWidth(), rightDraw.getIntrinsicHeight());
        }
        if (topDraw != null) {
            topDraw.setBounds(0, 0, topDraw.getIntrinsicWidth(), topDraw.getIntrinsicHeight());
        }
        if (bottomDraw != null) {
            bottomDraw.setBounds(0, 0, bottomDraw.getIntrinsicWidth(), bottomDraw.getIntrinsicHeight());
        }
        mRightBtn.setCompoundDrawables(leftDraw, topDraw, rightDraw, bottomDraw);
    }

    /**
     * 设置右边按钮图标和字的距离
     * @param padding
     */
    public void setRightBtnDrawablePadding(int padding) {
        mRightBtn.setCompoundDrawablePadding(padding);
    }

    /**
     * 获取Drawable实例根据资源Id
     * @param id 资源ID
     * @return Drawable
     */
    private Drawable getDrawable(int id) {
        try {
            return mContext.getResources().getDrawable(id);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 设置默认的右按钮监听
     *
     * @param listener 监听器实例
     */
    public void setRightClickListener(OnClickListener listener) {
        mRightBtn.setOnClickListener(listener);
    }

    /**
     * 设置左边按钮文本
     *
     * @param text 文本
     */
    public void setRightButtonText(String text) {
        mRightBtn.setText(text);
    }

    /**
     * 设置左边按钮文本
     *
     * @param resId 资源ID
     */
    public void setRightButtonText(int resId) {
        setRightButtonText(mContext.getString(resId));
    }

    /**
     * 设置左边按钮文本颜色
     *
     * @param color 颜色值
     */
    public void setRightButtonTextColor(int color) {
        int colorTemp;
        try {
            colorTemp = getResources().getColor(color);
        } catch (Exception e) {
            colorTemp = color;
        }
        mRightBtn.setTextColor(colorTemp);
    }

    /**
     * 设置左边按钮文本大小
     *
     * @param textSize 大小值
     */
    public void setRightButtonTextSize(float textSize) {
        mRightBtn.setTextSize(textSize);
    }

    /**
     * 设置右边按钮背景
     * @param resId  资源Id
     * @param width  组件宽
     * @param height 组件高
     */
    public void setRightButtonBackground(int resId, int width, int height) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    DensityUtils.dpToPx(mContext, width), DensityUtils.dpToPx(mContext, height));
            params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            mRightBtn.setLayoutParams(params);
            mRightLayout.removeAllViews();
            mRightLayout.addView(mRightBtn);
            mRightBtn.setBackgroundResource(resId);
        } catch (Exception e) {
            try {
                mRightBtn.setBackgroundColor(resId);
            } catch (Exception e3) {
            }
        }
    }

    /**
     * 设置右边按钮背景
     * @param resId 资源Id
     */
    public void setRightButtonBackground(int resId) {
        try {
            mRightBtn.setBackgroundResource(resId);
        } catch (Exception e) {
            try {
                mRightBtn.setBackgroundColor(resId);
            } catch (Exception e3) {
            }
        }
    }

    /**
     * 设置标题信息
     *
     * @param title 标题字符串
     */
    public void setActionBarTitle(CharSequence title) {
        mMiddleTv.setText(title);
    }

    /**
     * 设置标题信息
     *
     * @param resId 标题资源Id
     */
    public void setActionBarTitle(int resId) {
        setActionBarTitle(mContext.getString(resId));
    }

    /**
     * 设置标题颜色
     *
     * @param color 颜色值
     */
    public void setActionBarTitleColor(int color) {
        mMiddleTv.setTextColor(color);
    }

    /**
     * 设置标题大小
     *
     * @param size 大小值
     */
    public void setActionBarTitleSize(float size) {
        mMiddleTv.setTextSize(size);
    }

    /**
     * 设置头部横条的高度
     * @param height 高度值
     */
    public void setActionBarLayoutHeight(int height) {
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtils.dpToPx(getContext(), height));
        setLayoutParams(mParams);
    }

    public Button getRightBtn() {
        return mRightBtn;
    }

    public Button getLeftBtn() {
        return mLeftBtn;
    }
}

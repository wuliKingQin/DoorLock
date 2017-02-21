package com.saiyi.framework.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 文件描述：信息提示Dialog,在创建这个实例时有两种方法，一种是构造函数带一个参数的，在实例化以后需要调用
 *          setOnConfirmListener方法来注册确认按钮的监听，当然如果你什么都不想做，也可以不用设置任何监听
 * 创建作者：黎丝军
 * 创建时间：16/8/26
 */
public class InfoHintDialog extends Dialog implements View.OnClickListener {

    //取消按钮Id
    private static final int CANCEL_BOTTOM_ID = 0x100000;
    //确认按钮Id
    private static final int CONFIRM_BOTTOM_ID = 0x100001;
    //提示框的标题
    private TextView mTitleTv;
    //提示框内容显示
    private TextView mShowInfoTv;
    //取消按钮
    private TextView mCancelBtn;
    //确认按钮
    private TextView mSureBtn;
    //内容布局容器
    private LinearLayout mContentLayout;
    //底部布局容器
    private LinearLayout mBottomLayout;
    //获取屏幕大小
    private DisplayMetrics mMetrics;
    //布局参数
    private LinearLayout.LayoutParams mLayoutParams;
    //按确认时可以附带的消息
    private Object mInfo;
    //按钮监听器
    private OnConfirmListener mListener;

    public InfoHintDialog(Context context) {
        this(context,null);
    }

    public InfoHintDialog(Context context,OnConfirmListener listener) {
        super(context);
        mListener = listener;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mMetrics);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        createDialogLayout(context, CANCEL_BOTTOM_ID, CONFIRM_BOTTOM_ID);
        setContentView(mContentLayout, new ViewGroup.LayoutParams((int) ((double) mMetrics.widthPixels * 0.8D), ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 创建Dialog布局
     *
     * @param context 运行环境
     */
    private void createDialogLayout(Context context, int... ids) {
        mContentLayout = new LinearLayout(context);
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.setBackgroundColor(Color.WHITE);

        mTitleTv = new TextView(context);
        mTitleTv.setTextSize(20);
        mTitleTv.setTextColor(Color.BLACK);
        mTitleTv.setBackgroundColor(Color.WHITE);
        mTitleTv.setGravity(Gravity.LEFT);
        mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(context, 45));
        mLayoutParams.leftMargin = dpToPx(context,20);
        mLayoutParams.topMargin = dpToPx(context,20);
        mTitleTv.setLayoutParams(mLayoutParams);

        mShowInfoTv = new TextView(context);
        mShowInfoTv.setTextSize(16);
        mShowInfoTv.setTextColor(Color.GRAY);
        mShowInfoTv.setGravity(Gravity.LEFT);
        mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(context, 60));
        mLayoutParams.leftMargin = dpToPx(context, 20);
        mLayoutParams.rightMargin = mLayoutParams.leftMargin;
        mShowInfoTv.setLayoutParams(mLayoutParams);


        mBottomLayout = new LinearLayout(context);
        mBottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        mBottomLayout.setBackgroundColor(Color.WHITE);
        mBottomLayout.setGravity(Gravity.RIGHT);
        mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(context, 45));
        mLayoutParams.bottomMargin = dpToPx(context,20);
        mLayoutParams.rightMargin = dpToPx(context,10);
        mBottomLayout.setLayoutParams(mLayoutParams);

        mSureBtn = new TextView(context);
        mSureBtn.setId(ids[0]);
        mSureBtn.setText("确定");
        mSureBtn.setTextSize(16);
        mSureBtn.setTextColor(Color.parseColor("#99D6FF"));
        mSureBtn.setOnClickListener(this);
        mSureBtn.setBackgroundColor(Color.WHITE);
        mLayoutParams = new LinearLayout.LayoutParams(dpToPx(context, 60), dpToPx(context, 45));
        mLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        mSureBtn.setLayoutParams(mLayoutParams);

        mCancelBtn = new TextView(context);
        mCancelBtn.setId(ids[1]);
        mCancelBtn.setText("取消");
        mCancelBtn.setTextSize(16);
        mCancelBtn.setTextColor(Color.GRAY);
        mCancelBtn.setOnClickListener(this);
        mCancelBtn.setBackgroundColor(Color.WHITE);
        mLayoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mCancelBtn.setLayoutParams(mLayoutParams);

        mBottomLayout.addView(mCancelBtn);
        mBottomLayout.addView(mSureBtn);

        mContentLayout.addView(mTitleTv);
        mContentLayout.addView(mShowInfoTv);
        mContentLayout.addView(mBottomLayout);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleTv.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitleTv.setText(titleId);
    }

    /**
     * 设置标题颜色
     *
     * @param color 颜色值
     */
    public void setTitleColor(int color) {
        mTitleTv.setTextColor(color);
    }

    /**
     * 设置标题大小
     *
     * @param size 大小值
     */
    public void setTitleSize(float size) {
        mTitleTv.setTextSize(size);
    }

    /**
     * 设置取消按钮的文本
     *
     * @param text 文本信息
     */
    public void setCancelBtnText(String text) {
        mCancelBtn.setText(text);
    }

    /**
     * 设置取消按钮的文本
     *
     * @param resId 文本信息
     */
    public void setCancelBtnText(int  resId) {
        mCancelBtn.setText(resId);
    }

    /**
     * 设置取消按钮是否可见
     * @param visibility
     */
    public void setCancelBtnVisibility(int visibility) {
        mCancelBtn.setVisibility(visibility);
    }

    /**
     * 设置内容信息
     * @param text 信息
     */
    public void setContentText(String text) {
        mShowInfoTv.setText(text);
    }

    /**
     * 设置内容信息
     * @param resId 资源Id
     */
    public void setContentText(int resId) {
        mShowInfoTv.setText(resId);
    }

    /**
     * 将显示内容文本用Html格式显示
     * @param text 显示信息
     */
    public  void setContentWithHtml(String text) {
        mShowInfoTv.setText(Html.fromHtml(text));
    }

    /**
     * 设置取消按钮的监听
     *
     * @param cancelListener 取消按钮监听器
     */
    public void setCancelListener(View.OnClickListener cancelListener) {
        mCancelBtn.setOnClickListener(cancelListener);
    }

    /**
     * 设置确认按钮监听
     *
     * @param confirmListener 监听器
     */
    public void setSureListener(View.OnClickListener confirmListener) {
        mSureBtn.setOnClickListener(confirmListener);
    }

    /**
     * 设置确认按钮文本
     *
     * @param text 文本信息
     */
    public void setSureBtnText(String text) {
        mSureBtn.setText(text);
    }

    /**
     * 设置确认按钮是否可见
     * @param visibility
     */
    public void setSureBtnVisibility(int visibility) {
        mSureBtn.setVisibility(visibility);
    }
    /**
     * 设置确认按钮文本
     *
     * @param resId 文本信息
     */
    public void setSureBtnText(int resId) {
        mSureBtn.setText(resId);
    }

    /**
     * 设置底部背景颜色
     *
     * @param color 颜色
     */
    public void setBottomBackgroundColor(int color) {
        mBottomLayout.setBackgroundColor(color);
    }

    /**
     * 设置Dialog背景颜色
     *
     * @param color 颜色值
     */
    public void setBackgroundColor(int color) {
        mContentLayout.setBackgroundColor(color);
    }

    /**
     * 设置按钮监听器
     * @param listener 按钮监听器
     */
    public void setDialogButtonListener(OnConfirmListener listener) {
        mListener = listener;
    }

    /**
     * 将dp转换成px
     *
     * @param context 运行环境
     * @param dp      dp
     * @return px
     */
    protected int dpToPx(Context context, float dp) {
        return context == null ? -1 : (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * 设置确认信息
     * @param info 信息
     */
    public void setConfirmInfo(Object info) {
        mInfo = info;
    }

    /**
     * 设置取消按钮在默认和按下时的背景颜色
     *
     * @param defaultColor 没有按下时的背景颜色
     * @param pressedColor 按下时的背景颜色
     */
    public void setCancelBtnSelectorDrawable(int defaultColor, int pressedColor) {
        final StateListDrawable selectorDrawable = new StateListDrawable();
        selectorDrawable.addState(new int[]{-android.R.attr.state_pressed}, new ColorDrawable(defaultColor));
        selectorDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        mCancelBtn.setBackgroundDrawable(selectorDrawable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case CONFIRM_BOTTOM_ID:
                if(mListener != null) {
                    mListener.onConfirm(mInfo);
                }
                break;
            case CANCEL_BOTTOM_ID:
            default:
                break;
        }
        dismiss();
    }

    /**
     * 按钮监听器
     */
    public interface OnConfirmListener {
        /**
         * 确认按钮监听
         *
         * @param info 信息
         */
        void onConfirm(Object info);
    }
}
package com.lisijun.fingerprint.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lisijun.fingerprint.R;

/**
 * 描述：指纹识别弹出框
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 15:05
 */

public class FingerprintDialog extends Dialog {

    //指纹识别提示
    private TextView mResultHintTv;
    //取消按钮
    private TextView mCancelBtn;
    //根布局
    private View mRootView;
    //获取屏幕大小
    private DisplayMetrics mMetrics;
    //取消识别监听
    private OnCancelListener mCancelListener;
    //运行环境
    private Context mContext;

    public FingerprintDialog(Context context) {
        super(context);
        mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mMetrics);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialogfingerprint_hint,null);
        initView();
        setListeners();
        setContentView(mRootView, new ViewGroup.LayoutParams((int) ((double) mMetrics.widthPixels * 0.8D), ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mResultHintTv = (TextView) mRootView.findViewById(R.id.tv_fingerprint_hint);
        mCancelBtn = (TextView)mRootView.findViewById(R.id.tv_fingerprint_cancel);
    }

    /**
     * 设置监听
     */
    private void setListeners() {
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCancelListener != null) {
                    mCancelListener.onCancel(FingerprintDialog.this);
                } else {
                    dismiss();
                }
            }
        });
    }

    /**
     * 设置提示文本
     * @param hint 提示信息
     */
    public void setResultHint(String hint) {
        mResultHintTv.setText(hint);
    }
    /**
     * 设置提示文本
     * @param hintResId 提示信息资源Id
     */
    public void setResultHint(int hintResId) {
        mResultHintTv.setText(hintResId);
    }

    /**
     * 设置取消监听器
     * @param listener 取消监听实例
     */
    public void setOnCancelListener(OnCancelListener listener) {
        mCancelListener = listener;
    }

    /**
     * 失败动画
     */
    public void failAnimation() {
        final Animation shakeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        mResultHintTv.startAnimation(shakeAnimation);
    }
}

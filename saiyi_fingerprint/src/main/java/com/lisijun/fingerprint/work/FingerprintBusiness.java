package com.lisijun.fingerprint.work;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.lisijun.fingerprint.R;
import com.lisijun.fingerprint.dialog.FingerprintDialog;


/**
 * 描述：指纹业务类
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 15:26
 */

public class FingerprintBusiness implements
        DialogInterface.OnCancelListener {

    //指纹运行环境
    private Context mContext;
    //提示dialog
    private FingerprintDialog mHintDialog;
    //指纹识别接口回调
    private FingerprintCallback mCallback;
    //用于取消指纹识别
    private CancellationSignal mCancellationSignal;
    //业务接口回调
    private IFingerBussCallback mBussCallback;
    //指纹识别管理器
    private FingerprintManagerCompat mFingerprintMgr;
    //用于指纹处理线程转换
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int type = msg.what;
            switch (type) {
                //指纹识别成功
                case FConstant.MSG_AUTH_SUCCESS:
                    if(mBussCallback != null) {
                        mBussCallback.onSuccess((FingerprintManagerCompat.AuthenticationResult)msg.obj);
                    }
                    mHintDialog.cancel();
                    break;
                //指纹识别错误
                case FConstant.MSG_AUTH_ERROR:
                //指纹识别失败
                case FConstant.MSG_AUTH_FAILED:
                //指纹识别帮助信息
                case FConstant.MSG_AUTH_HELP:
                    if(mBussCallback != null) {
                        final boolean isCancel = mBussCallback.onFail(mHintDialog,type,(String)msg.obj);
                        if(isCancel)mHintDialog.cancel();
                    } else {
                        mHintDialog.setResultHint("指纹识别失败");
                    }
                    // 左右移动动画
                    mHintDialog.failAnimation();
                    break;
                default:
                    break;
            }
        }
    };

    public FingerprintBusiness(Context context) {
        mContext = context;
        mHintDialog = new FingerprintDialog(context);
        mHintDialog.setOnCancelListener(this);
        mFingerprintMgr = FingerprintManagerCompat.from(mContext);
    }

    /**
     * 开始指纹识别
     */
    public void startFingerDetection() {
        if(mBussCallback != null) {
            mBussCallback.onPre(mHintDialog);
        }
        if (mFingerprintMgr.isHardwareDetected()) {
            if (mFingerprintMgr.hasEnrolledFingerprints()) {
                fingerDetection();
            } else {
                if(mBussCallback != null) {
                    mBussCallback.onCheckSupport("您还没有录入指纹，请先录入指纹");
                } else {
                    Toast.makeText(mContext,"您还没有录入指纹，请先录入指纹",Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if(mBussCallback != null) {
                mBussCallback.onCheckSupport("您手机不支持指纹识别");
            } else {
                Toast.makeText(mContext,"您手机不支持指纹识别",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 指纹检测
     */
    private void fingerDetection() {
        mCallback = new FingerprintCallback(mHandler);
        try {
            final CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            if(mCancellationSignal == null) {
                mCancellationSignal = new CancellationSignal();
            }
            mFingerprintMgr.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, mCancellationSignal, mCallback, null);
            mHintDialog.show();
        } catch (Exception e) {
            Toast.makeText(mContext,"指纹识别出错", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
        dialog.dismiss();
    }

    /**
     * 设置指纹识别回调业务接口
     * @param callback 接口实例
     */
    public void setIFingerBussCallback(IFingerBussCallback callback) {
        mBussCallback = callback;
    }
}

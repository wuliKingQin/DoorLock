package cn.saiyi.doorlock.other;

import android.os.CountDownTimer;

import com.saiyi.framework.util.LogUtils;

/**
 * 描述：用于重发
 * 创建作者：黎丝军
 * 创建时间：2016/12/19 11:21
 */

public class RetryUtil {

    //判断是否还需要重发
    private static boolean isNeedRetry;
    //重发器
    private static CountDownTimer mRetryCounter;
    //是否是首次执行
    private static boolean isFirst;

    /**
     * 开始重发
     * @param retryMillTime 延迟执行时间，单位为毫秒
     * @param callback 接口回调实例
     */
    public static void startRetry(long retryMillTime,final IRetryCallback callback) {
        isNeedRetry = true;
        isFirst = true;
        mRetryCounter = new CountDownTimer(retryMillTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isFirst && callback != null) {
                    isFirst = false;
                    callback.onRetry();
                }
                LogUtils.d("RetryUtil:" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if(isNeedRetry && callback != null) {
                    callback.onRetry();
                }
            }
        };
        mRetryCounter.start();
    }

    /**
     * 取消重发
     */
    public static void cancel() {
        isNeedRetry = false;
        if(mRetryCounter != null) {
            mRetryCounter.cancel();
            mRetryCounter = null;
        }
    }

    /**
     * 重发接口回调
     */
    public interface IRetryCallback {
        /**
         * 重发地方
         */
        void onRetry();
    }
}

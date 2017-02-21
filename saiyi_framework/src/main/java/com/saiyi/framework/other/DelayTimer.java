package com.saiyi.framework.other;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.saiyi.framework.util.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件描述：启动页定时器和倒计时器
 * 创建作者：黎丝军
 * 创建时间：16/7/28 PM3:45
 */
public class DelayTimer {

    //循环周期
    private static final int PERIOD = 1 * 1000;
    //停止值
    private static final int STOP_VALUE = -1;
    //定时器
    private Timer mTimer;
    //定时任务
    private TimerTask mTimerTask;
    //启动监听器
    private OnLauncherListener mListener;
    //倒计时监听器
    private OnCountDownListener mDownListener;
    //用于线程之间传递信息
    private Message mMessage;
    //总时间，默认是十秒
    private int mAllTime;
    //判断是否是倒计
    private boolean isDownTime = false;
    //需要被finish掉的activity
    private Activity mFinishActivity;
    //显示延迟的时间
    private TextView mShowTimeTv;
    //用于保存初始值
    private String mShowTimeStr;
    //保存之前的文本颜色
    private int mShowTimeColor;
    //用于判断显示格式
    private ShowMode mMode;
    //小时单位
    private String mHourUnit;
    //分钟单位
    private String mMinuteUnit;
    //秒单位
    private String mSecondUnit;
    //将子线转到Ui线程
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(isDownTime) {
                updateState(msg.what);
            } else {
                delayResult();
            }
        }
    };

    //是否取消
    private boolean isCancel = false;

    //更新状态
    private void updateState(int time){
        if(time <= STOP_VALUE) {
            if(mDownListener != null) {
                mDownListener.onDelayEnd();
            } else {
                recoverShowTimeState();
            }
        } else {
            if(mShowTimeTv != null) {
                String showStr;
                switch (mMode) {
                    case HOUR:
                        showStr = getTimeStr(time / 3600) + mHourUnit + getTimeStr((time / 60) % 60)+ mMinuteUnit + getTimeStr(time % 60) + mSecondUnit;
                        break;
                    case MINUTE:
                        showStr = getTimeStr(time / 60) + (TextUtils.isEmpty(mMinuteUnit.trim()) == true ? ":" : mMinuteUnit) + getTimeStr(time % 60) + mSecondUnit;
                        break;
                    default:
                        showStr = time + mSecondUnit;
                        break;
                }
                mShowTimeTv.setText(showStr);
            }
            if(mDownListener != null) {
                mDownListener.onUpdateView(mShowTimeTv, time);
            }
        }
    }

    private String getTimeStr(int time) {
        return (time < 10 ? "0" + time:time + "");
    }

    /**
     * 延迟启动处理结果
     */
    private void delayResult() {
        if(mListener != null) {
            if(mListener.isLogin()) {
                mListener.login();
            } else {
                mListener.unLogin();
            }
            if(mFinishActivity != null) {
                mFinishActivity.finish();
            }
        }
    }

    /**
     * 设置显示时间的TextView
     * @param timeView 时间显示器
     */
    public void setShowTimeView(TextView timeView) {
        mShowTimeTv = timeView;
        mShowTimeColor = timeView.getCurrentTextColor();
        mShowTimeStr = timeView.getText().toString().trim();
    }

    /**
     * 恢复显示初始状态
     */
    public void recoverShowTimeState() {
        recoverShowTimeState(mShowTimeStr,mShowTimeColor);
    }
    /**
     * 恢复显示初始状态
     */
    public void recoverShowTimeState(String endStr,int endColor) {
        cancelTimer();
        if(mShowTimeTv != null) {
            mShowTimeStr = endStr;
            mShowTimeColor = endColor;
            mShowTimeTv.setEnabled(true);
            mShowTimeTv.setText(endStr);
            mShowTimeTv.setTextColor(endColor);
        }
    }

    public DelayTimer() {
        this(null);
    }

    public DelayTimer(Activity activity) {
        mFinishActivity = activity;
        mHourUnit = ":";
        mMinuteUnit = " ";
        mSecondUnit = "";
    }

    /**
     * 获取定时器任务
     * @return 任务
     */
    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if(isDownTime) {
                    int time = mAllTime;
                    for(;time >= -1 && !isCancel;time--) {
                        mMessage = mHandler.obtainMessage();
                        mMessage.what = time;
                        mMessage.sendToTarget();
                        try{
                            Thread.sleep(PERIOD);
                        } catch (Exception e) {
                        }
                    }
                } else {
                    mMessage = mHandler.obtainMessage();
                    mMessage.what = 1;
                    mMessage.sendToTarget();
                }
                LogUtils.d("定时器执行");
            }
        };
    }

    /**
     * 延时启动的方法，该方法必须要调用，否则无效
     * @param delayTime 延时毫秒时间，默认是2秒
     */
    public void delayLauncher(int delayTime) {
       delayLauncher(delayTime,null);
    }

    /**
     * 延时启动的方法，该方法必须要调用，否则无效
     * @param delayTime 延时毫秒时间，默认是2秒
     */
    public void delayLauncher(int delayTime,OnLauncherListener listener) {
        isDownTime = false;
        mTimer = new Timer();
        mTimerTask = getTimerTask();
        if(listener != null) mListener = listener;
        mTimer.schedule(mTimerTask,delayTime == 0 ? 2000:delayTime);
    }

    /**
     * 倒计时
     * @param allTime 倒计时时间，如果是0则默认是10秒
     */
    public void countDown(int allTime) {
        countDown(allTime,0,null);
    }

    /**
     * 倒计时
     * @param allTime 倒计时时间，如果是0则默认是10秒
     * @param delayTime 延迟启动时间,单位是秒
     */
    public void countDown(int allTime,int delayTime) {
       countDown(allTime,delayTime,null);
    }

    /**
     * 倒计时
     * @param allTime 倒计时时间，如果是0则默认是10秒
     * @param delayTime 延迟启动时间,单位是秒
     */
    public void countDown(int allTime,int delayTime,OnCountDownListener listener) {
        countDown(ShowMode.SECOND,allTime,delayTime,listener);
    }

    /**
     * 倒计时
     * @param mode 显示模式
     * @param allTime 总时间
     * @param delayTime 延迟时间
     * @param listener 监听实例
     */
    public void countDown(ShowMode mode,int allTime,int delayTime,OnCountDownListener listener) {
        countDown(mode,allTime,delayTime,false,Color.GRAY,listener);
    }

    /**
     * 倒计时
     * @param mode 显示模式
     * @param allTime 总时间
     */
    public void countDown(ShowMode mode,int allTime) {
        countDown(mode,allTime,null);
    }

    /**
     * 倒计时
     * @param mode 显示模式
     * @param allTime 总时间
     * @param listener 监听实例
     */
    public void countDown(ShowMode mode,int allTime,OnCountDownListener listener) {
        countDown(mode,allTime,false,listener);
    }

    /**
     *  倒计时
     * @param mode 显示模式
     * @param allTime 总时间
     * @param enable 是否能点击
     * @param listener 监听实例
     */
    public void countDown(ShowMode mode,int allTime,boolean enable,OnCountDownListener listener) {
        countDown(mode,allTime,0,enable,0,listener);
    }

    /**
     * 倒计时
     * @param mode 显示模式
     * @param allTime 总时间
     * @param delayTime 延迟时间
     * @param enable 是否能点击
     * @param listener 监听实例
     */
    public void countDown(ShowMode mode,int allTime,int delayTime,boolean enable,int textColor,OnCountDownListener listener) {
        mMode = mode;
        isDownTime = true;
        isCancel = false;
        mTimer = new Timer();
        mTimerTask = getTimerTask();
        mAllTime = allTime == 0 ? 10:allTime;
        if(mShowTimeTv != null) {
            mShowTimeTv.setEnabled(enable);
            if(textColor != 0) {
                mShowTimeTv.setTextColor(textColor);
            }
        }
        if(listener != null)mDownListener = listener;
        mTimer.schedule(mTimerTask,delayTime);
    }

    /**
     * 取消定时器
     */
    public void cancelTimer() {
        isCancel = true;
        if(mTimerTask != null && mTimer != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    /**
     * 设置倒计时时间显示单位
     * @param hourUnit 小时单位默认是":"
     * @param minuteUnit 分钟单位默认是"  "
     * @param secondUnit 秒单位默认没有
     */
    public void setShowUnit(String hourUnit,String minuteUnit,String secondUnit) {
        mHourUnit = hourUnit == null ? ":" : hourUnit;
        mMinuteUnit = minuteUnit == null ? " " : minuteUnit;
        mSecondUnit = secondUnit == null ? "" : secondUnit;
    }

    /**
     * 设置监听器
     * @param listener 监听实例
     */
    public void setOnLauncherListener(OnLauncherListener listener) {
        mListener = listener;
    }

    /**
     * 倒计时器监听
     * @param listener 监听实例
     */
    public void setOnCountDownListener(OnCountDownListener listener) {
        mDownListener = listener;
    }

    /**
     * 启动监听器
     */
    public interface OnLauncherListener {

        /**
         * 在这里写用户是否登录的判断
         * @return false表示没有登录，true表示已经登录
         */
        boolean isLogin();

        /**
         * 用户没有登录
         */
        void unLogin();

        /**
         * 用户已经登录
         */
        void login();
    }

    /**
     * 倒计时监听器
     */
    public interface OnCountDownListener {

        /**
         * 延迟结束
         */
        void onDelayEnd();
        /**
         * 更新时间
         * @param time 时间
         * @param view 显示视图
         */
        void onUpdateView(TextView view, int time);
    }

    /**
     * 显示模式
     */
    public enum ShowMode {
        /**
         * 小时
         */
        HOUR,
        /**
         * 分钟
         */
        MINUTE,
        /**
         * 秒
         */
        SECOND
    }
}

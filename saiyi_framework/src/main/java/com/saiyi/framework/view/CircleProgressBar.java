package com.saiyi.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * 文件描述：外边带刻度的圆形转转进度条，该控件实例化后默认圆形进度条自动开始转，
 * 也就是不需要调用startRoll()方法来启动。不需要则需要调用setAutoRoll()方法设置为false,
 * 这个时候就需要调用startRoll()方法了。调用stopRoll()方法来使转转停止转动。当然当进度值到达百分之百后，
 * 程序会自动停止转转，不需要手动调用stopRoll()方法。如果需要设置外盘刻度颜色，则调用setDegreeColor()方法设置
 * 创建作者：黎丝军
 * 创建时间：16/8/4 AM11:10
 */
public class CircleProgressBar extends View {

    //视图宽
    private int width;
    //视图高
    private int height;
    //用于判断是否初始化视图
    private boolean isInitView;
    //圆心x坐标
    private int centerX;
    //圆心y坐标
    private int centerY;
    //用于去掉锯齿
    private PaintFlagsDrawFilter mDrawFilter;
    //用于绘制渐变色
    private SweepGradient mSweepGradient;
    //渐变使用的颜色
    private int[] mColors;
    //绘制刻度线用的画笔
    private Paint mDegreePaint;
    //绘制进度调颜色
    private Paint mProgressBarPaint;
    //画进度值的颜色
    private Paint mProgressValuePaint;
    //渐变角度
    private int mShadowDegree;
    //刻度盘半径
    private int mScaleDishR;
    //进度半径
    private int mProgressR;
    //进度值
    private int mProgressValue;
    //最大进度值
    private int mProgressMaxValue;
    //用于渐变颜色
    private Matrix mRotateMat;
    //用于结束进度
    private boolean isFinish;
    //进度矩形框
    private RectF mProgressRectF;
    //开始角度
    private float mStartDegree;
    //当前角度
    private float mCurrentDegree;
    //旋转速度，默认是5
    private int mRollSpeed = 2;
    //保存主线程Id
    private long mMainThreadId;
    //滚动动画线程
    private RollAnimThread mRollAimThread;
    //在程序一启动是否自动转动，默认是自动转动
    private boolean isRoll = true;
    //是否绘制刻度盘
    private boolean isDrawDish = true;

    public CircleProgressBar(Context context) {
        this(context,null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isFinish = true;
        isInitView = false;
        mShadowDegree = 180;
        mStartDegree = 0;
        mCurrentDegree = 360;
        mProgressMaxValue = 100;
        mMainThreadId = Thread.currentThread().getId();
        mRotateMat = new Matrix();
        mProgressRectF = new RectF();
        mRollAimThread = new RollAnimThread();
        //渐变颜色设置
        mColors = new int[]{Color.WHITE,Color.WHITE,Color.parseColor("#90CEF7"),Color.parseColor("#50B2F3"),Color.parseColor("#50B2F3")};
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //初始化画笔
        mDegreePaint = new Paint();
        mDegreePaint.setColor(Color.parseColor("#90CEF7"));
        mDegreePaint.setStrokeWidth(dipToPx(1.4f));

        mProgressBarPaint = new Paint();
        mProgressBarPaint.setAntiAlias(true);
        mProgressBarPaint.setStyle(Paint.Style.STROKE);
        mProgressBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressBarPaint.setStrokeWidth(dipToPx(2));
        mProgressBarPaint.setColor(Color.parseColor("#50B2F3"));

        mProgressValuePaint = new Paint();
        mProgressValuePaint.setColor(Color.parseColor("#50B2F3"));
        mProgressValuePaint.setTextSize(dipToPx(20));
    }

    //初始化视图
    private void initView() {
        if(!isInitView) {
            isInitView = true;
            mProgressRectF.left = centerX - mProgressR;
            mProgressRectF.top = centerY - mProgressR;
            mProgressRectF.right = centerX + mProgressR;
            mProgressRectF.bottom = centerY + mProgressR;
            mSweepGradient = new SweepGradient(centerX, centerY, mColors, null);
            if(isRoll) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startRoll();
                    }
                },1000);
            }
        }
    }

    /**
     * 调用该方法是进度圆圈转动起来
     */
    public void startRoll() {
        if(mRollAimThread != null && !mRollAimThread.isAlive()) {
            mRollAimThread.start();
        }
    }

    /**
     * 设置转转是否程序一打开就自动转
     * @param isRoll true表示自动转，否则不自动转，
     *               此时需要调用startRoll()方法来开始转动
     */
    public void setAutoRoll(boolean isRoll) {
        this.isRoll = isRoll;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        if(isDrawDish) {
            drawScaleDish(canvas);
        }
        updateShadowDegree(canvas);
        drawProgressText(canvas);
        stopRollAnimThread();
    }

    /**
     * 停止旋转动画
     */
    private void stopRollAnimThread() {
        if(mProgressValue >= mProgressMaxValue
                && mRollAimThread != null) {
            isFinish = false;
            mRollAimThread.interrupt();
            mRollAimThread = null;
        }
    }

    /**
     * 停止滚动
     */
    public void stopRoll() {
        if(mRollAimThread != null) {
            isFinish = false;
            mRollAimThread.interrupt();
            mRollAimThread = null;
        }
    }

    /**
     * 绘制刻度盘
     * @param canvas 画布
     */
    private void drawScaleDish(Canvas canvas) {
        for (int i = 0; i < 40; i++) {
            canvas.drawLine(centerX,centerY - mProgressR - 30,centerX,centerY - mScaleDishR - 30, mDegreePaint);
            canvas.rotate(9, centerX, centerY);
        }
    }

    /**
     * 更新渐变颜色角度
     */
    private void updateShadowDegree(Canvas canvas) {
        mRotateMat.setRotate(mShadowDegree, centerX, centerY);
        mSweepGradient.setLocalMatrix(mRotateMat);
        mProgressBarPaint.setShader(mSweepGradient);
        canvas.drawArc(mProgressRectF, mStartDegree, mCurrentDegree, false, mProgressBarPaint);

    }

    /**
     * 绘制进度文本
     * @param canvas 画布
     */
    private void drawProgressText(Canvas canvas) {
        final Rect rect = new Rect();
        final String text = (int)(((float)mProgressValue / mProgressMaxValue) * 100) + "%";
        mProgressValuePaint.getTextBounds(text, 0, text.length(), rect);
        final float x = centerX - rect.centerX();
        final float y = centerX - rect.centerY();
        canvas.drawText(text, x, y, mProgressValuePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        centerX = width / 2;
        centerY = height / 2;
        mProgressR = centerX > centerY ? centerY / 2:centerX / 2;
        mScaleDishR = mProgressR + 5;
        initView();
    }

    /**
     * 设置进度渐变颜色，如setProgressShadeColor（浅蓝色，蓝色），默认尾巴是白色
     * 如果需要改尾巴颜色请使用setProgressShadeColor（new int[]{}）方法,然后自定义颜色值
     * @param tintColor 浅颜色
     * @param deepColor 深颜色
     */
    public void setProgressShadeColor(int tintColor,int deepColor) {
        mColors = new int[]{Color.WHITE,Color.WHITE,tintColor,deepColor,deepColor};
    }

    /**
     * 设置进度渐变颜色
     * @param colors 实现渐变颜色至少需要传四个颜色值，如
     *               new int[]{白色，浅蓝色，蓝色，蓝色}
     *               当然你可以有更多的颜色，自己设置几个颜色试一试就Ok了
     */
    public void setProgressShadeColor(int[] colors) {
        mColors = colors;
    }

    /**
     * 设置进度值
     * @param progressValue 进度值
     */
    public void setProgressValue(int progressValue) {
        mProgressValue = progressValue;
        if(mMainThreadId == Thread.currentThread().getId()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    /**
     * 设置进度条的宽度
     * @param width 宽度值
     */
    public void setProgressBarStrokeWidth(int width) {
        mProgressBarPaint.setStrokeWidth(dipToPx(width));
    }

    /**
     * 设置进度值的颜色
     * @param color 颜色值
     */
    public void setProgressValueColor(int color) {
        mProgressValuePaint.setColor(color);
    }

    /**
     * 设置外盘刻度颜色
     * @param color 颜色值
     */
    public void setDegreeColor(int color) {
        mDegreePaint.setColor(color);
    }

    /**
     * 设置进度最大值
     * @param progressMaxValue 进度最大值
     */
    public void setProgressMaxValue(int progressMaxValue) {
        mProgressMaxValue = progressMaxValue;
    }

    /**
     * 设置转速
     * @param rollSpeed 转速值，默认是2秒
     */
    public void setRollSpeed(int rollSpeed) {
        mRollSpeed = rollSpeed;
    }

    /**
     * 设置是否绘制刻度盘
     * @param drawDish true表示有，false表示没有
     */
    public void setDrawDish(boolean drawDish) {
        isDrawDish = drawDish;
    }

    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 用于绘制旋转动画线程
     */
    class RollAnimThread extends Thread {
        RollAnimThread() {
            super("RollAnimThread");
        }
        @Override
        public void run() {
            while(isFinish) {
                if(mShadowDegree >= 360) {
                    mShadowDegree = 0;
                }
                mShadowDegree++;
                try {
                    Thread.sleep(mRollSpeed);
                }catch (Exception e) {
                }
                postInvalidate();
            }
        }
    }
}

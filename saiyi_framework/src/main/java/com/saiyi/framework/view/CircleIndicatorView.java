package com.saiyi.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述：圆形指示器进度视图，用于连接网络
 * 创建作者：黎丝军
 * 创建时间：16/8/3 PM3:29
 */
public class CircleIndicatorView extends View {

    //是否结束
    private boolean isFinish = true;
    //是否停止滚动
    private boolean isStop = true;
    //滚动时间，默认是一秒
    private int mRollTime = 200;
    //默认圆半径
    private int mCircleR = 10;
    //圆行个数，默认是5个
    private int circleCount = 5;
    //深色背景颜色
    private int mDarkBgColor;
    //浅色背景颜色
    private int mLightBgColor;
    //用于实现渐变颜色滚动效果
    private RollThread mRollThread;
    //记录当前滚动位置
    private int currentPosition = 0;
    //判断是否初始化了
    private boolean isInitCircle = false;
    //圆形列表
    private List<Circle> mCircleList = new ArrayList<>();

    public CircleIndicatorView(Context context) {
        this(context, null, 0);
    }
    public CircleIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDarkBgColor = Color.parseColor("#50B2F3");
        mLightBgColor = Color.parseColor("#90CEF7");
        mRollThread = new RollThread();
    }

    /**
     * 使用视图的宽高来创建圆形
     * @param width 视图宽
     * @param height 视图高
     */
    private void initCircle(int width,int height) {
        Circle circle;
        int count = 0;
        float maxX = width * 0.1f;
        float maxY = height * 0.5f;
        float currentX = maxX;
        mCircleR = (mCircleR > maxX || mCircleR > maxY) ? (int)(maxX < maxY ? maxX:maxY) : mCircleR;
        for(;count < circleCount;count++) {
            circle = new Circle();
            circle.cR = mCircleR;
            circle.cX = currentX;
            circle.cY = maxY;
            circle.darkBgColor = mDarkBgColor;
            circle.lightBgColor = mLightBgColor;
            mCircleList.add(circle);
            currentX += 2 * maxX;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(Circle circle:mCircleList) {
            circle.drawCircle(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!isInitCircle) {
            isInitCircle = true;
            initCircle(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
        }
    }

    /**
     * 开始滚动
     */
    public void startRoll() {
        if(!isInitCircle) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    roll();
                }
            },1000);
        } else {
            roll();
        }
    }

    /**
     * 往复滚动
     */
    private void roll() {
        if(mRollThread != null && !mRollThread.isAlive()) {
            mRollThread.start();
        } else {
            isStop = true;
        }
    }

    /**
     * 停止滚动
     */
    public void stopRoll() {
        isStop = false;
    }

    /**
     * 结束运行，该方法在activity的activity里onDestroy中调用
     */
    public void finish() {
        isStop = false;
        isFinish = false;
        mRollThread.interrupt();
        mRollThread = null;
    }

    /**
     * 用于实现滚动效果
     */
    class RollThread extends Thread {
        //用于判断是否翻面
        boolean isReverse = true;

        RollThread() {
        }

        @Override
        public void run() {
            while(isFinish) {
                while(isStop) {
                    if(!isInitCircle) {
                    } else {
                        if(currentPosition > circleCount) {
                            isReverse = false;
                            currentPosition = circleCount - 1;
                        }
                        if(currentPosition < 0) {
                            isReverse = true;
                            currentPosition = 0;
                        }
                        int count;
                        Circle circle;
                        if(isReverse) {
                            for(count = 0;count < circleCount;count++) {
                                circle = mCircleList.get(count);
                                if(count <= currentPosition) {
                                    circle.isDrawLight = false;
                                } else {
                                    circle.isDrawLight = true;
                                }
                            }
                            currentPosition ++;
                        } else {
                            for(count = circleCount - 1;count >= 0;count--) {
                                circle = mCircleList.get(count);
                                if(count >= currentPosition) {
                                    circle.isDrawLight = true;
                                } else {
                                    circle.isDrawLight = false;
                                }
                            }
                            currentPosition --;
                        }
                        postInvalidate();
                        try {
                            Thread.sleep(mRollTime);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 圆形
     */
    class Circle {
        //圆半径
        int cR;
        //圆x坐标
        float cX;
        //圆形y坐标
        float cY;
        //深色背景颜色
        int darkBgColor;
        //浅色背景颜色
        int lightBgColor;
        //画笔
        Paint circlePaint;
        //是否绘制浅颜色，默认是绘制浅颜色
        boolean isDrawLight = true;

        Circle() {
            circlePaint = new Paint();
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setAntiAlias(true);
        }

        /**
         * 绘制圆形
         * @param canvas 画布
         */
        void drawCircle(Canvas canvas) {
            if(isDrawLight) {
                circlePaint.setColor(lightBgColor);
            } else {
                circlePaint.setColor(darkBgColor);
            }
            canvas.drawCircle(cX,cY,cR,circlePaint);
        }
    }
}

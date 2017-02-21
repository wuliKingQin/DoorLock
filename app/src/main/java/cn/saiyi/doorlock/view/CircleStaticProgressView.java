package cn.saiyi.doorlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.saiyi.doorlock.R;

/**
 * 描述：绘制外圆静态进度条
 * 创建作者：黎丝军
 * 创建时间：2016/10/18 15:15
 */

public class CircleStaticProgressView extends TextView {

    //绘制外圆
    private Paint mCirclePaint;
    //坐标x
    private int mCircleX;
    //坐标y
    private int mCircleY;
    //半径r
    private int mCircleR;
    //总进度
    private int mAllProgress = 100;

    public CircleStaticProgressView(Context context) {
        this(context,null);
    }

    public CircleStaticProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleStaticProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCirclePaint = new Paint();
        mCirclePaint.setStrokeWidth(5);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(context.getResources().getColor(R.color.color13));
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCircleX = getWidth() / 2;
        mCircleY = getHeight() / 2;
        mCircleR = mCircleX > mCircleY ? mCircleY - 10:mCircleX - 10;
        canvas.drawCircle(mCircleX,mCircleY,mCircleR,mCirclePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置圆颜色
     * @param color 颜色值
     */
    public void setCircleColor(int color) {
        mCirclePaint.setColor(color);
        invalidate();
    }

    /**
     * 设置进度
     * @param progress 进度值
     */
    public void setTextProgress(int progress) {
        setText((int)(((float)progress / mAllProgress) * 100) + "%");
    }

    /**
     * 设置总进度值
     * @param allProgress 总进度值
     */
    public void setAllProgressValue(int allProgress) {
        mAllProgress = allProgress;
    }
}

package cn.saiyi.doorlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.saiyi.framework.util.DensityUtils;

/**
 * 描述：电量显示视图控件，使用该控件时需要在制定固定的宽度和高度，暂时不支持wrap_content字段
 * 创建作者：黎丝军
 * 创建时间：2016/10/14 14:53
 */

public class BatteryShowView extends View {

    //当前电池电量值
    private int mCurrentPowerValue = 10;
    //电池总量值
    private int mAllPowerValue = 100;
    //绘制电池电量的画笔
    private Paint mPowerValuePaint;
    //绘制电池矩形框的画笔
    private Paint mPowerRectPaint;
    //绘制电池百分比的画笔
    private Paint mPowerValueTextPaint;
    //电池矩形框
    private RectF mPowerRect;
    //电池值矩形框
    private RectF mPowerValueRect;
    //电池电冒矩形框
    private RectF mPowerHotRect;
    //绘制电池百分比文本框
    private RectF mPowerTextRect;
    //电池当前电量长度
    private float mPowerWidth;
    //电池总长度
    private float mPowerAllWidth;
    //电池百分比
    private float mPowerPercent;
    //电池文本大小
    private float mPowerTextSize = 12;
    //电冒大小
    private int mPowerHotSize;

    public BatteryShowView(Context context) {
        this(context,null);
    }

    public BatteryShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPowerValuePaint = new Paint();
        mPowerValuePaint.setStyle(Paint.Style.FILL);
        mPowerValuePaint.setAntiAlias(true);
        mPowerValuePaint.setColor(Color.WHITE);

        mPowerRectPaint = new Paint();
        mPowerRectPaint.setStrokeWidth(2);
        mPowerRectPaint.setAntiAlias(true);
        mPowerRectPaint.setStyle(Paint.Style.STROKE);
        mPowerRectPaint.setColor(Color.WHITE);

        mPowerValueTextPaint = new Paint();
        mPowerValueTextPaint.setStyle(Paint.Style.FILL);
        mPowerValueTextPaint.setAntiAlias(true);
        mPowerValueTextPaint.setTextSize(DensityUtils.spToPx(context,mPowerTextSize));
        mPowerValueTextPaint.setColor(Color.BLACK);

        mPowerRect = new RectF();
        mPowerHotRect = new RectF();
        mPowerTextRect = new RectF();
        mPowerValueRect = new RectF();

        mPowerHotSize = DensityUtils.dpToPx(context,3f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mPowerRect.set(getWidth() / 2 + 10,4,getWidth() - 10,getHeight() - 4 + getPaddingBottom());
        mPowerRectPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mPowerRect,mPowerRectPaint);
        drawPowerValue(canvas);
        mPowerHotRect.set(getWidth() - 10,getHeight()/2 - mPowerHotSize,getWidth() - 2,getHeight() / 2 + mPowerHotSize + getPaddingBottom());
        mPowerRectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mPowerHotRect,mPowerRectPaint);
        drawProgressText(canvas);
        canvas.restore();
    }

    /**
     * 绘制电池电量值
     * @param canvas 画布
     */
    private void drawPowerValue(Canvas canvas) {
        mPowerPercent = (float)mCurrentPowerValue / mAllPowerValue;
        mPowerAllWidth = mPowerRect.width();
        mPowerWidth = mPowerPercent * mPowerAllWidth;
        mPowerValueRect.set(getWidth() / 2 + 11,5,getWidth() / 2 + mPowerWidth + 10,getHeight() - 5 + getPaddingBottom());
        if(mPowerPercent < 0.2f) {
            mPowerValuePaint.setColor(Color.RED);
        } else {
            mPowerValuePaint.setColor(Color.WHITE);
        }
        canvas.drawRect(mPowerValueRect,mPowerValuePaint);
    }

    /**
     * 绘制进度文本
     * @param canvas 画布
     */
    private void drawProgressText(Canvas canvas) {
        final Rect rect = new Rect();
        mPowerTextRect.set(getPaddingLeft(),getPaddingTop(),getWidth() / 2 , getHeight() - getPaddingBottom());
        final String text = ((int)(mPowerPercent * 100)) + "%";
        mPowerValueTextPaint.getTextBounds(text, 0, text.length(), rect);
        final float x = mPowerTextRect.centerX() - rect.centerX();
        final float y = mPowerTextRect.centerY() - rect.centerY();
        canvas.drawText(text, x, y, mPowerValueTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 获取当前电量值
     * @return 当前电量值
     */
    public int getPowerValue() {
        return mCurrentPowerValue;
    }

    /**
     * 设置当前电量
     * @param powerValue 当前电量
     */
    public void setPowerValue(int powerValue) {
        this.mCurrentPowerValue = powerValue;
        invalidate();
    }

    /**
     * 设置当前电量
     * @param grade 当前电量
     */
    public void setGradePowerValue(byte grade) {
        int powerValue = 0;
        switch (grade) {
            case Grade.GRADE_0:
                powerValue = 20;
                break;
            case Grade.GRADE_1:
                powerValue = 40;
                break;
            case Grade.GRADE_2:
                powerValue = 60;
                break;
            case Grade.GRADE_3:
                powerValue = 80;
                break;
            case Grade.GRADE_4:
                powerValue = 100;
                break;
            default:
                break;
        }
        setPowerValue(powerValue);
    }

    /**
     * 获取总电量值
     * @return 总电量值
     */
    public int getAllPowerValue() {
        return mAllPowerValue;
    }

    /**
     * 设置总电量显示
     * @param allPowerValue 总电量值
     */
    public void setAllPowerValue(int allPowerValue) {
        this.mAllPowerValue = allPowerValue;
    }

    /**
     * 电量等级
     */
    private interface Grade {
        //零电量
        byte GRADE_0 = 0x00;
        //还有一点电量
        byte GRADE_1 = 0x01;
        //还有一半的电量
        byte GRADE_2 = 0x02;
        //还有三分子二的电量
        byte GRADE_3 = 0x03;
        //满格电量
        byte GRADE_4 = 0x04;
    }
}

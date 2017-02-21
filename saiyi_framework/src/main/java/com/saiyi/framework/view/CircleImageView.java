package com.saiyi.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.saiyi.framework.util.LogUtils;

/**
 * 文件描述：自定义圆形ImageView，该类实现了外边框和内边框的功能
 * 创建作者：黎丝军
 * 创建时间：16/7/28
 */
public class CircleImageView extends ImageView {

    //运行时
    private Context mContext;
    //边框大小
    private int mBorderSize = 0;
    // 控件默认宽
    private int mDefaultWidth = 0;
    //控件默认宽
    private int mDefaultHeight = 0;
    //内边框颜色
    private int mBorderInsideColor = 0xFFFFFFFF;
    //外边框颜色
    private int mBorderOutsideColor = 0xFFFFFFFF;
    //控件默认颜色
    private int mDefaultColor = 0xFFFFFFFF;
    //保存以前的路径
    private String mUrl = null;
    //保存头像
    private Bitmap mHeadIcon = null;
    //保存默认Id
    private int mDefaultId = 0;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mBorderSize = 0;
        mBorderOutsideColor = 0;
        mBorderInsideColor = 0;
    }

    /**
     * 设置圆形参数，主要包括边框大小，外边框颜色，内边框颜色值
     * @param borderSize   边框大小
     * @param outSideColor 外边框颜色
     * @param inSideColor  内边框颜色值
     */
    public void setCircleParams(int borderSize, int outSideColor, int inSideColor) {
        mBorderSize = borderSize;
        mBorderInsideColor = inSideColor;
        mBorderOutsideColor = outSideColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap bitmap;
        try {
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        } catch (Throwable e) {
            LogUtils.d("圆角图片获取bitmap异常" + e.toString());
            return;
        }
        if (mDefaultWidth == 0) {
            mDefaultWidth = getWidth();
        }
        if (mDefaultHeight == 0) {
            mDefaultHeight = getHeight();
        }
        int radius = 0;
        // 定义画两个边框，分别为外圆边框和内圆边框
        if (mBorderInsideColor != mDefaultColor && mBorderOutsideColor != mDefaultColor) {
            radius = (mDefaultWidth < mDefaultHeight ? mDefaultWidth : mDefaultHeight) / 2 - 2 * mBorderSize;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderSize / 2, mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderSize + mBorderSize / 2, mBorderOutsideColor);
            // 定义画一个边框
        } else if (mBorderInsideColor != mDefaultColor && mBorderOutsideColor == mDefaultColor) {
            radius = (mDefaultWidth < mDefaultHeight ? mDefaultWidth : mDefaultHeight) / 2 - mBorderSize;
            drawCircleBorder(canvas, radius + mBorderSize / 2, mBorderInsideColor);
            // 定义画一个边框
        } else if (mBorderInsideColor == mDefaultColor && mBorderOutsideColor != mDefaultColor) {
            radius = (mDefaultWidth < mDefaultHeight ? mDefaultWidth : mDefaultHeight) / 2 - mBorderSize;
            drawCircleBorder(canvas, radius + mBorderSize / 2, mBorderOutsideColor);
        } else {// 没有边框
            radius = (mDefaultWidth < mDefaultHeight ? mDefaultWidth : mDefaultHeight) / 2;
        }
        Bitmap roundBitmap = obtainCircleBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, mDefaultWidth / 2 - radius, mDefaultHeight / 2 - radius, null);
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param bmp    位图
     * @param radius 绘制半径
     * @return 圆形Bitmap
     */
    public Bitmap obtainCircleBitmap(Bitmap bmp, int radius) {
        try {
            // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
            int x, y;
            int squareWidth;
            int squareHeight;
            Bitmap squareBitmap;
            Bitmap scaledSrcBmp;
            int diameter = radius * 2;
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            if (bmpHeight > bmpWidth) {// 高大于宽
                squareWidth = squareHeight = bmpWidth;
                x = 0;
                y = (bmpHeight - bmpWidth) / 2;
                // 截取正方形图片
                squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
            } else if (bmpHeight < bmpWidth) {// 宽大于高
                squareWidth = squareHeight = bmpHeight;
                x = (bmpWidth - bmpHeight) / 2;
                y = 0;
                squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
            } else {
                squareBitmap = bmp;
            }
            if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
                scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
            } else {
                scaledSrcBmp = squareBitmap;
            }
            Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                    scaledSrcBmp.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                    scaledSrcBmp.getHeight() / 2,
                    scaledSrcBmp.getWidth() / 2,
                    paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
            bmp = null;
            squareBitmap = null;
            scaledSrcBmp = null;
            return output;
        } catch (Throwable e) {
            LogUtils.d("裁剪原型图片:" + e.toString());
        }
        return null;
    }

    /**
     * 边缘画圆方法
     *
     * @param canvas 画布
     * @param radius 半径
     * @param color  颜色值
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        //设置paint的style为实心;STROKE为空心
        paint.setStyle(Paint.Style.STROKE);
        //设置paint的外框宽度
        paint.setStrokeWidth(mBorderSize);
        canvas.drawCircle(mDefaultWidth / 2, mDefaultHeight / 2, radius, paint);
    }

    /**
     * 设置内边框颜色值
     *
     * @param borderInsideColor 内边框颜色值
     */
    public void setBorderInsideColor(int borderInsideColor) {
        mBorderInsideColor = borderInsideColor;
    }

    /**
     * 设置外边框颜色值
     *
     * @param borderOutsideColor 外边框颜色值
     */
    public void setBorderOutsideColor(int borderOutsideColor) {
        mBorderOutsideColor = borderOutsideColor;
    }

    /**
     * 设置边框大小
     *
     * @param borderSize 边框大小值
     */
    public void setBorderSize(int borderSize) {
        mBorderSize = borderSize;
    }

    /**
     * 获取圆形图片
     *
     * @param resId  资源图片ID
     * @param radius 半径
     * @return Bitmap
     */
    public Bitmap obtainCircleBitmap(int resId, int radius) {
        final Bitmap target = BitmapFactory.decodeResource(mContext.getResources(), resId);
        return obtainCircleBitmap(target, radius);
    }

    /**
     * 设置背景资源图片
     *
     * @param resId 资源Id
     */
    public void setBitmapBackground(int resId) {
        setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

}

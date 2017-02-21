package com.lisijun.gesture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.lisijun.gesture.R;
import com.lisijun.gesture.bean.GPoint;
import com.lisijun.gesture.util.ScreenUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：绘制手势线条视图
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 9:26
 */

public class GLineView extends View {

    // 声明起点坐标
    private int mov_x;
    private int mov_y;
    // 声明画笔
    private Paint paint;
    // 画布
    private Canvas canvas;
    // 位图
    private Bitmap bitmap;
    // 装有各个view坐标的集合
    private List<GPoint> list;
    // 记录画过的线
    private List<Pair<GPoint, GPoint>> lineList;
    // 自动选中的情况点
    private Map<String, GPoint> autoCheckPointMap;
    // 是否允许绘制
    private boolean isDrawEnable = true;
    //屏幕的宽度和高度
    private int[] screenDisplay;
    //手指当前在哪个Point内
    private GPoint currentPoint;
    //用户绘图的回调
    private GestureCallBack callBack;
    //用户当前绘制的图形密码
    private StringBuilder passWordSb;
    // 是否为校验
    private boolean isVerify;
    //用户传入的passWord
    private String passWord;
    //用于保存绘制颜色值
    private int mDrawColor;
    //绘制错误颜色值
    private int mDrawErrorColor;

    public GLineView(Context context, List<GPoint> list, boolean isVerify,
                     String passWord, GestureCallBack callBack) {
        super(context);
        mDrawColor = context.getResources().getColor(R.color.gesture_color1);
        mDrawErrorColor = context.getResources().getColor(R.color.gesture_color2);
        screenDisplay = ScreenUtil.getScreenDisplay(context);
        // 创建一个画笔
        paint = new Paint(Paint.DITHER_FLAG);
        // 设置位图的宽高
        bitmap = Bitmap.createBitmap(screenDisplay[0], screenDisplay[0], Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
        // 设置非填充
        paint.setStyle(Paint.Style.STROKE);
        // 笔宽5像素
        paint.setStrokeWidth(10);
        // 设置默认连线颜色
        paint.setColor(mDrawColor);
        // 不显示锯齿
        paint.setAntiAlias(true);
        lineList = new ArrayList<>();
        this.list = list;
        this.callBack = callBack;
        // 初始化密码缓存
        this.isVerify = isVerify;
        this.passWordSb = new StringBuilder();
        this.passWord = passWord;
        initAutoCheckPointMap();
    }

    //初始化点数据
    private void initAutoCheckPointMap() {
        autoCheckPointMap = new HashMap<>();
        autoCheckPointMap.put("1,3", getGPointByNum(2));
        autoCheckPointMap.put("1,7", getGPointByNum(4));
        autoCheckPointMap.put("1,9", getGPointByNum(5));
        autoCheckPointMap.put("2,8", getGPointByNum(5));
        autoCheckPointMap.put("3,7", getGPointByNum(5));
        autoCheckPointMap.put("3,9", getGPointByNum(6));
        autoCheckPointMap.put("4,6", getGPointByNum(5));
        autoCheckPointMap.put("7,9", getGPointByNum(8));
    }

    /**
     * 根据个数来获取手势点
     * @param num 个数
     * @return 手势点实例
     */
    private GPoint getGPointByNum(int num) {
        for (GPoint point : list) {
            if (point.getNum() == num) {
                return point;
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrawEnable == false) {
            return true;
        }
        // 设置默认连线颜色
        paint.setColor(mDrawColor);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mov_x = (int) event.getX();
                mov_y = (int) event.getY();
                // 判断当前点击的位置是处于哪个点之内
                currentPoint = getPointAt(mov_x, mov_y);
                if (currentPoint != null) {
                    currentPoint.setPointState(GPoint.POINT_STATE_SELECTED);
                    passWordSb.append(currentPoint.getNum());
                }
                // canvas.drawPoint(mov_x, mov_y, paint);// 画点
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                clearScreenAndDrawList();
                // 得到当前移动位置是处于哪个点内
                GPoint pointAt = getPointAt((int) event.getX(), (int) event.getY());
                // 代表当前用户手指处于点与点之前
                if (currentPoint == null && pointAt == null) {
                    return true;
                } else {// 代表用户的手指移动到了点上
                    if (currentPoint == null) {// 先判断当前的point是不是为null
                        // 如果为空，那么把手指移动到的点赋值给currentPoint
                        currentPoint = pointAt;
                        // 把currentPoint这个点设置选中为true;
                        currentPoint.setPointState(GPoint.POINT_STATE_SELECTED);
                        passWordSb.append(currentPoint.getNum());
                    }
                }
                if (pointAt == null || currentPoint.equals(pointAt) || GPoint.POINT_STATE_SELECTED == pointAt.getPointState()) {
                    // 点击移动区域不在圆的区域，或者当前点击的点与当前移动到的点的位置相同，或者当前点击的点处于选中状态
                    // 那么以当前的点中心为起点，以手指移动位置为终点画线
                    canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), event.getX(), event.getY(), paint);// 画线
                } else {
                    // 如果当前点击的点与当前移动到的点的位置不同
                    // 那么以前前点的中心为起点，以手移动到的点的位置画线
                    canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), pointAt.getCenterX(), pointAt.getCenterY(), paint);// 画线
                    pointAt.setPointState(GPoint.POINT_STATE_SELECTED);
                    // 判断是否中间点需要选中
                    GPoint betweenPoint = getBetweenCheckPoint(currentPoint, pointAt);
                    if (betweenPoint != null && GPoint.POINT_STATE_SELECTED != betweenPoint.getPointState()) {
                        // 存在中间点并且没有被选中
                        Pair<GPoint, GPoint> pair1 = new Pair<>(currentPoint, betweenPoint);
                        lineList.add(pair1);
                        passWordSb.append(betweenPoint.getNum());
                        Pair<GPoint, GPoint> pair2 = new Pair<>(betweenPoint, pointAt);
                        lineList.add(pair2);
                        passWordSb.append(pointAt.getNum());
                        // 设置中间点选中
                        betweenPoint.setPointState(GPoint.POINT_STATE_SELECTED);
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    } else {
                        Pair<GPoint, GPoint> pair = new Pair<>(currentPoint, pointAt);
                        lineList.add(pair);
                        passWordSb.append(pointAt.getNum());
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 当手指抬起的时候
                if (isVerify) {
                    // 手势密码校验
                    // 清掉屏幕上所有的线，只画上集合里面保存的线
                    if (TextUtils.equals(passWord,passWordSb.toString())) {
                        // 代表用户绘制的密码手势与传入的密码相同
                        callBack.checkedSuccess();
                    } else {
                        // 用户绘制的密码与传入的密码不同。
                        callBack.checkedFail();
                    }
                } else {
                    callBack.onGestureCodeInput(passWordSb.toString());
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 指定时间去清除绘制的状态
     * @param delayTime 延迟执行时间
     */
    public void clearDrawLineState(boolean isError,long delayTime) {
        if (isError) {
            // 绘制红色提示路线
            isDrawEnable = false;
            drawErrorPathTip();
        }
        new Handler().postDelayed(new clearStateRunnable(), delayTime);
    }

    /**
     * 清除绘制状态的线程
     */
    final class clearStateRunnable implements Runnable {
        public void run() {
            // 重置passWordSb
            passWordSb = new StringBuilder();
            // 清空保存点的集合
            lineList.clear();
            // 重新绘制界面
            clearScreenAndDrawList();
            for (GPoint p : list) {
                p.setPointState(GPoint.POINT_STATE_NORMAL);
            }
            invalidate();
            isDrawEnable = true;
        }
    }

    /**
     * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
     * @param x 手触摸的x坐标
     * @param y 手触摸的y坐标
     * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
     */
    private GPoint getPointAt(int x, int y) {
        for (GPoint point : list) {
            // 先判断x
            int leftX = point.getLeftX();
            int rightX = point.getRightX();
            if (!(x >= leftX && x < rightX)) {
                // 如果为假，则跳到下一个对比
                continue;
            }

            int topY = point.getTopY();
            int bottomY = point.getBottomY();
            if (!(y >= topY && y < bottomY)) {
                // 如果为假，则跳到下一个对比
                continue;
            }
            // 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
            return point;
        }
        return null;
    }

    /**
     * 检查两点之前的点并返回
     * @param pointStart 开始点
     * @param pointEnd 结束点
     * @return 中间点实例
     */
    private GPoint getBetweenCheckPoint(GPoint pointStart, GPoint pointEnd) {
        int startNum = pointStart.getNum();
        int endNum = pointEnd.getNum();
        String key = null;
        if (startNum < endNum) {
            key = startNum + "," + endNum;
        } else {
            key = endNum + "," + startNum;
        }
        return autoCheckPointMap.get(key);
    }

    /**
     * 清掉屏幕上所有的线，然后画出集合里面的线
     */
    private void clearScreenAndDrawList() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Pair<GPoint, GPoint> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                    pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
    }

    /**
     * 校验错误/两次绘制不一致提示
     */
    private void drawErrorPathTip() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setColor(mDrawErrorColor);// 设置默认线路颜色
        for (Pair<GPoint, GPoint> pair : lineList) {
            pair.first.setPointState(GPoint.POINT_STATE_WRONG);
            pair.second.setPointState(GPoint.POINT_STATE_WRONG);
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                    pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
        }
        invalidate();
    }

    /**
     * 手密码检验回调接口
     */
    public interface GestureCallBack {

        /**
         * 用户设置/输入了手势密码
         * @param inputCode 输入的密码
         */
        void onGestureCodeInput(String inputCode);

        /**
         * 代表用户绘制的密码与传入的密码相同
         */
        void checkedSuccess();

        /**
         * 代表用户绘制的密码与传入的密码不相同
         */
        void checkedFail();
    }
}

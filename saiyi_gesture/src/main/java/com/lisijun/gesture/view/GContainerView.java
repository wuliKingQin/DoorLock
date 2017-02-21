package com.lisijun.gesture.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lisijun.gesture.R;
import com.lisijun.gesture.bean.GPoint;
import com.lisijun.gesture.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：手势密码容器视图
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 9:52
 */

public class GContainerView extends ViewGroup {
    //点个数
    private int baseNum = 6;
    //屏幕宽高
    private int[] screenDisplay;
    //每个点区域的宽度
    private int blockWidth;
    //声明一个集合用来封装坐标集合
    private List<GPoint> list;
    //运行环境
    private Context context;
    //是否验证
    private boolean isVerify;
    //绘制线视图
    private GLineView gestureDrawLine;

    /**
     * 包含9个ImageView的容器，初始化
     * @param context
     * @param isVerify 是否为校验手势密码
     * @param passWord 用户传入密码
     * @param callBack 手势绘制完毕的回调
     */
    public GContainerView(Context context, boolean isVerify, String passWord,GLineView.GestureCallBack callBack) {
        super(context);
        this.list = new ArrayList<>();
        this.context = context;
        this.isVerify = isVerify;
        screenDisplay = ScreenUtil.getScreenDisplay(context);
        blockWidth = screenDisplay[0]/3;
        // 添加9个图标
        addChild();
        // 初始化一个可以画线的view
        gestureDrawLine = new GLineView(context, list, isVerify, passWord, callBack);
    }

    /**
     * 添加子视图
     */
    private void addChild(){
        for (int i = 0; i < 9; i++) {
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.mipmap.ic_gesture_dark);
            this.addView(image);
            invalidate();
            // 第几行
            int row = i / 3;
            // 第几列
            int col = i % 3;
            // 定义点的每个属性
            int leftX = col*blockWidth+blockWidth/baseNum;
            int topY = row*blockWidth+blockWidth/baseNum;
            int rightX = col*blockWidth+blockWidth-blockWidth/baseNum;
            int bottomY = row*blockWidth+blockWidth-blockWidth/baseNum;
            GPoint p = new GPoint(leftX, rightX, topY, bottomY, image,i+1);
            this.list.add(p);
        }
    }

    /**
     * 设置父容器
     * @param parent 父视图
     */
    public void setParentView(ViewGroup parent){
        // 得到屏幕的宽度
        int width = screenDisplay[0];
        LayoutParams layoutParams = new LayoutParams(width, width);
        this.setLayoutParams(layoutParams);
        gestureDrawLine.setLayoutParams(layoutParams);
        parent.addView(gestureDrawLine);
        parent.addView(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            //第几行
            int row = i/3;
            //第几列
            int col = i%3;
            View v = getChildAt(i);
            v.layout(col*blockWidth+blockWidth/baseNum, row*blockWidth+blockWidth/baseNum,
                    col*blockWidth+blockWidth-blockWidth/baseNum, row*blockWidth+blockWidth-blockWidth/baseNum);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 遍历设置每个子view的大小
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 保留路径delayTime时间长
     * @param delayTime
     */
    public void clearDrawLineState(boolean isError,long delayTime) {
        gestureDrawLine.clearDrawLineState(isError,delayTime);
    }
}

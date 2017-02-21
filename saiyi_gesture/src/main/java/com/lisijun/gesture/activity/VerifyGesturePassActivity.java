package com.lisijun.gesture.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lisijun.gesture.R;
import com.lisijun.gesture.interfaces.GConstant;
import com.lisijun.gesture.view.GContainerView;
import com.lisijun.gesture.view.GLineView;

/**
 * 描述：验证手势密码，启动该类是需要传手势密码，通过intent.putExtra(GESTURE_PASS,"密码");
 *       否则验证密码将不通过，除了传密码过来然后传忘记密码的界面class实例过来，
 *       通过intent.putExtra(FORGET_ACTIVITY_CLASS,"忘记密码class实例");
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 10:52
 */

public class VerifyGesturePassActivity extends AppCompatActivity
        implements GLineView.GestureCallBack{
    //用于忘记密码跳转的界面class
    public final static String FORGET_ACTIVITY_CLASS = "forgetActivityClass";
    //用于主界面的class
    public final static String MAIN_ACTIVITY_CLASS = "mainActivityClass";
    //用于判断是否从手势密码进入修改密码界面
    public final static String IS_GESTURE_PASS = "isGesturePass";
    //忘记密码
    private TextView mForgetPassTv;
    //手势密码视图
    private GContainerView mGPassContainerView;
    //绘制手势密码容器
    private FrameLayout mGestureContainer;
    //用于忘记密码
    private Class<?> mForgetPassActivity;
    //主界面
    private Class<?> mMainActivity;
    //保存手势密码
    private String mGesturePass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_pass);
        mForgetPassTv = (TextView) findViewById(R.id.tv_gesture_forget_pass);
        mGestureContainer = (FrameLayout) findViewById(R.id.fl_gesture_verify_container);

        initView();
        setListeners();
    }

    private void initView() {
        mGesturePass = getIntent().getStringExtra(GConstant.GESTURE_PASS);
        mForgetPassActivity = (Class<?>)getIntent().getSerializableExtra(FORGET_ACTIVITY_CLASS);
        mMainActivity = (Class<?>) getIntent().getSerializableExtra(MAIN_ACTIVITY_CLASS);
        mGPassContainerView = new GContainerView(this,true,mGesturePass,this);
        mGPassContainerView.setParentView(mGestureContainer);
    }

    /**
     * 设置监听
     */
    private void setListeners() {
        mForgetPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mForgetPassActivity != null) {
                    final Intent intent = new Intent(VerifyGesturePassActivity.this,mForgetPassActivity);
                    intent.putExtra(IS_GESTURE_PASS,true);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyGesturePassActivity.this,"你没有设置跳转界面",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onGestureCodeInput(String inputCode) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK) {
        }
        return true;
    }

    @Override
    public void checkedSuccess() {
        mGPassContainerView.clearDrawLineState(false,50L);
        Toast.makeText(this, "手势密码正确", Toast.LENGTH_SHORT).show();
        if(mMainActivity != null) {
            startActivity(new Intent(this,mMainActivity));
        }
        finish();
    }

    @Override
    public void checkedFail() {
        mGPassContainerView.clearDrawLineState(true,1300L);
        Toast.makeText(this, "手势密码错误", Toast.LENGTH_SHORT).show();
        // 左右移动动画
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        mGestureContainer.startAnimation(shakeAnimation);
    }
}

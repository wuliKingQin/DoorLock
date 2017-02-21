package com.lisijun.gesture.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lisijun.gesture.R;
import com.lisijun.gesture.interfaces.GConstant;
import com.lisijun.gesture.view.GContainerView;
import com.lisijun.gesture.view.GLineView;

/**
 * 描述：添加手势密码界面,设置的手势密码将通过onActivityResult方法返回,监听结果RESULT_OK就行了
 *       然后通过VerifyGesturePassActivity.GESTURE_PASS键去获取密码值自己保存。
 * 创建作者：黎丝军
 * 创建时间：2016/10/19 10:00
 */

public class AddGesturePassActivity extends AppCompatActivity
        implements GLineView.GestureCallBack {

    //是否用来开锁
    private boolean isUseOpenLock = true;
    //用于判断是否首次输入密码
    private boolean mIsFirstInput = true;
    //第一次密码
    private String mFirstPassword = null;
    //返回按钮
    private ImageView mBackBtn;
    //绘制密码提示
    private TextView mDrawPassHintTv;
    //标题
    private TextView mTitleTv;
    //开锁开关
    private CheckBox mOpenLockChb;
    //手势密码视图
    private GContainerView mGPassContainerView;
    //绘制手势密码容器
    private FrameLayout mGestureContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture_pass);
        mDrawPassHintTv = (TextView) findViewById(R.id.tv_draw_pass_hint);
        mBackBtn = (ImageView) findViewById(R.id.iv_gesture_back);
        mTitleTv = (TextView) findViewById(R.id.tv_gesture_title);
        mOpenLockChb = (CheckBox) findViewById(R.id.chb_gesture_open_lock);
        mGestureContainer = (FrameLayout) findViewById(R.id.fl_gesture_container);
        mGPassContainerView = new GContainerView(this,false,"",this);

        initView();
        setListeners();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        isUseOpenLock = getIntent().getBooleanExtra(GConstant.IS_GESTURE_OPEN_LOCK,true);
        final String title = getIntent().getStringExtra("title");
        if(title != null) {
            mTitleTv.setText(title);
        }
        mOpenLockChb.setChecked(isUseOpenLock);
        mGPassContainerView.setParentView(mGestureContainer);
    }

    /**
     * 设置监听器
     */
    private void setListeners() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHandle();
            }
        });

        mOpenLockChb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isUseOpenLock = isChecked;
            }
        });
    }

    @Override
    public void onGestureCodeInput(String inputCode) {
        if (!isInputPassValidate(inputCode)) {
            Toast.makeText(this,"最少链接4个点, 请重新输入",Toast.LENGTH_SHORT).show();
            mGPassContainerView.clearDrawLineState(false,0L);
            return;
        }
        if (mIsFirstInput) {
            mFirstPassword = inputCode;
            mDrawPassHintTv.setText("请再次绘制相同密码");
            mGPassContainerView.clearDrawLineState(false,0L);
        } else {
            if (TextUtils.equals(inputCode,mFirstPassword)) {
                Toast.makeText(this, "设置手势密码成功", Toast.LENGTH_SHORT).show();
                mGPassContainerView.clearDrawLineState(false,0L);
                getIntent().putExtra(GConstant.GESTURE_PASS,inputCode);
                getIntent().putExtra(GConstant.IS_GESTURE_OPEN_LOCK,isUseOpenLock);
                setResult(RESULT_OK,getIntent());
                finish();
            } else {
                Toast.makeText(this, "两次密码不一致，请重新绘制", Toast.LENGTH_SHORT).show();
                // 左右移动动画
                Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
                mGestureContainer.startAnimation(shakeAnimation);
                // 保持绘制的线，1.5秒后清除
                mGPassContainerView.clearDrawLineState(true,1300L);
            }
        }
        mIsFirstInput = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            backHandle();
        }
        return true;
    }

    /**
     * 处理按钮返回键
     */
    private void backHandle() {
        getIntent().putExtra(GConstant.IS_GESTURE_OPEN_LOCK,isUseOpenLock);
        setResult(RESULT_OK,getIntent());
        finish();
    }

    @Override
    public void checkedSuccess() {
    }

    @Override
    public void checkedFail() {
    }

    /**
     * 判断是否是有效密码
     * @param inputPassword 输入的密码
     * @return true表示有效，否则无效
     */
    private boolean isInputPassValidate(String inputPassword) {
        if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
            return false;
        }
        return true;
    }
}

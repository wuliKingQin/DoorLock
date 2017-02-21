package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;

import com.lisijun.gesture.activity.VerifyGesturePassActivity;
import com.lisijun.gesture.interfaces.GConstant;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.PreferencesUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.util.PassUtil;

/**
 * 描述：启动页
 * 创建作者：黎丝军
 * 创建时间：2016/9/28 17:30
 */

public class LaunchActivity extends AbsBaseActivity {

    //流动时间差
    private final static long RUN_TIME = 1000;
    //定时器时间,默认是3秒
    private final static long COUNT_DOWN_TIME = 3 * RUN_TIME;
    //用于定时器
    private CountDownTimer mCountDownTimer;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_launch);
    }

    @Override
    public void findViews() {
        ActivityCompat.requestPermissions(this,getPermissions(),REQUEST_CODE);
    }

    @Override
    public void initObjects() {
        mCountDownTimer = new CountDownTimer(COUNT_DOWN_TIME,RUN_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if(isLogin()) {
                    if(PassUtil.isGestureOpenLock()) {
                        startGesturePass();
                    } else {
                        startActivity(MainActivity.class);
                    }
                } else {
                    startActivity(LoginActivity.class);
                }
                finish();
            }
        };
    }

    /**
     * 启动手势密码
     */
    private void startGesturePass() {
        final String pass = PreferencesUtils.getString(GConstant.GESTURE_PASS,"");
        final Intent intent = new Intent(getBaseContext(),VerifyGesturePassActivity.class);
        intent.putExtra(GConstant.GESTURE_PASS,pass);
        intent.putExtra(VerifyGesturePassActivity.FORGET_ACTIVITY_CLASS, ResetPassActivity.class);
        intent.putExtra(VerifyGesturePassActivity.MAIN_ACTIVITY_CLASS,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mCountDownTimer.start();
    }

    /**
     * 判断是用户是否登录
     * @return 如果用户登录则返回true,否则返回false
     */
    private boolean isLogin() {
        return LoginUtil.isLogin();
    }

    @Override
    public void setListeners() {
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
        mCountDownTimer = null;
    }
}

package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.saiyi.framework.activity.AbsBaseActivity;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：输入信息界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 16:11
 */

public class InputInfoActivity extends AbsBaseActivity {

    //输入界面标题
    public final static String TITLE = "title";
    //输入提示
    public final static String INPUT_HINT = "input_hint";

    private EditText mInputInfoEdt;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_input_info);
    }

    @Override
    public void findViews() {
        mInputInfoEdt = getViewById(R.id.edt_input);
    }

    @Override
    public void initObjects() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(getIntent().getStringExtra(TITLE));
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mInputInfoEdt.setHint(getIntent().getStringExtra(INPUT_HINT));
    }

    @Override
    public void setListeners() {
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

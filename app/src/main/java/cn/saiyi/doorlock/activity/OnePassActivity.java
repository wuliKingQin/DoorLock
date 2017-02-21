package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.saiyi.framework.activity.AbsBaseActivity;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.PassUtil;
import cn.saiyi.doorlock.util.TimeUtil;

/**
 * 描述：一次性密码界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 15:22
 */

public class OnePassActivity extends AbsBaseActivity {

    //时间
    private TextView mCreateTimeTv;
    //密码
    private TextView mCreatePassTv;
    //显示密码
    private CheckBox mShowPassCkb;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_one_pass);
    }

    @Override
    public void findViews() {
        mCreateTimeTv = getViewById(R.id.tv_show_create_time);
        mCreatePassTv = getViewById(R.id.tv_show_create_pass);
        mShowPassCkb = getViewById(R.id.ckb_look_pass);
    }

    @Override
    public void initObjects() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.control_one_pass);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        final String deviceMac = getIntent().getStringExtra(Constant.WIFI_MAC);
        mCreatePassTv.setText(PassUtil.getOnePass(deviceMac));
        mCreateTimeTv.setText(PassUtil.getOnePassCreateTime(deviceMac));
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mShowPassCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int inputType;
                if(isChecked) {
                    inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                } else {
                    inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                }
                mCreatePassTv.setInputType(inputType);
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

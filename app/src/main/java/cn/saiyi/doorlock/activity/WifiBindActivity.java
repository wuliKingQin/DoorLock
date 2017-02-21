package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.NetUtil;

/**
 * 描述：wifi绑定界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 17:26
 */

public class WifiBindActivity extends AbsBaseActivity {

    //设备名
    public static final String DEVICE_NAME = "deviceName";
    //wifi名字
    private EditText mWifiNameEdt;
    //wifi密码
    private EditText mWifiPasswordEdt;
    //连接wifi按钮
    private Button mConnectWifiBtn;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_wifi_bind);
    }

    @Override
    public void findViews() {
        mWifiNameEdt = getViewById(R.id.edt_wifi_name);
        mWifiPasswordEdt = getViewById(R.id.edt_wifi_password);
        mConnectWifiBtn = getViewById(R.id.btn_connect_wifi);
    }

    @Override
    public void initObjects() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.add_device_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mWifiNameEdt.setText(NetUtil.getSSID(this));
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mConnectWifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiConfigHandle();
            }
        });
    }

    /**
     * wifi配置
     */
    private void wifiConfigHandle() {
        final String wifiName = mWifiNameEdt.getText().toString().trim();
        final String wifiPassword = mWifiPasswordEdt.getText().toString().trim();
        if(!TextUtils.isEmpty(wifiName) && !TextUtils.isEmpty(wifiPassword)) {
            final Intent intent = new Intent(this,DeviceConnectActivity.class);
            intent.putExtra("wifiName",wifiName);
            intent.putExtra("wifiPass",wifiPassword);
            intent.putExtra(DEVICE_NAME,getIntent().getStringExtra(DEVICE_NAME));
            startActivity(intent);
        } else {
            ToastUtils.toast(this,"wifi输入信息不全");
        }
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

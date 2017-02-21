package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lisijun.websocket.socket.OnSocketMsgListener;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.device.Device;
import cn.saiyi.doorlock.device.IFunc;
import cn.saiyi.doorlock.device.ISendCallback;
import cn.saiyi.doorlock.device.Result;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.util.DecodeUtil;
import cn.saiyi.doorlock.util.EncodeUtil;
import cn.saiyi.doorlock.util.EncryptUtil;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：时效密码界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/15 15:56
 */

public class AgingPassActivity extends AbsBaseActivity
        implements OnSocketMsgListener{

    //保存开始日期
    private String mStartMonth;
    private String mStartDay;
    private String mStartHour;
    private String mStartMinute;
    //保存结束日期
    private String mEndMonth;
    private String mEndDay;
    private String mEndHour;
    private String mEndMinute;
    //日期选择
    private TextView mDateTv;
    //时间选择
    private TextView mTimeTv;
    //密码输入
    private EditText mPassEdt;
    //创建密码
    private Button mCreateBtn;
    //取消密码
    private Button mCancelBtn;
    //设备实例
    private Device mDevice;
    //存储临时密码
    private String mAgingPass;
    //保存设备管理员密码
    private String mAdminPass;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_aging_pass);
    }

    @Override
    public void findViews() {
        mDateTv = getViewById(R.id.tv_aging_pass_date);
        mTimeTv = getViewById(R.id.tv_aging_pass_time);
        mPassEdt = getViewById(R.id.edt_aging_pass_password);
        mCreateBtn = getViewById(R.id.btn_aging_pass_create);
        mCancelBtn = getViewById(R.id.btn_aging_pass_cancel);
    }

    @Override
    public void initObjects() {
        mDevice = new Device();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.control_aging_pass);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mDevice.setDeviceMac(getIntent().getStringExtra(Constant.WIFI_MAC));
        mAdminPass = getIntent().getStringExtra(Constant.DEVICE_PASS);
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(AgingPassActivity.this,DateSelectedActivity.class);
                intent.putExtra("isStartDate",true);
                startActivityForResult(intent,12);
            }
        });
        mTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStartMonth != null) {
                    final Intent intent = new Intent(AgingPassActivity.this,DateSelectedActivity.class);
                    intent.putExtra("isStartDate",false);
                    intent.putExtra("startMonth",mStartMonth);
                    intent.putExtra("startDay",mStartDay);
                    intent.putExtra("startHour",mStartHour);
                    intent.putExtra("startMinute",mStartMinute);
                    startActivityForResult(intent,12);
                } else {
                    ToastUtils.toast(getBaseContext(),"请先选择开始日期");
                }
            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAgingPassHandle();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDevice.registerMsgListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 12:
                if(resultCode ==  21) {
                    mStartMonth = data.getStringExtra("startMonth");
                    mStartDay = data.getStringExtra("startDay");
                    mStartHour = data.getStringExtra("startHour");
                    mStartMinute = data.getStringExtra("startMinute");
                    if(mStartMonth != null) {
                        final String hour = (Integer.valueOf(mStartHour) < 10 &&
                                Integer.valueOf(mStartHour) > 0) ? "0" + mStartHour : mStartHour;
                        final String minute = (Integer.valueOf(mStartMinute) < 10 &&
                                Integer.valueOf(mStartMinute) > 0) ? "0" + mStartMinute : mStartMinute;
                        mDateTv.setText(mStartMonth + mStartDay + "\t" + hour + ":" + (TextUtils.equals(minute,"0") ? "00" :minute));
                        mTimeTv.setText("");
                    }
                } else if(resultCode == 12) {
                    mEndMonth = data.getStringExtra("endMonth");
                    mEndDay = data.getStringExtra("endDay");
                    mEndHour = data.getStringExtra("endHour");
                    mEndMinute = data.getStringExtra("endMinute");
                    if(TextUtils.equals(mStartMonth,mEndMonth) && TextUtils.equals(mStartDay,mEndDay) &&
                            TextUtils.equals(mStartHour,mEndHour) && TextUtils.equals(mStartMinute,mEndMinute)) {
                        ToastUtils.toast(this,"选择的时间不能重复，请重新选择");
                    } else {
                        if(mEndMonth != null) {
                            final String hour = (Integer.valueOf(mEndHour) < 10 &&
                                    Integer.valueOf(mEndHour) > 0) ? "0" + mEndHour : mEndHour;
                            final String minute = (Integer.valueOf(mEndMinute) < 10 &&
                                    Integer.valueOf(mEndMinute) > 0) ? "0" + mEndMinute : mEndMinute;
                            mTimeTv.setText(mEndMonth + mEndDay + "\t" + hour + ":" + (TextUtils.equals(minute,"0") ? "00" :minute));
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 生成时效密码处理
     */
    private void createAgingPassHandle() {
        final String dateStr = mDateTv.getText().toString().trim();
        final String timeStr = mTimeTv.getText().toString().trim();
        mAgingPass = mPassEdt.getText().toString().trim();
        if(!TextUtils.isEmpty(dateStr)) {
            if(!TextUtils.isEmpty(timeStr)) {
                if(!TextUtils.isEmpty(mAgingPass)) {
                    if(mAgingPass.length() >= 8) {
                        ProgressUtils.showDialog(this,"正在设置中，……",true,null);
                        sendEncryptHandle();
                    } else {
                        ToastUtils.toast(this,"密码长度必须等于8位");
                    }
                } else  {
                    ToastUtils.toast(this,"您还没有输入密码");
                }
            } else {
                ToastUtils.toast(this,"您还没选择时间");
            }
        } else {
            ToastUtils.toast(this,"您还没有选择日期");
        }
    }

    /**
     * 发送加密请求
     */
    private void sendEncryptHandle() {
        mDevice.sendCmd(IFunc.REQUEST_KEY,new byte[]{IFunc.SETTING_AGING_PASS}, new ISendCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                ToastUtils.toast(getBaseContext(),"生成时效密码失败");
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 发送时效密码处理
     */
    private void sendAgingPassHandle(byte k1,byte k2) {
        final byte[] data = new byte[8];
        data[0] = (byte)getSplitDate(mStartMonth);
        data[1] = (byte)getSplitDate(mStartDay);
        data[2] = (byte)getSplitDate(mEndMonth);
        data[3] = (byte)getSplitDate(mEndDay);
        data[4] = (byte)((int)Integer.valueOf(mStartHour));
        data[5] = (byte)((int)Integer.valueOf(mStartMinute));
        data[6] = (byte)((int)Integer.valueOf(mEndHour));
        data[7] = (byte)((int)Integer.valueOf(mEndMinute));
        final byte[] eAccountPass = EncryptUtil.encrypt(k1,k2,EncodeUtil.strToBytes(mAdminPass));
        final byte[] eAgingPass = EncryptUtil.encrypt(k1,k2,EncodeUtil.strToBytes(mAgingPass));
        byte[] eMergeArray = EncryptUtil.mergeArray(eAccountPass,eAgingPass);
        eMergeArray = EncryptUtil.mergeArray(eMergeArray,data);
        LogUtils.d(DecodeUtil.bytesToHexStr(eMergeArray));
        mDevice.sendCmd(IFunc.SETTING_AGING_PASS,eMergeArray, new ISendCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int errorCode, String errorInfo) {
                ToastUtils.toast(getBaseContext(),"生成时效密码失败");
                ProgressUtils.dismissDialog();
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    /**
     * 获取月份
     * @param date 月份
     * @return 得到日期
     */
    private int getSplitDate(String date) {
        return Integer.valueOf(date.substring(0,date.length() - 1));
    }

    @Override
    public void onReceiveMsg(String deviceName, String mac, String cmd) {
        final byte[] result = EncodeUtil.hexStrToBytes(cmd.toUpperCase());
        switch (result[4]) {
            case IFunc.REQUEST_KEY:
                if(result[6] == IFunc.SETTING_AGING_PASS) {
                    sendAgingPassHandle(result[7],result[8]);
                }
                break;
            case IFunc.SETTING_AGING_PASS:
                final Result result1 = new Result() {
                    @Override
                    public void onSuccess(String hintInfo) {
                        ToastUtils.toast(getBaseContext(),"生成时效密码成功");
                        finish();
                    }

                    @Override
                    protected void onFail(String failInfo) {
                        ToastUtils.toast(getBaseContext(),"生成时效密码失败");
                    }
                };
                result1.resultHandle(result[6]);
                ProgressUtils.dismissDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.unregisterMsgListener(this);
    }
}

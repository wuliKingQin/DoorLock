package cn.saiyi.doorlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.saiyi.framework.activity.AbsBaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.view.PickerView;

/**
 * 描述：时间选择界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/17 11:36
 */

public class TimeSelectedActivity extends AbsBaseActivity {
    //当前小时
    private int mCurrentHour;
    //当前分钟
    private int mCurrentMinute;
    //开始选择月
    private String mStartHour;
    //开始选择天
    private String mStartMinute;
    //结束的月
    private String mEndHour;
    //结束的天
    private String mEndMinute;
    //开始选择前缀时间
    private PickerView mDateStartView;
    //后缀时间
    private PickerView mDateEndView;
    //日历
    private Calendar mCalendar;
    //是否点击了一下步
    private boolean isClickNext = false;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_date_or_time);
    }

    @Override
    public void findViews() {
        mDateStartView = getViewById(R.id.pv_start);
        mDateEndView = getViewById(R.id.pv_end);
    }

    @Override
    public void initObjects() {
        mCalendar = Calendar.getInstance();

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle("选择时间");
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(R.color.color13);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        actionBar.setRightButtonText("下一步");
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute =  mCalendar.get(Calendar.MINUTE);

        mStartHour = String.valueOf(mCurrentHour);
        mStartMinute =String.valueOf(mCurrentMinute);

        initPickerView();
    }

    /**
     * 初始化时间选择器数据
     */
    private void initPickerView() {
        mDateStartView.setData(getHour(mCurrentHour));
        mDateEndView.setData(getMinute(mCurrentMinute));
        mDateStartView.setSelected(mStartHour);
        mDateEndView.setSelected(mStartMinute);
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClickNext) {
                    finish();
                } else {
                    isClickNext = false;
                    initPickerView();
                    actionBar.setRightButtonText("下一步");
                }
            }
        });
        actionBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals("下一步",((TextView)v).getText().toString().trim())) {
                    isClickNext = true;
                    mDateStartView.setData(getHour(getIntTime(mStartHour)));
                    mDateEndView.setData(getMinute(getIntTime(mStartMinute)));
                    mDateStartView.setSelected(mStartHour);
                    mDateEndView.setSelected(mStartHour);
                    mEndHour = mStartHour;
                    mEndMinute = mStartHour;
                    actionBar.setRightButtonText("完成");
                } else {
                    final Intent intent = new Intent();
                    intent.putExtra("time",mStartHour + ":" + mStartMinute + "~" + mEndHour + ":" + mEndMinute);
                    setResult(12,intent);
                    finish();
                }
            }
        });
        mDateStartView.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text) {
                if(!isClickNext) {
                    mStartHour = text;
                    if(getIntTime(text) == mCurrentHour) {
                        mDateEndView.setData(getMinute(getIntTime(mStartMinute)));
                    } else {
                        mDateEndView.setData(getMinute(0));
                    }
                } else {
                    mEndHour = text;
                    if(getIntTime(text) == getIntTime(mStartHour)) {
                        mDateEndView.setData(getMinute(getIntTime(mStartMinute)));
                    } else {
                        mDateEndView.setData(getMinute(0));
                    }
                }
            }
        });
        mDateEndView.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text) {
                if(!isClickNext) {
                    mStartMinute = text;
                } else {
                    mEndMinute = text;
                }
            }
        });

    }

    /**
     * 获取小时
     * @return 小时数据集
     */
    private List<String> getHour(int selectedHour) {
        final List<String>  hourData = new ArrayList<>();
        int index = selectedHour;
        for(;index < 24;index ++) {
            if(index == 0) {
                hourData.add("00" + "时");
            } else {
                hourData.add(index + "时");
            }
        }
        return hourData;
    }


    /**
     * 获取分钟
     * @return 返回分钟数据集
     */
    private List<String> getMinute(int selectedMinute) {
        final List<String>  minuteData = new ArrayList<>(1);
        int index = selectedMinute;
        for(;index < 60;index ++) {
            if(index == 0) {
                minuteData.add("00"+ "分");
            } else {
                minuteData.add(index + "分");
            }
        }
        return minuteData;
    }

    /**
     * 获取时间
     * @param time 时间
     * @return 得到时间
     */
    private int getIntTime(String time) {
        if(time.equals("00")) {
            return 0;
        }
        return Integer.valueOf(time);
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

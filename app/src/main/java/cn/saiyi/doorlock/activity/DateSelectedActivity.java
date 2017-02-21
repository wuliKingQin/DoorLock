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
 * 描述：日期选择界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/15 16:41
 */

public class DateSelectedActivity extends AbsBaseActivity {

    //当月
    private int mCurrentMonth;
    //当天
    private int mCurrentDay;
    //开始选择月
    private String mStartMonth;
    //开始选择天
    private String mStartDay;
    //结束的月
    private String mEndMonth;
    //结束的天
    private String mEndDay;
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
    //判断是否是开始日期选择
    private boolean isStartDate = true;

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
        isStartDate = getIntent().getBooleanExtra("isStartDate",true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle("选择日期");
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_white_back,25,25);
        actionBar.setRightButtonText("下一步");
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        if(isStartDate) {
            mCurrentMonth = mCalendar.get(Calendar.MONTH) + 1;
            mCurrentDay =  mCalendar.get(Calendar.DAY_OF_MONTH);
            mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            mCurrentMinute = mCalendar.get(Calendar.MINUTE);

            mStartMonth = mCurrentMonth + "月";
            mStartDay = mCurrentDay + "日";
            mStartHour = String.valueOf(mCurrentHour) + "时";
            mStartMinute =String.valueOf(mCurrentMinute) + "分";
            initPickerView(mCurrentMonth,mCurrentDay,mStartMonth,mStartDay);
        } else {
            mStartMonth = getIntent().getStringExtra("startMonth");
            mStartDay = getIntent().getStringExtra("startDay");
            mStartHour = getIntent().getStringExtra("startHour");
            mStartMinute = getIntent().getStringExtra("startMinute");
            mCurrentMonth = getSplitDate(mStartMonth);
            mCurrentDay = getSplitDate(mStartDay);
            mEndMonth = mStartMonth;
            mEndDay = mStartDay;
            mEndHour = mStartHour;
            mEndMinute = mStartMinute;
            mCurrentHour = getIntTime(mStartHour);
            mCurrentMinute = getIntTime(mStartMinute);
            initPickerView(mCurrentMonth,mCurrentDay,mEndMonth,mEndDay);
        }
    }

    /**
     * 初始化时间选择器数据
     */
    private void initPickerView(int currentMonth,int currentDay,String startMonth,String startDay) {
        mDateStartView.setData(getMonth(currentMonth));
        mDateEndView.setData(getDay(currentMonth,currentDay));
        mDateStartView.setSelected(startMonth);
        mDateEndView.setSelected(startDay);
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
                    initPickerView(mCurrentMonth,mCurrentDay,mStartMonth,mStartDay);
                    actionBar.setRightButtonText("下一步");
                }
            }
        });
        actionBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals("下一步",((TextView)v).getText().toString().trim())) {
                    isClickNext = true;
                    if(isStartDate) {
                        if(getSplitDate(mStartDay) == mCurrentDay) {
                            mDateStartView.setData(getHour(mCurrentHour));
                            mDateEndView.setData(getMinute(mCurrentMinute));
                            mDateStartView.setSelected(mStartHour);
                            mDateEndView.setSelected(mStartMinute);
                        } else {
                            mDateStartView.setData(getHour(0));
                            mDateEndView.setData(getMinute(0));
                            mDateStartView.setSelected(mStartHour);
                            mDateEndView.setSelected(mStartMinute);
                        }

                    } else {
                        if(getSplitDate(mEndDay) == mCurrentDay) {
                            mDateStartView.setData(getHour(mCurrentHour));
                            mDateEndView.setData(getMinute(mCurrentMinute));
                            mDateStartView.setSelected(mEndHour);
                            mDateEndView.setSelected(mEndMinute);
                        } else {
                            mDateStartView.setData(getHour(0));
                            mDateEndView.setData(getMinute(0));
                            mDateStartView.setSelected(mEndHour);
                            mDateEndView.setSelected(mEndMinute);
                        }
                    }
                    actionBar.setRightButtonText("完成");
                } else {
                    final Intent intent = new Intent();
                    if(isStartDate) {
                        intent.putExtra("startMonth",mStartMonth);
                        intent.putExtra("startDay",mStartDay);
                        intent.putExtra("startHour",getSelectTime(mStartHour));
                        intent.putExtra("startMinute",getSelectTime(mStartMinute));
                        setResult(21,intent);
                    } else {
                        intent.putExtra("endMonth",mEndMonth);
                        intent.putExtra("endDay",mEndDay);
                        intent.putExtra("endHour",getSelectTime(mEndHour));
                        intent.putExtra("endMinute",getSelectTime(mEndMinute));
                        setResult(12,intent);
                    }
                    finish();
                }
            }
        });
        mDateStartView.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text) {
                if(!isClickNext) {
                    if(isStartDate) {
                        mStartMonth = text;
                        if(getSplitDate(text) == mCurrentMonth) {
                            mDateEndView.setData(getDay(getSplitDate(text),mCurrentDay));
                        } else {
                            mDateEndView.setData(getDay(getSplitDate(text),1));
                        }
                    } else {
                        mEndMonth = text;
                        if(getSplitDate(text) == getSplitDate(mStartMonth)) {
                            mDateEndView.setData(getDay(getSplitDate(text),getSplitDate(mStartDay)));
                        } else {
                            mDateEndView.setData(getDay(getSplitDate(text),1));
                        }
                    }
                } else {
                    if(isStartDate) {
                        mStartHour = text;
                        if(getIntTime(text) == mCurrentHour && getSplitDate(mStartDay) == mCurrentDay) {
                            mDateEndView.setData(getMinute(mCurrentMinute));
                        } else {
                            mDateEndView.setData(getMinute(0));
                        }
                    } else {
                        mEndHour = text;
                        if(getIntTime(text) == getIntTime(mStartHour) && getSplitDate(mEndDay) == mCurrentDay) {
                            mDateEndView.setData(getMinute(getIntTime(mStartMinute)));
                        } else {
                            mDateEndView.setData(getMinute(0));
                        }
                    }
                }
            }
        });
        mDateEndView.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(String text) {
                if(!isClickNext) {
                    if(isStartDate) {
                        mStartDay = text;
                    } else {
                        mEndDay = text;
                    }
                } else {
                    if(isStartDate) {
                        mStartMinute = text;
                    } else {
                        mEndMinute = text;
                    }
                }
            }
        });

    }

    /**
     * 获取月
     * @return 月数据集
     */
    private List<String> getMonth(int selectedMonth) {
        final List<String>  monthData = new ArrayList<>();
        int index = selectedMonth;
        for(;index < 13;index ++) {
            monthData.add(index+ "月");
        }
        return monthData;
    }


    /**
     * 获取天
     * @return 返回天数据集
     */
    private List<String> getDay(int selectedMonth,int selectedDay) {
        final List<String>  dayData = new ArrayList<>(1);
        int index = selectedDay;
        int maxDay = getMaxDay(selectedMonth);
        for(;index <= maxDay;index ++) {
            dayData.add(index  + "日");
        }
        return dayData;
    }

    /**
     * 获取最大天的，某月的
     * @param month 月
     * @return 最大天
     */
    private int getMaxDay(int month) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取月份
     * @param date 月份
     * @return 得到日期
     */
    private int getSplitDate(String date) {
        try {
            return Integer.valueOf(date.substring(0,date.length() - 1));
        } catch (Exception e) {
            return Integer.valueOf(date);
        }
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
                hourData.add("0" + "时");
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
                minuteData.add("0" + "分");
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
        return getSplitDate(time);
    }

    /**
     * 获取选择的时间
     * @param time 时间
     * @return 时间字符串
     */
    private String getSelectTime(String time) {
        return String.valueOf(getSplitDate(time));
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

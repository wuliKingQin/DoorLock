package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.adapter.OpenLockRecordAdapter;
import cn.saiyi.doorlock.bean.OpenLockRecordBean;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;
import cn.saiyi.doorlock.util.TimeUtil;

/**
 * 描述：开锁记录界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 17:35
 */

public class OpenLockRecordActivity extends AbsBaseActivity {

    //设备mac
    private String mDeviceMac;
    //没有数据视图
    private View mNoDataView;
    //开锁记录适配器
    private RecyclerView mOpenLockRcv;
    //开锁记录列表适配器
    private OpenLockRecordAdapter mAdapter;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_open_lock_record);
    }

    @Override
    public void findViews() {
        mNoDataView = getViewById(R.id.tv_open_no_data);
        mOpenLockRcv = getViewById(R.id.rcy_open_lock);
    }

    @Override
    public void initObjects() {
        mDeviceMac = getIntent().getStringExtra(Constant.WIFI_MAC);
        mAdapter = new OpenLockRecordAdapter(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.record_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        actionBar.setRightButtonText("清空");
        actionBar.setRightButtonTextColor(R.color.color7);

        mOpenLockRcv.setLayoutManager(new LinearLayoutManager(this));
        mOpenLockRcv.setAdapter(mAdapter);

        requestRecordData();
    }

    /**
     * 请求记录数据方法
     */
    private void requestRecordData() {
        HttpRequest.get(URL.OPEN_LOCK_RECORD + mDeviceMac,new BaseHttpRequestCallback<JSONArray>(){
                    @Override
                    protected void onSuccess(JSONArray jsonArray) {
                        try {
                            int index = 0;
                            String date;
                            String headIcon;
                            JSONObject jsonItem;
                            OpenLockRecordBean recordBean;
                            List<OpenLockRecordBean> data = new ArrayList<>();
                            for(;index < jsonArray.size();index ++) {
                                recordBean = new OpenLockRecordBean();
                                jsonItem = jsonArray.getJSONObject(index);
                                if(jsonItem != null) {
                                    headIcon = jsonItem.getString("headimg");
                                    if(headIcon == null) headIcon = LoginUtil.getHeadUrl();
                                    recordBean.setDescribe(jsonItem.getString("tname") + "开锁");
                                    recordBean.setHeadUrl(headIcon);
                                    recordBean.setTypeIconUrl(jsonItem.getString("typeid"));
                                    date = jsonItem.getString("date");
                                    recordBean.setDate(TimeUtil.getTime(date,"yyyy-MM-dd"));
                                    recordBean.setTime(TimeUtil.getTime(date,"HH:mm"));
                                    recordBean.setName(jsonItem.getString("tname"));
                                    data.add(recordBean);
                                }
                            }
                            mAdapter.setListData(data);
                        }catch (Exception e) {
                        }
                    }
                }
        );
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecordHandle();
            }
        });
        mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    /**
     * 清除记录处理方法
     */
    private void clearRecordHandle() {
        final JSONParam jsonParam = new JSONParam();
        jsonParam.putJSONParam("mac",mDeviceMac);
        jsonParam.putJSONParam("phone", LoginUtil.getAccount());
        ProgressUtils.showDialog(this,"正在清除中，……",null);
        HttpRequest.post(URL.CLEAR_OPEN_LOCK_RECORD,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
               try {
                    final int result = jsonObject.getInteger("result");
                    if(result == 1) {
                        requestRecordData();
                        ToastUtils.toast(getBaseContext(),"清除完成");
                    } else {
                        onFailure(result,null);
                    }
                } catch (Exception e){
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getBaseContext(),"清除开锁记录失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }

    //数据列表变化监听
    private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            updateView(mAdapter.getItemCount());
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateView(itemCount);
        }

        /**
         * 视图更新
         * @param itemCount 列表个数
         */
        private void updateView(int itemCount) {
            if(itemCount > 0) {
                mNoDataView.setVisibility(View.GONE);
                mOpenLockRcv.setVisibility(View.VISIBLE);
            } else {
                mNoDataView.setVisibility(View.VISIBLE);
                mOpenLockRcv.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

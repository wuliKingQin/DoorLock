package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;

/**
 * 描述：建议界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/20 15:50
 */

public class OpinionActivity extends AbsBaseActivity {

    //建议输入框
    private EditText mOpinionEdt;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_opinion);
    }

    @Override
    public void findViews() {
        mOpinionEdt = getViewById(R.id.edt_opinion);
    }

    @Override
    public void initObjects() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.opinion_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        actionBar.setRightButtonText("发送");
        actionBar.setRightButtonTextColor(R.color.color7);
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
                sendOpinionHandle();
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    /**
     * 发送意见处理方法
     */
    private void sendOpinionHandle() {
        final String opinionStr = mOpinionEdt.getText().toString().trim();
        if(!TextUtils.isEmpty(opinionStr)) {
            ProgressUtils.showDialog(this,"正在反馈中，……",true,null);
            JSONParam jsonParam = new JSONParam();
            jsonParam.putJSONParam("descriptor",opinionStr);
            HttpRequest.post(URL.OPINION_ADD,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
                @Override
                protected void onSuccess(JSONObject jsonObject) {
                    try {
                        final int result = jsonObject.getInteger("result");
                        if(result == 1) {
                            ToastUtils.toast(getBaseContext(),"反馈成功");
                            finish();
                        } else {
                            onFailure(-1,null);
                        }
                    }catch (Exception e) {
                        onFailure(-1,null);
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    ToastUtils.toast(getBaseContext(),"反馈失败");
                }

                @Override
                public void onFinish() {
                    ProgressUtils.dismissDialog();
                }
            });
        } else {
            ToastUtils.toast(this,"请输入您的意见后再发送");
        }
    }
}

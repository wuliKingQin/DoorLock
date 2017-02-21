package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.PhoneUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.http.JSONParam;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：修改界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/20 16:04
 */

public class ModifyActivity extends AbsBaseActivity {

    //用于传送修改标题
    public final static String MODIFY_TITLE = "modifyTitle";
    //用于传送修改类型
    public final static String MODIFY_WHAT = "modifyWhat";
    //修改信息
    private EditText mModifyEdt;
    //标题
    private String mTitle;
    //修改什么
    private int mModifyWhat;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_modify);
    }

    @Override
    public void findViews() {
        mModifyEdt = getViewById(R.id.edt_modify_info);
    }

    @Override
    public void initObjects() {
        mTitle = getIntent().getStringExtra(MODIFY_TITLE);
        mModifyWhat = getIntent().getIntExtra(MODIFY_WHAT,0);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(mTitle);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        actionBar.setRightButtonText(R.string.finish);
        actionBar.setRightButtonTextColor(R.color.color7);

        hintHandle();
    }

    /**
     * 提示处理方法
     */
    private void hintHandle() {
        mModifyEdt.setHint("输入您的" + mTitle);
        if(mModifyWhat == 2)  {
            final InputFilter[] filters = {new InputFilter.LengthFilter(11)};
            mModifyEdt.setFilters(filters);
            mModifyEdt.setInputType(InputType.TYPE_CLASS_PHONE);
        }
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
                modifyHandle();
            }
        });
    }

    /**
     * 修改处理方法
     */
    private void modifyHandle() {
        final String modifyStr = mModifyEdt.getText().toString().trim();
        if(!TextUtils.isEmpty(modifyStr)) {
            final JSONParam jsonParam = new JSONParam();
            jsonParam.putJSONParam("phone", LoginUtil.getAccount());
            switch (mModifyWhat) {
                case 0:
                    jsonParam.putJSONParam("name", modifyStr);
                    break;
                case 1:
                    jsonParam.putJSONParam("address", modifyStr);
                    break;
                case 2:
                    if(!PhoneUtils.isPhone(modifyStr)) {
                        ToastUtils.toast(getBaseContext(),"请输入正确的手机号");
                        return;
                    }
                    jsonParam.putJSONParam("newphone", modifyStr);
                    break;
                default:
                    break;
            }
            ProgressUtils.showDialog(this,"正在修改中，……",null);
            HttpRequest.post(URL.MODIFY_USER_INFO,jsonParam.getJSONParam(),new BaseHttpRequestCallback<JSONObject>() {
                @Override
                protected void onSuccess(JSONObject jsonObject) {
                    try {
                        final int result = jsonObject.getInteger("result");
                        if(result == 1) {
                            setResult(RESULT_OK,getIntent());
                            finish();
                            ToastUtils.toast(getBaseContext(),"修改信息成功");
                        } else {
                            onFailure(result,"修改信息失败");
                        }
                    } catch (Exception e) {
                        onFailure(-1,"修改信息失败");
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    ToastUtils.toast(getBaseContext(),msg);
                }

                @Override
                public void onFinish() {
                    ProgressUtils.dismissDialog();
                }
            });
        } else {
            ToastUtils.toast(this,"您先填写修改信息后再按完成");
        }
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

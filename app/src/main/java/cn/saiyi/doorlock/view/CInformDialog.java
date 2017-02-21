package cn.saiyi.doorlock.view;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.saiyi.framework.util.ResourcesUtils;

import cn.saiyi.doorlock.R;

/**
 * 文件描述：提示工具视图
 * 创建作者：黎丝军
 * 创建时间：16/8/5 PM4:12
 */
public class CInformDialog {

    //弹出框
    private AlertDialog mDialog = null;
    //视图
    private View mDialogView = null;
    //提示标题
    private TextView mTitle = null;
    //提示内容信息
    private TextView mInformInfo = null;
    //输入信息
    private EditText mInputInfo = null;
    //确定按钮
    private Button mSureBtn = null;
    //取消按钮
    private Button mCancelBtn = null;

    public CInformDialog() {
    }

    /**
     * 创建Dialog
     * @param context 运行环境
     */
    public void create(Context context) {
        mDialogView = ResourcesUtils.findViewById(context, R.layout.dialog_inform);
        mTitle = (TextView) mDialogView.findViewById(R.id.tv_inform_title);
        mInformInfo = (TextView) mDialogView.findViewById(R.id.tv_inform_info);
        mInputInfo = (EditText) mDialogView.findViewById(R.id.edt_modify);
        mSureBtn = (Button) mDialogView.findViewById(R.id.btn_inform_sure);
        mCancelBtn = (Button) mDialogView.findViewById(R.id.btn_inform_cancel);

        mDialog = new AlertDialog.Builder(context).create();
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setView(new EditText(context));

        mSureBtn.setOnClickListener(mCancelListener);
        mCancelBtn.setOnClickListener(mCancelListener);
    }

    /**
     * 显示弹出框
     */
    public void showDialog() {
        showDialog(null);
    }

    /**
     * 显示弹出框
     * @param title 标题
     */
    public void showDialog(String title) {
        showDialog(title,null);
    }

    /**
     * 显示弹出框
     * @param title 标题
     * @param content 内容
     */
    public void showDialog(String title, String content) {
        showDialog(title,content,null);
    }

    /**
     * 显示弹出框
     * @param title 标题
     * @param sureButtonListener 确认监听器
     */
    public  void showDialog(String title, String content, View.OnClickListener sureButtonListener) {
        if(!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        if(!TextUtils.isEmpty(content)) {
            mInformInfo.setText(content);
        }
        if(sureButtonListener != null) {
            mSureBtn.setOnClickListener(sureButtonListener);
        }
        mDialog.show();
        mDialog.setContentView(mDialogView);
    }

    /**
     * 获取输入的后值
     * @return 返回输入的内容
     */
    public String getInputText() {
        return mInputInfo.getText().toString().trim();
    }

    /**
     * 设置确定按钮监听器
     * @param listener 监听器
     */
    public void setSureButtonListener(View.OnClickListener listener) {
        mSureBtn.setOnClickListener(listener);
    }

    /**
     * 设置取消按钮监听器
     * @param listener
     */
    public void setCancelButtonListener(View.OnClickListener listener) {
        mCancelBtn.setOnClickListener(listener);
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public void setDialogTitle(String title) {
        if(!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    /**
     * 设置模式，模式只有两种，提示模式和输入模式
     * @param model INFORM or INPUT
     */
    public void setModel(Model model) {
        if(model == Model.INFORM) {
            mInputInfo.setVisibility(View.INVISIBLE);
            mInformInfo.setVisibility(View.VISIBLE);
        } else {
            mInputInfo.setVisibility(View.VISIBLE);
            mInformInfo.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置输入框信息
     * @param text 文本
     */
    public void setInputText(String text) {
        if(text != null) {
            mInputInfo.setText(text);
        }
    }

    /**
     * 蓝牙
     * @param hintText
     */
    public void setInputHintText(String hintText) {
        if(!TextUtils.isEmpty(hintText)) {
            mInputInfo.setHint(hintText);
        }
    }

    /**
     * 设置输入框类型
     * @param type 类型值
     */
    public void setInputType(int type) {
        mInputInfo.setInputType(type);
    }

    /**
     * 设置提示信息
     * @param message 提示信息
     */
    public void setDialogMessage(String message) {
        if(!TextUtils.isEmpty(message)) {
            mInformInfo.setText(message);
        }
    }

    /**
     * 设置确定按钮文本
     * @param text 文本
     */
    public  void setSureButtonText(String text) {
        mSureBtn.setText(text);
    }

    /**
     * 取消Dialog
     */
    public void cancelDialog() {
        mDialog.cancel();
    }

    /**
     * 取消按钮监听器
     */
    private View.OnClickListener mCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDialog.cancel();
        }
    };

    /**
     * 获取确定按钮Id
     * @return Integer
     */
    public int getSureButtonId() {
        return R.id.btn_inform_sure;
    }

    /**
     * 是提示模式还是输入模式
     * 默认是输入模式
     */
    public enum Model {
        /**
         * 提示模式
         */
        INFORM,
        /**
         * 输入模式
         */
        INPUT
    }
}

package cn.saiyi.doorlock.blls;
import android.content.Context;
import com.saiyi.framework.util.ToastUtils;
import java.util.regex.Pattern;
import cn.saiyi.doorlock.interfaces.IRequestCallBack;
import cn.saiyi.doorlock.other.BusinessType;

/**
 * 描述：基础业务类
 * 创建作者：黎丝军
 * 创建时间：2016/10/11 13:41
 */

public class BaseBusiness implements IBusiness {

    //回调接口类型
    private BusinessType mBusinessType;
    //运行环境
    private Context mContext;
    //请求回调监听
    private IRequestCallBack mRequestCallBack;
    //用于判断是否是手机号
    private Pattern mPhoneNumber = Pattern.compile("^((13[0-9])|(14[5,7])|17[0,6,7,8]|(15[^4,\\D])|(18[0-9]))\\d{8}$");


    public BaseBusiness(Context context) {
        mContext = context;
    }

    /**
     * 请求回调处理
     * @param type 回调类型
     * @param isSuccess 回调类型
     * @param info 回调信息
     */
    protected void requestCallBackHandle(BusinessType type, int isSuccess, Object...info) {
        if(mRequestCallBack != null) {
            if(isSuccess == IRequestCallBack.SUCCESS) {
                if(info.length == 1) {
                    mRequestCallBack.onSuccess(type,info[0]);
                } else if(info.length == 2) {
                    mRequestCallBack.onSuccess(type,info[0],String.valueOf(info[1]));
                } else if(info.length >= 3) {
                    mRequestCallBack.onSuccess(type,info[0],String.valueOf(info[1]),String.valueOf(info[2]));
                }
            } else {
                mRequestCallBack.onError(type,Integer.valueOf(String.valueOf(info[0])),String.valueOf(info[1]));
            }
        } else {
            if(isSuccess == IRequestCallBack.SUCCESS) {
                ToastUtils.toast(mContext,"request success!");
            } else {
                ToastUtils.toast(mContext,String.valueOf(info[1]));
            }
        }
    }

    @Override
    public IRequestCallBack getRequestCallBack() {
        return mRequestCallBack;
    }

    @Override
    public void setRequestCallBack(IRequestCallBack callBack) {
        mRequestCallBack = callBack;
    }

    /**
     * 设置回调接口类型，及请求类型
     * @param businessType 类型实例
     */
    public void setCallBackType(BusinessType businessType) {
        mBusinessType = businessType;
    }

    protected BusinessType getCallBackType() {
        return mBusinessType;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    /**
     * 显示toast
     * @param resId 资源Id
     */
    protected void showToast(int resId) {
        showToast(getContext().getString(resId));
    }

    /**
     * 显示弹框提示
     * @param content 内容
     */
    protected void showToast(String content) {
        ToastUtils.toast(mContext,content);
    }

    /**
     * 判断是否是手机号
     * @param phoneNum 手机号
     * @return true表示是，false表示不是
     */
    protected  boolean isPhone(String phoneNum) {
        if (phoneNum == null || phoneNum.trim().length() == 0)
            return false;
        return mPhoneNumber.matcher(phoneNum).matches();
    }
}

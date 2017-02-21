package cn.saiyi.doorlock.blls;

import android.content.Context;

import cn.saiyi.doorlock.other.BusinessType;

/**
 * 描述：重设密码业务类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 11:25
 */

public class ResetPassBusiness extends RegisterBusiness {

    public ResetPassBusiness(Context context) {
        super(context);
        setCallBackType(BusinessType.FIND_PASS);
    }
}

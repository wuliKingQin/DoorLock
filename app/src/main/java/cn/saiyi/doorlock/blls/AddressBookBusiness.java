package cn.saiyi.doorlock.blls;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.blls.AbsBaseBusiness;
import com.saiyi.framework.other.ListenersMgr;
import com.saiyi.framework.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import cn.saiyi.doorlock.bean.ContactBean;
import cn.saiyi.doorlock.interfaces.IUpdateUICallBack;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.other.URL;
/**
 * 描述：通讯录业务类
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 15:08
 */

public class AddressBookBusiness extends AbsBaseBusiness {

    @Override
    public void initObject() {

    }

    @Override
    public void initData(Bundle bundle) {

    }

    /**
     * 添加需要分享的人
     * @param activity 当前界面实例
     * @param contactList 需要分享的联系人
     */
    public void addShareAccount(final Activity activity, List<ContactBean> contactList) {
        if(contactList.size() <= 0) {
            ToastUtils.toast(getContext(),"请选择要添加分享的联系人");
        } else {
            for(ContactBean item:contactList) {
                final JSONObject jsonParam = new JSONObject();
                jsonParam.put("phone",item.getPhone());
                jsonParam.put("mac",item.getMac());
                RequestParams params = new RequestParams();
                params.applicationJson(jsonParam);
                HttpRequest.post(URL.ADD_SHARE_USER,params,new BaseHttpRequestCallback<JSONObject>() {
                    @Override
                    protected void onSuccess(JSONObject resultInfo) {
                        try {
                            final int result = resultInfo.getInteger("result");
                            if(result == 1) {
                                activity.finish();
                                ToastUtils.toast(getContext(),"添加分享成功!");
                            } else {
                                onFailure(Constant.ERROR_CODE,"添加分享用户失败");
                            }
                        } catch (Exception e) {
                            onFailure(Constant.ERROR_CODE,"服务器错误");
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String msg) {
                        if(Constant.ERROR_CODE == errorCode) {
                            ToastUtils.toast(getContext(),msg);
                        } else {
                            ToastUtils.toast(getContext(),"网络出错");
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取联系人列表
     * @return 联系人列表信息
     */
    public List<ContactBean> getContactList(List<String> existUser,String mac) {
        String name;
        String phoneNum;
        List<ContactBean> list = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, sortOrder);
        ContactBean contactBean;
        while (cursor.moveToNext()) {
            contactBean = new ContactBean();
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNum = String.valueOf(phoneNum.trim().replace(" ", "").replace("+", ""));
            contactBean.setName(name);
            contactBean.setPhone(phoneNum);
            contactBean.setMac(mac);
            contactBean.setShared(isExist(existUser,phoneNum));
            list.add(contactBean);
        }
        cursor.close();
        return list;
    }

    /**
     * 判断是否已经是分享用户
     * @param existUser 分享用户列表
     * @param phone 手机号
     * @return true表示是，false 表示不是
     */
    private boolean isExist(List<String> existUser,String phone) {
        for(String user:existUser) {
            if(TextUtils.equals(user,phone)) {
                return true;
            }
        }
        return false;
    }
}

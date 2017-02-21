package cn.saiyi.doorlock.bean;

import android.text.TextUtils;

import com.saiyi.framework.bean.BaseBean;

/**
 * 描述：开锁记录Bean
 * 创建作者：黎丝军
 * 创建时间：2016/10/8 17:57
 */

public class OpenLockRecordBean extends BaseBean {
    //头像url
    private String headUrl;
    //开锁记录描述
    private String describe;
    //开锁类型图片
    private String typeIconUrl;
    //开锁日期
    private String date;
    //开锁时间
    private String time;
    //用户名
    private String name;

    public OpenLockRecordBean() {
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTypeIconUrl() {
        return typeIconUrl;
    }

    public void setTypeIconUrl(String typeIconUrl) {
        if(TextUtils.equals(typeIconUrl,"1")) {
            this.typeIconUrl = "ic_distance";
        } else if(TextUtils.equals(typeIconUrl,"2")) {
            this.typeIconUrl = "ic_finger";
        } else if(TextUtils.equals(typeIconUrl,"3")) {
            this.typeIconUrl = "ic_distance";
        } else if(TextUtils.equals(typeIconUrl,"4")) {
            this.typeIconUrl = "ic_door_card";
        } else if(TextUtils.equals(typeIconUrl,"5")) {
            this.typeIconUrl = "ic_aging_password";
        } else if(TextUtils.equals(typeIconUrl,"6")) {
            this.typeIconUrl = "ic_one_pass";
        } else if(TextUtils.equals(typeIconUrl,"7")) {
            this.typeIconUrl = "ic_shark";
        } else {
            this.typeIconUrl = "ic_bluetooth";
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

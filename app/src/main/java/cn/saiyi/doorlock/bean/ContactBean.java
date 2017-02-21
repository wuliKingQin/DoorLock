package cn.saiyi.doorlock.bean;

import com.saiyi.framework.bean.BaseBean;

/**
 * 描述：联系人bean
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 15:12
 */

public class ContactBean extends BaseBean {

    //头像地址
    private String headIconUrl;
    //联系人的名字
    private String name;
    //联系人的电话
    private String phone;
    //设备Mac
    private String mac;
    //是否已经被分享
    private boolean isShared = false;

    public  ContactBean() {
    }

    public  ContactBean(String name,String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getHeadIconUrl() {
        return headIconUrl;
    }

    public void setHeadIconUrl(String headIconUrl) {
        this.headIconUrl = headIconUrl;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }
}

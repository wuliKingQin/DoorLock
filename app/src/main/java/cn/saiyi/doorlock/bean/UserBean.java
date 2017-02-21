package cn.saiyi.doorlock.bean;

import com.saiyi.framework.bean.BaseBean;

/**
 * 描述：用户实例，在用户登录成功后，或者再启动页时获取用户信息
 * 创建作者：黎丝军
 * 创建时间：2016/10/14 17:58
 */

public class UserBean extends BaseBean {

    //姓名
    private String name;
    //头像urL
    private String headUrl;
    //电话号码
    private String phone;
    //地址
    private String address;

    public UserBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

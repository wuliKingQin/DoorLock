package cn.saiyi.doorlock.bean;

import com.saiyi.framework.bean.BaseBean;

/**
 * 描述：产品实例
 * 创建作者：黎丝军
 * 创建时间：2016/10/18 8:41
 */

public class ProductBean extends BaseBean {

    //产品图片
    private String iconUrl;
    //产品名字
    private String name;

    public ProductBean() {
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

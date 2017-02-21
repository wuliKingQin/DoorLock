package cn.saiyi.doorlock.bean;

import com.saiyi.framework.bean.BaseBean;

/**
 * 描述：照片实例
 * 创建作者：黎丝军
 * 创建时间：2016/10/20 13:57
 */

public class PhotoBean extends BaseBean {

    //用于照片url
    private String photoUrl;
    //用于获取本地图片
    private int photoResId;
    //用于保存图片名
    private String photoName;
    //是否被选中
    private boolean isSelected = false;

    public PhotoBean() {
    }

    public PhotoBean(String photoUrl,int photoResId,String photoName) {
        this.photoUrl = photoUrl;
        this.photoResId = photoResId;
        this.photoName = photoName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getPhotoResId() {
        return photoResId;
    }

    public void setPhotoResId(int photoResId) {
        this.photoResId = photoResId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

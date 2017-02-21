package cn.saiyi.doorlock.bean;
/**
 * 描述：设置bean
 * 创建作者：黎丝军
 * 创建时间：2016/11/10 8:53
 */

public class SettingBean {

    //编号id
    private int id;
    //功能名
    private String funcName;
    //该功能是否打开
    private boolean isOpen;

    public SettingBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}

package cn.saiyi.doorlock.interfaces;

/**
 * 描述：用于组装字节数据的接口，子类去实现具体的格式
 * 创建作者：黎丝军
 * 创建时间：2016/11/15 15:27
 */

public interface IByteParam {

    /**
     * 设置头部字节
     * @param highByte 高位字节
     * @param lowByte 低位字节
     */
    void setHeadByte(int highByte, int lowByte);

    /**
     * 设置类型字节
     * @param highByte 高位字节
     * @param lowByte 低位字节
     */
    void setTypeByte(int highByte, int lowByte);

    /**
     * 设置命令字节
     * @param cmdByte 命令字节
     */
    void setCmdByte(int cmdByte);

    /**
     * 设置数据帧序号
     * @param frameNum 帧序号
     */
    void setFrameNum(int frameNum);

    /**
     * 添加一个字节
     * @param value 需要添加的字节值,格式如0xaa
     */
    void putByte(byte value);

    /**
     * 添加字符串
     * @param value 字符值
     */
    void putString(String value);

    /**
     * 添加一个整型数据
     * @param value 需要添加的字节值,格式如0xaa
     */
    void putInt(int value);

    /**
     * 添加字节数组
     * @param values 值
     */
    void putBytes(byte[] values);

    /**
     * 数据校验
     */
    void checkoutData();

    /**
     * 获取字节数组值
     * @return byte[]
     */
    byte[] getValues();

    /**
     * 获取头部字节数组
     * @return byte[]
     */
    byte[] getHeadByte();

    /**
     * 获取类型字节数组
     * @return byte[]
     */
    byte[] getTypeByte();

    /**
     * 获取命令字节
     * @return 命令字
     */
    byte getCmdByte();

    /**
     * 获取帧序号字节
     * @return 帧序号字节
     */
    int getFrameNum();

    /**
     * 获取数据大小
     * @return 大小值
     */
    int getDataSize();

    /**
     * 清空参数
     */
    void clear();
}

package com.lisijun.bluetooth.interfaces;
/**
 * 描述：字节参数接口
 * 创建作者：黎丝军
 * 创建时间：2017/1/19 9:14
 */

public interface IByteParam {

    /**
     * 默认数据大小为16个字节
     */
    int DEFAULT_SIZE = 16;

    /**
     * 添加整型数据
     * @param intParam 整型参数
     */
    void putInt(int intParam);

    /**
     * 添加字符串数据
     * @param stringParam 字符串参数
     */
    void putString(String stringParam);

    /**
     * 添加字符数据
     * @param charParam 字符参数
     */
    void putChar(char charParam);

    /**
     * 添加字节参数
     * @param byteParam 字节参数
     */
    void putByte(byte byteParam);

    /**
     * 实现添加字节数组
     * @param bytes 字节数据
     */
    void putBytes(byte[] bytes);

    /**
     * 获取整型值
     * @param index 索引
     * @return 获取整值
     */
    int getInt(int index);

    /**
     * 获取字节数据
     * @param index 索引
     * @return 字节数据
     */
    byte getByte(int index);

    /**
     * 获取字符数据
     * @param index 索引
     * @return 字符数据
     */
    char getChar(int index);

    /**
     * 获取字符串数据
     * @param index 索引
     * @return 字符串数据
     */
    String getString(int index);

    /**
     * 获取十六进制字符串
     * @return 数据字符串
     */
    String getHexString();

    /**
     * 获取字节数据
     * @return 数据
     */
    byte[] getData();

    /**
     * 清除数据
     */
    void clear();
}

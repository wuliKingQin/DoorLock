package com.lisijun.bluetooth.impl;
import com.lisijun.bluetooth.interfaces.IByteParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：字节参数的实现
 * 创建作者：黎丝军
 * 创建时间：2017/1/19 9:47
 */

public class ByteParam implements IByteParam {

    //用于保存字节数据
    private List<Byte> mData;
    //保存数据大小
    private int mLength = 0;
    //总长度
    private int mAllLength = 0;

    public ByteParam() {
        this(DEFAULT_SIZE);
    }

    public ByteParam(int size){
        if(size <= 0) {
            mAllLength = DEFAULT_SIZE;
        } else {
            mAllLength = size;
        }
        mData = new ArrayList<>(mAllLength);
    }

    public ByteParam(byte[] data) {
        this(data.length);
        mLength = data.length;
        for (byte byteData : data) {
            mData.add(byteData);
        }
    }

    @Override
    public void putInt(int intParam) {
        if(mLength < mAllLength) {
            mData.add((byte) intParam);
            mLength = mData.size();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void putString(String stringParam) {
        final char[] charStr = stringParam.toCharArray();
        for (char charByte : charStr) {
            putChar(charByte);
        }
    }

    @Override
    public void putChar(char charParam) {
        if(mLength < mAllLength) {
            mData.add((byte)charParam);
            mLength = mData.size();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void putByte(byte byteParam) {
        if(mLength < mAllLength) {
            mData.add(byteParam);
            mLength = mData.size();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void putBytes(byte[] bytes) {
        for (byte byteParam : bytes) {
            putByte(byteParam);
        }
    }

    @Override
    public int getInt(int index) {
        try {
            return mData.get(index);
        } catch (Exception e) {
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public byte getByte(int index) {
        try {
            return mData.get(index);
        } catch (Exception e) {
        }
       return Byte.MAX_VALUE;
    }

    @Override
    public char getChar(int index) {
        try {
            return (char) (byte)mData.get(index);
        } catch (Exception e) {
        }
        return Character.MAX_VALUE;
    }

    @Override
    public String getString(int index) {
        try {
            return Byte.toString(getByte(index));
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public String getHexString() {
        final StringBuilder stringBuilder = new StringBuilder(mData.size());
        for(byte byteChar : mData) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }
        return stringBuilder.toString();
    }

    @Override
    public byte[] getData() {
        final int size = mAllLength;
        final byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            if(i < mLength) {
                data[i] = mData.get(i);
            }
        }
        return data;
    }

    /**
     * 数据总长度
     * @return 数据总长度值
     */
    public int size() {
        return mAllLength;
    }

    /**
     * 获取数据当前数据长度
     * @return 当前数据长度
     */
    public int length() {
        return mLength;
    }

    @Override
    public void clear() {
        mData.clear();
    }
}

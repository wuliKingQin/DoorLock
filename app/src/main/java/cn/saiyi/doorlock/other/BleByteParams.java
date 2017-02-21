package cn.saiyi.doorlock.other;

import java.util.ArrayList;
import java.util.List;

import cn.saiyi.doorlock.interfaces.IByteParam;

/**
 * 描述：实现蓝牙字节参数
 * 创建作者：黎丝军
 * 创建时间：2016/11/15 15:42
 */

public class BleByteParams implements IByteParam {

    //头部高位字节
    private byte mHeadHighByte;
    //头部低位字节
    private byte mHeadLowByte;
    //类型高位字节
    private byte mTypeHighByte;
    //类型低位字节
    private byte mTypeLowByte;
    //命令字节
    private byte mCmdByte;
    //保存帧序号
    private byte mFrameNum;
    //用于临时存储发送的数据
    protected List<Byte> mTempData;
    //用于装载数据
    protected List<Byte> mContents;

    public BleByteParams() {
        mTypeHighByte = 0;
        mTypeLowByte = 0;
        mContents = new ArrayList<>();
    }

    public BleByteParams(byte[] data) {
        mContents = new ArrayList<>();
        if(data != null && data.length >= 6) {
            mHeadHighByte = data[0];
            mHeadLowByte = data[1];
            mTypeHighByte = data[2];
            mTypeLowByte = data[3];
            mCmdByte = data[4];
            mFrameNum = data[5];
            int index = 7;
            for(;index < data.length - 1;index ++) {
                mContents.add(data[index]);
            }
        }
    }

    @Override
    public void setHeadByte(int highByte, int lowByte) {
        mHeadHighByte = (byte)highByte;
        mHeadLowByte = (byte)lowByte;
    }

    @Override
    public void setTypeByte(int highByte, int lowByte) {
        mTypeHighByte = (byte)highByte;
        mTypeLowByte = (byte)lowByte;
    }

    @Override
    public void setCmdByte(int cmdByte) {
        mCmdByte = (byte)cmdByte;
    }

    @Override
    public void setFrameNum(int frameNum) {
        mFrameNum = (byte)frameNum;
    }

    @Override
    public void putByte(byte value) {
        mContents.add(value);
    }

    @Override
    public void putString(String value) {
        if(value != null) {
            final byte[] bytes = value.getBytes();
            for (byte aByte : bytes) {
                putByte(aByte);
            }
        }
    }

    @Override
    public void putInt(int value) {
        putByte((byte)value);
    }

    @Override
    public void putBytes(byte[] values) {
        for (byte aByte : values) {
            putByte(aByte);
        }
    }

    @Override
    public void checkoutData() {
        if(mTempData != null) {
            byte tempt = mTempData.get(0);
            int position = 1;
            for(;position < mTempData.size();position ++) {
                tempt ^= mTempData.get(position);
            }
            mTempData.add(tempt);
        }
    }

    /**
     * 组装数据
     */
    private void packageData() {
        mTempData = new ArrayList<>();
        mTempData.add(mHeadHighByte);
        mTempData.add(mHeadLowByte);
        if(mTypeHighByte == 0 && mTypeLowByte == 0) {
        } else {
            mTempData.add(mTypeHighByte);
            mTempData.add(mTypeLowByte);
        }
        mTempData.add(mCmdByte);
        mTempData.add(mFrameNum);
        mTempData.add((byte)mContents.size());
        mTempData.addAll(mContents);
    }

    /**
     * 获取内容字节
     * @return byte[]
     */
    public byte[] getContent() {
        final byte[] data = new byte[mContents.size()];
        int index = 0;
        for(;index < mContents.size();index ++) {
            data[index] = mContents.get(index);
        }
        return data;
    }

    @Override
    public byte[] getValues() {
        packageData();
        checkoutData();
        int index = 0;
        final byte[] data = new byte[mTempData.size()];
        for (;index < mTempData.size(); index++) {
            data[index] = mTempData.get(index);
        }
        mTempData.clear();
        return data;
    }

    @Override
    public byte[] getHeadByte() {
        return new byte[]{mHeadHighByte,mHeadLowByte};
    }

    @Override
    public byte[] getTypeByte() {
        return new byte[]{mTypeHighByte,mTypeLowByte};
    }

    @Override
    public byte getCmdByte() {
        return mCmdByte;
    }

    @Override
    public int getFrameNum() {
        return mFrameNum;
    }

    @Override
    public int getDataSize() {
        return mContents.size();
    }

    @Override
    public void clear() {
        mContents.clear();
    }
}

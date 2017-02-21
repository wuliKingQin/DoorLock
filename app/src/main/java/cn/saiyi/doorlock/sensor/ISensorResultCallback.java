package cn.saiyi.doorlock.sensor;

import android.hardware.Sensor;

/**
 * 描述：传感器结果回调接口，该接口主要处理摇一摇结果的返回
 * 创建作者：黎丝军
 * 创建时间：2016/11/1 9:23
 */

public interface ISensorResultCallback {

    /**
     * 结果处理方法
     * @param speed 摇一摇速度值
     */
    void onSharkResult(double speed);

    /**
     * 传感器改变方法
     * @param sensor 传感器实例
     * @param x x轴偏转
     * @param y y轴偏转
     * @param z z轴偏转
     */
    void onSensorChange(Sensor sensor,float x,float y,float z);
}

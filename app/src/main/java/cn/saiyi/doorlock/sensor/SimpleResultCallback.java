package cn.saiyi.doorlock.sensor;

import android.hardware.Sensor;

/**
 * 描述：实现回调接口空方法
 * 创建作者：黎丝军
 * 创建时间：2016/11/1 10:01
 */

public class SimpleResultCallback implements ISensorResultCallback {

    public SimpleResultCallback() {
    }

    @Override
    public void onSharkResult(double speed) {

    }

    @Override
    public void onSensorChange(Sensor sensor, float x, float y, float z) {

    }
}

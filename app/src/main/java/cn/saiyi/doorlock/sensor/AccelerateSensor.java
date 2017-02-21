package cn.saiyi.doorlock.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

/**
 * 描述：加速传感器，主要用来实现摇一摇功能.
 *       使用：
 *       1.在manifest文件中添加权限<uses-permission android:name="android.hardware.sensor.accelerometer"/>
 *       2.在你需要摇一摇的地方发实例化：
 *         XXActivity extends Activity{
 *             //声明对象
 *             private AccelerateSensor mSensor;
 *
 *             protected void onCreate(Bundle savedInstanceState) {
 *                   mSensor = new AccelerateSensor(this,new SimpleResultCallback() {
 *                   @Override
 *                   public void onSharkResult(double speed) {
 *                            //如果需要震动调用
 *                            mSensor.playShark();
 *                   });
 *                   //如果是蓝牙摇一摇，该方法最好在蓝牙连接成功后调用
 *                   mSensor.registerListener();
 *             }
 *
 *             @Override
 *             protected void onDestroy() {
 *                  super.onDestroy();
 *                  //注销监听
 *                  mSensor.unRegisterListener();
 *             }
 *
 *         }
 * 创建作者：黎丝军
 * 创建时间：2016/11/1 9:21
 */

public class AccelerateSensor implements SensorEventListener {

    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHARK_VALUE = 6000;
    // 两次检测的时间间隔
    private static final int UPDATE_INTERVAL_TIME = 70;
    //加速传感器x坐标
    private float lastX;
    //加速传感器y坐标
    private float lastY;
    //加速传感器z坐标
    private float lastZ;
    //运行环境
    private Context mContext;
    // 上次检测时间
    private long mLateUpdateTime;
    //加速传感器
    private Sensor mAccelerateSensor;
    //传感器管理器
    private SensorManager mSensorMgr;
    //结果回调接口
    private ISensorResultCallback mResultCallback;
    //震动实现
    private Vibrator mShakeVibrator;

    public AccelerateSensor(Context context) {
        this(context,null);
    }

    public AccelerateSensor(Context context,ISensorResultCallback callback) {
        mResultCallback = callback;
        mContext = context;
        mShakeVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mSensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerateSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 注册传感器监听
     */
    public void registerListener() {
        if(mSensorMgr != null) {
            mSensorMgr.registerListener(this,mAccelerateSensor,SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * 注销传感器监听器
     */
    public void unRegisterListener() {
        if(mSensorMgr != null) {
            mSensorMgr.unregisterListener(this);
        }
    }

    /**
     * 设置传感器结果监听器
     * @param callback 接口回调实例
     */
    public void setSensorResultCallback(ISensorResultCallback callback) {
        mResultCallback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        final long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        final long timeInterval = currentUpdateTime - mLateUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPDATE_INTERVAL_TIME)
            return;
        // 现在的时间变成last时间
        mLateUpdateTime = currentUpdateTime;
        // 获得x,y,z坐标
        final float x = event.values[0];
        final float y = event.values[1];
        final float z = event.values[2];
        // 获得x,y,z的变化值
        final float deltaX = x - lastX;
        final float deltaY = y - lastY;
        final float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        final double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
        if(mResultCallback != null) {
            // 达到速度阀值，发出提示
            if (speed >= SPEED_SHARK_VALUE) {
                mResultCallback.onSharkResult(speed);
            }
            mResultCallback.onSensorChange(event.sensor,x,y,z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 播放震动
     */
    public void playShark() {
        playShark(new long[]{500,200,500,200}, -1);
    }

    /**
     * 播放震动
     * @param shakeRhythm 节奏数组
     * @param repeat -1为重复，其他为不重复
     */
    public void playShark(long[] shakeRhythm,int repeat) {
        if(mShakeVibrator != null) {
            mShakeVibrator.vibrate(new long[]{500,200,500,200}, -1);
        }
    }

    /**
     * 取消震动
     */
    public void cancelShark() {
        if(mShakeVibrator != null) {
            mShakeVibrator.cancel();
        }
    }
}

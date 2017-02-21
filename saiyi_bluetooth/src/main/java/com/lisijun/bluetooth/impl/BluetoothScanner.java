package com.lisijun.bluetooth.impl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.lisijun.bluetooth.R;
import com.lisijun.bluetooth.interfaces.IBleScanCallback;

import java.util.UUID;

/**
 * 描述：蓝牙扫描器，该类主要封装了检查手机是否支持蓝牙，蓝牙是否开启，蓝牙扫描等操作。
 *       注意：在使用该类前需要在manifest文件中注册两个权限，一个特征运行和一个BluetoothService服务。
 *       如下：permission.BLUETOOTH、permission.BLUETOOTH_ADMIN 、 <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
 *       和<service android:name=".service.BluetoothService" android:enabled="true"/>
 *       另外还需要注意，在android6.0上还需要加上以下两个权限，否则在android6.0上不能扫描到蓝牙设备
 *       permission.ACCESS_FINE_LOCATION和permission.ACCESS_COARSE_LOCATION
 *       使用该类的步骤大致如下，如：
 *       假设场景是在一个activity中
 *       public class XXActivity extends Activity
 *              implements IBleScanCallback {
 *           private BluetoothScanner bleScanner = null;
 *           public void onCreate(Bundle bundle) {
 *               …………
 *               bleScanner = new BluetoothScanner(this);
 *               //设置蓝牙扫描回调接口
 *               bleScanner.setBluetoothScanCallback(this);
 *               //开始扫描
 *               bleScanner.scanBluetooth();
 *               …………
 *           }
 *
 *           protected void onResume() {
 *                super.onResume();
 *                //检查蓝牙是否开启
 *                bleScanner.checkBluetoothEnable();
 *           }
 *
 *           protected void onDestroy() {
 *               super.onDestroy()
 *               //释放资源
 *               bleScanner.destroy();
 *           }
 *
 *           protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *                //如果手机没有打开蓝牙，那么调用checkBluetoothEnable()方法后会走到这里，
 *                //所以你这里需要简单处理一下,如下：
 *                 if (requestCode == BluetoothScanner.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
 *                      //处理如果用户选择取消打开蓝牙，那么你要结束掉该界面
 *                 } else {
 *                      //如果用户选择打开的处理，这个按自己需要进行处理
 *                 }
 *           }
 *
 *           @Override
 *          public void onScanResult(BluetoothDevice device) {
 *               //蓝牙扫描结果将走这个方法，扫描到一个蓝牙设备将调用一次
 *          }
 *
 *          @Override
 *          public void onScanFail(String failInfo) {
 *              //蓝牙扫描失败，有几种情况，比如手机不支持，蓝牙相关类初始化失败等
 *          }
 *
 *          @Override
 *          public void onScanFinish() {
 *              //蓝牙扫描完成，主要是蓝牙扫描是有个时间的，默认时间是10秒
 *          }
 *       }
 *
 * 创建作者：黎丝军
 * 创建时间：2016/10/28 14:46
 */

public class BluetoothScanner {

    //该变量需要在onActivityResult()方法中使用
    public final static int REQUEST_ENABLE_BT = 1;
    //超时时间，默认是10秒
    private long mTimeOutTime = 10 * 1000;
    //判断是否扫描结束
    private boolean isScanEnd = false;
    //该类用于实例化BluetoothAdapter
    private BluetoothManager mBluetoothMgr;
    //蓝牙扫描操作实例
    private BluetoothAdapter mBluetoothHandle;
    //蓝牙扫描设备回调接口
    private IBleScanCallback mScanCallback;
    //用来扫描蓝牙设备超时处理
    private Handler mHandle = new Handler();
    //运行环境
    private Context mContext;

    public BluetoothScanner(Context context) {
        mContext = context;
    }

    /**
     * 扫描蓝牙
     * 该方法需要在界面类的onCreate()方法中调用，当然或者其他地方
     * 但需要注意的是，必须在checkBluetoothEnable()方法前调用
     */
    public void scanBluetooth() {
        scanBluetooth(null);
    }

    /**
     * 根据服务的UUID去扫描蓝牙设备
     * 该方法需要在界面类的onCreate()方法中调用，当然或者其他地方
     * 但需要注意的是，必须在checkBluetoothEnable()方法前调用
     * @param uuids uuid数组
     */
    public void scanBluetooth(UUID[] uuids) {
        if(mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mBluetoothMgr = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothHandle = mBluetoothMgr.getAdapter();
            if(mBluetoothHandle != null) {
                isScanEnd = false;
                mHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mScanCallback != null) {
                            isScanEnd = true;
                            mScanCallback.onScanFinish();
                        }
                        mBluetoothHandle.stopLeScan(scanCallback);
                    }
                },mTimeOutTime);
                if(uuids != null) {
                    mBluetoothHandle.startLeScan(uuids,scanCallback);
                } else {
                    mBluetoothHandle.startLeScan(scanCallback);
                }
            } else {
                scanFail(R.string.bluetooth_scanner_fail_hint);
            }
        } else {
            scanFail(R.string.bluetooth_scanner_support_hint);
        }
    }

    /**
     * 检查蓝牙是否开启，如果检查手机没有打开，则自动跳到开启界面
     * 该方法需要界面类的onResume()方法调用
     */
    public void checkBluetoothEnable() {
        if (mBluetoothHandle != null && !mBluetoothHandle.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * 设置扫描蓝牙回调监听器
     * @param callback 回调实例接口
     */
    public void setBluetoothScanCallback(IBleScanCallback callback) {
        mScanCallback = callback;
    }

    /**
     * 设置扫描结束时间，如果不调用设置该时间
     * 那么默认是10秒
     * @param endTime 扫描结束时间，单位是毫秒
     */
    public void setScanEndTime(long endTime) {
        mTimeOutTime = endTime;
    }

    /**
     * 该方法在界面onDestroy()方法被调用
     */
    public void destroy() {
        if(mBluetoothHandle != null) {
            mBluetoothHandle.stopLeScan(scanCallback);
        }
    }

    /**
     * 扫描失败方法
     * @param resId 错误提示资源
     */
    private void scanFail(int resId) {
        isScanEnd = true;
        if(mScanCallback != null) {
            mScanCallback.onScanFail(mContext.getString(resId));
        }
    }

    /**
     * 该回调接口用于监听蓝牙扫描时将扫描到的蓝牙设备返回。
     * 在这里我就可以向列表中添加数据
     */
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mScanCallback != null) {
                        mScanCallback.onScanResult(device,rssi);
                    }
                }
            });
        }
    };

    /**
     * 返回扫描是否结束
     * @return true表示结束，false表示没有结束
     */
    public boolean isScanEnd() {
        return isScanEnd;
    }
}

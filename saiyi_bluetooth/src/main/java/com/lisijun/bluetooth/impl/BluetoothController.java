package com.lisijun.bluetooth.impl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.lisijun.bluetooth.R;
import com.lisijun.bluetooth.interfaces.IBleRunCallback;
import com.lisijun.bluetooth.interfaces.IBleService;
import com.lisijun.bluetooth.interfaces.IByteParam;
import com.lisijun.bluetooth.interfaces.ISendCallback;
import com.lisijun.bluetooth.service.BluetoothService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述：蓝牙控制类，该类主要被用在选择某个蓝牙设备后处理蓝牙的连接，断开和发送数据等操作。
 *       注意：在使用该类前需要在manifest文件中注册两个权限，一个特征运行和一个BluetoothService服务。
 *       如下：permission.BLUETOOTH、permission.BLUETOOTH_ADMIN 、 <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
 *       和<service android:name=".service.BluetoothService" android:enabled="true"/>
 *       使用用例如下：
 *       假设场景是在一个activity中
 *       public class XXActivity extends Activity
 *              implements IBluetoothRunCallback {
 *               private String mBleAddress;
 *               private BluetoothController bleController = null;
 *               public void onCreate(Bundle bundle) {
 *                      …………
 *                      mBleAddress = getIntent().getStringExtra("bleAddress");
 *                      bleController = new BluetoothController(this);
 *                      //注册蓝牙设备的接口回调
 *                      bleController.registerCallback(this);
 *                      //设置发送数据时的延迟时间，不设置为500毫秒
 *                      bleController.setDelayedTime(600);
 *                      //设置连接成功后蓝牙设备服务、通知特征和写特征的UUID
 *                      //在这里只需要设置UUID前缀头部就好了
 *                      bleController.setUuidHeads("0000fff0","0000fff4","0000fff6");
 *                      //绑定蓝牙服务
 *                      bleController.bindBleService();
 *                      …………
 *               }
 *
 *               @Override
 *               protected void onResume() {
 *                      super.onResume();
 *                        //注册蓝牙接收器广播
 *                      bleController.registerMsgReceiver();
 *                      //开始连接，当然这个连接可以在一个点击事件里
 *                      //只需要在注册了蓝牙接收器广播了以后
 *                      bleController.connectBleDevice(mBleAddress);
 *              }
 *
 *              @Override
 *              protected void onDestroy() {
 *                      super.onDestroy()
 *                      //释放资源
 *                      bleController.closeBleService();
 *                      bleController.unRegisterMsgReceiver();
 *                      bleController.unbindBleService();
 *              }
 *
 *              @Override
 *              public void onInitBleServiceFinish(String info) {
 *                      //提示正在连接蓝牙信息
 *              }
 *
 *              @Override
 *              public void onConnSuccess(List<BluetoothGattService> services) {
 *                      //蓝牙连接成功
 *                      //在蓝牙连接成功后，列如向蓝牙设备发送一条数据
 *                      mBleController.sendData("命令字节数据".getBytes());
 *              }
 *
 *              @Override
 *              public void onConnFail(String failInfo) {
 *                      //提示蓝牙连接失败
 *              }
 *
 *              @Override
 *              public void onReceiveData(String uuid, byte[] data) {
 *                      //蓝牙设备发送数据到手机接收的方法
 *              }
 *           }
 * 创建作者：黎丝军
 * 创建时间：2016/10/29 17:16
 */

public class BluetoothController {

    //用于默认的延迟时间
    private final static long DEFAULT_TIME = 500;
    //连接默认超时时间为1分钟
    private final static long CONNECT_TIME_OUT = 30 * 1000;
    //保存服务的UUID
    private String mServiceUuid;
    //保存通知的UUID
    private String mNotifyUuid;
    //读的UUID
    private String mReadUuid;
    //设置通知的descrUUID
    private String mNotifyDescrUuid;
    //保存写特征的UUID
    private String mWriteUuid;
    //运行环境
    private Context mContext;
    //保存蓝牙地址
    private String mBleAddress;
    //判断是否已经连接
    private boolean isConnected = false;
    //是否初始化完成
    private boolean isInitBleFinish = false;
    //用于判断是否发送成功
    private boolean isSendSuccess = false;
    //延迟发送数据，默认是100毫秒
    private long mDelayedTime = DEFAULT_TIME;
    //用于连接超时时间
    private long mConnectTimeOut = CONNECT_TIME_OUT;
    //发送超时时间
    private long mSendTimeOut = CONNECT_TIME_OUT;
    //用于建立连接时定时，如果在1分钟内没有连接，
    // 怎直接失败
    private CountDownTimer mConnectDownTimer;
    // 发送超时没有回复倒计时
    private CountDownTimer mSendDownTimer;
    //发送数据回调，该接口只提示发送成功还是失败
    private ISendCallback mSendCallback;
    //消息接收广播实例
    private BroadcastReceiver mMsgReceiver;
    //用来操作操作与蓝牙设备的连接、断开和数据发送等
    private IBleService mBleService;
    //用于读的特征保存
    private BluetoothGattCharacteristic mReadChar;
    //用于写通知信息
    private BluetoothGattCharacteristic mNotifyChar;
    //用于写信息
    private BluetoothGattCharacteristic mWriteChar;
    //线程池服务类，用于发送消息用，这样可以防止消息被阻塞
    private ExecutorService mThreadPoolService = Executors.newSingleThreadExecutor();
    //用于蓝牙控制界面的接口回调
    private final List<IBleRunCallback> callbacks = new ArrayList<>();
    //用来绑定蓝牙连接服务
    private final ServiceConnection bleConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleService = ((BluetoothService.LocalBinder)service).getBleService();
            if(!mBleService.initBle()) {
                //初始化蓝牙服务失败
                messageHandle(IBleService.ACTION_CONNECTED_FAIL,null);
            } else {
                messageHandle(IBleService.ACTION_GATT_CONNECTING,null);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    //用于发送接口将子线程转到主线程中进行提示
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mSendCallback != null) {
                switch (msg.what) {
                    case 1:
                        if(isSendSuccess) {
                            mSendCallback.onSuccess();
                        } else {
                            sendDataTimeOutHandle();
                        }
                        break;
                    default:
                        mSendCallback.onFail();
                        break;
                }
            }
        }
    };

    public BluetoothController(Context context,String bleAddress) {
        mContext = context;
        mBleAddress = bleAddress;
        mMsgReceiver = new BluetoothMsgReceiver();
    }

    /**
     *  绑定蓝牙服务
     */
    public void bindBleService() {
        mContext.bindService(new Intent(mContext, BluetoothService.class),
                bleConnection,Context.BIND_AUTO_CREATE);
    }

    /**
     * 设置延迟发送时间
     * @param delayedTime 延迟时间，单位是毫毛
     */
    public void setDelayedTime(long delayedTime) {
        mDelayedTime = delayedTime;
    }

    /**
     * 设置设备蓝牙对应的UUID,该类必须要调用设置，
     * 否则发送数据到蓝牙设备将发送失败
     * @param serviceUuidHead 服务UUID前缀
     * @param notifyUuidHead 通知UUID前缀
     * @param writeUuidHead 写UUID前缀
     */
    public void setUuidHeads(String serviceUuidHead,String notifyUuidHead,String writeUuidHead) {
        mServiceUuid = IBleService.UUID_TAIL + serviceUuidHead;
        mNotifyUuid = IBleService.UUID_TAIL + notifyUuidHead;
        mWriteUuid = IBleService.UUID_TAIL + writeUuidHead;
    }

    /**
     * 设置读取的UUID头
     * @param readUuidHead 去读UUID头部
     */
    public void setReadUuidHead(String readUuidHead) {
        mReadUuid =  IBleService.UUID_TAIL + readUuidHead;
    }

    /**
     * 设置通知描述的UUID
     * @param notifyDescrUuid 通知特征描述UUID
     */
    public void setNotifyDescriptorUuidHead(String notifyDescrUuid) {
        mNotifyDescrUuid = notifyDescrUuid + IBleService.UUID_DESCR_TAIL;
    }

    /**
     * 注册蓝牙消息接收器
     * 如果需要可以在onResume()再次调用
     */
    public void registerMsgReceiver() {
        mContext.registerReceiver(mMsgReceiver,makeGattUpdateIntentFilter());
    }

    /**
     * 连接蓝牙设备,该方法需要在以上步骤都完成后才能调用
     */
    public void connectBleDevice() {
        if(mBleService != null) {
            if(!mBleService.connectBleDevice(mBleAddress)){
                messageHandle(IBleService.ACTION_CONNECTED_FAIL,null);
            } else {
                connectTimeOutHandle();
            }
        } else {
            messageHandle(IBleService.ACTION_CONNECTED_FAIL,null);
        }
    }

    /**
     * 根据蓝牙地址关闭蓝牙设备
     */
    public void closeBle() {
        if(mBleService != null) {
            isConnected = false;
            if(mSendDownTimer != null) {
                mSendDownTimer.cancel();
                mSendDownTimer = null;
            }
            if(mConnectDownTimer != null) {
                mConnectDownTimer.cancel();
                mConnectDownTimer = null;
            }
            mBleService.closeBle(mBleAddress);
        }
    }

    /**
     * 连接超时处理
     */
    private void connectTimeOutHandle() {
        if(mConnectDownTimer != null) {
            mConnectDownTimer.cancel();
        }
        mConnectDownTimer = new CountDownTimer(mConnectTimeOut,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isConnected) {
                    if(mConnectDownTimer != null) {
                        mConnectDownTimer.cancel();
                        mConnectDownTimer = null;
                    }
                    Log.d("LiSiJun",mBleAddress + "取消超时处理 connect state=" + isConnected);
                } else {
                    Log.d("LiSiJun",mBleAddress + "onTick countdowntime=" + millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                Log.d("LiSiJun",mBleAddress + "onFinish connected state=" + isConnected);
                if(!isConnected) {
                    closeBle();
                    messageHandle(IBleService.ACTION_CONNECTED_FAIL,null);
                }
                if(mConnectDownTimer != null) {
                    mConnectDownTimer.cancel();
                    mConnectDownTimer = null;
                }
            }
        };
        mConnectDownTimer.start();
    }

    /**
     * 发送数据超时没有回复的处理
     */
    private void sendDataTimeOutHandle() {
        if(mSendDownTimer != null) {
            mSendDownTimer.cancel();
        }
        mSendDownTimer = new CountDownTimer(mSendTimeOut,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isSendSuccess) {
                    mHandler.sendEmptyMessage(1);
                    if(mSendDownTimer != null) {
                        mSendDownTimer.cancel();
                        mSendDownTimer = null;
                    }
                }
            }

            @Override
            public void onFinish() {
                if(!isSendSuccess) {
                    mHandler.sendEmptyMessage(0);
                }
                if(mSendDownTimer != null) {
                    mSendDownTimer.cancel();
                    mSendDownTimer = null;
                }
            }
        };
        mSendDownTimer.start();
    }

    /**
     * 发送数据到蓝牙设备
     * @param data 发送的数据
     */
    public void sendData(byte[] data) {
        sendData(data,null);
    }

    /**
     * 发送数据到蓝牙设备
     * @param byteParam 字节数据
     * @param sendDataCallback 发送回调接口
     */
    public void sendData(IByteParam byteParam,ISendCallback sendDataCallback) {
        sendData(byteParam.getData(),true,sendDataCallback);
    }

    /**
     * 发送数据到蓝牙设备
     * @param data 字节数据
     * @param sendDataCallback 发送回调接口
     */
    public void sendData(final byte[] data,final ISendCallback sendDataCallback) {
        sendData(data,true,sendDataCallback);
    }

    /**
     * 发送数据到蓝牙设备
     * @param data 发送的数据
     * @param needTimeOut 是否需要超时处理，true表示需要，false表示不需要，
     *                    所谓超时处理就是发送后如果没有接受到回应数据那么超过比如10秒就是超时
     */
    public void sendData(byte[] data,boolean needTimeOut) {
        sendData(data,needTimeOut,null);
    }

    /**
     * 发送数据到蓝牙设备
     * @param byteParam 字节数据
     * @param needTimeOut 是否需要超时处理，true表示需要，false表示不需要
     * @param sendDataCallback 发送回调接口
     */
    public void sendData(IByteParam byteParam, boolean needTimeOut,ISendCallback sendDataCallback) {
        sendData(byteParam.getData(),needTimeOut,sendDataCallback);
    }

    /**
     * 发送数据到蓝牙设备
     * @param data 字节数据
     * @param needTimeOut 是否需要超时处理，true表示需要，false表示不需要
     * @param sendDataCallback 发送回调接口
     */
    public void sendData(final byte[] data,final boolean needTimeOut,final ISendCallback sendDataCallback) {
        isSendSuccess = false;
        mThreadPoolService.submit(new Runnable() {
            @Override
            public void run() {
                mSendCallback = sendDataCallback;
                try {
                    Thread.sleep(mDelayedTime);
                } catch (Exception e) {
                }
                if(mWriteChar != null) {
                    mWriteChar.setValue(data);
                    isSendSuccess = mBleService.writeCharacteristic(mWriteChar,mBleAddress);
                }
                if(!isSendSuccess) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    if(needTimeOut) {
                        isSendSuccess = false;
                    }
                    mHandler.sendEmptyMessage(1);
                }
            }
        });
    }

    /**
     * 设置通知特征开启，并设置其通知特征的描述器也开启能通知
     * 如果添加了这个方法，收不到消息，就先卸载掉程序后，在重写运行应该就ok了
     * @param characteristic 特征
     * @param enabled 是否能
     */
    public void setNotifyCharacteristic(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if(mBleService != null) {
            boolean isEnableNotify;
            isEnableNotify = mBleService.setCharacteristicNotification(characteristic,enabled,mBleAddress);
            if(isEnableNotify && !TextUtils.isEmpty(mNotifyDescrUuid)) {
                final List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
                if(descriptorList != null && descriptorList.size() > 0) {
                    for(BluetoothGattDescriptor descriptor : descriptorList) {
                        if(TextUtils.equals(mNotifyDescrUuid,descriptor.getUuid().toString())) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBleService.getGatt(mBleAddress).writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }
    }

    /**
     * 注销消息接收广播，如果只是在某个界面进行了注册，
     * 那么在这个界面的onDestroy()方法中必须调用，如果是在manifest中注册的则不需要
     */
    public void unRegisterMsgReceiver() {
        if(mMsgReceiver != null) {
            mContext.unregisterReceiver(mMsgReceiver);
            mMsgReceiver = null;
        }
    }

    /**
     * 将蓝牙服务与界面解除绑定，
     * 所有该方法需要在onDestroy()方法中必须调用
     */
    public void unbindBleService() {
        if(bleConnection != null) {
            mContext.unbindService(bleConnection);
            mBleService = null;
        }
    }

    /**
     * 注册蓝牙运行接口回调方法，
     * 该方法在连接蓝牙设备的界面里实现
     * @param callback 接口实例
     */
    public void registerCallback(IBleRunCallback callback) {
        if(!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    /**
     * 注销蓝牙接口回调接口，
     * 该方法在需要实现的蓝牙控制界面的onDestroy()方法里调用
     * @param callback 接口实例
     */
    public void unRegisterCallback(IBleRunCallback callback) {
        if(callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    /**
     * 初始化蓝牙信息，在发现蓝牙设备后
     */
    private void initBluetoothService() {
        final List<BluetoothGattService> services = mBleService.getGattServices(mBleAddress);
        if(services != null) {
            List<BluetoothGattCharacteristic> characteristics;
            for (BluetoothGattService service : services) {
                if(TextUtils.equals(mServiceUuid,service.getUuid().toString())) {
                    characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        if(TextUtils.equals(mNotifyUuid,characteristic.getUuid().toString())) {
                            mNotifyChar = characteristic;
                            setNotifyCharacteristic(mNotifyChar,true);
                        }
                        if(TextUtils.equals(mWriteUuid,characteristic.getUuid().toString())) {
                            mWriteChar = characteristic;
                        }
                        if(TextUtils.equals(mReadUuid,characteristic.getUuid().toString())) {
                            mReadChar = characteristic;
                        }
                    }
                }
            }
        }
    }

    /**
     * 广播过滤器
     * @return IntentFilter 实例
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_RSSI);
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_GATT_CONNECTING);
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_CONNECTED_FAIL);
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(mBleAddress + "-" + IBleService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }


    /**
     * 用来接收与蓝牙设备的交互的信息
     * 该广播即可以在manifest中注册成为常驻型广播，
     * 也可以调用registerMsgReceiver()方法在需要的地方进行注册
     */
    public class BluetoothMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action != null) {
                if(mBleAddress != null) {
                    final String[] broadcastAction = action.split("-");
                    if(broadcastAction != null && broadcastAction.length >= 2) {
                        if(TextUtils.equals(mBleAddress,broadcastAction[0])) {
                            messageHandle(broadcastAction[1],intent);
                        }
                    }
                } else {
                    messageHandle(action,intent);
                }
            }
        }
    }

    /**
     * 消息处理，主要是将消息分发
     * @param action 广播类型
     * @param data 数据
     */
    private void messageHandle(String action,Intent data) {
        for (IBleRunCallback callback : callbacks) {
            switch (action) {
                case IBleService.ACTION_GATT_CONNECTED:
                    if (data == null || (data != null && !data.getBooleanExtra(
                            IBleService.EXTRA_IS_SERVICES_DISCOVERED, false))) {
                        callback.onConnFail(mContext.getString(R.string.bluetooth_control_fail_hint));
                    }
                    break;
                case IBleService.ACTION_GATT_CONNECTING:
                    if(!mBleService.checkBluetoothEnable()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        ((Activity)mContext).startActivityForResult(enableBtIntent, BluetoothScanner.REQUEST_ENABLE_BT);
                    } else {
                        isInitBleFinish = true;
                        callback.onInitBleServiceFinish(mContext.getString(R.string.bluetooth_control_connecting_hint));
                    }
                    break;
                case IBleService.ACTION_GATT_DISCONNECTED:
                    isConnected = false;
                    callback.onConnFail(mContext.getString(R.string.bluetooth_control_disconnect_hint));
                    break;
                case IBleService.ACTION_DATA_AVAILABLE:
                    isSendSuccess = true;
                    Log.d("LiSiJun",data.getStringExtra(IBleService.EXTRA_DATA_STR));
                    final byte[] result = data.getByteArrayExtra(IBleService.EXTRA_DATA_BYTE);
                    final int position = callback.getFuncPosition();
                    if((position >= 0 && position < result.length)) {
                        if(callback.getFunc() == result[position] || callback.getFunc() == Byte.MAX_VALUE) {
                            callback.onReceiveData(data.getStringExtra(IBleService.EXTRA_UUID),new ByteParam(result));
                        }
                    }
                    break;
                case IBleService.ACTION_GATT_SERVICES_DISCOVERED:
                    initBluetoothService();
                    isConnected = true;
                    callback.onConnSuccess(mBleService.getGattServices(mBleAddress));
                    break;
                case IBleService.ACTION_CONNECTED_FAIL:
                    isConnected = false;
                    callback.onConnFail(mContext.getString(R.string.bluetooth_control_fail_hint));
                    break;
                case IBleService.ACTION_RSSI:
                    callback.onReceiveRssi(data.getIntExtra(IBleService.EXTRA_DATA_RSSI,0));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断是否已经连接蓝牙设备
     * @return true表示已经连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 用于判断是否初始化完成
     * @return true表示完成
     */
    public boolean isInitBleFinish() {
        return isInitBleFinish;
    }

    /**
     * 读取设备rssi，调用该方法后会在onReceviceRssi()方法中接收到该值
     */
    public void readRemoteRssi() {
        if(mBleService != null) {
            final BluetoothGatt bleGatt = mBleService.getGatt(mBleAddress);
            if(bleGatt != null) {
                bleGatt.readRemoteRssi();
            }
        }
    }

    /**
     * 设置连接超时时间
     * @param connectTimeOut 单位毫秒
     */
    public void setConnectTimeOut(long connectTimeOut) {
        mConnectTimeOut = connectTimeOut == 0 ? CONNECT_TIME_OUT:connectTimeOut;
    }

    /**
     * 设置发送超时时间
     * @param sendTimeOut 单位毫秒
     */
    public void setSendTimeOut(long sendTimeOut) {
        mSendTimeOut = sendTimeOut == 0 ? CONNECT_TIME_OUT:sendTimeOut;
    }
}

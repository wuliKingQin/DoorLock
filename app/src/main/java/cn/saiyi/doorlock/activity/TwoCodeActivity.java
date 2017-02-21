package cn.saiyi.doorlock.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.DensityUtils;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.Utils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;
import cn.saiyi.doorlock.twocode.camera.CameraManager;
import cn.saiyi.doorlock.twocode.decoding.CaptureActivityHandler;
import cn.saiyi.doorlock.twocode.decoding.InactivityTimer;
import cn.saiyi.doorlock.twocode.decoding.RGBLuminanceSource;
import cn.saiyi.doorlock.twocode.view.ViewfinderView;

/**
 * 文件描述：二维码扫描界面
 * 创建作者：黎丝军
 * 创建时间：16/8/2 PM4:18
 */
public class TwoCodeActivity extends AbsBaseActivity
        implements SurfaceHolder.Callback {

    //扫描成功
    public static final int SCAN_RESULT_OK = 20;
    //扫描失败
    public static final int SCAN_RESULT_ERROR = -20;
    //扫描结果
    public static final String SCAN_RESULT = "scanResult";
    //临时信息
    public static final String TEMPT_INFO = "tempInfo";
    //振动值
    private static final long VIBRATE_DURATION = 200L;
    //播放声音时的音量
    private static final float BEEP_VOLUME = 0.10f;
    //用于存放临时信息
    private String tempInfo;
    //是否有振动
    private boolean vibrate;
    //是否播放beep
    private boolean playBeep;
    //用于判断是否绘制界面,默认值是false
    private boolean hasSurface;
    //字符串集
    private String characterSet;
    //用于播放声音
    private MediaPlayer mediaPlayer;
    //用于异步处理结果
    private CaptureActivityHandler handler;
    //用于编码格式
    private Vector<BarcodeFormat> decodeFormats;
    //用于显示照相机画面
    private SurfaceView surfaceView;
    //扫描二维码中间那个矩形框视图
    private ViewfinderView viewfinderView;
    //用于在二维码界面时，界面休眠时关闭二维码界面
    private InactivityTimer inactivityTimer;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_two_code);
    }

    @Override
    public void findViews() {
        surfaceView = getViewById(R.id.preview_view);
        viewfinderView = getViewById(R.id.viewfinder_view);
    }

    @Override
    public void initObjects() {
        hasSurface = false;
        tempInfo = getIntent().getStringExtra(TEMPT_INFO);
        inactivityTimer = new InactivityTimer(this);
    }

    /**
     * 初始化照相机
     * @param surfaceHolder 视图句柄
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SurfaceView surfaceView = getSurfaceView();
        final SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
    }

    /**
     * 初始化声音
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    /**
     * 媒体播放监听
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.two_code_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);
        actionBar.setRightButtonText(R.string.two_code_right);
        actionBar.setRightButtonTextColor(R.color.color7);
        CameraManager.initK(this,(int)DensityUtils.getScreenWidth(this) * 800 / 1080);
        viewfinderView.setHintText(R.string.two_code_hint);
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent();
                if (Build.VERSION.SDK_INT < 19) {
                    innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent,31);
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 31:
                handlePhoto(data);
                break;
            default:
                break;
        }
    }

    /**
     * 处理二维码扫描结果
     * @param obj
     * @param barcode
     */
    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        //获取二维码信息，跳转页面
        final Intent resultIntent = new Intent();
        if(obj != null) {
            final String scanInfo = obj.getText();
            resultIntent.putExtra(SCAN_RESULT, scanInfo);
            resultIntent.putExtra(TEMPT_INFO,tempInfo);
            if(!TextUtils.isEmpty(scanInfo)) {
                setResult(SCAN_RESULT_OK, resultIntent);
            } else {
                setResult(SCAN_RESULT_ERROR, resultIntent);
            }
        } else {
            setResult(SCAN_RESULT_ERROR, resultIntent);
        }
        finish();
    }

    /**
     * 用于播放声音和振动
     */
    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * 处理选择的照片
     * @param data 图片地址
     */
    public void handlePhoto(Intent data) {
        if(data != null) {
            final String[] proj = { MediaStore.Images.Media.DATA };
            // 获取选中图片的路径
            final Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String photoPath = cursor.getString(column_index);
                if (photoPath == null) {
                    photoPath = Utils.getPath(getApplicationContext(),data.getData());
                }
                new ScanCodeThread(photoPath).start();
            } else {
                handleDecode(null,null);
            }
            cursor.close();
        } else {
            handleDecode(null,null);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    /**
     * 用于处理扫描图片二维码线程
     */
    private class ScanCodeThread extends Thread {

        //图片路径
        private String photoPath;

        public ScanCodeThread(String photoPath) {
            this.photoPath = photoPath;
        }

        @Override
        public void run() {
            final Result result = scanningImage();
            handleDecode(result,null);
        }

        /**
         * 扫描图片
         * @return 返回扫描结果
         */
        private Result scanningImage() {
            if (TextUtils.isEmpty(photoPath)) {
                return null;
            }
            // DecodeHintType 和EncodeHintType
            Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 先获取原大小
            Bitmap scanBitmap = BitmapFactory.decodeFile(photoPath, options);
            int sampleSize = (int) (options.outHeight / (float) 200);
            if (sampleSize <= 0) {
                sampleSize = 1;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            scanBitmap = BitmapFactory.decodeFile(photoPath, options);
            if(scanBitmap != null) {
                RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();
                try {
                    return reader.decode(bitmap1, hints);
                } catch (ReaderException e) {
                    LogUtils.d(e.getMessage());
                }
            }
            return null;
        }
    }

    public CaptureActivityHandler getHandler() {
        return handler;
    }
}

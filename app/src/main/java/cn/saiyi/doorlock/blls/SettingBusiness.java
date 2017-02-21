package cn.saiyi.doorlock.blls;

import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.saiyi.framework.blls.AbsBaseBusiness;
import com.saiyi.framework.util.LogUtils;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import java.io.File;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.FileMgr;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.AppUtil;

/**
 * 描述：设置业务类
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 10:00
 */

public class SettingBusiness extends AbsBaseBusiness {

    //文件管理器，主要用来保存下载的apk
    private FileMgr mFileMgr;

    @Override
    public void initObject() {
        mFileMgr = FileMgr.instance();
    }

    @Override
    public void initData(Bundle bundle) {
        mHintDialog.setTitle(R.string.dialog_hint);
    }

    /**
     * 检测app更新
     */
    public void checkAppUpdate() {
        ProgressUtils.showDialog(getContext(),"正在检测中，……",null);
        HttpRequest.post(URL.VERSION_CHECK,new BaseHttpRequestCallback<JSONObject>() {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                try {
                    final int versionCode = jsonObject.getInteger("versionCode");
                    if(versionCode > AppUtil.getVersionCode(getContext())) {
                        mHintDialog.setContentText("有新版本，确定要更新吗？");
                        mHintDialog.setSureListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHintDialog.dismiss();
                                updateNewAppHandle();
                            }
                        });
                        mHintDialog.show();
                    } else {
                        ToastUtils.toast(getContext(),"已经是最新版，不需要更新");
                    }
                } catch (Exception e){
                    onFailure(-1,null);
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                ToastUtils.toast(getContext(),"app更新检测失败");
            }

            @Override
            public void onFinish() {
                ProgressUtils.dismissDialog();
            }
        });
    }

    /**
     * 更新app
     */
    private void updateNewAppHandle() {
        final File apkFile = new File(mFileMgr.getApkPath(),mFileMgr.getApkFileName());
        HttpRequest.download(URL.DOWNLOAD_FILE + mFileMgr.getApkFileName(),apkFile,new FileDownloadCallback() {

            @Override
            public void onProgress(int progress, long networkSpeed) {
            }

            @Override
            public void onFailure() {
                ToastUtils.toast(getContext(),"下载app失败");
            }

            @Override
            public void onDone() {
                mHintDialog.setContentText("安装包下载完成，确定要现在安装吗？");
                mHintDialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHintDialog.dismiss();
                        if(apkFile != null && apkFile.exists()) {
                            mFileMgr.installApk(getContext(),apkFile);
                        } else {
                            onFailure();
                        }
                    }
                });
                mHintDialog.show();
            }
        });
    }
}

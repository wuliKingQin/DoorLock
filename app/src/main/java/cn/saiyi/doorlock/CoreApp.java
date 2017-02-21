package cn.saiyi.doorlock;

import android.app.Application;
import android.graphics.BitmapFactory;

import com.alibaba.fastjson.JSONObject;
import com.pgyersdk.crash.PgyCrashManager;
import com.saiyi.framework.AppHelper;
import com.saiyi.framework.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.RequestParams;
import cn.saiyi.doorlock.other.FileMgr;
import cn.saiyi.doorlock.other.URL;
import cn.saiyi.doorlock.util.LoginUtil;

/**
 * 描述：应用程序首先需要执行的部分
 * 创建作者：黎丝军
 * 创建时间：2016/9/28 17:26
 */

public class CoreApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化app帮助类
        AppHelper.instance().initCoreApp(this);
        //初始化http
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        //注册蒲光英bug测试
        PgyCrashManager.register(this);
    }
}

package cn.saiyi.doorlock.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saiyi.framework.fragment.BaseFragment;
import com.saiyi.framework.util.ProgressUtils;
import com.saiyi.framework.util.ToastUtils;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：商城碎片界面
 * 创建作者：黎丝军
 * 创建时间：2016/9/29 17:41
 */

public class MallFragment extends BaseFragment {

    //商城web视图
    private WebView mMallWebView;
    //商城地址
    private String mMallUrl;

    @Override
    public void onContentView() {
        setContentView(R.layout.fragment_mall);
    }

    @Override
    public void findViews() {
        mMallWebView = getViewById(R.id.wv_mall);
    }

    @Override
    public void initObjects() {
        mMallUrl = "http://baidu.com";
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.mall_title);
        setTitleSize(Constant.TEXT_SIZE);
        setTitleColor(R.color.color7);
        setActionBarBackgroundColor(Color.WHITE);
        ProgressUtils.showDialog(getContext(),"正在加载中，……",true,null);
        if (Build.VERSION.SDK_INT >= 19) {
            mMallWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //下载url
        mMallWebView.loadUrl(mMallUrl);
        mMallWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = mMallWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mMallWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //按返回键处理
        mMallWebView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK) {
                    ProgressUtils.dismissDialog();
                    if(mMallWebView.canGoBack()) {
                        mMallWebView.goBack();//返回上一页面
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            }
        });
        //加载监听
        mMallWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    ProgressUtils.dismissDialog();
                } else {
                    // 加载中
                }
            }
        });
    }

    @Override
    public void setListeners() {

    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}

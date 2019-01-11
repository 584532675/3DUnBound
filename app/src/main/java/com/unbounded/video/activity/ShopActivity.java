package com.unbounded.video.activity;

import android.content.Intent;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unbounded.video.BaseActivity;

/**
 * Created by zjf on 2017/7/17.
 */

public class ShopActivity extends BaseActivity {
    private long LastClickTime = 0;
//    String url = "http://mall.jd.com/index-698821.html";
    String url = "https://item.taobao.com/item.htm?spm=a1z10.3-c.w4002-16829072124.16.3ff19e117chomV&id=557945463121";
    WebView webview;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_shop;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_shop));
    }

    @Override
    public void initView() {
        super.initView();

        webview = (WebView) findViewById(com.unbounded.video.R.id.shop_webview);

        init();
    }

    @Override
    public void initEvent() {
        super.initEvent();

    }

    private void init(){
        progressDid();
        //WebView加载web资源
        webview.loadUrl(url);
        //启用支持javascript
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        // 支持中文，否则页面中中文显示乱码
        settings.setDefaultTextEncodingName("GBK");
        webview.setWebChromeClient(new WebChromeClient());
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(mWebviewclient);
    }

    WebViewClient mWebviewclient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("info","onPageFinished......");
            progressDiddismiss();
            super.onPageFinished(view, url);
        }
    };

}

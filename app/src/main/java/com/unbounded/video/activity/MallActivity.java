package com.unbounded.video.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.youzan.androidsdk.YouzanToken;
import com.youzan.androidsdk.basic.YouzanBrowser;
import com.youzan.androidsdk.event.AbsAuthEvent;

/**
 * Created by Administrator on 2018/7/5 0005.
 * 2.商城界面
 */

public class MallActivity extends BaseActivity {

    private YouzanBrowser youzanYb;
    private static final int YOU_ZAN_TOKEN = 1;
    private ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void initDatas() {
        setTitleViewVisible(false);
        youzanYb = (YouzanBrowser) findViewById(R.id.youzan_yb);
        backIv = (ImageView) findViewById(R.id.back_iv);
        youzanYb.loadUrl("https://h5.youzan.com/v2/feature/NvI1uzvXrB");
        youzanYb.subscribe(new AbsAuthEvent() {
            @Override
            public void call(Context context, boolean b) {
                boolean isLogin = SharedPrefsUtil.getValue(MallActivity.this,
                        Constants.IS_LOGIN, false);
                if (isLogin) {
                    YouzanToken youzanToken = new YouzanToken();
                    youzanToken.setAccessToken(SharedPrefsUtil.getValue(MallActivity.this,
                            Constants.ACCESS_TOKEN, ""));
                    youzanToken.setCookieKey(SharedPrefsUtil.getValue(MallActivity.this,
                            Constants.COOKIE_KEY, ""));
                    youzanToken.setCookieValue(SharedPrefsUtil.getValue(MallActivity.this,
                            Constants.COOKIE_VALUE, ""));
                    youzanYb.sync(youzanToken);
                } else {
                    Intent intent = new Intent(MallActivity.this, LoginActivity.class);
                    intent.setAction(Constants.MALLACTIVITY_TYPE);
                    startActivityForResult(intent, 1);
                }
            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youzanYb.pageGoBack();
                if (!youzanYb.canGoBack()) {
                    backIv.setVisibility(View.GONE);
                }
            }
        });
        youzanYb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (youzanYb.canGoBack()) {
                    backIv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.mall_activity;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            YouzanToken youzanToken = new YouzanToken();
            youzanToken.setAccessToken(SharedPrefsUtil.getValue(MallActivity.this,
                    Constants.ACCESS_TOKEN, ""));
            youzanToken.setCookieKey(SharedPrefsUtil.getValue(MallActivity.this,
                    Constants.COOKIE_KEY, ""));
            youzanToken.setCookieValue(SharedPrefsUtil.getValue(MallActivity.this,
                    Constants.COOKIE_VALUE, ""));
            youzanYb.sync(youzanToken);
        }else{
            if(backIv.getVisibility() == View.VISIBLE)
            {
                backIv.performClick();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(backIv.getVisibility() == View.VISIBLE)
        {
            backIv.performClick();
        }else{
            super.onBackPressed();
        }
    }
}

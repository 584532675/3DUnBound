package com.unbounded.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;

/**
 * Created by zjf on 2017/11/22 0022.
 */

public class AboutUsActivity extends BaseActivity {
    ImageView qrimg/*,logoiv,appnameiv*/;
    RelativeLayout qrrela; //

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_aboutus;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.word_abousus));
    }

    @Override
    public void initView() {
        super.initView();

        qrimg = (ImageView) findViewById(com.unbounded.video.R.id.aboutus_qrimg);
//        logoiv = (ImageView) findViewById(com.unbounded.video.R.id.aboutus_logoiv);
//        appnameiv = (ImageView) findViewById(com.unbounded.video.R.id.aboutus_appnameiv);
        qrrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.aboutus_qrimgrela);
        TextView aboutusAppversiontv= (TextView) findViewById(R.id.aboutus_appversiontv);

    }
    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams params = qrimg.getLayoutParams();
        params.height = screenWidth * 351 / 1080;
        params.width = screenWidth * 351 / 1080;
        qrimg.setLayoutParams(params);

    /*    ViewGroup.LayoutParams appnameivparams = (ViewGroup.LayoutParams) appnameiv.getLayoutParams();
        appnameivparams.height = screenWidth * 47 / 1080;
        appnameivparams.width = screenWidth * 225 / 1080;
        appnameiv.setLayoutParams(appnameivparams);

        ViewGroup.LayoutParams logoivparams = (ViewGroup.LayoutParams) logoiv.getLayoutParams();
        logoivparams.height = screenWidth * 155 / 1080;
        logoivparams.width = screenWidth * 158 / 1080;
        logoiv.setLayoutParams(logoivparams);*/

        ViewGroup.LayoutParams qrrelaparams = qrrela.getLayoutParams();
        qrrelaparams.height = (screenWidth - 100*oneDp) * 773/542;
        qrrelaparams.width = screenWidth - 100*oneDp;
        qrrela.setLayoutParams(qrrelaparams);
    }
}

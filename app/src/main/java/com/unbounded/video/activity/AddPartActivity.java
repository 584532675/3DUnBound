package com.unbounded.video.activity;

import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;

/**
 * Created by zjf on 2017/7/26.
 */

public class AddPartActivity extends BaseActivity{
    TextView appnametv;
    ImageView logoimg;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_addpart;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_Loginpart));
    }

    @Override
    public void initView() {
        super.initView();

        appnametv = (TextView) findViewById(com.unbounded.video.R.id.addpart_line);
        logoimg = (ImageView) findViewById(com.unbounded.video.R.id.addpart_img);
    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams params = logoimg.getLayoutParams();
        params.height = screenWidth * 5 / 12;
        params.width = screenWidth * 5 / 12;
        logoimg.setLayoutParams(params);

        ViewGroup.LayoutParams params1 = appnametv.getLayoutParams();
        params1.height = screenHeight * 204 / 1920;
        appnametv.setLayoutParams(params1);
    }
}

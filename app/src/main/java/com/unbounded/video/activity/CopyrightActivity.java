package com.unbounded.video.activity;

import android.content.Intent;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;

/**
 * Created by zjf on 2017/8/10.
 */

public class CopyrightActivity extends BaseActivity {
    TextView contenttv;

    @Override
    protected int getContentView() {
        return R.layout.activity_copyright;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(R.string.word_copyright));
    }

    @Override
    public void initView() {
        super.initView();

        contenttv = (TextView) findViewById(R.id.copyright_tv);
    }
}

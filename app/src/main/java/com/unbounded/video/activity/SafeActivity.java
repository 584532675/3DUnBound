package com.unbounded.video.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.SharedPrefsUtil;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class SafeActivity extends BaseActivity {
    RelativeLayout changephonerela,changePswrela;
    TextView phonenumtv;
    String phoneStr;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_safe;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_safe));
        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        phoneStr = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, "");
        phonenumtv.setText(phoneStr);
    }

    @Override
    public void initView() {
        super.initView();

        changePswrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.safe_changepswrela);
        changephonerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.safe_changephonerela);
        phonenumtv = (TextView) findViewById(com.unbounded.video.R.id.safe_phonenum);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        changePswrela.setOnClickListener(this);
        changephonerela.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            //更换手机号
            case com.unbounded.video.R.id.safe_changephonerela:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else {
                    openActivity(UpdatePhoneActivity.class);
                }

                break;
            //修改密码
            case com.unbounded.video.R.id.safe_changepswrela:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else {
                    openActivity(UpdatePswActivity.class);
                }

                break;
        }
    }
}

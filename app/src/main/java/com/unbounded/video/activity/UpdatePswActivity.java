package com.unbounded.video.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class UpdatePswActivity extends BaseActivity {
    public final static int UpdatePsw_Flag = 0;
    ContainsEmojiEditText oldpswedt,newpswedt,newpsw2edt;
    String oldpsw,newpsw,newpsw2;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UpdatePsw_Flag:
                    String updatevalue = msg.obj.toString();
                    Log.e("info","updatevalue="+updatevalue);

                    progressDid1dismiss();
                    if (!(TextUtils.isEmpty(updatevalue))) {
                        if("true".equals(updatevalue)){
                            ToastUtil.showToast(UpdatePswActivity.this, com.unbounded.video.R.string.Word_commitsuccess, ToastUtil.CENTER);
                            finish();

                        }else if("false".equals(updatevalue)){
                            ToastUtil.showToast(UpdatePswActivity.this, com.unbounded.video.R.string.Word_updatepswfail, ToastUtil.CENTER);
                        }
                    }

                    break;

            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_changepsw;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setRightImgVisible(true);
        setRightImgBg(com.unbounded.video.R.mipmap.finishbtn1);
        setTitleName(getString(com.unbounded.video.R.string.Word_setpsw));

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, userId);
    }

    @Override
    public void initView() {
        super.initView();

        oldpswedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.updatepsw_oldpswedt);
        newpswedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.updatepsw_newpswedt);
        newpsw2edt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.updatepsw_newpsw2edt);

    }

    @Override
    public void onRightClick(View v) {
        super.onRightClick(v);

        updatePsw();
    }

    /**
     *
     */
    private void updatePsw(){
        oldpsw = oldpswedt.getText().toString().trim();
        newpsw = newpswedt.getText().toString().trim();
        newpsw2 = newpsw2edt.getText().toString().trim();


        if(TextUtils.isEmpty(oldpsw)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_oldpswhint, ToastUtil.CENTER);
            return;
        }
        if(TextUtils.isEmpty(newpsw)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_enternewpsw, ToastUtil.CENTER);
            return;
        }
        if(TextUtils.isEmpty(newpsw2)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_enternewpsw2, ToastUtil.CENTER);
            return;
        }
        if(newpsw.length() < 6){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_pswlength, ToastUtil.CENTER);
            return;
        }
        if(oldpsw.equals(newpsw)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_newpswdiffold, ToastUtil.CENTER);
            return;
        }

        if(newpsw.equals(newpsw2)){
            commitUpdatePsw();

        }else {
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_twopswdiff, ToastUtil.CENTER);
        }
    }

    /**
     *
     */
    private void commitUpdatePsw(){
        progressDid1();

        keyList.clear();
        valueList.clear();

        keyList.add("userid");
        keyList.add("password");
        keyList.add("newPassword");

        valueList.add(userId);
        valueList.add(oldpsw);
        valueList.add(newpsw);

        String forgeturl = HttpConstants.ChangePsw_Url;
        HttpPostUtil post = new HttpPostUtil(handler, forgeturl, keyList, valueList, UpdatePsw_Flag);
        Thread thread = new Thread(post);
        thread.start();
    }


}

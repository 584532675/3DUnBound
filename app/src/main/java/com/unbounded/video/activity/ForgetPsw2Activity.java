package com.unbounded.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/19 0019.
 */

public class ForgetPsw2Activity extends BaseActivity {
    public final static int ForgetPsw_Flag = 0;
    String mobileStr,recodeStr,msgId,psw1,psw2;
    ContainsEmojiEditText pswedt1,pswedt2;
    Button commitbtn;
    ImageView pswvisibleiv,psw2visibleiv;
    boolean pswvisible = false,psw2visible = false;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //忘记密码
                case ForgetPsw_Flag:
                    String forgetvalue = msg.obj.toString();
                    Log.e("info","forgetvalue="+forgetvalue);
                    progressDiddismiss();
                    if (!(TextUtils.isEmpty(forgetvalue))) {
                        if("true".equals(forgetvalue)){
                            ToastUtil.showToast(ForgetPsw2Activity.this, com.unbounded.video.R.string.Word_forgetpswsuccess, ToastUtil.CENTER);
                            ExitApplication.getInstance().exitForgetActivitys();

                        }else if("false".equals(forgetvalue)){
                            ToastUtil.showToast(ForgetPsw2Activity.this, com.unbounded.video.R.string.word_forgetpswerror, ToastUtil.CENTER);
                        }
                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(ForgetPsw2Activity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        whiteBar();
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_forget2;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(true);
        setTitleBg(com.unbounded.video.R.color.white);
        setLeftBtnBg(com.unbounded.video.R.mipmap.black_back);

        ExitApplication.getInstance().addLoginActivity(this);
        ExitApplication.getInstance().addForgetActivity(this);

        mobileStr = intent.getStringExtra("mobileStr");

    }

    @Override
    public void initView() {
        super.initView();

        pswedt1 = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_pswedt);
        pswedt2 = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_repswedt);
        commitbtn = (Button) findViewById(com.unbounded.video.R.id.register_registerbtn);
        pswvisibleiv = (ImageView) findViewById(com.unbounded.video.R.id.register_pswvisible);
        psw2visibleiv = (ImageView) findViewById(com.unbounded.video.R.id.register_repswvisible);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        commitbtn.setOnClickListener(this);
        pswvisibleiv.setOnClickListener(this);
        psw2visibleiv.setOnClickListener(this);
    }

    //提交
    private void commitMethod(){
        psw1 = pswedt1.getText().toString().trim();
        psw2 = pswedt2.getText().toString().trim();

        if(TextUtils.isEmpty(psw1)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_pswhint, ToastUtil.CENTER);
            return;
        }
        if(TextUtils.isEmpty(psw2)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_psw2hint, ToastUtil.CENTER);
            return;
        }
        if(psw1.length() < 6){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_pswlength, ToastUtil.CENTER);
            return;
        }

        if(psw1.equals(psw2)){
            postCommit();
        }else {
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_twopswdiff, ToastUtil.CENTER);
        }

    }

    private void postCommit(){
        progressDid();

        keyList.clear();
        valueList.clear();

        keyList.add("phone");
        keyList.add("password");
        valueList.add(mobileStr);
        valueList.add(psw1);

        String forgeturl = HttpConstants.UpdatePswByPhone_Url;
        HttpPostUtil post = new HttpPostUtil(handler, forgeturl, keyList, valueList, ForgetPsw_Flag);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 密码可见切换
     */
    private void pswView(){
        psw1 = pswedt1.getText().toString().trim();

        if(pswvisible == false){
            if(!(TextUtils.isEmpty(psw1))){
                //选择状态 显示明文--设置为可见的密码
                pswedt1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                pswvisibleiv.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
                pswvisible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pswvisibleiv.setImageResource(com.unbounded.video.R.mipmap.eyeicon);
            pswvisible = false;
        }
        pswedt1.setSelection(pswedt1.getText().toString().length());
    }
    /**
     * 确认密码可见切换
     */
    private void psw2View(){
        psw2 = pswedt2.getText().toString().trim();

        if(psw2visible == false){
            if(!(TextUtils.isEmpty(psw2))){
                //选择状态 显示明文--设置为可见的密码
                pswedt2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                psw2visibleiv.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
                psw2visible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            psw2visibleiv.setImageResource(com.unbounded.video.R.mipmap.eyeicon);
            psw2visible = false;
        }
        pswedt2.setSelection(pswedt2.getText().toString().length());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            //提交
            case com.unbounded.video.R.id.register_registerbtn:
                commitMethod();

                break;
            //隐藏新密码
            case com.unbounded.video.R.id.register_pswvisible:
                pswView();
                break;
            //隐藏确认密码
            case com.unbounded.video.R.id.register_repswvisible:
                psw2View();
                break;
        }
    }
}
